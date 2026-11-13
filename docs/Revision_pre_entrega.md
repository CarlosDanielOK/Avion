# Revisión técnica previa a la entrega

## Resultado

La implementación Java revisada compila con `--release 17`, sus seis clases de pruebas automatizadas finalizan correctamente y las pruebas de humo de persistencia y menú fueron satisfactorias. No se encontraron dependencias externas, servicios web ni pasarelas de pago: la interacción sigue siendo por consola y las devoluciones se simulan.

## Correcciones aplicadas en esta revisión

1. Se corrigió el vencimiento de reservas confirmadas con saldo impago: una vez superada la fecha límite de diez días, la reserva se anula y libera sus cupos.
2. La lista de pasajeros incluye reservas confirmadas y reservas pendientes cuya retención siga vigente; excluye reservas anuladas o vencidas. Si cambia la composición de reservas o pasajeros, la lista anterior se invalida y debe emitirse nuevamente antes de iniciar la salida.
3. Se rechaza la creación de reservas para salidas canceladas, finalizadas o en curso.
4. El precio se calcula con la tarifa vigente de la fecha de salida y los servicios adicionales realmente contratados; si se agrega un adicional, el precio anterior se invalida para forzar el recálculo.
5. `CalculadoraPrecioReserva` recibe promociones por constructor y evalúa la interfaz `Promocion`, para que la extensión de promociones no requiera modificar su algoritmo.
6. `Reserva` crea el pasajero a partir de `DatosPasajero`, reflejando el patrón GRASP Creador en código y UML.
7. La emisión de listas y el inicio de salidas actualizan vencimientos y el cupo mínimo antes de continuar. Una salida programada no puede iniciar sin alcanzar el estado CONFIRMADA o COMPLETA; si llega a siete días sin cupo mínimo, se cancela y se procesan las devoluciones de agencia.
8. Se registra el medio del último pago en la devolución simulada, conforme al supuesto S13.
9. Se centralizó la actualización de vencimientos y del estado de la salida al iniciar operaciones sobre una reserva (S09). Una reserva vencida se anula antes de permitir calcular, cobrar, confirmar, cancelar o modificar; si la salida también debe cancelarse por cupo mínimo, se procesan las devoluciones completas.
10. `Salida.totalServiciosAdicionales()` excluye reservas pendientes y anuladas de la base de cálculo de honorarios, y considera solo reservas confirmadas o finalizadas.
11. La emisión de una lista actualiza vencimientos y el estado de la salida; los cambios posteriores en reservas o pasajeros invalidan la lista anterior para que no se inicie la salida con datos desactualizados.

## Pendientes que no pueden cerrarse con el volcado proporcionado

- El archivo Repomix describe `docs/Documento de diseño.pdf` y varios PDF UML, pero no incluye sus bytes (Repomix excluye los binarios). Por eso no se pudo revisar visualmente ese PDF ni actualizarlo.
- Las fuentes PlantUML (`.puml`) de los diagramas de secuencia y del diagrama de clases final se actualizaron. Las exportaciones `.svg` y PDF disponibles en el volcado son exportaciones previas y deben regenerarse antes de entregar.
- El volcado `.md` no contiene el directorio `.git`, por lo que no se pudo comprobar el historial, los pull requests ni los tags `etapa-1` y `etapa-2` requeridos por la consigna.
- La consigna original indica entrega de Etapa 2 el 04/11/2026 y defensa el 11/11/2026; el README de la versión recibida indica 12/11/2026 y 19/11/2026. Confirmar con la profesora cuál cronograma quedó vigente y corregir ambos documentos para que coincidan.

## Checklist final del grupo

- [ ] Regenerar `02-secuencia-crear-reserva.svg` y `03-secuencia-cancelar-reserva.svg` desde sus `.puml`.
- [ ] Regenerar `04-clases-final.svg` y `04-clases-final.pdf` desde `04-clases-final.puml`.
- [ ] Actualizar `docs/Documento de diseño.pdf` con los diagramas finales y verificar que sus textos describan la implementación actual.
- [ ] Verificar que `git tag --list` incluya `etapa-1` y `etapa-2` y que ambos apunten a los commits correctos.
- [ ] Confirmar las fechas definitivas con la cátedra.
- [ ] Ejecutar en el equipo del grupo los casos manuales PM01–PM10 del plan de pruebas y practicar el recorrido antes de la defensa.
