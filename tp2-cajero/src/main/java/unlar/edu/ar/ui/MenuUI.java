package unlar.edu.ar.ui;

import unlar.edu.ar.service.CajeroService;
// import unlar.edu.ar.exception.*;

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
        System.out.println("========================================");
        System.out.println("   BIENVENIDO AL CAJERO AUTOMÁTICO UNLaR   ");
        System.out.println("========================================");
        
        // Simulamos un inicio de sesión pidiendo la cuenta
        System.out.print("Por favor, ingrese su Número de Cuenta para operar: ");
        String cuentaActual = scanner.nextLine();

        int opcion = -1;

        while (opcion != 0) {
            mostrarOpciones();
            
            try {
                // Leemos como número para cumplir con el manejo de InputMismatchException
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiamos el buffer del enter

                // Requisito 2.4: Uso de 'switch expression' (Java 14+)
                switch (opcion) {
                    case 1 -> realizarDeposito(cuentaActual);
                    case 2 -> realizarExtraccion(cuentaActual);
                    case 3 -> realizarTransferencia(cuentaActual);
                    case 4 -> consultarSaldo(cuentaActual);
                    case 5 -> verHistorial(cuentaActual);
                    case 0 -> System.out.println("Cerrando sesión... ¡Gracias por utilizar nuestro banco!");
                    default -> System.out.println("❌ Opción inválida. Intente nuevamente.");
                }

            } catch (InputMismatchException e) {
                // Requisito 2.4: Validación de entrada numérica
                System.out.println("❌ ERROR FATAL: El teclado del cajero solo acepta números.");
                scanner.nextLine(); // Limpiamos la "basura" que escribió el usuario para que no se trabe el bucle
            } catch (Exception e) {
                // Atrapamos nuestras excepciones personalizadas (SaldoInsuficiente, CuentaInactiva, etc.)
                System.out.println("⚠️ ATENCIÓN: " + e.getMessage());
            }
        }
    }

    private void mostrarOpciones() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Depositar Dinero");
        System.out.println("2. Extraer Dinero");
        System.out.println("3. Transferir a otra cuenta");
        System.out.println("4. Consultar Saldo");
        System.out.println("5. Ver Últimos Movimientos");
        System.out.println("0. Retirar Tarjeta (Salir)");
        System.out.print("Seleccione una opción: ");
    }

    // --- MÉTODOS DE OPERACIÓN ---

    private void realizarDeposito(String cuenta) throws Exception {
        System.out.print("Ingrese el monto a depositar: $");
        double monto = scanner.nextDouble();
        scanner.nextLine(); 
        
        cajeroService.depositar(cuenta, monto);
        System.out.println("✅ Depósito exitoso.");
    }

    private void realizarExtraccion(String cuenta) throws Exception {
        System.out.print("Ingrese el monto a extraer: $");
        double monto = scanner.nextDouble();
        scanner.nextLine();
        
        cajeroService.extraer(cuenta, monto);
        System.out.println("✅ Por favor, retire su dinero por la ranura inferior.");
    }

    private void realizarTransferencia(String cuentaOrigen) throws Exception {
        System.out.print("Ingrese el Número de Cuenta de destino: ");
        String cuentaDestino = scanner.nextLine();
        
        System.out.print("Ingrese el monto a transferir: $");
        double monto = scanner.nextDouble();
        scanner.nextLine();
        
        cajeroService.transferir(cuentaOrigen, cuentaDestino, monto);
        System.out.println("✅ Transferencia enviada correctamente a la cuenta: " + cuentaDestino);
    }

    private void consultarSaldo(String cuenta) throws Exception {
        double saldo = cajeroService.consultarSaldo(cuenta);
        // Requisito 2.4: Formato de moneda específico
        System.out.println("💰 Su saldo actual es: " + formatearMoneda(saldo));
    }

    private void verHistorial(String cuenta) throws Exception {
        System.out.println("\n--- ÚLTIMOS MOVIMIENTOS ---");
        List<String> historial = cajeroService.obtenerUltimosMovimientos(cuenta);
        
        if (historial.isEmpty()) {
            System.out.println("No hay movimientos registrados.");
        } else {
            // Imprimimos cada línea del historial (que ya viene formateada desde la clase Transaccion)
            historial.forEach(System.out::println);
        }
    }

    // --- UTILIDAD ---
    
    // Cumple con el Requisito 2.4 de formatear el dinero como $XXX,XXX.00
    private String formatearMoneda(double monto) {
        // Usamos Locale.US para asegurar que el separador de miles sea la coma y el de decimales el punto
        return String.format(Locale.US, "$%,.2f", monto);
    }
}