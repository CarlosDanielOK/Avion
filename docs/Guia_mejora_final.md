# Guía de la mejora final: exportación a TXT (CU25 y CU26)

## 1. Qué se agregó

| Caso de uso | Qué hace |
|---|---|
| **CU25 Exportar lista de pasajeros** | Emite la lista de una salida (con las reglas que ya existían) y la guarda en `exportaciones/lista_pasajeros_salida_N.txt`. |
| **CU26 Historial de viajes de un cliente** | Busca al cliente por DNI y guarda todas sus reservas (realizados, pendientes, anuladas, con total/pagado/saldo) en `exportaciones/historial_cliente_DNI.txt`. |

Ambos aparecen en el **menú principal, opción 7**. Después de generar el archivo, el menú lo lee y lo muestra en pantalla.

**Archivos**

| Archivo | Cambio |
|---|---|
| `persistencia/ExportadorTxt.java` | NUEVO. Arma y escribe los TXT. |
| `controladores/ControladorExportaciones.java` | NUEVO. Coordina el caso de uso. |
| `modelo/Agencia.java` | +1 método: `buscarReservasDeCliente`. |
| `interfaz/MenuConsola.java` | Opción 7 del menú, `menuExportaciones`, `mostrarArchivo`. |
| `Main.java` | Crea el exportador y el controlador nuevos. |
| `interfaz/DatosEjemplo.java` | +2 reservas de Carla para que el historial tenga datos. |
| `test/persistencia/ExportadorTxtTest.java` | NUEVO. Pruebas. |


## 2. Guion de demostración (2 minutos)

1. Borra `datos/agencia.dat` (la opción 6 solo carga datos si la agencia está vacía).
2. Ejecuta el programa y elige **6**: carga los datos de ejemplo.
3. Elige **7 → 2** e ingresa `35000001` (Carla): muestra 1 viaje realizado, 2 pendientes y el detalle de cada reserva.
4. Elige **7 → 2** e ingresa `99999999`: muestra el flujo de error (cliente inexistente).
5. Elige **3 → 5** (salida 1, guía 101, patente AB123CD) y luego **7 → 1** (salida 1): genera la lista de pasajeros con guía.
6. Abre la carpeta `exportaciones/` y muestra los archivos.

**¿Dónde se guardan los datos?** En `datos/agencia.dat` (serialización). Persisten entre ejecuciones, no son temporales. Los TXT son solo salidas: el programa no los vuelve a cargar.

## 3. Archivos en Java vs. Python

| Python | Java (`java.nio.file`, como en el PDF de la profesora) |
|---|---|
| `with open(f, "w")` + `write()` | `Files.write(ruta, lineas)`: abre, escribe todas las líneas, **cierra solo** y reemplaza el contenido |
| `open(f, "a")` | `Files.write(ruta, lineas, StandardOpenOption.APPEND)` (no se usa acá) |
| `readlines()` | `Files.readAllLines(ruta)` |
| `os.makedirs(...)` | `Files.createDirectories(ruta)` |
| `try / except OSError` | `try / catch (IOException e)` |

Sobre `try/catch/finally`: el estilo antiguo es abrir el archivo, usar `try`, y cerrarlo a mano en `finally` (como en `RepositorioAgenciaArchivo`). La forma moderna del PDF es `try-with-resources` (equivalente a `with open`), y todavía más corta es `Files.write`, que ya cierra internamente. Por eso **acá no hace falta `finally`**.

## 4. El código, en orden de ejecución

### 4.1 Arranque (`Main`)
1. `new ExportadorTxt("exportaciones")`: crea el exportador con la carpeta donde irán los archivos.
2. `new ControladorExportaciones(agencia, operaciones, exportador)`: le da las 3 piezas que necesita.
3. `new MenuConsola(..., exportaciones, entrada)`: el menú recibe el controlador nuevo.

### 4.2 `MenuConsola` (solo pide datos y muestra)
1. `mostrarMenu` imprime la línea "7. Exportar a TXT". Si el usuario elige 7, el `switch` llama a `submenu(7)`.
2. `submenu` ejecuta `menuExportaciones()` dentro de un `try`. Atrapa `ReglaNegocioException` (cliente o salida inexistente, salida cancelada) y `IllegalStateException` (falla de escritura), y muestra el mensaje en vez de cerrar el programa.
3. `menuExportaciones` imprime las opciones y lee la elección con `leerEntero`.
   - Opción 1: lee el número de salida y llama a `exportaciones.exportarListaPasajeros(...)`.
   - Opción 2: lee el documento y llama a `exportaciones.exportarHistorialCliente(...)`.
   - En ambos casos Java **primero** evalúa la llamada al controlador (que devuelve un `Path`) y **después** se la pasa a `mostrarArchivo`.
4. `mostrarArchivo(ruta)` imprime dónde quedó el archivo y lo lee con `Files.readAllLines(ruta)`, línea por línea con un `for`. Como `readAllLines` lanza `IOException` (checked), hay un `catch` que avisa sin romper el programa.

### 4.3 `ControladorExportaciones` (coordina)
- **`exportarListaPasajeros(idSalida)`**
  1. `agencia.buscarSalida(idSalida)`: si no existe lanza `EntidadNoEncontradaException`.
  2. `operaciones.emitirListaPasajeros(idSalida)`: **reutiliza** la regla ya existente (marca la lista como emitida, rechaza salidas canceladas o sin reservas vigentes y guarda). No se duplica lógica.
  3. `exportador.exportarListaPasajeros(salida, pasajeros)`: escribe el archivo y devuelve la ruta.
- **`exportarHistorialCliente(documento)`**
  1. `agencia.buscarCliente(documento)`: si no existe, lanza la excepción y no se genera nada.
  2. `agencia.buscarReservasDeCliente(documento)`: obtiene las reservas del cliente.
  3. `exportador.exportarHistorialCliente(...)`: escribe el archivo.

### 4.4 `Agencia.buscarReservasDeCliente` (filtra)
Crea una lista vacía, recorre **todas** las reservas con `for`, y agrega solo las cuyo `getCliente().getDocumento()` coincide (`equalsIgnoreCase`, igual que `buscarCliente`). Si el cliente no tiene reservas, devuelve lista vacía (no `null`).

### 4.5 `ExportadorTxt` (arma el texto y escribe)
- **Constructor:** valida que la carpeta no sea nula ni vacía, y la guarda como `Path`.
- **`exportarListaPasajeros(salida, pasajeros)`**
  1. Crea `List<String> lineas` y agrega el encabezado: título, salida, fechas, guía (`"sin asignar"` si es `null`) y total de pasajeros.
  2. Con un `for` y un contador `numero`, agrega una línea por pasajero: nombre, DNI, contacto de emergencia y restricciones (si es `null` o vacío escribe `"ninguna"`).
  3. Llama a `escribir("lista_pasajeros_salida_N.txt", lineas)`.
- **`exportarHistorialCliente(cliente, reservas)`**
  1. Inicia 3 contadores (`realizados`, `pendientes`, `anuladas`) y una lista `detalle`.
  2. Por cada reserva: `FINALIZADA` suma a realizados, `ANULADA` a anuladas, y el resto (`PENDIENTE`/`CONFIRMADA`) a pendientes, porque el viaje todavía no se hizo.
  3. Calcula el `saldo` con `calcularSaldoPendiente()` y arma la línea con `String.format(Locale.US, ...)`, que da el formato `$1,234.00`. Agrega `PAGADA` si el saldo es menor a 0,01, o `CON SALDO` si no.
  4. Arma `lineas` (encabezado + resumen + detalle). Si no hay reservas, agrega "El cliente no tiene reservas registradas".
  5. Llama a `escribir(...)`.
- **`escribir(nombreArchivo, lineas)`** (privado)
  1. `Files.createDirectories(carpeta)`: crea la carpeta si no existe (si existe, no hace nada).
  2. `carpeta.resolve(nombreArchivo)`: une carpeta y nombre en una ruta, sin preocuparse por `/` o `\`.
  3. `Files.write(ruta, lineas)`: crea el archivo (o lo reemplaza), escribe en UTF-8, **lo cierra solo** y devuelve la misma ruta.
  4. Si algo falla lanza `IOException` (checked), se atrapa con `catch` y se relanza como `IllegalStateException("No se pudo escribir...", e)`. El patrón es el mismo que ya usa `RepositorioAgenciaArchivo`, y el `e` conserva la causa original.

### 4.6 `DatosEjemplo`
Ya existía (menú opción 6). Agregué para Carla: la reserva 4 (pagada, confirmada y `finalizar()`: simula un viaje ya realizado) y la reserva 5 (sin pagos: queda `PENDIENTE`). Así su historial muestra los 3 casos.

### 4.7 Pruebas (`ExportadorTxtTest`)
Usa una carpeta temporal (`Files.createTempDirectory`) para no ensuciar el proyecto. Comprueba:
- que el filtro por cliente funciona,
- el nombre y contenido de la lista (incluidas las tildes),
- el conteo de estados del historial,
- el historial vacío,
- que una escritura fallida lanza `IllegalStateException`.

## 5. Patrones para defender (solo los que realmente están)

| Patrón | Dónde | Problema → decisión → ventaja |
|---|---|---|
| **SOLID – SRP** | `ExportadorTxt` | Si `Salida`, `Reserva` o `MenuConsola` armaran archivos, tendrían dos motivos de cambio. Se dejó una sola clase para archivos de texto, así un cambio de formato no toca el modelo. |
| **GRASP – Fabricación pura** | `ExportadorTxt` | Escribir TXT no es un concepto del negocio de turismo, así que no tiene lugar natural en las clases del dominio. Se creó una clase artificial, como `GestorPagos`. |
| **GRASP – Controlador** | `ControladorExportaciones` | El menú no debe buscar datos ni coordinar clases. El controlador recibe la orden, busca y delega. |
| **GRASP – Experto** | `Agencia.buscarReservasDeCliente` | La agencia es quien conoce todas las reservas, así que ella sabe filtrarlas. |

**No digas** OCP ni DIP para esta mejora: no están aplicados (el exportador no está detrás de una interfaz). Si la profesora pide más, usa los de tu TPO original. Si te pregunta cómo mejorarlo, puedes decir que una interfaz `Exportador` permitiría sumar formatos (CSV) sin tocar el controlador.

## 6. Preguntas probables

- **¿Por qué `IOException` es especial?** Es una excepción *checked*: el compilador obliga a atraparla (`catch`) o declararla (`throws`). Acá se atrapa en `escribir` y se relanza como `IllegalStateException`.
- **¿Qué hace `Files.write` si el archivo ya existe?** Lo reemplaza, como `"w"` en Python. Para agregar al final se usa `StandardOpenOption.APPEND`.
- **¿Cómo se cierra el archivo?** `Files.write` lo cierra internamente. Con `BufferedWriter` se usaría `try-with-resources`.
- **¿Por qué no se escribe el archivo en `Salida` o `Reserva`?** Por SRP: el modelo no debe saber de archivos.
- **¿Por qué el controlador llama a `emitirListaPasajeros` y no arma la lista él?** Para reutilizar las reglas existentes (salida cancelada, sin reservas, lista emitida) sin duplicarlas.
- **¿Por qué `Path` y no `File`?** `Path`/`Files` es la API moderna (Java 7+) que muestra el PDF, y es más corta.
- **¿Qué pasa si el cliente no existe?** `buscarCliente` lanza `EntidadNoEncontradaException`, el menú la atrapa y muestra el mensaje. No se genera archivo.
- **¿Qué significa "pendiente"?** `PENDIENTE` o `CONFIRMADA`: el viaje aún no se realizó. "Realizado" = `FINALIZADA`.
- **¿Cómo lo probaste?** Con `ExportadorTxtTest`, que usa una carpeta temporal y verifica el contenido leyendo el archivo.
- **¿Qué es `Locale.US` en `String.format`?** Fija el formato numérico (`1,234.00`) sin depender de la configuración regional.

## 7. Documentación a actualizar

- **Supuestos nuevos:**
  - el historial se consulta por DNI, sin usuario ni contraseña;
  - "realizados" = reservas `FINALIZADA`;
  - "pendientes" = `PENDIENTE` o `CONFIRMADA`;
  - los archivos se guardan en `exportaciones/` y se reemplazan al regenerarse;
  - los TXT están en UTF-8.
- **Diagramas:** `docs/uml/06-clases-mejora-final.puml` y `docs/uml/07-secuencia-historial-cliente.puml` (abrir en draw.io: Organizar → Insertar → Avanzado → PlantUML, o en plantuml.com).
- **README:** mencionar la opción 7 del menú.
