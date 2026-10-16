
# Análisis del Enunciado – Agencia de Turismo Rumbo Sur

## 1. Introducción

Rumbo Sur es una agencia de turismo receptivo dedicada a organizar tours y excursiones de aventura, culturales y de naturaleza.

Actualmente, la agencia administra las reservas, los cupos y los pagos mediante planillas, lo que dificulta la organización de la información y el seguimiento de las operaciones.

Se propone desarrollar un sistema de gestión en Java, aplicando los conceptos de Programación Orientada a Objetos.

## 2. Problemática identificada

La gestión mediante planillas presenta dificultades para centralizar la información y controlar los distintos procesos de la agencia.

Entre las principales necesidades se encuentran:

- Organizar el catálogo de tours y excursiones.
- Controlar los cupos disponibles de cada salida.
- Registrar correctamente clientes y pasajeros.
- Calcular tarifas, promociones y descuentos.
- Llevar un seguimiento de los pagos y saldos.
- Administrar cancelaciones y devoluciones.
- Coordinar la asignación de guías y vehículos.
- Generar reportes para la administración.

  
### Funcionalidades adicionales propuestas

**RF25 – Lista de espera**

El sistema permitirá registrar interesados en salidas que hayan alcanzado su capacidad máxima, manteniendo el orden de inscripción para gestionar la disponibilidad de lugares.

**RF26 – Recomendación de tours**

El sistema permitirá recomendar tours según las preferencias, el presupuesto y las fechas disponibles del cliente, mediante criterios predefinidos, sin utilizar inteligencia artificial.


## 3. Objetivo general

Desarrollar un sistema de gestión que permita administrar las operaciones de Rumbo Sur, centralizando la información y automatizando los procesos principales de la agencia.


## 4. Actores del sistema

Se identifican los actores que interactúan directamente con el sistema y los participantes externos involucrados en los procesos de la agencia.

### Administrador
Gestiona la información general del sistema, administra los parámetros de operación y consulta los reportes.

### Vendedor
Registra clientes, gestiona reservas, consulta disponibilidad y administra los pagos correspondientes.

### Coordinador de operaciones
Organiza las salidas programadas, asigna guías y vehículos, y consulta las listas de pasajeros.

### Guía turístico
Dispone de acceso de consulta a la información necesaria para las salidas asignadas, según el supuesto S01.

### Participantes externos
El cliente y el proveedor intervienen en los procesos comerciales de la agencia, pero no se consideran actores directos del software.


## 5. Entidades principales

Las principales entidades identificadas para el modelado son:

- Tour
- Actividad del itinerario
- Salida
- Cliente
- Pasajero
- Reserva
- Tarifa
- Promoción
- Pago
- Servicio adicional
- Guía
- Vendedor
- Coordinador
- Administrador
- Vehículo
- Proveedor
- Cancelación

Estas entidades constituyen una identificación inicial y deberán revisarse durante la elaboración del diagrama de clases.

## 6. Procesos principales

### Gestión de tours

Permite administrar los tours, sus categorías, itinerarios, duración y tarifas.

### Gestión de salidas

Permite programar salidas, establecer cupos y asignar guías y vehículos.

### Gestión de reservas

Permite registrar clientes, pasajeros y reservas, verificando la disponibilidad de lugares.

### Gestión de pagos

Permite registrar señas, pagos posteriores y saldos pendientes.

### Gestión de cancelaciones

Permite cancelar o modificar reservas aplicando las políticas establecidas.

### Gestión de reportes

Permite consultar información sobre ocupación, ventas, ingresos y cancelaciones.

## 7. Alcance inicial del sistema

El sistema deberá contemplar las funcionalidades mínimas definidas en el enunciado:

- Administración de tours y salidas.
- Registro de clientes y reservas.
- Cálculo de precios y promociones.
- Registro de pagos.
- Contratación de servicios adicionales.
- Cancelación y modificación de reservas.
- Asignación de guías y transportes.
- Emisión de listas de pasajeros.
- Liquidación de honorarios.
- Generación de reportes administrativos.

## 8. Conclusión

El análisis inicial permite identificar las principales necesidades de Rumbo Sur y establecer una base para el modelado del sistema.

A partir de este relevamiento se podrán definir los requerimientos funcionales, las reglas de negocio y los diagramas UML correspondientes a la primera etapa del trabajo práctico.
