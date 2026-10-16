
# Requerimientos Funcionales – Sistema de Gestión Rumbo Sur

## Introducción

Los requerimientos funcionales describen las operaciones que deberá realizar el sistema para satisfacer las necesidades comerciales, administrativas y operativas de la agencia de turismo Rumbo Sur.

## 1. Gestión de tours

### RF01 – Administrar tours
El sistema deberá permitir registrar, consultar y modificar tours, incluyendo código, nombre, descripción, duración, categoría y cupos mínimo y máximo.

### RF02 – Gestionar itinerarios
El sistema deberá permitir registrar las actividades de cada tour, indicando día, orden, horario, descripción y lugar, así como su vinculación con servicios de proveedores externos cuando corresponda.

### RF03 – Gestionar tarifas
El sistema deberá permitir establecer y consultar las tarifas base de los tours según temporada baja, media o alta, incluyendo sus períodos de vigencia.

### RF04 – Programar salidas
El sistema deberá permitir crear salidas asociadas a un tour, indicando fechas de inicio y finalización, cupos, estado, guía y vehículo asignados.

### RF05 – Gestionar estados de salidas
El sistema deberá controlar los estados de las salidas y actualizar su disponibilidad según la cantidad de pasajeros registrados y las condiciones establecidas.

## 2. Gestión de clientes y reservas

### RF06 – Registrar clientes
El sistema deberá permitir registrar y consultar clientes, almacenando nombre, apellido, documento, teléfono, correo electrónico y país de residencia.

### RF07 – Registrar pasajeros
El sistema deberá permitir incorporar pasajeros a una reserva, registrando sus datos personales, fecha de nacimiento, tipo de pasajero, contacto de emergencia y restricciones relevantes.

### RF08 – Crear reservas
El sistema deberá permitir generar reservas asociadas a un cliente, una salida y un vendedor, incluyendo uno o más pasajeros y verificando la disponibilidad de cupos.

### RF09 – Calcular precios
El sistema deberá calcular el precio total de una reserva según la tarifa de temporada, el tipo de pasajero, las promociones aplicables y los servicios adicionales.

### RF10 – Aplicar promociones
El sistema deberá evaluar las promociones por reserva anticipada y por grupo, seleccionando la más beneficiosa cuando ambas resulten aplicables.

### RF11 – Gestionar servicios adicionales
El sistema deberá permitir contratar excursiones opcionales, traslados y seguros para cada pasajero, incluyendo los servicios obligatorios según el tipo de tour.

## 3. Gestión de pagos y cancelaciones

### RF12 – Registrar pagos
El sistema deberá permitir registrar señas y pagos posteriores, identificando fecha, importe y medio de pago.

### RF13 – Controlar saldos
El sistema deberá calcular el saldo pendiente de cada reserva y verificar el cumplimiento del plazo establecido para completar el pago.

### RF14 – Confirmar o anular reservas
El sistema deberá confirmar reservas al cumplirse la condición de la seña y anular aquellas que no completen el saldo dentro del plazo correspondiente, liberando sus cupos.

### RF15 – Cancelar reservas
El sistema deberá permitir registrar cancelaciones, sus motivos y fechas, y calcular las devoluciones según la política establecida.

### RF16 – Modificar reservas
El sistema deberá permitir gestionar modificaciones y reemplazos de pasajeros, verificando los plazos y requisitos del tour.

### RF17 – Registrar ajustes
El sistema deberá permitir corregir movimientos económicos de reservas pagadas mediante notas de ajuste, sin eliminar los registros originales.

## 4. Gestión operativa

### RF18 – Administrar guías y vehículos
El sistema deberá permitir registrar y consultar guías, sus habilitaciones, idiomas y honorarios, así como vehículos, sus tipos, patentes y capacidades.

### RF19 – Asignar recursos
El sistema deberá permitir asignar guías y vehículos a las salidas, verificando que cumplan las condiciones de disponibilidad, habilitación y capacidad.

### RF20 – Emitir listas de pasajeros
El sistema deberá generar la lista de pasajeros de cada salida, incluyendo documentos, contactos de emergencia e información relevante para el guía.

### RF21 – Gestionar salidas
El sistema deberá permitir iniciar y finalizar salidas, verificando las condiciones operativas obligatorias.

### RF22 – Liquidar honorarios
El sistema deberá calcular los honorarios de los guías correspondientes a salidas finalizadas, considerando días trabajados y ventas de servicios adicionales.

### RF23 – Administrar proveedores
El sistema deberá permitir registrar y consultar proveedores, incluyendo nombre, CUIT, contacto y tipo de servicio.

## 5. Reportes administrativos

### RF24 – Generar reportes
El sistema deberá permitir emitir reportes de ocupación por salida, ventas por vendedor, ingresos por tour y cancelaciones.

## 6. Funcionalidades complementarias propuestas

### RF25 – Gestionar lista de espera para salidas completas
El sistema deberá permitir registrar clientes interesados en salidas que hayan alcanzado su cupo máximo, incorporándolos a una lista de espera ordenada por fecha de solicitud. Cuando se liberen lugares debido a una cancelación, el sistema deberá identificar a los clientes en espera y permitir al vendedor ofrecer los cupos disponibles, respetando el orden de inscripción y la cantidad de pasajeros solicitada.

### RF26 – Recomendar tours según las preferencias del cliente
El sistema deberá permitir registrar las preferencias turísticas de los clientes, considerando categorías de interés, presupuesto disponible y fechas deseadas. A partir de esta información, deberá generar recomendaciones de tours que coincidan con dichas preferencias, mostrando las salidas disponibles y sus tarifas correspondientes.
