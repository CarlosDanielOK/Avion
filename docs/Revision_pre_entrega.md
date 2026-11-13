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

## Estado de la documentación y verificaciones finales

El documento `docs/Documento de diseño.pdf` fue actualizado a partir de las exportaciones UML vigentes. Conserva las secciones de Etapa 1 e incorpora el diagrama de clases final actualizado y un anexo de Etapa 2 con trazabilidad de cambios, correspondencia UML-código y síntesis de pruebas. Las exportaciones SVG y el PDF de `04-clases-final` también están incluidos en `docs/uml/`.

No se pudo verificar el historial de GitHub desde el ZIP porque no contiene el directorio `.git`. Tampoco se pueden dar por ejecutadas las pruebas manuales PM01–PM10: el plan las presenta como recorridos para que el grupo las ejecute en su entorno.

## Checklist final del grupo

- [x] Regenerar las exportaciones SVG de los diagramas UML actualizados.
- [x] Regenerar `04-clases-final.pdf` desde `04-clases-final.puml`.
- [x] Actualizar `docs/Documento de diseño.pdf` e inspeccionar visualmente sus páginas modificadas.
- [ ] Verificar que `git tag --list` incluya `etapa-1` y `etapa-2` y que ambos apunten a los commits correctos.
- [ ] Confirmar que los commits de los tres integrantes y las revisiones por Pull Request sean visibles en GitHub.
- [ ] Ejecutar en el equipo del grupo los casos manuales PM01–PM10 del plan de pruebas y practicar la demostración.
- [x] Fecha de defensa confirmada: 19/11/2026 (jueves).

## Fechas de entrega

La entrega de la Etapa 2 fue extendida excepcionalmente al 13/11/2026 debido a inconvenientes con Microsoft Teams. La defensa oral está prevista para el 19/11/2026 (jueves).
