package unlar.edu.ar.service;

import unlar.edu.ar.model.CuentaBancaria;
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

    // Metodo para cargar cuentas al inicio del día (como pide el Main)
    public void agregarCuenta(CuentaBancaria cuenta) {
        cuentas.put(cuenta.getNumeroCuenta(), cuenta);
    }

    // --- OPERACIONES PRINCIPALES ---

    public void depositar(String numeroCuenta, double monto) throws CuentaInactivaException {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor a cero.");
        }
        CuentaBancaria cuenta = obtenerCuentaValidada(numeroCuenta);

        // Actualizamos saldo
        cuenta.setSaldo(cuenta.getSaldo() + monto);

        // Registramos
        registrarMovimiento(cuenta, TipoTransaccion.DEPOSITO, monto, "Depósito en cajero");
    }

    public void extraer(String numeroCuenta, double monto)
            throws CuentaInactivaException, SaldoInsuficienteException, LimiteExtraccionExcedidoException {
        CuentaBancaria cuenta = obtenerCuentaValidada(numeroCuenta);

        // Validamos las reglas de negocio antes de tocar la plata
        validarExtraccion(cuenta, monto);

        // Actualizamos saldo
        cuenta.setSaldo(cuenta.getSaldo() - monto);

        // Registramos
        registrarMovimiento(cuenta, TipoTransaccion.EXTRACCION, monto, "Extracción en cajero");
    }

    // La transferencia es un poco mas compleja porque involucra DOS cuentas,
    // pero la logica es la misma: validamos TODO antes de tocar la plata.
    // O pasa todo, o no pasa nada
    public void transferir(String cuentaOrigen, String cuentaDestino, double monto)
            throws CuentaInactivaException, SaldoInsuficienteException, LimiteExtraccionExcedidoException {
        // Transaccion atomica: validamos TODO antes de modificar nada
        CuentaBancaria origen = obtenerCuentaValidada(cuentaOrigen);
        CuentaBancaria destino = obtenerCuentaValidada(cuentaDestino);
        validarExtraccion(origen, monto);

        // Si llegó hasta acá sin tirar Exception, procedemos a mover la plata
        origen.setSaldo(origen.getSaldo() - monto);
        destino.setSaldo(destino.getSaldo() + monto);

        // Registramos en el historial de ambas cuentas
        registrarMovimiento(origen, TipoTransaccion.TRANSFERENCIA, monto, "Transferencia enviada a " + cuentaDestino);
        registrarMovimiento(destino, TipoTransaccion.TRANSFERENCIA, monto, "Transferencia recibida de " + cuentaOrigen);
    }

    public double consultarSaldo(String numeroCuenta) throws CuentaInactivaException {
        CuentaBancaria cuenta = obtenerCuentaValidada(numeroCuenta);
        // La consulta de saldo no modifica el estado ni descuenta dinero [cite: 96]
        registrarMovimiento(cuenta, TipoTransaccion.CONSULTA, 0, "Consulta de saldo");
        return cuenta.getSaldo();
    }

    // funcion obtenerUltimosMovimientos
    public List<String> obtenerUltimosMovimientos(String numeroCuenta) throws CuentaInactivaException {
        CuentaBancaria cuenta = obtenerCuentaValidada(numeroCuenta);
        List<String> historial = cuenta.getHistorialTransacciones();

        // Devolvemos solo las últimas 10 transacciones
        int fromIndex = Math.max(0, historial.size() - 10);
        return historial.subList(fromIndex, historial.size());
    }

    // --- MÉTODOS PRIVADOS (De apoyo interno) ---

    // Este metodo centraliza la validación para no repetir codigo:
    // En lugar de chequear si la cuenta está activa en todos los metodos,
    // armamos este. Si en el futuro el banco pide otra validacion, solo tocamos ese
    // pedacito

    private CuentaBancaria obtenerCuentaValidada(String numeroCuenta) throws CuentaInactivaException {
        CuentaBancaria cuenta = cuentas.get(numeroCuenta);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta número " + numeroCuenta + " no existe.");
        }
        if (!cuenta.isActiva()) {
            throw new CuentaInactivaException("Operación denegada: La cuenta se encuentra inactiva.");
        }
        return cuenta;
    }

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