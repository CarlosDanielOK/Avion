# Diagramas UML de Rumbo Sur

Esta carpeta contiene los archivos fuente PlantUML (`.puml`), sus imágenes exportadas
(`.png`) y el archivo editable del diagrama de casos de uso.

## Inventario de diagramas

- `01-clases-inicial.puml` / `01-clases-inicial.png`: modelo previo a la aplicación de patrones.
- `02-secuencia-crear-reserva.puml` / `02-secuencia-crear-reserva.png`: CU08, con flujo alternativo por falta de cupos o requisitos.
- `03-secuencia-cancelar-reserva.puml` / `03-secuencia-cancelar-reserva.png`: CU15, con política de devolución y reserva no cancelable.
- `04-clases-final.puml` / `04-clases-final.png`: modelo posterior a la aplicación de SOLID y GRASP.
- `Rumbo_Sur_Casos_de_Uso_Editables.drawio`: fuente editable de los diagramas de casos de uso.

## Cambios del diagrama de clases inicial al final

- `Reserva` deja de concentrar la creación del objeto, el cálculo del precio y la cancelación.
- Se incorporan `ControladorReservas` y `MenuConsola` para separar la interfaz del flujo de los casos de uso.
- Se incorporan `CalculadoraPrecioReserva`, `Promocion`, `PromocionAnticipada` y `PromocionGrupo` para aplicar OCP y encapsular el cálculo de promociones.
- Se incorporan `GestorPagos` y `GestorCancelaciones` para separar las políticas de pagos y cancelaciones, en línea con SRP.
- Se incorpora `RepositorioReservas` junto con `RepositorioReservasArchivo` para que la lógica dependa de una abstracción y no del archivo concreto, aplicando DIP y Bajo Acoplamiento.
- `Salida` y `Reserva` conservan las responsabilidades sobre la información que conocen: disponibilidad, estado y saldo.
- `DatosPasajero` desacopla los datos de entrada del proceso de creación y permite que `Reserva` administre la incorporación de sus pasajeros, aplicando Creador.
- Se agregan excepciones de dominio para representar incumplimientos de reglas en los flujos de creación y cancelación.

Estos cambios se reflejan en el diagrama final y se justifican en `docs/solid-grasp.md`.
