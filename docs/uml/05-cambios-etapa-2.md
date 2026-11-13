# Cambios y justificaciones entre Etapa 1 y Etapa 2 – Rumbo Sur

Este documento registra las decisiones incorporadas durante la codificación. Su objetivo es mantener trazabilidad entre el diseño aprobado en Etapa 1, el código Java y las fuentes PlantUML vigentes.

| Elemento | Decisión implementada en Java | Justificación y reflejo UML |
|---|---|---|
| Promociones | `CalculadoraPrecioReserva` recibe una `List<Promocion>` por constructor y recorre la interfaz. El constructor sin argumentos configura las dos promociones requeridas actualmente. | Mantiene OCP: se puede incorporar otra implementación sin modificar el algoritmo central. El diagrama muestra la lista de `Promocion` y la relación de composición compartida. |
| Creación de pasajeros | `Reserva.agregarPasajero(DatosPasajero, String)` crea el objeto `Pasajero` y lo agrega a la colección. El menú recoge datos y delega la creación a `Reserva`. | Implementa GRASP Creador. El diagrama muestra la operación y la relación de composición `Reserva`–`Pasajero`. |
| Creación de pagos | `GestorPagos` crea cada `Pago`; `Reserva` registra el pago recibido. | La responsabilidad de construcción queda en el gestor especializado. El diagrama representa la dependencia `GestorPagos` → `Pago`; `Reserva` mantiene la colección. |
| Vencimientos | Las reservas pendientes vencen al finalizar la retención definida por S04/S09. Las confirmadas con saldo pendiente se anulan al superar el plazo de pago total. | La regla se centraliza en `Reserva.estaVencida` y se aplica al refrescar la salida o antes de operar sobre la reserva. Los cupos se liberan al anular. |
| Lista operativa (CU20) | La lista incluye pasajeros de reservas confirmadas y pendientes cuya retención siga vigente. Excluye reservas anuladas o vencidas. La emisión refresca vencimientos y estado antes de generar la lista. | CU20 exige una salida con reservas vigentes; S04/S09 mantienen vigentes las reservas pendientes durante su retención. La nota del diagrama aclara esta interpretación. El inicio de salida sigue limitado a estados CONFIRMADA/COMPLETA y exige lista, guía y vehículo. |
| Cancelación (S15) | `Reserva.puedeCancelarse()` exige estado PENDIENTE/CONFIRMADA y que la salida no esté EN_CURSO ni FINALIZADA. El controlador y el propio objeto aplican la validación. | Evita que la interfaz o una llamada directa omitan la regla. Se refleja en `Reserva` y `ControladorReservas`. |
| Honorarios (RN31/RN32) | `Salida.totalServiciosAdicionales()` suma únicamente importes asociados a reservas CONFIRMADAS o FINALIZADAS. | Una reserva pendiente no es una venta confirmada y una anulada deja de formar parte de la base de honorarios. La nota del diagrama final documenta el filtro. |
| Persistencia y cierre | `RepositorioAgenciaArchivo` utiliza `try`, `catch` y `finally` explícitos para cerrar los flujos; `Main` cierra el `Scanner` en `finally`. | Hace visible en el código el manejo de excepciones y liberación de recursos, conforme al temario y a la consigna. |
| Pruebas | Se agregaron escenarios para vencimientos, reservas vigentes, liquidación de adicionales y cancelación según el estado de la salida. | `docs/Plan_de_pruebas.md` identifica entradas, resultados esperados y resultados obtenidos de las pruebas automatizadas; las pruebas manuales se presentan aparte como guía para la defensa. |

## Artefactos vigentes

- `04-clases-final.puml`: diagrama de clases final, actualizado con firmas y reglas descritas arriba.
- `02-secuencia-crear-reserva.puml` y `03-secuencia-cancelar-reserva.puml`: diagramas de secuencia correspondientes a los métodos que ejecuta la implementación.
- `docs/Plan_de_pruebas.md`: plan de pruebas de Etapa 2.

Las imágenes SVG y los PDF deben exportarse nuevamente desde las fuentes PlantUML vigentes antes de entregar. No deben reutilizarse exportaciones de una versión anterior si no fueron regeneradas.
