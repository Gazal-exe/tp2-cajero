package unlar.edu.ar;

import unlar.edu.ar.model.CuentaBancaria;
import unlar.edu.ar.model.EstadoCuenta;
import unlar.edu.ar.service.CajeroService;
import unlar.edu.ar.ui.MenuUI;

public class Main {
    public static void main(String[] args) {
        CajeroService service = new CajeroService();

        // 1. Creamos los objetos para tener las referencias
        CuentaBancaria c1 = new CuentaBancaria("101", "Almonacid Nahuel", 50000.0);
        CuentaBancaria c2 = new CuentaBancaria("102", "Ibañez Yessica", 20000.0);
        CuentaBancaria c3 = new CuentaBancaria("103", "Gonzalez Esteban", 5000.0);
        CuentaBancaria c4 = new CuentaBancaria("104", "Arias Molino Walter", 15000.0);

        // 2. Los agregamos (todos entran como ACTIVA para que la simulación no falle)
        service.agregarCuenta(c1);
        service.agregarCuenta(c2);
        service.agregarCuenta(c3);
        service.agregarCuenta(c4);

        // 3. Simulación de 15 transacciones
        System.out.println(">>> EJECUTANDO SIMULACIÓN DE 15 TRANSACCIONES...");
        try {
            service.depositar("101", 5000.0); service.extraer("101", 2000.0);
            service.consultarSaldo("101"); service.transferir("101", "102", 1000.0);
            service.depositar("102", 500.0); service.extraer("102", 1500.0);
            service.transferir("102", "103", 2000.0); service.consultarSaldo("102");
            service.depositar("103", 1000.0); service.extraer("103", 500.0);
            service.transferir("103", "101", 300.0); service.consultarSaldo("103");
            service.depositar("101", 100.0); service.extraer("102", 100.0);
            service.transferir("102", "101", 50.0);

            System.out.println("Simulación finalizada con éxito.\n");
        } catch (Exception e) {
            System.err.println("Error en simulación: " + e.getMessage());
        }

        // 4. AHORA SÍ, aplicamos los estados para la prueba del usuario
        // Esteban puede entrar pero no mover plata
        c3.setEstado(EstadoCuenta.BLOQUEADA); 
        // Walter ni siquiera puede entrar
        c4.setEstado(EstadoCuenta.INACTIVA);

        // 5. Lanzamos el Menu
        MenuUI menu = new MenuUI(service);
        menu.iniciar();
    }
}