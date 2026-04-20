package unlar.edu.ar.model;

import unlar.edu.ar.util.FormatoUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaccion {

    private TipoTransaccion tipo;
    private double monto;
    private LocalDateTime fechaHora;
    private String descripcion;

    // Constructor
    public Transaccion(TipoTransaccion tipo, double monto, String descripcion) {
        this.tipo = tipo;
        this.monto = monto;
        this.fechaHora = LocalDateTime.now();
        this.descripcion = descripcion;
    }

    // --- GETTERS ---
    public TipoTransaccion getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Metodo para formatear la transaccion (Punto 2.5)
    public String formatearParaHistorial(double saldoResultante) {
        // Le damos el formato [2024-01-15 14:30:25]
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaFormateada = fechaHora.format(formatter);

         // Usamos StringBuilder y el util de formato para mantener una salida consistente.
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(fechaFormateada).append("] ")
                .append(tipo.name()).append(": ").append(FormatoUtil.formatearMoneda(monto))
                .append(" | Saldo: ").append(FormatoUtil.formatearMoneda(saldoResultante));

        return sb.toString();
    }
}