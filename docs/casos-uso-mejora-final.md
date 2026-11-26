### CU25 – Exportar lista de pasajeros a TXT

**Actor principal:** Coordinador de operaciones.

**Objetivo:** Obtener un archivo de texto con la lista de pasajeros de una salida, para guardarlo, imprimirlo o entregarlo al guía.

**Precondición:** Salida existente con reservas vigentes (misma que CU20).

**Flujo principal:**
1. El coordinador elige "Exportar a TXT" y "Exportar lista de pasajeros".
2. Ingresa el número de salida.
3. El sistema emite la lista de pasajeros (incluye CU20).
4. El sistema escribe `exportaciones/lista_pasajeros_salida_N.txt` con salida, fechas, guía y, por pasajero, documento, contacto de emergencia y restricciones.
5. El sistema informa la ruta del archivo y muestra su contenido.

**Flujos alternativos:**
- Si la salida no existe, se informa el error y no se genera archivo.
- Si la salida está cancelada o no tiene reservas vigentes, CU20 la rechaza y no se genera archivo.
- Si no se puede escribir el archivo, se informa "No se pudo escribir el archivo".

**Postcondición:** Archivo de texto disponible en `exportaciones/` (reemplaza el anterior de la misma salida).

### CU26 – Consultar y exportar historial de un cliente

**Actor principal:** Vendedor.

**Objetivo:** Ver y guardar todos los viajes de un cliente: realizados, pendientes y anuladas, con total, pagado y saldo.

**Precondición:** Cliente registrado.

**Flujo principal:**
1. El vendedor elige "Exportar a TXT" y "Historial de viajes de un cliente".
2. Ingresa el documento del cliente.
3. El sistema busca al cliente y sus reservas.
4. El sistema cuenta realizados (finalizadas), pendientes (pendientes o confirmadas) y anuladas, y arma una línea por reserva.
5. El sistema escribe `exportaciones/historial_cliente_DNI.txt`.
6. El sistema informa la ruta y muestra el contenido.

**Flujos alternativos:**
- Si el cliente no existe, se informa el error y no se genera archivo.
- Si el cliente no tiene reservas, el archivo indica "El cliente no tiene reservas registradas".
- Si no se puede escribir el archivo, se informa el error.

**Postcondición:** Archivo de texto disponible en `exportaciones/`.
