

# 5. Aplicación de principios SOLID y patrones GRASP

La aplicación de los principios SOLID y los patrones GRASP tiene como objetivo establecer una distribución adecuada de responsabilidades entre las clases del Sistema de Gestión Rumbo Sur, favoreciendo la cohesión, el bajo acoplamiento, la extensibilidad y el mantenimiento del software.

Las decisiones propuestas se fundamentan en los requerimientos funcionales RF01–RF26, las reglas de negocio RN01–RN35, los supuestos S01–S17 y los casos de uso previamente identificados.

Dado que el proyecto se encuentra en la etapa de diseño, los problemas descriptos representan riesgos potenciales de implementación y no defectos comprobados en código existente.

Las clases propuestas se contrastan con el diagrama de clases final para mantener la coherencia entre los artefactos del proyecto.

---

## 5.1. Problemas detectados

A partir del análisis de los requerimientos funcionales, las reglas de negocio y los casos de uso definidos para Rumbo Sur, se identificaron posibles problemas de diseño relacionados con la distribución de responsabilidades, la dependencia entre clases y la incorporación de nuevas funcionalidades.

Debido a que el sistema contempla procesos comerciales, administrativos y operativos, resulta necesario evitar que una misma clase concentre operaciones que deberían estar separadas.

Los problemas identificados se presentan como situaciones que podrían surgir durante el diseño y la implementación. Por lo tanto, se proponen decisiones preventivas basadas en SOLID y GRASP.

### P01 – Concentración de responsabilidades en la gestión de reservas

La creación de una reserva involucra verificar cupos, registrar pasajeros, calcular importes, aplicar promociones y controlar pagos.

Si todas estas operaciones se implementaran directamente en una única clase `Reserva`, esta tendría múltiples motivos de cambio, dificultando su mantenimiento.

### P02 – Dependencia de condiciones específicas para calcular descuentos

El sistema contempla tarifas según el tipo de pasajero y promociones por reserva anticipada o por grupo.

Implementar todas las variantes mediante condiciones dentro de un único método dificultaría incorporar nuevas promociones y aumentaría el riesgo de modificar cálculos existentes.

### P03 – Acoplamiento entre la lógica de negocio y la persistencia

El sistema deberá conservar información mediante archivos, según el supuesto S11.

Si las clases encargadas de gestionar reservas también realizaran directamente la lectura y escritura de archivos, cualquier modificación del almacenamiento afectaría la lógica de negocio.

### P04 – Distribución inadecuada de las responsabilidades de cálculo

El sistema necesita calcular importes, saldos y cupos disponibles.

Si estas operaciones se concentraran en un controlador general, este debería acceder continuamente a los datos internos de diferentes objetos, incrementando sus dependencias.

### P05 – Falta de separación entre la interfaz y los procesos del sistema

Los casos de uso de creación y cancelación de reservas requieren varias validaciones y operaciones coordinadas.

Si la interfaz de usuario ejecutara directamente esas reglas, se dificultaría su reutilización y la realización de pruebas independientes.

### P06 – Comportamientos sin un lugar natural en las clases del negocio

El cálculo del total de una reserva necesita datos de la salida, de los pasajeros, de las tarifas y de las promociones. A su vez, la política de devolución depende de los días restantes hasta la salida y de los pagos realizados.

Ninguna clase del dominio (`Reserva`, `Salida` o `Pasajero`) representa por sí sola todo ese conocimiento.

Si estas responsabilidades se asignaran completamente a `Reserva`, la clase tendría que conocer reglas de tarifas, promociones y devoluciones, perdiendo cohesión y aumentando su acoplamiento.

---

## 5.2. Tres principios SOLID

Se seleccionaron tres principios SOLID especialmente relevantes para el diseño de Rumbo Sur:

- **SRP – Principio de Responsabilidad Única.**
- **OCP – Principio Abierto/Cerrado.**
- **DIP – Principio de Inversión de Dependencias.**

### 5.2.1. SRP – Principio de Responsabilidad Única

**Problema identificado:** P01 – Concentración de responsabilidades en la gestión de reservas.

#### Fundamentación

La gestión de reservas comprende actividades que responden a reglas de negocio diferentes.

Por ejemplo, el cálculo del importe depende de las tarifas y promociones, mientras que el registro de pagos depende de las condiciones de cobro y confirmación.

Si una misma clase concentrara todos estos procesos, una modificación de las promociones podría afectar accidentalmente las operaciones de pagos o cancelaciones.

El principio de Responsabilidad Única establece que una clase debe tener un motivo principal de cambio, relacionado con la responsabilidad que cumple dentro del sistema.

#### Decisión de diseño

Se propone distribuir las responsabilidades entre clases especializadas:

- **`Reserva`:** mantiene los datos de la reserva, sus pasajeros, servicios adicionales, estado, importe total, vencimiento de pago y pagos asociados.
- **`CalculadoraPrecioReserva`:** calcula el importe correspondiente al tour, las promociones y los servicios adicionales.
- **`GestorPagos`:** coordina el registro de pagos y aplica las condiciones económicas correspondientes al medio de pago.
- **`GestorCancelaciones`:** aplica la política de cancelación y determina los importes de devolución.

La clase `Reserva` conserva las operaciones propias de su estado, como calcular el saldo pendiente, registrar los pagos asociados, confirmar o anular la reserva y verificar su vencimiento.

Las clases gestoras coordinan procesos especializados que requieren consultar o modificar varios objetos.

#### Clases involucradas

`Reserva`, `CalculadoraPrecioReserva`, `GestorPagos` y `GestorCancelaciones`.

#### Ventaja obtenida

Esta separación mejora la cohesión, facilita las pruebas unitarias y permite modificar las políticas de precios, pagos o cancelaciones sin alterar innecesariamente otras responsabilidades.

**Trazabilidad:** RF08–RF17; RN10–RN28 y RN33; S04–S06.

---

### 5.2.2. OCP – Principio Abierto/Cerrado

**Problema identificado:** P02 – Dependencia de condiciones específicas para calcular descuentos.

#### Fundamentación

Rumbo Sur contempla promociones por reserva anticipada y por grupo.

La primera establece un descuento del 15 % cuando la reserva se realiza con más de 60 días de anticipación; la segunda establece un descuento del 10 % para reservas de seis o más pasajeros.

Según RN17, ambas promociones no son acumulables y debe seleccionarse la más beneficiosa.

Si cada promoción se implementara mediante condiciones dentro de `CalculadoraPrecioReserva`, incorporar una nueva promoción exigiría modificar repetidamente el método principal.

El principio Abierto/Cerrado establece que las clases deben estar abiertas a la extensión, pero cerradas a modificaciones innecesarias de su comportamiento existente.

#### Decisión de diseño

Se propone aplicar polimorfismo mediante una interfaz común:

- **`Promocion`:** interfaz que define la operación `calcularDescuento(reserva, subtotalTour)`.
- **`PromocionAnticipada`:** implementación que evalúa el descuento por anticipación.
- **`PromocionGrupo`:** implementación que evalúa el descuento por cantidad de pasajeros.
- **`CalculadoraPrecioReserva`:** clase que consulta las promociones disponibles mediante la interfaz común y selecciona el mayor descuento aplicable.

Cada promoción encapsula sus propias condiciones.

La incorporación de nuevas promociones podrá realizarse mediante nuevas implementaciones de `Promocion`, sin modificar el algoritmo central de evaluación, siempre que se mantenga el contrato definido.

#### Orden de cálculo propuesto

1. Obtener la tarifa base correspondiente a la temporada.
2. Aplicar la tarifa según el tipo de pasajero.
3. Evaluar las promociones comerciales y seleccionar la más beneficiosa, sin acumular descuentos incompatibles.
4. Incorporar los servicios adicionales, incluyendo el seguro obligatorio cuando corresponda.
5. Aplicar, cuando corresponda, el recargo por financiación con tarjeta únicamente sobre el importe financiado.

La tarifa especial para jubilados se considera en el cálculo según las reglas de negocio definidas para el tipo de pasajero.

Este orden deberá mantenerse consistente con las reglas de negocio y con la implementación definitiva.

#### Clases involucradas

`Promocion`, `PromocionAnticipada`, `PromocionGrupo` y `CalculadoraPrecioReserva`.

#### Ventaja obtenida

El diseño permite incorporar nuevas promociones mediante implementaciones adicionales de la interfaz, sin modificar el algoritmo central de cálculo.

También facilita probar cada promoción de manera independiente.

**Trazabilidad:** RF09–RF11; RN12–RN19 y RN22; S03, S05, S06 y S16.

---

### 5.2.3. DIP – Principio de Inversión de Dependencias

**Problema identificado:** P03 – Acoplamiento entre la lógica de negocio y la persistencia.

#### Fundamentación

El supuesto S11 establece que la información del sistema se conservará mediante archivos.

Sin embargo, si las operaciones de reserva dependieran directamente de una clase encargada de escribir archivos, la lógica de negocio quedaría vinculada a un mecanismo particular de almacenamiento.

Esto dificultaría modificar la persistencia y realizar pruebas independientes.

El principio de Inversión de Dependencias establece que los componentes de alto nivel deben depender de abstracciones, en lugar de depender directamente de implementaciones concretas.

#### Decisión de diseño

Se propone establecer una abstracción para el acceso a los datos:

- **`RepositorioReservas`:** interfaz que define operaciones como `guardar(reserva)` y `buscarPorId(numero)`.
- **`RepositorioReservasArchivo`:** clase que implementa la interfaz y realiza las operaciones de lectura y escritura de archivos.
- **`ControladorReservas`:** clase que utiliza la abstracción `RepositorioReservas`, sin depender directamente de los detalles del almacenamiento.

La implementación concreta del repositorio se proporcionará al construir o configurar el controlador.

De esta forma, los componentes de coordinación dependen de una interfaz y no de una tecnología específica.

#### Clases involucradas

`ControladorReservas`, `RepositorioReservas` y `RepositorioReservasArchivo`.

El mismo criterio podrá extenderse a las demás entidades que se guardan en archivos, como clientes, tours y salidas, conforme al supuesto S11.

#### Ventaja obtenida

Esta decisión permite modificar el mecanismo de persistencia sin alterar la coordinación de reservas.

También facilita reemplazar el repositorio por una implementación de prueba y verificar las operaciones sin utilizar archivos reales.

**Trazabilidad:** RF08, RF12–RF17; S02 y S11.

---

## 5.3. Cuatro patrones GRASP

Se seleccionaron cuatro patrones GRASP para orientar la asignación de responsabilidades entre las clases del sistema:

- **Experto en Información (Information Expert).**
- **Creador (Creator).**
- **Controlador (Controller).**
- **Fabricación Pura (Pure Fabrication).**

### 5.3.1. Experto en Información (Information Expert)

**Problema identificado:** P04 – Distribución inadecuada de las responsabilidades de cálculo.

#### Fundamentación

Rumbo Sur necesita calcular importes, saldos pendientes y cupos disponibles.

Si todas estas operaciones se concentraran en un controlador general, este debería consultar permanentemente información interna de diferentes objetos, incrementando sus dependencias.

El patrón Experto en Información establece que una responsabilidad debe asignarse, preferentemente, a la clase que dispone de los datos necesarios para cumplirla.

#### Decisión de diseño

Se propone distribuir las responsabilidades de la siguiente manera:

- **`Salida`:** conoce su capacidad y la ocupación correspondiente, por lo que puede determinar los cupos disponibles.
- **`Reserva`:** conoce el importe total y los pagos asociados, por lo que puede calcular el saldo pendiente.
- **`CalculadoraPrecioReserva`:** reúne las reglas necesarias para determinar el precio a partir de tarifas, promociones y servicios adicionales.
- **`ControladorReservas`:** coordina las operaciones, pero delega los cálculos en las clases responsables.

Por ejemplo, cuando el vendedor intenta crear una reserva, `ControladorReservas` solicita a `Salida` que verifique la disponibilidad, en lugar de calcular directamente los cupos.

Para determinar los cupos disponibles, `Salida` debe considerar tanto las reservas confirmadas como las reservas pendientes que mantienen una retención vigente, según RN07, S04 y S14.

Del mismo modo, para consultar el saldo de una reserva, se utiliza el comportamiento correspondiente de `Reserva`.

#### Clases involucradas

`Salida`, `Reserva`, `CalculadoraPrecioReserva` y `ControladorReservas`.

#### Ventaja obtenida

Esta distribución favorece la cohesión, evita la concentración de lógica en el controlador y facilita las pruebas unitarias.

Cada clase administra las operaciones relacionadas con la información que conoce.

**Trazabilidad:** RF04, RF05, RF08, RF09 y RF13; RN07; S04 y S14.

---

### 5.3.2. Creador (Creator)

**Problema identificado:** creación y asociación de pasajeros sin un responsable claramente definido.

#### Fundamentación

Cada reserva contiene uno o más pasajeros y está asociada a una salida.

La creación e incorporación de pasajeros requiere mantener relaciones consistentes y respetar las restricciones del negocio.

Si el menú o el controlador crearan los pasajeros y modificaran directamente la colección de la reserva, podrían generarse asociaciones inconsistentes.

El patrón Creador establece criterios para asignar la responsabilidad de crear objetos a una clase que los contiene, los agrega o administra su ciclo de vida.

#### Decisión de diseño

Se propone que la clase `Reserva` sea responsable de crear e incorporar las instancias de `Pasajero` que componen su registro, mediante la operación:

`agregarPasajero(datos: DatosPasajero)`

El `ControladorReservas` coordinará la creación de la reserva y verificará previamente la disponibilidad y las restricciones del tour.

Una vez superadas esas validaciones, `Reserva` administrará la creación y asociación de sus pasajeros.

Esta decisión se fundamenta en que `Reserva` agrupa y administra a los pasajeros vinculados con una operación comercial.

En el diagrama de clases final, esta relación se representa mediante composición:

`Reserva "1" *-- "1..*" Pasajero`

#### Clases involucradas

`Reserva`, `Pasajero` y `ControladorReservas`.

#### Ventaja obtenida

Se establece un criterio claro de creación y asociación de objetos, evitando que diferentes componentes modifiquen directamente la colección de pasajeros.

Esto favorece la consistencia de las reservas y permite centralizar las validaciones propias de su composición.

**Trazabilidad:** RF07 y RF08; RN10, RN11 y RN19; S03 y S04.

---

### 5.3.3. Controlador (Controller)

**Problema identificado:** P05 – Falta de separación entre la interfaz y los procesos del sistema.

#### Fundamentación

El caso de uso **CU08 – Crear reserva** requiere coordinar el registro de pasajeros, la validación de disponibilidad, el cálculo del importe, la incorporación de servicios adicionales y las condiciones de pago.

Asimismo, **CU15 – Cancelar reserva** requiere verificar condiciones, calcular devoluciones, actualizar el estado y liberar los cupos correspondientes.

Estas operaciones no deberían ejecutarse directamente desde el menú o la interfaz de usuario.

El patrón Controlador establece que los eventos del sistema deben recibirse y coordinarse mediante una clase responsable de delegar las operaciones a los objetos adecuados.

#### Decisión de diseño

Se propone utilizar `ControladorReservas` como responsable de recibir las solicitudes relacionadas con la creación y cancelación de reservas.

Este controlador delegará las operaciones en las clases especializadas:

- **`Salida`:** consulta de disponibilidad y condiciones de participación.
- **`Reserva`:** administración de pasajeros, servicios, estado, vencimiento y pagos asociados.
- **`CalculadoraPrecioReserva`:** determinación de importes.
- **`GestorPagos`:** registro de pagos y aplicación de condiciones de cobro.
- **`GestorCancelaciones`:** aplicación de la política de devolución.
- **`RepositorioReservas`:** recuperación y almacenamiento de registros.

El controlador coordinará el flujo de trabajo sin concentrar los cálculos específicos de cada regla de negocio.

Por ejemplo, ante una solicitud de creación de reserva, verificará la disponibilidad, coordinará las validaciones, solicitará el cálculo del precio y determinará si corresponde una reserva pendiente o el pago total inmediato.

Para las reservas pendientes, también coordinará la verificación del vencimiento, según S04 y S14.

Ante una solicitud de cancelación, recuperará la reserva, verificará si puede cancelarse, solicitará el cálculo de devolución a `GestorCancelaciones` y coordinará la actualización de su estado.

#### Clases involucradas

`ControladorReservas`, `Reserva`, `Salida`, `CalculadoraPrecioReserva`, `GestorPagos`, `GestorCancelaciones` y `RepositorioReservas`.

#### Ventaja obtenida

La separación permite mantener la interfaz independiente de la lógica de negocio, reutilizar las operaciones desde diferentes puntos del sistema y facilitar la construcción de los diagramas de secuencia.

**Trazabilidad:** CU08 y CU15; RF08–RF17; RN07, RN10–RN28; S04, S06 y S14.

---

### 5.3.4. Fabricación Pura (Pure Fabrication)

**Problema identificado:** P06 – Comportamientos sin un lugar natural en las clases del negocio.

#### Fundamentación

El cálculo del precio y la política de devolución no son responsabilidades que puedan asignarse completamente a una reserva, un tour o un pasajero por sí mismos.

Son procesos que combinan información de varias clases y aplican políticas específicas.

Ubicarlos íntegramente en `Reserva` la obligaría a conocer tarifas, promociones, reglas de cobro y políticas de cancelación, reduciendo su cohesión.

El patrón Fabricación Pura propone crear clases que no representan directamente una entidad del negocio, sino que se incorporan al diseño para agrupar comportamientos especializados con alta cohesión y bajo acoplamiento.

#### Decisión de diseño

Se crean las siguientes clases especializadas:

- **`CalculadoraPrecioReserva`:** calcula el total a partir de la tarifa de la salida, el factor correspondiente a cada pasajero, las promociones y los servicios adicionales.
- **`GestorPagos`:** coordina el registro de pagos y aplica el recargo correspondiente cuando se utiliza financiación con tarjeta.
- **`GestorCancelaciones`:** aplica la política de cancelación, calcula el porcentaje de devolución y determina el importe a reintegrar.

La clase `Reserva` conserva sus datos y las operaciones sobre su propio estado, como `totalAbonado()`, `calcularSaldo()`, `confirmar()` y `anular()`.

La ejecución efectiva de un reintegro no se considera completada únicamente por calcular el importe de devolución. Ese proceso deberá registrarse según el mecanismo de pagos definido en la implementación.

Esta decisión se diferencia del principio SRP:

- **SRP:** justifica separar responsabilidades según sus motivos de cambio.
- **Fabricación Pura:** justifica crear clases especializadas que no representan directamente una entidad del dominio, pero permiten ubicar comportamientos que no pertenecen naturalmente a una única clase existente.

#### Clases involucradas

`CalculadoraPrecioReserva`, `GestorPagos`, `GestorCancelaciones` y `Reserva`.

#### Ventaja obtenida

La clase `Reserva` mantiene una mayor cohesión, las políticas de cálculo pueden probarse de forma aislada y las reglas de precio, cobro y devolución pueden cambiar sin modificar innecesariamente otras partes del modelo.

**Trazabilidad:** RF09, RF10, RF12 y RF15; RN15, RN16 y RN23–RN28; S15 y S16.

---

## 5.4. Relación entre problemas, SOLID y GRASP

La siguiente tabla resume cómo las decisiones de diseño propuestas permiten abordar los problemas identificados.

| **Problema** | **Descripción** | **Principio o patrón aplicado** | **Solución propuesta** |
|---|---|---|---|
| P01 | Concentración de responsabilidades en reservas | SOLID: SRP | Separar las responsabilidades de reserva, cálculo de precios, pagos y cancelaciones. |
| P02 | Dificultad para incorporar promociones | SOLID: OCP | Definir una interfaz `Promocion` y distintas implementaciones. |
| P03 | Acoplamiento entre negocio y persistencia | SOLID: DIP | Utilizar `RepositorioReservas` como abstracción de almacenamiento. |
| P04 | Distribución inadecuada de cálculos | GRASP: Experto en Información | Asignar el cálculo de cupos a `Salida` y el de saldos a `Reserva`. |
| P05 | Interfaz acoplada a procesos de negocio | GRASP: Controlador | Incorporar `ControladorReservas` para coordinar los casos de uso. |
| P06 | Cálculos y políticas sin lugar natural en el dominio | GRASP: Fabricación Pura | Crear `CalculadoraPrecioReserva`, `GestorPagos` y `GestorCancelaciones`. |

El patrón **GRASP Creador** complementa estas decisiones al establecer que `Reserva` administra la creación e incorporación de sus pasajeros, manteniendo consistente la relación entre ambos objetos.

---

## 5.5. Conclusión

La aplicación de los principios SOLID y los patrones GRASP permite establecer criterios concretos para distribuir las responsabilidades del Sistema de Gestión Rumbo Sur.

Los principios **SRP, OCP y DIP** contribuyen a separar funcionalidades, facilitar la incorporación de promociones y evitar dependencias directas con los mecanismos de persistencia.

Por su parte, los patrones **Experto en Información, Creador, Controlador y Fabricación Pura** permiten asignar responsabilidades a las clases adecuadas, coordinar los procesos del sistema y ubicar los cálculos en clases especializadas.

Las decisiones propuestas se relacionan con los requerimientos, las reglas de negocio, los supuestos y los casos de uso definidos para la agencia.

El diseño contempla especialmente la gestión de reservas pendientes, la disponibilidad de cupos, los servicios adicionales, las promociones, los pagos y las cancelaciones.

Su aplicación definitiva deberá reflejarse de manera consistente en el diagrama de clases final, los diagramas de secuencia y la posterior implementación en Java.

De esta forma, el proyecto establece una base de diseño orientado a objetos que favorece la cohesión, la extensibilidad, la reutilización y el mantenimiento del sistema.

