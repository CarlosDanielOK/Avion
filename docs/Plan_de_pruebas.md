# Plan de pruebas – Rumbo Sur

## 1. Alcance y ejecución

Este plan registra las pruebas automatizadas ejecutables incluidas en `test/` y las comprobaciones de humo realizadas sobre la versión revisada. Las pruebas están implementadas como clases Java con método `main` y aserciones, sin dependencias externas.

Para reproducirlas desde la raíz del proyecto con JDK 17 o superior (PowerShell):

```powershell
New-Item -ItemType Directory -Force out
javac --release 17 -encoding UTF-8 -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object FullName)
New-Item -ItemType Directory -Force out-test
javac --release 17 -encoding UTF-8 -cp out -d out-test (Get-ChildItem -Path test -Recurse -Filter *.java | ForEach-Object FullName)
java -cp "out;out-test" modelo.AgenciaTest
java -cp "out;out-test" modelo.SalidaTest
java -cp "out;out-test" controladores.GeneradorReportesTest
java -cp "out;out-test" controladores.ControladorOperacionesTest
java -cp "out;out-test" controladores.ControladorReservasTest
java -cp "out;out-test" modelo.CalculadoraPrecioReservaTest
```

## 2. Pruebas ejecutadas y resultados

| ID | Clase / escenario | Datos de entrada relevantes | Resultado esperado | Resultado obtenido |
|---|---|---|---|---|
| PT01 | `AgenciaTest` – búsquedas | Tour `CUL01`, cliente documento `222`; consulta de tour inexistente `NOEXISTE` | Encontrar registros sin distinguir mayúsculas y lanzar `EntidadNoEncontradaException` si no existe | **OK** |
| PT02 | `SalidaTest` – validaciones y cupos | Fechas invertidas; cupos fuera del rango del tour; reservas con dos pasajeros y cupo mínimo de dos | Rechazar datos inválidos; pasar a CONFIRMADA al alcanzar el mínimo y a COMPLETA al ocupar el máximo | **OK** |
| PT03 | `SalidaTest` – vencimiento de reserva confirmada | Total $1.000; pago parcial $300; salida a 10 días; verificación al día siguiente del límite | Anular la reserva con saldo impago y liberar los cupos | **OK** |
| PT04 | `SalidaTest` – lista de pasajeros | Una reserva confirmada y una pendiente | La lista operativa incluye solo al pasajero de la reserva confirmada | **OK** |
| PT05 | `ControladorReservasTest` – flujo de reserva | Tarifa base $1.000; adicional traslado $100 y excursión $200; pago; confirmación; ajuste de $50; segundo pago por transferencia y cancelación | Total inicial $1.100; total recalculado $1.300; ajuste a $1.350; cancelación libera cupos y conserva el último medio de pago | **OK** |
| PT06 | `ControladorReservasTest` – alta y mínimo | Alta en salida previamente cancelada; salida a cinco días con una reserva que alcanza mínimo de dos pasajeros | Rechazar la primera y confirmar la salida en el segundo caso, manteniendo la reserva pendiente hasta el pago de seña | **OK** |
| PT07 | `ControladorOperacionesTest` – recursos | Dos salidas superpuestas, un vehículo y dos guías | Rechazar el uso simultáneo del mismo vehículo y evitar una asignación parcial | **OK** |
| PT08 | `ControladorOperacionesTest` – cancelación por mínimo desde emisión de lista | Salida inicialmente a diez días, por debajo del mínimo; simulación de consulta a cinco días; reserva confirmada y abonada | Al intentar emitir la lista, actualizar el estado, cancelar la salida y anular la reserva con devolución del 100 % | **OK** |
| PT09 | `CalculadoraPrecioReservaTest` – extensión de promociones | Calculadora construida con una nueva implementación de `Promocion` que descuenta 20 % sobre $1.000 | Aplicar $200 de descuento sin modificar la calculadora | **OK** |
| PT10 | `GeneradorReportesTest` – reportes | Agencia vacía y agencia con reserva vigente por $2.000 y cancelada por $1.000, devolución de $500 | Mensajes claros en reportes vacíos; ocupación y ventas excluyen anuladas; ingresos netos $2.500; informar cancelación | **OK** |
| PT11 | Persistencia por serialización | Carga de datos de ejemplo, guardado y recuperación en archivo temporal | Recuperar 2 tours, 2 salidas y 3 reservas | **OK** |
| PT12 | Inicio del menú | Ejecución de `Main` y selección `0` | Mostrar menú y finalizar sin excepción | **OK** |
| PT13 | `ControladorReservasTest` – vencimiento al iniciar una operación | Reserva pendiente creada hoy; se intenta confirmar simulando una fecha tres días posterior | Aplicar S04/S09 antes de confirmar, anular la reserva vencida y liberar el cupo | **OK** |

**Resultado global:** compilación de todo `src/` y `test/` con `javac --release 17` sin errores; las seis clases de prueba finalizaron con `OK` y cubren los 13 escenarios automatizados documentados; las dos comprobaciones de humo también finalizaron correctamente.

## 3. Pruebas manuales recomendadas para la demostración oral

Estas pruebas son una guía de ejecución manual; no se presentan como resultados ya obtenidos por las pruebas automatizadas.

| ID | Acción | Resultado que debe observarse |
|---|---|---|
| PM01 | Cargar datos de ejemplo desde el menú | Dos tours, salidas, recursos, clientes y reservas de demostración disponibles para operar y consultar reportes |
| PM02 | Crear una reserva con un pasajero adulto, uno menor de 3 años y uno menor de 12 | Los tres ocupan cupo; el menor de 3 no abona tarifa y el menor de 12 aplica el porcentaje previsto |
| PM03 | Intentar una reserva de aventura con edad inferior a la mínima o sin seguro obligatorio en el tour | El sistema rechaza los datos o incorpora el seguro obligatorio según el caso |
| PM04 | Registrar una seña del 30 % y confirmar; luego probar con menos del 30 % | La primera reserva se confirma; la segunda permanece pendiente y no se confirma |
| PM05 | Registrar pagos con tarjeta en más de una cuota | Se informa y conserva por separado el recargo del 7 %; el saldo se calcula sobre el capital |
| PM06 | Intentar confirmar a diez días o menos con saldo pendiente | La confirmación se rechaza hasta completar el pago |
| PM07 | Cancelar con 30, 15, 7 y 6 días de anticipación y luego cancelar por agencia | Aplicar 90 %, 50 %, 20 %, 0 % y 100 %, respectivamente, sobre el capital abonado |
| PM08 | Asignar un guía no habilitado o un vehículo con capacidad insuficiente | La asignación se rechaza y no deja una relación parcial |
| PM09 | Emitir lista, iniciar y finalizar salida | La lista solo contiene reservas confirmadas; el inicio exige lista, guía habilitado y vehículo adecuado; al finalizar se finalizan las reservas confirmadas |
| PM10 | Cerrar y volver a abrir el programa con datos guardados | Los datos persisten en `datos/agencia.dat` |

## 4. Observaciones

- Las pruebas no requieren JUnit ni bibliotecas de terceros; se ejecutan con Java estándar.
- Los casos manuales PM01–PM10 deben probarse en el equipo del grupo antes de la defensa para conocer el recorrido del menú y preparar ejemplos consistentes.
- La devolución se simula mediante mensajes y datos guardados en la cancelación. No se realiza una operación bancaria ni se integra un proveedor de pagos.
