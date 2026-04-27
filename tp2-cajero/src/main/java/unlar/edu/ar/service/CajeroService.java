package unlar.edu.ar.service;

import unlar.edu.ar.model.CuentaBancaria;
import unlar.edu.ar.model.EstadoCuenta;
import unlar.edu.ar.model.TipoTransaccion;
import unlar.edu.ar.model.Transaccion;
import unlar.edu.ar.exception.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CajeroService {

    // banco de datos en memoria
    private Map<String, CuentaBancaria> cuentas;
    
    // control de limites diarios 
    private Map<String, Double> extraccionesDiarias;
    private LocalDate diaActual;

    public CajeroService() {
        this.cuentas = new HashMap<>();
        this.extraccionesDiarias = new HashMap<>();
        this.diaActual = LocalDate.now();
    }

    public void agregarCuenta(CuentaBancaria cuenta) {
        cuentas.put(cuenta.getNumeroCuenta(), cuenta);
    }

    public void validarAcceso(String numeroCuenta) throws CuentaInactivaException {
        validarIngreso(numeroCuenta);
    }

    private CuentaBancaria validarIngreso(String numeroCuenta) throws CuentaInactivaException {
        CuentaBancaria cuenta = cuentas.get(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta número " + numeroCuenta + " no existe.");
        }
        if (cuenta.getEstado() == EstadoCuenta.INACTIVA) {
            throw new CuentaInactivaException("Operación denegada: La cuenta se encuentra inactiva/desactivada.");
        }
        return cuenta;
    }

    private CuentaBancaria obtenerCuentaParaOperar(String numeroCuenta) throws CuentaInactivaException, CuentaBloqueadaException {
        CuentaBancaria cuenta = validarIngreso(numeroCuenta);
        if (cuenta.getEstado() == EstadoCuenta.BLOQUEADA) {
            throw new CuentaBloqueadaException("Cuenta BLOQUEADA: Puede consultar saldo, pero no mover fondos.");
        }
        return cuenta;
    }

    // --- OPERACIONES PRINCIPALES ---

    public void depositar(String numeroCuenta, double monto) throws Exception {
        CuentaBancaria cuenta = obtenerCuentaParaOperar(numeroCuenta);
        if (monto <= 0) throw new IllegalArgumentException("Monto inválido.");

        cuenta.setSaldo(cuenta.getSaldo() + monto);
        registrarMovimiento(cuenta, TipoTransaccion.DEPOSITO, monto, "Depósito en cajero");
    }

    public void extraer(String numeroCuenta, double monto) throws Exception {
        CuentaBancaria cuenta = obtenerCuentaParaOperar(numeroCuenta);
        
        // 1. Validamos que tenga plata
        validarFondos(cuenta, monto);
        
        // 2. Validamos que no se pase del limite diario (solo para extracciones)
        validarLimiteExtraccionDiaria(numeroCuenta, monto);

        // 3. Modificamos saldos y registros
        cuenta.setSaldo(cuenta.getSaldo() - monto);
        
        // 4. Sumamos al contador diario de esta cuenta
        double extraidoHoy = extraccionesDiarias.getOrDefault(numeroCuenta, 0.0);
        extraccionesDiarias.put(numeroCuenta, extraidoHoy + monto);

        registrarMovimiento(cuenta, TipoTransaccion.EXTRACCION, monto, "Extracción en cajero");
    }

    public void transferir(String cuentaOrigen, String cuentaDestino, double monto) throws Exception {
        CuentaBancaria origen = obtenerCuentaParaOperar(cuentaOrigen);
        CuentaBancaria destino = validarIngreso(cuentaDestino);

        // En transferencias SOLO validamos fondos, NO el limite del cajero
        validarFondos(origen, monto);

        origen.setSaldo(origen.getSaldo() - monto);
        destino.setSaldo(destino.getSaldo() + monto);

        registrarMovimiento(origen, TipoTransaccion.TRANSFERENCIA, monto, "Transferencia enviada a " + cuentaDestino);
        registrarMovimiento(destino, TipoTransaccion.TRANSFERENCIA, monto, "Transferencia recibida de " + cuentaOrigen);
    }

    public double consultarSaldo(String numeroCuenta) throws CuentaInactivaException {
        CuentaBancaria cuenta = validarIngreso(numeroCuenta);
        registrarMovimiento(cuenta, TipoTransaccion.CONSULTA, 0, "Consulta de saldo");
        return cuenta.getSaldo();
    }

    public List<String> obtenerUltimosMovimientos(String numeroCuenta) throws CuentaInactivaException {
        CuentaBancaria cuenta = validarIngreso(numeroCuenta);
        List<String> historial = cuenta.getHistorialTransacciones();
        int fromIndex = Math.max(0, historial.size() - 10);
        return historial.subList(fromIndex, historial.size());
    }

    // --- METODOS PRIVADOS (De apoyo interno) ---

    // Este metodo revisa si cambió de dia a las 00:00 para reiniciar los limites
    private void verificarCambioDeDia() {
        if (!LocalDate.now().equals(diaActual)) {
            extraccionesDiarias.clear(); // Borramos el historial del día anterior
            diaActual = LocalDate.now();
        }
    }

    // Valida que el monto sea logico y que alcance el saldo (Se usa en Extracción y Transferencia)
    private void validarFondos(CuentaBancaria cuenta, double monto) throws SaldoInsuficienteException {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        if (cuenta.getSaldo() < monto) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Su saldo actual es: $" + cuenta.getSaldo());
        }
    }

    // Valida el limite de 10.000 y calcula el remanente inteligente (Solo Extracción)
    private void validarLimiteExtraccionDiaria(String numeroCuenta, double monto) throws LimiteExtraccionExcedidoException {
        verificarCambioDeDia();
        double extraidoHoy = extraccionesDiarias.getOrDefault(numeroCuenta, 0.0);
        double limiteDiario = 10000.0;

        if (extraidoHoy + monto > limiteDiario) {
            double disponibleHoy = limiteDiario - extraidoHoy;
            if (disponibleHoy <= 0) {
                throw new LimiteExtraccionExcedidoException("Límite diario alcanzado. No puede extraer más dinero por hoy.");
            } else {
                throw new LimiteExtraccionExcedidoException("Límite excedido. Usted ya extrajo dinero hoy. Solo puede extraer $" + disponibleHoy + " más por hoy.");
            }
        }
    }

    private void registrarMovimiento(CuentaBancaria cuenta, TipoTransaccion tipo, double monto, String descripcion) {
        Transaccion transaccion = new Transaccion(tipo, monto, descripcion);
        String registro = transaccion.formatearParaHistorial(cuenta.getSaldo());
        cuenta.getHistorialTransacciones().add(registro);
    }
}
