package unlar.edu.ar.ui;

import unlar.edu.ar.service.CajeroService;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MenuUI {

    private CajeroService cajeroService;
    private Scanner scanner;

    public MenuUI(CajeroService cajeroService) {
        this.cajeroService = cajeroService;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        boolean cajeroEncendido = true;

        // BUCLE EXTERNO: Mantiene el cajero prendido para nuevos clientes
        while (cajeroEncendido) {
            System.out.println("\n========================================");
            System.out.println("   BIENVENIDO AL CAJERO AUTOMÁTICO UNLaR   ");
            System.out.println("========================================");

            System.out.print("Por favor, ingrese su Número de Cuenta (o '999' para apagar): ");
            String cuentaActual = scanner.nextLine();

            // Opción técnica para salir del programa completo
            if (cuentaActual.equals("999")) {
                System.out.println("Apagando sistema de seguridad... Adiós.");
                break; 
            }

            // Validamos ingreso antes de entrar al menú de esa cuenta
            try {
                cajeroService.consultarSaldo(cuentaActual); // Usamos una consulta rápida para validar existencia/estado
                System.out.println("Sesión iniciada.");
            } catch (Exception e) {
                System.out.println("ACCESO DENEGADO: " + e.getMessage());
                continue; // Vuelve a pedir cuenta
            }

            int opcion = -1;

            // BUCLE INTERNO: La sesión del usuario actual
            while (opcion != 0) {
                mostrarOpciones();

                try {
                    opcion = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcion) {
                        case 1 -> realizarDeposito(cuentaActual);
                        case 2 -> realizarExtraccion(cuentaActual);
                        case 3 -> realizarTransferencia(cuentaActual);
                        case 4 -> consultarSaldo(cuentaActual);
                        case 5 -> verHistorial(cuentaActual);
                        case 0 -> System.out.println("Cerrando sesión de la cuenta " + cuentaActual + "...");
                        default -> {
                            System.out.println("Opción inválida.");
                            continue;
                        }
                    }

                    if (opcion != 0) {
                        opcion = gestionarContinuacion();
                    }

                } catch (InputMismatchException e) {
                    System.out.println("ERROR: Use solo números.");
                    scanner.nextLine();
                } catch (Exception e) {
                    System.out.println("ATENCIÓN: " + e.getMessage());
                    opcion = gestionarContinuacion();
                }
            }
            // Al terminar el while interno (opcion == 0), vuelve al inicio del while externo
        }
    }

    private void mostrarOpciones() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Depositar Dinero");
        System.out.println("2. Extraer Dinero");
        System.out.println("3. Transferir a otra cuenta");
        System.out.println("4. Consultar Saldo");
        System.out.println("5. Ver Últimos Movimientos");
        System.out.println("0. Cerrar Sesión / Salir"); // Cambiamos el texto
        System.out.print("Seleccione una opción: ");
    }

    private int gestionarContinuacion() {
        System.out.println("\n----------------------------------------");
        System.out.println("¿Desea realizar otra operación?");
        System.out.println("1. Volver al menú principal");
        System.out.println("0. Cerrar Sesión");
        System.out.print("Seleccione: ");

        try {
            int respuesta = scanner.nextInt();
            scanner.nextLine();
            return (respuesta == 0) ? 0 : -1;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }

    // --- MÉTODOS DE OPERACIÓN (Sin cambios) ---

    private void realizarDeposito(String cuenta) throws Exception {
        System.out.print("Ingrese el monto a depositar: $");
        double monto = scanner.nextDouble();
        scanner.nextLine();
        cajeroService.depositar(cuenta, monto);
        System.out.println("Depósito exitoso.");
    }

    private void realizarExtraccion(String cuenta) throws Exception {
        System.out.print("Ingrese el monto a extraer: $");
        double monto = scanner.nextDouble();
        scanner.nextLine();
        cajeroService.extraer(cuenta, monto);
        System.out.println("Por favor, retire su dinero.");
    }

    private void realizarTransferencia(String cuentaOrigen) throws Exception {
        System.out.print("Cuenta destino: ");
        String cuentaDestino = scanner.nextLine();
        System.out.print("Monto: $");
        double monto = scanner.nextDouble();
        scanner.nextLine();
        cajeroService.transferir(cuentaOrigen, cuentaDestino, monto);
        System.out.println("Transferencia realizada.");
    }

    private void consultarSaldo(String cuenta) throws Exception {
        double saldo = cajeroService.consultarSaldo(cuenta);
        System.out.println("Su saldo actual es: " + formatearMoneda(saldo));
    }

    private void verHistorial(String cuenta) throws Exception {
        System.out.println("\n--- ÚLTIMOS MOVIMIENTOS ---");
        List<String> historial = cajeroService.obtenerUltimosMovimientos(cuenta);
        if (historial.isEmpty())
            System.out.println("No hay movimientos.");
        else
            historial.forEach(System.out::println);
    }

    private String formatearMoneda(double monto) {
        return String.format(Locale.US, "$%,.2f", monto);
    }
}