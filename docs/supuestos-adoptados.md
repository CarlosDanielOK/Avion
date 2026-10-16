
# Supuestos Adoptados – Sistema de Gestión Rumbo Sur

## 1. Introducción

A partir del análisis del enunciado de la agencia Rumbo Sur, se identificaron aspectos que requieren definiciones adicionales para garantizar un funcionamiento coherente del sistema.

Los siguientes supuestos constituyen decisiones preliminares del grupo y deberán validarse antes de la implementación.

## 2. Supuestos adoptados

### S01 – Acceso al sistema

Se considera que el administrador, los vendedores y el coordinador de operaciones serán usuarios directos del sistema. Los guías podrán consultar la información correspondiente a sus salidas asignadas.

### S02 – Identificación de registros

Cada cliente, tour, salida, reserva y pago tendrá un identificador único para facilitar su administración y evitar duplicaciones.

### S03 – Cálculo de edades

La edad de cada pasajero se determinará según la fecha de inicio de la salida, tanto para calcular tarifas como para verificar restricciones de participación.

### S04 – Reservas pendientes

Las reservas pendientes de seña conservarán temporalmente los cupos solicitados durante un plazo máximo de 48 horas desde su creación, siempre que dicho plazo no supere la fecha límite de pago correspondiente.

Si la reserva se realiza dentro de los diez días anteriores a la salida, se deberá abonar el importe total al momento de registrarla, debido a que el plazo habitual para cancelar el saldo ya habrá vencido.

Cuando una reserva pendiente no cumpla con las condiciones de pago dentro del plazo establecido, será anulada y los cupos retenidos volverán a estar disponibles.

### S05 – Aplicación de descuentos

Los descuentos por tipo de pasajero se calcularán antes de evaluar las promociones comerciales. Entre las promociones por anticipación y por grupo se aplicará únicamente la más beneficiosa.

### S06 – Servicios adicionales

Las promociones comerciales se aplicarán sobre el valor del tour, mientras que los servicios adicionales se incorporarán posteriormente al importe de la reserva.

### S07 – Lista de espera

La lista de espera estará asociada a una salida específica. Los clientes podrán solicitar una cantidad determinada de lugares, pero su inscripción no implicará una reserva ni ocupará cupos.

### S08 – Asignación de lugares liberados

Cuando se produzca una cancelación, el vendedor podrá consultar las solicitudes de la lista de espera y ofrecer los lugares disponibles respetando su orden de inscripción.

Se propone que una solicitud conserve su prioridad hasta que sea atendida o retirada.

### S09 – Recomendación de tours

Las recomendaciones se generarán mediante filtros sobre la información registrada, sin utilizar inteligencia artificial ni servicios externos. Se considerarán categoría, presupuesto, fechas y requisitos de participación.

### S10 – Preferencias del cliente

Las preferencias turísticas podrán registrarse y modificarse durante la atención comercial. Su utilización será opcional y no condicionará la posibilidad de realizar una reserva.

### S11 – Persistencia de información

Los datos de clientes, tours, salidas, reservas, pagos, listas de espera y preferencias se almacenarán mediante archivos para conservarlos entre ejecuciones del sistema.

### S12 – Interfaz de usuario

Se propone desarrollar una interfaz por consola con menú de opciones, organizada según las funcionalidades y responsabilidades de los usuarios del sistema.

### S13 – Oferta y aceptación de cupos liberados

Se propone que las ofertas de lugares disponibles se gestionen mediante el vendedor, respetando el orden y las condiciones establecidas en RN34.

Cada oferta tendrá una vigencia máxima de 24 horas desde su comunicación al cliente. Durante ese período, el cliente podrá aceptarla o rechazarla.

Si la oferta es rechazada, la solicitud se retirará de la lista de espera.

Si vence el plazo sin respuesta, la oferta quedará sin efecto y la solicitud se marcará como vencida, permitiendo continuar con la siguiente solicitud compatible.

La aceptación de la oferta no confirmará automáticamente la reserva. El vendedor deberá registrar la reserva correspondiente, verificar nuevamente la disponibilidad y aplicar las condiciones habituales de pago y confirmación.

El plazo de 24 horas constituye un supuesto propuesto por el grupo y deberá validarse antes de su implementación.

### S14 – Vencimiento de reservas pendientes

El sistema verificará el vencimiento de las reservas pendientes al iniciar operaciones relacionadas con reservas y al consultar la disponibilidad de cupos.

Si transcurrieron 48 horas desde su creación sin alcanzar la seña requerida, o si se alcanzó previamente el límite de pago aplicable, la reserva será anulada automáticamente al ejecutarse dicha verificación.

La anulación liberará los cupos retenidos y dejará registrado el cambio de estado, permitiendo mantener actualizada la disponibilidad de cada salida.

### S15 – Interpretación de fechas de cancelación

Los días de anticipación para cancelaciones se calcularán como días calendario completos entre la fecha de cancelación y la fecha de inicio de la salida.

Este criterio será utilizado de manera uniforme para RN23 a RN28 y deberá validarse con el grupo.

### S16 – Reglas de precio y recargo

El descuento de jubilados se considera una tarifa especial por tipo de pasajero; las promociones comerciales RN15 y RN16 no se acumulan entre sí.

Conforme a S05 y S06, primero se determina la tarifa por pasajero, luego se aplica la promoción comercial elegida sobre el tour y finalmente se suman los adicionales.

El recargo del 7 % se aplicará únicamente al importe financiado con tarjeta en cuotas.

Este orden de cálculo es una interpretación propuesta, pendiente de validación.

### S17 – Alcance y datos de las recomendaciones

Las recomendaciones (RF26) usarán filtros determinísticos sobre tours y salidas existentes; no garantizarán disponibilidad futura ni reservarán lugares.

Antes de confirmar una reserva se validarán nuevamente los cupos, la edad mínima y las condiciones del tour.
