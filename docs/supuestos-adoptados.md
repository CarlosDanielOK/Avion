
# Supuestos Adoptados – Sistema de Gestión Rumbo Sur

## 1. Introducción

A partir del análisis del enunciado de la agencia Rumbo Sur, se identificaron aspectos que requieren definiciones adicionales para garantizar un funcionamiento coherente del sistema.

Los siguientes supuestos constituyen decisiones preliminares del grupo y deberán validarse antes de la implementación.

## 2. Supuestos adoptados

### S01 – Acceso al sistema

Se considera que el administrador, los vendedores y el coordinador de operaciones serán usuarios directos del sistema. Los guías son personal de la agencia pero no usan el sistema: sus datos los carga el coordinador.

### S02 – Identificación de registros

Cada cliente, tour, salida, reserva y pago tendrá un identificador único para facilitar su administración y evitar duplicaciones.

### S03 – Cálculo de edades

La edad de cada pasajero se determinará según la fecha de inicio de la salida, tanto para calcular tarifas como para verificar restricciones de participación.

### S04 – Reservas pendientes

Las reservas pendientes de seña conservarán temporalmente los cupos solicitados hasta su fecha de vencimiento, que es la menor entre 2 días calendario desde su creación y 10 días antes del inicio de la salida (fecha límite de pago del saldo, RN21). La reserva se considera vencida cuando la fecha actual supera su fecha de vencimiento.

Ejemplo: una reserva creada el 2 de marzo para una salida el 10 de abril vence el 4 de marzo (el menor entre el 4 de marzo y el 31 de marzo). El 5 de marzo ya está vencida.

Si la reserva se realiza dentro de los diez días anteriores a la salida, se deberá abonar el importe total al momento de registrarla, debido a que el plazo habitual para cancelar el saldo ya habrá vencido.

Cuando una reserva pendiente no cumpla con las condiciones de pago dentro del plazo establecido, será anulada y los cupos retenidos volverán a estar disponibles.

### S05 – Aplicación de descuentos

Los descuentos por tipo de pasajero se calcularán antes de evaluar las promociones comerciales. Entre las promociones por anticipación y por grupo se aplicará únicamente la más beneficiosa.

### S06 – Servicios adicionales

Las promociones comerciales se aplicarán sobre el valor del tour, mientras que los servicios adicionales se incorporarán posteriormente al importe de la reserva.

### S07 – Persistencia de información

Toda la información del sistema (tours, salidas, clientes, reservas, pagos, guías, vehículos y proveedores) se guardará en un archivo mediante serialización de objetos Java, para conservarla entre ejecuciones. La lógica de negocio accede al almacenamiento solo a través de la interfaz `RepositorioAgencia`.

### S08 – Interfaz de usuario

Se propone desarrollar una interfaz por consola con menú de opciones, organizada según las funcionalidades y responsabilidades de los usuarios del sistema.

### S09 – Vencimiento de reservas pendientes

El sistema verificará el vencimiento de las reservas pendientes al iniciar operaciones relacionadas con reservas y al consultar la disponibilidad de cupos.

Si la fecha actual supera la fecha de vencimiento (S04) y no se alcanzó la seña requerida, la reserva será anulada automáticamente al ejecutarse dicha verificación.

La anulación liberará los cupos retenidos y dejará registrado el cambio de estado, permitiendo mantener actualizada la disponibilidad de cada salida.

### S10 – Interpretación de fechas de cancelación

Los días de anticipación para cancelaciones se calcularán como días calendario completos entre la fecha de cancelación y la fecha de inicio de la salida.

Este criterio será utilizado de manera uniforme para RN23 a RN28 y deberá validarse con el grupo.

Los tramos se interpretan de la siguiente manera: con 30 días o más de anticipación se devuelve el 90 %; de 29 a 15 días, el 50 %; de 14 a 7 días, el 20 %; con menos de 7 días no hay devolución. Si la cancelación la provoca la agencia, la devolución es del 100 % sin importar los días.

### S11 – Reglas de precio y recargo

El descuento de jubilados se considera una tarifa especial por tipo de pasajero; las promociones comerciales RN15 y RN16 no se acumulan entre sí.

Conforme a S05 y S06, primero se determina la tarifa por pasajero, luego se aplica la promoción comercial elegida sobre el tour y finalmente se suman los adicionales.

El recargo del 7 % se aplicará únicamente al importe de capital que se registre en un pago con tarjeta en cuotas. El total de la reserva representa el precio de los servicios, sin recargo; cada `Pago` conservará por separado el importe de capital y el recargo aplicado. El saldo de la reserva se calculará sobre el capital pendiente, mientras que el importe efectivamente cobrado será capital más recargo.

Este orden de cálculo es una interpretación adoptada por el grupo. La devolución se calcula sobre el capital abonado, sin recargos, de acuerdo con S17.

### S12 – Estados de la reserva

Los estados de la reserva son los del enunciado. Pendiente: creada, sin la seña. Confirmada: al registrarse la seña del 30 %. Anulada: por falta de pago en plazo (S09), por cancelación solicitada por el cliente o por la agencia, o por cancelación de la salida. Finalizada: cuando finaliza la salida.

No existe un estado «cancelada» para la reserva: toda cancelación deja la reserva en estado anulada y registra una cancelación con fecha, motivo, porcentaje e importe devuelto.

### S13 – Medio de devolución

La devolución se acredita por el mismo medio de pago utilizado en la reserva. Si se abonó con más de un medio, se utiliza el del último pago registrado.

### S14 – Comisión del vendedor

Cada vendedor tiene una comisión, registrada como dato de su ficha. El sistema no liquida comisiones: el reporte de ventas por vendedor informa los importes vendidos.

### S15 – Reservas que pueden cancelarse

Una reserva puede cancelarse si está en estado pendiente o confirmada y su salida no está en curso ni finalizada. Una reserva anulada o finalizada no puede cancelarse.

### S16 – Tipos de pasajero y edades

El tipo «menor» corresponde a pasajeros menores de 12 años (RN13). Los pasajeros de 12 años o más se registran como adulto o jubilado. Un pasajero menor de 3 años no abona tarifa, pero ocupa un lugar y debe registrarse. El sistema valida que el tipo informado sea coherente con la fecha de nacimiento.

### S17 – Base de cálculo de la devolución

La devolución se calcula sobre los importes abonados sin incluir el recargo por financiación con tarjeta. El porcentaje se aplica sobre ese total (RN23 a RN27).

### S18 – Cupos de la salida y del tour

El cupo máximo de una salida no puede superar el cupo máximo de su tour. El cupo mínimo para confirmarla es el del tour (RN05).

### S19 – Cancelación de una salida por cupo mínimo

Cuando el sistema detecta que faltan 7 días o menos para la salida sin alcanzar el cupo mínimo (RN06), cambia la salida a cancelada y cancela todas sus reservas vigentes como cancelación de la agencia, con devolución del 100 % (RN27).

### S20 – Temporada de una salida

La temporada y la tarifa base de una salida se determinan con la tarifa del tour que esté vigente en la fecha de inicio de la salida (RN12).

### S21 – Servicios adicionales de un tour

Cada servicio adicional pertenece a un tour y se marca como opcional u obligatorio para ese tour. Los servicios se contratan por pasajero. Los servicios obligatorios se incorporan automáticamente a todos los pasajeros al crear la reserva; en los tours de aventura, el seguro de viaje es un servicio obligatorio (RN19).
