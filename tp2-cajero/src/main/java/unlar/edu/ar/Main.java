package unlar.edu.ar;

import unlar.edu.ar.model.CuentaBancaria;
import unlar.edu.ar.service.CajeroService;
import unlar.edu.ar.ui.MenuUI;

public class Main {
    public static void main(String[] args) {
        // 1. Inicializamos
        CajeroService service = new CajeroService();

        // 2. Carga inicial de datos (Punto 4.12 y 130 del PDF)
        // Creamos al menos 3 cuentas diferentes
        service.agregarCuenta(new CuentaBancaria("101", "Lionel Messi", 50000.0));
        service.agregarCuenta(new CuentaBancaria("102", "Angel Di Maria", 20000.0));
        service.agregarCuenta(new CuentaBancaria("103", "Julian Alvarez", 5000.0));

        // 3. Simulación automática de un día de operaciones (Punto 130)
        // Ejecutamos 15 transacciones variadas para que el historial no esté vacío
        System.out.println(">>> EJECUTANDO SIMULACIÓN DE 15 TRANSACCIONES...");
        
        try {
            // Operaciones de la Cuenta 101
            service.depositar("101", 5000.0);         // 1
            service.extraer("101", 2000.0);           // 2
            service.consultarSaldo("101");            // 3
            service.transferir("101", "102", 1000.0); // 4
            
            // Operaciones de la Cuenta 102
            service.depositar("102", 500.0);          // 5
            service.extraer("102", 1500.0);           // 6
            service.transferir("102", "103", 2000.0); // 7
            service.consultarSaldo("102");            // 8
            
            // Operaciones de la Cuenta 103
            service.depositar("103", 1000.0);         // 9
            service.extraer("103", 500.0);            // 10
            service.transferir("103", "101", 300.0);  // 11
            service.consultarSaldo("103");            // 12
            
            // Operaciones extras para llegar a las 15
            service.depositar("101", 100.0);          // 13
            service.extraer("102", 100.0);            // 14
            service.transferir("102", "101", 50.0);   // 15

            System.out.println("✅ Simulación finalizada. Datos cargados en el sistema.\n");

        } catch (Exception e) {
            // Si algo falla en la simulación, lo reportamos pero no detenemos el programa
            System.err.println("⚠️ Error durante la simulación inicial: " + e.getMessage());
        }

        // 4. Lanzamos la interfaz de usuario
        // El cajero ahora queda a disposición de quien lo ejecute
        MenuUI menu = new MenuUI(service);
        menu.iniciar();
    }
}