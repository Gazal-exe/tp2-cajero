package unlar.edu.ar.ui;
import unlar.edu.ar.model.CuentaBancaria;
import java.util.Scanner;

public class MenuUI {
    private CuentaBancaria cuenta;
    private Scanner scanner;

    public MenuUI(CuentaBancaria cuenta) {
        this.cuenta = cuenta;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("=== Menú Principal ===");
            System.out.println("1. Consultar Saldo");
            System.out.println("2. Retirar Dinero");
            System.out.println("3. Depositar Dinero");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    consultarSaldo();
                    break;
                case 2:
                    retirarDinero();
                    break;
                case 3:
                    depositarDinero();
                    break;
                case 4:
                    System.out.println("Gracias por usar el cajero automático. ¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente nuevamente.");
            }
        } while (opcion != 4);
    }

    private void consultarSaldo() {
        System.out.println("Su saldo actual es: $" + cuenta.getSaldo());
    }

    private void retirarDinero() {
        System.out.print("Ingrese la cantidad a retirar: ");
        double cantidad = scanner.nextDouble();
        if (cuenta.retirar(cantidad)) {
            System.out.println("Retiro exitoso. Su nuevo saldo es: $" + cuenta.getSaldo());
        } else {
            System.out.println("Fondos insuficientes para realizar el retiro.");
        }
    }

    private void depositarDinero() {
        System.out.print("Ingrese la cantidad a depositar: ");
        double cantidad = scanner.nextDouble();
        cuenta.depositar(cantidad);
        System.out.println("Depósito exitoso. Su nuevo saldo es: $" + cuenta.getSaldo());
    }
}