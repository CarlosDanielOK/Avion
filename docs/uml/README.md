# Diagramas UML de Rumbo Sur

Esta carpeta contiene los archivos fuente PlantUML (`.puml`), sus imágenes exportadas (`.png`) y los diagramas de casos de uso.

## Inventario de diagramas

- `01-clases-inicial.puml` / `01-clases-inicial.png`: modelo previo a la aplicación de patrones.
- `02-secuencia-crear-reserva.puml` / `02-secuencia-crear-reserva.png`: CU08, con flujos alternativos por falta de cupos, requisitos no cumplidos y pago total requerido.
- `03-secuencia-cancelar-reserva.puml` / `03-secuencia-cancelar-reserva.png`: CU15, con política de devolución, reserva inexistente y reserva no cancelable.
- `04-clases-final.puml` / `04-clases-final.png`: modelo posterior a la aplicación de SOLID y GRASP.
- `casos-uso-administracion`, `casos-uso-reservas`, `casos-uso-pagos` y `casos-uso-operaciones` (`.puml` y `.png`): diagramas de casos de uso, con relaciones «include» y «extend».

## Cambios del diagrama de clases inicial al final

**Cambios por SOLID y GRASP**

- `Reserva` deja de concentrar el cálculo del precio, los pagos y la cancelación. Se incorporan `CalculadoraPrecioReserva`, `GestorPagos` y `GestorCancelaciones` (SRP y Fabricación Pura).
- Se incorporan `Promocion`, `PromocionAnticipada` y `PromocionGrupo` para poder agregar promociones sin modificar el cálculo (OCP).
- Se incorporan `RepositorioAgencia` y `RepositorioAgenciaArchivo`, y la clase raíz `Agencia`, para que la lógica dependa de una abstracción y no del archivo concreto (DIP). `Agencia` guarda las colecciones del sistema y ofrece las búsquedas.
- Se incorporan `MenuConsola`, `ControladorReservas` y `ControladorOperaciones` para separar la interfaz del flujo de los casos de uso, y `GeneradorReportes` para los reportes (Controlador).
- `DatosPasajero` desacopla los datos de entrada del proceso de creación, y `Reserva` crea a sus `Pasajero` y `NotaAjuste` (Creador).
- `Salida`, `Reserva` y `Pasajero` conservan las responsabilidades sobre la información que conocen: cupos, días hasta el inicio, pago total exigido, saldo y factor de tarifa (Experto).
- Se agregan las excepciones de dominio derivadas de `ReglaNegocioException`.

**Correcciones de consistencia respecto de la versión anterior**

- `Salida` ahora conoce a sus `Reserva` y `Guia` conoce a sus `Salida` (asociaciones sin dirección única), porque ambas necesitan esa información para calcular cupos y disponibilidad. Este cambio también se aplicó al diagrama inicial.
- `ServicioAdicional` pasa a pertenecer a un `Tour` y tiene el atributo `obligatorio`. Se elimina la relación redundante `Reserva`–`ServicioAdicional`: los servicios se contratan por pasajero.
- La regla de «faltan 10 días o menos» se mueve del controlador a `Salida` (`exigePagoTotal`), y `diasHastaSalida` pasa de `Reserva` a `Salida` (`diasHastaInicio`).
- Se quitan los métodos públicos `establecerEstado` y `definirTotal`: la reserva nace en estado pendiente y el vencimiento lo calcula la propia `Reserva` (`iniciarRetencion`).
- `Coordinador` y `Administrador` quedan solo con sus datos: la lógica de operación pasa a `ControladorOperaciones` y los reportes a `GeneradorReportes`.
- `crearReserva` se divide en `iniciarReserva` y `finalizarReserva`, porque la selección de servicios ocurre entre ambos pasos.
- `Salida` implementa `Comparable` (orden natural por fecha de inicio) para cumplir con el uso de ordenamiento; los reportes usan además un `Comparator`.

Estos cambios se justifican en `docs/solid-grasp.md`.
