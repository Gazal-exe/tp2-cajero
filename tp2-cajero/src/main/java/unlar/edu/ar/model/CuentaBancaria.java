package unlar.edu.ar.model;

import java.util.ArrayList;

public class CuentaBancaria {
    
    private final String numeroCuenta; 
    private double saldo;
    private String titular;
    private EstadoCuenta estado;
    private ArrayList<String> historialTransacciones;

    public CuentaBancaria(String numeroCuenta, String titular, double saldoInicial) {
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.saldo = saldoInicial;
        this.estado = EstadoCuenta.ACTIVA; // nace activa por defecto
        this.historialTransacciones = new ArrayList<>();
        
        // registramos primer movimiento
        this.historialTransacciones.add("Apertura de cuenta. Saldo inicial: $" + saldoInicial);
    }

    // --- GETTERS ---
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getTitular() {
        return titular;
    }

    public EstadoCuenta getEstado() {
        return this.estado;
    }

    public ArrayList<String> getHistorialTransacciones() {
        return historialTransacciones;
    }

    // --- SETTERS ---
    // no ponemos setter para numeroCuenta porque es final y no tiene que cambiarse

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public void setEstado(EstadoCuenta estado) {
        this.estado = estado;
    }

    // Metodo toString() informativo
    @Override
    public String toString() {
        return "CuentaBancaria{" +
                "numeroCuenta='" + numeroCuenta + '\'' +
                ", titular='" + titular + '\'' +
                ", saldo=$" + saldo +
                ", estado=" + this.estado +
                '}';
    }
}