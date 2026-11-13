# Diagramas UML de Rumbo Sur

Esta carpeta contiene las fuentes PlantUML (`.puml`) y sus exportaciones vigentes (`.svg` y, para el diagrama de clases final, `.pdf`). Las imágenes de secuencia y de clases final fueron regeneradas desde sus fuentes actualizadas. El documento consolidado `docs/Documento de diseño.pdf` incorpora los diagramas vigentes y un anexo de trazabilidad de Etapa 2.

## Inventario de diagramas

- `01-clases-inicial.puml` / `.svg`: modelo previo a la aplicación de patrones. La fuente se conserva como referencia de la Etapa 1.
- `02-secuencia-crear-reserva.puml`: CU08, con flujos alternativos por falta de cupos, requisitos no cumplidos, vencimientos y condiciones de pago. Su exportación `02-secuencia-crear-reserva.svg` está regenerada desde esta fuente.
- `03-secuencia-cancelar-reserva.puml`: CU15, con política de devolución, reserva inexistente, reserva no cancelable y actualización de vencimientos. Su exportación `03-secuencia-cancelar-reserva.svg` está regenerada desde esta fuente.
- `04-clases-final.puml`: modelo posterior a la aplicación de SOLID y GRASP, agrupado por capas (modelo del negocio, raíz y persistencia, control e interfaz, servicios y excepciones). Las exportaciones `04-clases-final.svg` y `04-clases-final.pdf` están generadas desde esta fuente vigente.
- `casos-uso-administracion`, `casos-uso-reservas`, `casos-uso-pagos` y `casos-uso-operaciones` (`.puml` y `.svg`): diagramas de casos de uso, con relaciones «include» y «extend».

## Cambios del diagrama de clases inicial al final

**Cambios por SOLID y GRASP**

- `Reserva` deja de concentrar el cálculo del precio, los pagos y la cancelación. Se incorporan `CalculadoraPrecioReserva`, `GestorPagos` y `GestorCancelaciones` (SRP y Fabricación Pura).
- Se incorporan `Promocion`, `PromocionAnticipada` y `PromocionGrupo` para poder agregar promociones sin modificar el cálculo (OCP).
- Se incorporan `RepositorioAgencia` y `RepositorioAgenciaArchivo`, y la clase raíz `Agencia`, para que la lógica dependa de una abstracción y no del archivo concreto (DIP). `Agencia` guarda las colecciones del sistema y ofrece las búsquedas.
- Se incorporan `MenuConsola`, `ControladorReservas` y `ControladorOperaciones` para separar la interfaz del flujo de los casos de uso, y `GeneradorReportes` para los reportes (Controlador).
- `DatosPasajero` desacopla los datos de entrada; `Reserva.agregarPasajero(datos, telefono)` construye el `Pasajero` y lo incorpora, y `Reserva.registrarAjuste` crea la `NotaAjuste` (Creador).
- `Salida`, `Reserva` y `Pasajero` conservan las responsabilidades sobre la información que conocen: cupos, días hasta el inicio, pago total exigido, saldo y factor de tarifa (Experto).
- Se agregan las excepciones de dominio derivadas de `ReglaNegocioException`.
- `CalculadoraPrecioReserva` recibe la lista de `Promocion` en su constructor (agregación), y ofrece un constructor por defecto con las dos promociones actuales. La evaluación recorre la interfaz `Promocion`; una nueva promoción puede agregarse sin cambiar el algoritmo (OCP).

**Correcciones de consistencia respecto de la versión anterior**

- `Salida` ahora conoce a sus `Reserva` y `Guia` conoce a sus `Salida` (asociaciones sin dirección única), porque ambas necesitan esa información para calcular cupos y disponibilidad. Este cambio también se aplicó al diagrama inicial.
- `ServicioAdicional` pasa a pertenecer a un `Tour` y tiene el atributo `obligatorio`. Se elimina la relación redundante `Reserva`–`ServicioAdicional`: los servicios se contratan por pasajero.
- La regla de «faltan 10 días o menos» se mueve del controlador a `Salida` (`exigePagoTotal`), y `diasHastaSalida` pasa de `Reserva` a `Salida` (`diasHastaInicio`).
- Se quitan los métodos públicos `establecerEstado` y `definirTotal`: la reserva nace en estado pendiente y el vencimiento lo calcula la propia `Reserva` (`iniciarRetencion`).
- `Coordinador` y `Administrador` quedan solo con sus datos: la lógica de operación pasa a `ControladorOperaciones` y los reportes a `GeneradorReportes`.
- `Salida.verificarVencimientos` anula reservas pendientes y confirmadas cuyo plazo de pago venció (S04/S09), usando la fecha recibida. `Salida.emitirLista` incluye pasajeros de reservas confirmadas y pendientes con retención vigente, y excluye anuladas o vencidas. La emisión invalida listas anteriores si cambia la composición de reservas o pasajeros. `Salida.finalizar` finaliza las reservas confirmadas. `ControladorReservas` actualiza la vigencia y el estado de la salida antes de iniciar operaciones sobre una reserva, y `registrarReserva` valida estado de la salida, requisitos y tipo de pasajero, y agrega los servicios obligatorios.
- `MenuConsola` coordina la carga de pasajeros, la selección de servicios y el cierre de la operación mediante llamadas separadas a `ControladorReservas`; la implementación conserva la misma separación de responsabilidades del diseño.
- `Salida` implementa `Comparable` (orden natural por fecha de inicio) para cumplir con el uso de ordenamiento; los reportes usan además un `Comparator`.

Estos cambios se justifican en [`../diseno/solid-grasp.md`](../diseno/solid-grasp.md).

## Sincronización con la implementación revisada

Los archivos `.puml` de `02-secuencia-crear-reserva`, `03-secuencia-cancelar-reserva` y `04-clases-final` son las fuentes vigentes del modelo. Sus exportaciones SVG y el PDF del diagrama de clases final están regenerados. El archivo `docs/Documento de diseño.pdf` también fue actualizado: incluye los diagramas vigentes y un anexo que explica las decisiones de Etapa 2 y su correspondencia con el código. Para mantener la consistencia, cualquier modificación posterior a una fuente `.puml` debe acompañarse de una nueva exportación de su SVG/PDF y de la actualización del documento consolidado.
