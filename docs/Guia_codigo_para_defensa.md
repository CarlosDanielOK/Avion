# Guia del codigo para la defensa oral

## 1. Como recorrer el proyecto

El punto de entrada es `Main`. Desde ahi se carga la agencia desde el archivo,
se crean los controladores y se abre `MenuConsola`.

La organizacion general es:

- `modelo`: entidades del negocio y sus reglas.
- `controladores`: coordinan casos de uso y llaman al modelo.
- `interfaz`: recibe datos por consola y muestra resultados.
- `persistencia`: carga y guarda la raiz `Agencia`.
- `enumeraciones`: conjuntos cerrados de valores.
- `excepciones`: errores de dominio propios del sistema.

## 2. Flujo general de una reserva

1. `MenuConsola` crea un `Cliente`, una `Salida`, un `Vendedor` y un `Reserva`.
2. `Reserva` crea sus objetos `Pasajero`.
3. `ControladorReservas` valida disponibilidad y requisitos.
4. `Salida` agrega la reserva y recalcula su estado.
5. `CalculadoraPrecioReserva` calcula el importe.
6. `GestorPagos` controla pagos y confirmacion.
7. `GestorCancelaciones` calcula devoluciones.
8. `RepositorioAgenciaArchivo` guarda el objeto completo.

## 3. Explicacion de las clases mas preguntables

### `Agencia`

Es la raiz del modelo y el contenedor central. Guarda tours, salidas, clientes,
empleados, reservas, vehiculos y proveedores. Implementa busquedas por codigo,
documento o identificador y usa `EntidadNoEncontradaException` cuando no
encuentra un objeto.

Es el objeto que se serializa completo en el archivo `datos/agencia.dat`.

### `Reserva`

Representa una venta. Conoce a su cliente, salida, vendedor, pasajeros, pagos,
ajustes y estado. Calcula el total pagado, el saldo pendiente y aplica las
transiciones permitidas de estado.

Responsabilidades principales:

- crear y reemplazar pasajeros;
- conservar o cambiar servicios adicionales;
- registrar pagos sin permitir superar el saldo;
- confirmar solo con al menos el 30 % pagado;
- verificar vencimientos;
- crear notas de ajuste.

No concentra toda la aplicacion: delega precios, pagos y devoluciones en clases
especializadas.

### `Salida`

Agrupa reservas para una fecha determinada. Es experta en:

- cupo maximo y cupos disponibles;
- vencimiento de reservas;
- estado de la salida;
- lista de pasajeros;
- guia y vehiculo;
- liquidacion y servicios vendidos.

Implementa `Comparable<Salida>` para ordenar salidas por fecha e identificador.

### `Tour`

Es una clase abstracta que representa el catalogo comercial. Sus
especializaciones son `TourAventura`, `TourCultural` y `TourNaturaleza`.

La clase base define comportamiento comun y las especializaciones sobrescriben
`getCategoria` y, cuando corresponde, `puedeParticipar`.

### `Pasajero`

Contiene datos personales y fecha de nacimiento. Con esa informacion calcula
edad y porcentaje de tarifa segun el inicio de la salida:

- menor de 3: 0 %;
- menor de 12: 60 %;
- jubilado: 90 %;
- adulto: 100 %.

Tambien puede contratar servicios adicionales.

### `CalculadoraPrecioReserva`

Separa el calculo del precio de `Reserva`. Calcula primero el subtotal por
pasajero, luego evalua promociones y finalmente suma los servicios adicionales.

Recibe una lista de objetos `Promocion`, por lo que agregar una promocion nueva
no exige modificar la calculadora. Esto aplica el principio OCP y polimorfismo.

### `Promocion`, `PromocionAnticipada`, `PromocionGrupo`

`Promocion` es una interfaz y define el contrato:

- `esAplicable`;
- `calcularDescuento`.

Las dos implementaciones contienen reglas diferentes. La calculadora elige el
descuento mayor y no acumula promociones.

### `GestorPagos`

Separa el manejo de pagos de `Reserva`. Crea pagos, controla recargos de tarjeta,
consulta el saldo y decide si una reserva puede confirmarse.

### `GestorCancelaciones`

Separa la politica de devolucion de `Reserva`. Selecciona el 90 %, 50 %, 20 %
o 0 % segun los dias restantes y usa 100 % para cancelacion de la agencia.

### `ControladorReservas`

Es el controlador GRASP del modulo comercial. Recibe operaciones del menu,
consulta entidades y coordina reserva, precio, pago, confirmacion y
cancelacion. Cada operacion valida vigencia y persiste cuando corresponde.

### `ControladorOperaciones`

Es el controlador GRASP del catalogo y de las operaciones de salida. Valida
tours, tarifas, proveedores, recursos, estados, listas y liquidaciones.

### `GeneradorReportes`

Agrupa consultas de reportes sin modificar el estado del negocio. Produce
ocupacion, ventas, ingresos y cancelaciones.

### `RepositorioAgencia` y `RepositorioAgenciaArchivo`

La interfaz `RepositorioAgencia` define que operaciones existen. La clase
concreta conoce el mecanismo de almacenamiento.

Esto permite que el programa dependa de una abstraccion y no directamente de
`ObjectOutputStream` o del disco (DIP).

## 4. Persistencia y manejo de archivos

El proyecto usa serializacion binaria, no archivos de texto como `.txt` o
`.csv`.

`repositorio.guardar(agencia)`:

1. verifica que exista la agencia;
2. crea el directorio `datos` si hace falta;
3. abre `datos/agencia.dat`;
4. escribe el objeto completo con `writeObject`;
5. cierra el stream en `finally`.

`repositorio.cargar()`:

1. verifica si el archivo existe;
2. abre el stream de lectura;
3. lee un `Object`;
4. verifica que sea una `Agencia` con `instanceof`;
5. realiza el cast a `Agencia`;
6. cierra el stream en `finally`.

Las clases serializables implementan `Serializable`. `Agencia` es el objeto
raiz y todas las clases que contiene tambien deben ser serializables.

Conceptos que pueden preguntarse:

- `IOException`: error de entrada/salida.
- `ClassNotFoundException`: la clase serializada no puede cargarse.
- `serialVersionUID`: identificador de version de una clase serializable.
- `try/catch/finally`: manejo y cierre del recurso.
- `Path.of`: construye una ruta.
- `Files.exists`: consulta si existe.
- `Files.createDirectories`: crea directorios.
- Ruta relativa: `datos/agencia.dat` se busca desde el directorio de ejecucion.

## 5. Jerarquia de excepciones

`ReglaNegocioException` es la clase base de errores propios del dominio.

Las clases derivadas sirven para distinguir situaciones:

- `CuposInsuficientesException`: no hay capacidad.
- `EntidadNoEncontradaException`: no existe un objeto buscado.
- `OperacionNoPermitidaException`: el estado no permite la accion.
- `PagoRequeridoException`: falta el pago necesario.
- `RequisitosNoCumplidosException`: condiciones previas no cumplidas.
- `ReservaNoCancelableException`: la reserva no puede cancelarse.

Capturar excepciones propias permite mostrar mensajes claros sin que el programa
termine por una operacion invalida.

## 6. Patrones aplicados

- **Herencia:** `Persona` contiene datos comunes y sirve de base a empleados,
  clientes y pasajeros.
- **Polimorfismo:** `Tour` y `Promocion` tienen implementaciones distintas.
- **Encapsulamiento:** los atributos son privados y se acceden por metodos.
- **Comparable:** `Salida` define el orden natural por fecha e identificador.
- **Comparator:** los reportes pueden ordenar con comparadores personalizados.
- **GRASP Controlador:** los controladores reciben los casos de uso.
- **GRASP Experto:** `Salida` calcula cupos y `Reserva` calcula saldos.
- **GRASP Creador:** `Reserva` crea objetos `Pasajero` que incorpora.
- **GRASP Fabricacion Pura:** calculadora, gestores y reportes separan reglas.
- **SOLID SRP:** una clase, una responsabilidad principal.
- **SOLID OCP:** nuevas promociones se agregan mediante la interfaz.
- **SOLID DIP:** los controladores usan la abstraccion `RepositorioAgencia`.
