Descripción del trabajo

En este trabajo se desarrolló un simulador de cajero automático en Java, aplicando Programación Orientada a Objetos, encapsulamiento, validaciones de negocio, manejo de excepciones personalizadas y registro de transacciones.

El sistema permite operar con múltiples cuentas bancarias y ejecutar las siguientes funcionalidades:

-depósito de dinero
-extracción de dinero
-transferencia entre cuentas del sistema
-consulta de saldo
-visualización del historial de movimientos
-También se implementaron validaciones para controlar errores y estados especiales de las cuentas, contemplando casos como saldo insuficiente, límite de extracción excedido, cuenta inactiva y cuenta bloqueada.

Estructura del proyecto

El proyecto fue organizado en los paquetes solicitados por la consigna:

-model
-exception
-service
-ui
-util
-Además: CuentaBancaria posee número de cuenta inmutable
se utilizaron enumeraciones para estados y tipos de transacción
cada operación registra fecha, tipo, monto y saldo resultante
el menú interactivo fue implementado por consola con switch
Simulación realizada

La clase principal ejecuta una simulación inicial de un día de operaciones con 4 cuentas bancarias y al menos 15 transacciones variadas, incluyendo:

-depósitos
-extracciones
-transferencias
-consultas de saldo

Luego de esa simulación, el sistema habilita el uso interactivo del cajero por consola para probar operaciones manualmente.

Excepciones implementadas

Se desarrollaron excepciones personalizadas para controlar situaciones de error:

-SaldoInsuficienteException
-LimiteExtraccionExcedidoException
-CuentaInactivaException
-PinInvalidoException
-CuentaBloqueadaException
-Diagrama de estados de la cuenta

La consigna solicita el siguiente flujo básico de estados:

ACTIVA -> INACTIVA -> CERRADA

Puede presentarse gráficamente de esta forma:

ACTIVA: la cuenta puede operar normalmente
INACTIVA: la cuenta fue desactivada y no permite operaciones
CERRADA: estado final de cierre definitivo
Aclaración sobre la implementación

Además de los estados pedidos en la consigna, en la implementación se contempló el estado BLOQUEADA como una validación adicional. En este caso, la cuenta puede consultar saldo, pero no puede mover fondos mediante depósitos, extracciones o transferencias.

Capturas incluidas

En las capturas de ejecución se muestran:

-secuencia de operaciones válidas
-manejo de al menos 2 excepciones diferentes
-historial de transacciones formateado
-funcionamiento general del menú interactivo

Aclaración sobre funcionalidades opcionales:

La consigna menciona PinInvalidoException como una validación de acceso opcional.
En esta implementación se contempló esa excepción dentro de la estructura del 
proyecto como parte de una posible ampliación del sistema, aunque no se desarrolló
una autenticación completa mediante PIN, ya que no era un requisito obligatorio 
para el funcionamiento principal del cajero automático.

Conclusión:

El sistema desarrollado cumple con los requisitos principales de la consigna, compila correctamente y ejecuta las operaciones esperadas, aplicando principios de orientación a objetos, control de errores y registro de auditoría de transacciones.

