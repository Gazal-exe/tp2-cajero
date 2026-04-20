package unlar.edu.ar.util;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatoUtil {

     // Reutilizamos una sola configuracion para mostrar importes con 2 decimales.
    private static final NumberFormat FORMATO_MONEDA;

    static {
        FORMATO_MONEDA = NumberFormat.getNumberInstance(Locale.US);
        FORMATO_MONEDA.setMinimumFractionDigits(2);
        FORMATO_MONEDA.setMaximumFractionDigits(2);
    }

     private FormatoUtil() {
        // Constructor privado para evitar instanciación
    }

    // Centraliza el formato pedido por la consigna: $XXX,XXX.00
    public static String formatearMoneda(double monto) {
        return "$" + FORMATO_MONEDA.format(monto);
    }

}
