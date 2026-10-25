# Diagramas UML de Rumbo Sur

Esta carpeta contiene los archivos fuente PlantUML (`.puml`), sus imágenes exportadas
(`.png`) y los diagramas de casos de uso, todos con su fuente PlantUML.

## Inventario de diagramas

- `01-clases-inicial.puml` / `01-clases-inicial.png`: modelo previo a la aplicación de patrones.
- `02-secuencia-crear-reserva.puml` / `02-secuencia-crear-reserva.png`: CU08, con flujo alternativo por falta de cupos o requisitos.
- `03-secuencia-cancelar-reserva.puml` / `03-secuencia-cancelar-reserva.png`: CU15, con política de devolución y reserva no cancelable.
- `04-clases-final.puml` / `04-clases-final.png`: modelo posterior a la aplicación de SOLID y GRASP.
- `casos-uso-administracion`, `casos-uso-reservas`, `casos-uso-pagos` y `casos-uso-operaciones` (`.puml` y `.png`): diagramas de casos de uso, con relaciones «include» y «extend».

## Cambios del diagrama de clases inicial al final

- `Reserva` deja de concentrar la creación del objeto, el cálculo del precio y la cancelación.
- Se incorporan `ControladorReservas` y `MenuConsola` para separar la interfaz del flujo de los casos de uso.
- Se incorporan `CalculadoraPrecioReserva`, `Promocion`, `PromocionAnticipada` y `PromocionGrupo` para aplicar OCP y encapsular el cálculo de promociones.
- Se incorporan `GestorPagos` y `GestorCancelaciones` para separar las políticas de pagos y cancelaciones, en línea con SRP.
- Se incorpora `RepositorioReservas` junto con `RepositorioReservasArchivo` para que la lógica dependa de una abstracción y no del archivo concreto, aplicando DIP.
- `Salida` y `Reserva` conservan las responsabilidades sobre la información que conocen: disponibilidad, estado y saldo.
- `CalculadoraPrecioReserva`, `GestorPagos` y `GestorCancelaciones` son clases artificiales que ubican los cálculos y políticas fuera de `Reserva`, aplicando Fabricación Pura.
- `DatosPasajero` desacopla los datos de entrada del proceso de creación y permite que `Reserva` administre la incorporación de sus pasajeros, aplicando Creador.
- `Reserva` incorpora los servicios adicionales, el seguro obligatorio en tours de aventura y el vencimiento de la reserva pendiente (`agregarServicioAdicional()`, `incluirSeguroObligatorio()`, `establecerVencimiento()`, `estaVencida()`), y `ControladorReservas` coordina estos pasos junto con el pago total inmediato.
- `Salida` incorpora `validarCondicionesInicio()`, `iniciar()` y `finalizar()` para aplicar las reglas de operación (RN29 y RN30).
- Se agregan excepciones de dominio para representar incumplimientos de reglas en los flujos de creación y cancelación.

Estos cambios se reflejan en el diagrama final y se justifican en `docs/solid-grasp.md`.
