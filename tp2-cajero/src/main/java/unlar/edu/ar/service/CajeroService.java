package unlar.edu.ar.service;

import unlar.edu.ar.model.CuentaBancaria;
import unlar.edu.ar.model.EstadoCuenta;
import unlar.edu.ar.model.TipoTransaccion;
import unlar.edu.ar.model.Transaccion;
import unlar.edu.ar.exception.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CajeroService {

    // Nuestro "banco de datos" en memoria
    private Map<String, CuentaBancaria> cuentas;

    public CajeroService() {
        this.cuentas = new HashMap<>();
    }

    // Metodo para cargar cuentas al inicio del dia (como pide el Main)
    public void agregarCuenta(CuentaBancaria cuenta) {
        cuentas.put(cuenta.getNumeroCuenta(), cuenta);
    }

    // valida cuentabancaria y estado, y devuelve la cuenta si todo ok. Si no, tira
    // la excepcion correspondiente
    private CuentaBancaria validarIngreso(String numeroCuenta) throws CuentaInactivaException {
        CuentaBancaria cuenta = cuentas.get(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta número " + numeroCuenta + " no existe.");
        }
        // CORRECCIÓN AQUÍ: Si es igual a INACTIVA, rebota.
        if (cuenta.getEstado() == EstadoCuenta.INACTIVA) {
            throw new CuentaInactivaException("Operación denegada: La cuenta se encuentra inactiva/desactivada.");
        }
        return cuenta;
    }

    // valida si se puede mover plata
    private CuentaBancaria obtenerCuentaParaOperar(String numeroCuenta)
            throws CuentaInactivaException, CuentaBloqueadaException {
        CuentaBancaria cuenta = validarIngreso(numeroCuenta);
        if (cuenta.getEstado() == EstadoCuenta.BLOQUEADA) {
            throw new CuentaBloqueadaException("Cuenta BLOQUEADA: Puede consultar saldo, pero no mover fondos.");
        }
        return cuenta;
    }

    // --- OPERACIONES PRINCIPALES ---

    public void depositar(String numeroCuenta, double monto) throws Exception {
        // Usamos el que chequea BLOQUEO
        CuentaBancaria cuenta = obtenerCuentaParaOperar(numeroCuenta);
        if (monto <= 0)
            throw new IllegalArgumentException("Monto inválido.");

        cuenta.setSaldo(cuenta.getSaldo() + monto);
        registrarMovimiento(cuenta, TipoTransaccion.DEPOSITO, monto, "Depósito en cajero");
    }

    public void extraer(String numeroCuenta, double monto) throws Exception {
        // Usamos el que chequea BLOQUEO
        CuentaBancaria cuenta = obtenerCuentaParaOperar(numeroCuenta);
        validarExtraccion(cuenta, monto);

        cuenta.setSaldo(cuenta.getSaldo() - monto);
        registrarMovimiento(cuenta, TipoTransaccion.EXTRACCION, monto, "Extracción en cajero");
    }

    public void transferir(String cuentaOrigen, String cuentaDestino, double monto) throws Exception {
        // Origen debe poder operar (no estar bloqueada)
        CuentaBancaria origen = obtenerCuentaParaOperar(cuentaOrigen);
        // Destino solo debe existir y no estar inactiva
        CuentaBancaria destino = validarIngreso(cuentaDestino);

        validarExtraccion(origen, monto);

        origen.setSaldo(origen.getSaldo() - monto);
        destino.setSaldo(destino.getSaldo() + monto);

        registrarMovimiento(origen, TipoTransaccion.TRANSFERENCIA, monto, "Transferencia enviada a " + cuentaDestino);
        registrarMovimiento(destino, TipoTransaccion.TRANSFERENCIA, monto, "Transferencia recibida de " + cuentaOrigen);
    }

    public double consultarSaldo(String numeroCuenta) throws CuentaInactivaException {
        // Para consultar saldo solo pedimos validarIngreso (aunque esté bloqueada puede
        // ver)
        CuentaBancaria cuenta = validarIngreso(numeroCuenta);
        registrarMovimiento(cuenta, TipoTransaccion.CONSULTA, 0, "Consulta de saldo");
        return cuenta.getSaldo();
    }

    public List<String> obtenerUltimosMovimientos(String numeroCuenta) throws CuentaInactivaException {
        // Para ver historial solo pedimos validarIngreso
        CuentaBancaria cuenta = validarIngreso(numeroCuenta);
        List<String> historial = cuenta.getHistorialTransacciones();
        int fromIndex = Math.max(0, historial.size() - 10);
        return historial.subList(fromIndex, historial.size());
    }

    // --- MÉTODOS PRIVADOS (De apoyo interno) ---



    // Aplica las reglas estrictas de extracción del TP
    private void validarExtraccion(CuentaBancaria cuenta, double monto)
            throws SaldoInsuficienteException, LimiteExtraccionExcedidoException {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        if (monto > 10000) { // Límite de $10.000 por operación [cite: 88]
            throw new LimiteExtraccionExcedidoException(
                    "Límite excedido: No puede operar más de $10,000 por transacción.");
        }
        if (cuenta.getSaldo() < monto) { // Saldo insuficiente [cite: 87]
            throw new SaldoInsuficienteException("Saldo insuficiente. Su saldo actual es: $" + cuenta.getSaldo());
        }
    }

    // Crea el objeto Transaccion y lo guarda en la cuenta usando el StringBuilder
    // que armamos antes
    private void registrarMovimiento(CuentaBancaria cuenta, TipoTransaccion tipo, double monto, String descripcion) {
        Transaccion transaccion = new Transaccion(tipo, monto, descripcion);
        String registro = transaccion.formatearParaHistorial(cuenta.getSaldo());
        cuenta.getHistorialTransacciones().add(registro);
    }
}