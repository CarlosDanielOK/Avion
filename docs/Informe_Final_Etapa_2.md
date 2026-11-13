
# INFORME FINAL — ETAPA 2
## SISTEMA DE GESTIÓN PARA AGENCIA DE TURISMO — RUMBO SUR

**Materia:** Programación Orientada a Objetos  
**Trabajo Práctico Obligatorio:** Propuesta B  
**Lenguaje de programación:** Java 17

### INTEGRANTES
- Bianca Chancalay
- Carlos Daniel Lazo
- Guadalupe Olivera

---

## 1. INTRODUCCIÓN

El presente informe describe el trabajo realizado durante la segunda etapa del desarrollo del Sistema de Gestión para la Agencia de Turismo Rumbo Sur.

El objetivo principal de esta etapa fue avanzar desde el análisis y diseño realizado anteriormente hacia la implementación del sistema en Java, aplicando los conceptos de programación orientada a objetos trabajados durante la cursada.

Para organizar el desarrollo, el equipo dividió las funcionalidades en módulos y distribuyó las clases y los casos de uso entre sus integrantes. Esta modalidad permitió trabajar de manera paralela y posteriormente integrar los componentes desarrollados.

Durante esta etapa se implementaron clases relacionadas con la administración de tours, salidas, reservas, pasajeros, precios, promociones, pagos, cancelaciones y operaciones de la agencia.

Asimismo, se realizaron comprobaciones individuales y tareas de integración para identificar y corregir incompatibilidades entre los distintos módulos.

---

## 2. CAMBIOS RESPECTO DEL DISEÑO ORIGINAL

Durante la implementación fue necesario realizar ajustes respecto del diseño elaborado en la primera etapa. Estos cambios surgieron principalmente al trasladar los diagramas UML a código Java y al integrar las clases desarrolladas por los distintos integrantes.

### 2.1. AJUSTES EN LAS CLASES Y SUS RESPONSABILIDADES

Se incorporaron métodos y validaciones para representar de manera más precisa las reglas de negocio.

En el módulo de reservas se desarrollaron operaciones para registrar pasajeros, calcular importes, administrar pagos, consultar saldos y gestionar cancelaciones.

También se incorporaron métodos de consulta y actualización necesarios para que otras clases pudieran interactuar con las reservas.

### 2.2. INTEGRACIÓN ENTRE RESERVAS Y SALIDAS

Uno de los ajustes más importantes estuvo relacionado con la comunicación entre las clases `Reserva` y `Salida`.

Durante las pruebas de compilación se detectó que la clase `Salida` utilizaba métodos que todavía no estaban disponibles en `Reserva` o que habían sido implementados con nombres diferentes.

Para resolver estas diferencias se agregaron métodos relacionados con:

- Identificación de reservas.
- Cantidad de pasajeros.
- Ocupación de cupos.
- Vencimiento de reservas pendientes.
- Finalización de reservas.
- Consulta de servicios adicionales.
- Validación de estados que permiten cancelaciones.

Estas modificaciones permitieron mejorar la compatibilidad entre ambos módulos, conservando las funcionalidades previamente desarrolladas.

### 2.3. AJUSTES EN LA GESTIÓN DE PAGOS Y CANCELACIONES

Se implementaron clases específicas para separar las responsabilidades vinculadas con el cálculo de precios, el registro de pagos y las políticas de devolución.

La clase `CalculadoraPrecioReserva` concentra las operaciones de cálculo, mientras que `GestorPagos` y `GestorCancelaciones` se encargan de las validaciones y operaciones correspondientes.

También se implementaron promociones por anticipación y por cantidad de pasajeros, respetando la regla de aplicar el descuento más conveniente cuando corresponda.

Durante la integración fue necesario incorporar una operación de cancelación compatible con el módulo de operaciones, para contemplar situaciones en las que la agencia cancela una salida.

### 2.4. ORGANIZACIÓN DEL CÓDIGO

La implementación se organizó mediante paquetes Java que permiten separar las entidades del negocio, los controladores, las enumeraciones, las excepciones y los componentes de persistencia.

Esta organización facilita la lectura del código, el mantenimiento y la integración de los módulos.

### 2.5. FUNCIONALIDADES COMPLEMENTARIAS INTEGRADAS

Se completaron las funcionalidades previstas en el modelo final para servicios
adicionales por pasajero, reemplazo de pasajeros y notas de ajuste. Los servicios
obligatorios se incorporan al registrar la reserva; el reemplazo valida la fecha
límite y los requisitos del tour; y las notas modifican el total sin eliminar ni
alterar pagos existentes.

---

## 3. DIFICULTADES ENCONTRADAS

### 3.1. DESARROLLO PARALELO E INTEGRACIÓN

La principal dificultad fue integrar las clases desarrolladas por diferentes integrantes.

Aunque cada módulo tenía responsabilidades definidas, durante la implementación aparecieron diferencias en los nombres de métodos, los parámetros esperados y las relaciones entre objetos.

Por ejemplo, algunos métodos utilizados desde el módulo de operaciones todavía no existían en las clases del módulo de reservas.

Estas situaciones se identificaron mediante la compilación conjunta y se resolvieron incorporando los métodos necesarios y ajustando las referencias entre clases.

### 3.2. ERRORES DE COMPILACIÓN

Durante las pruebas de integración se detectaron errores relacionados con métodos inexistentes y referencias a paquetes incorrectos.

Uno de los casos fue la referencia a `GestorCancelaciones` desde un paquete distinto de aquel en el que se encontraba implementada la clase.

También fue necesario revisar métodos de `Reserva` utilizados desde `Salida`, así como incorporar una operación de cancelación que permitiera conectar ambos módulos.

Después de aplicar las correcciones, se realizó una nueva compilación conjunta del código Java disponible, que finalizó sin errores del compilador.

### 3.3. COORDINACIÓN MEDIANTE GIT Y GITHUB

Otra dificultad estuvo relacionada con el trabajo simultáneo sobre distintas ramas del repositorio.

Para evitar sobrescribir el trabajo de otros integrantes, se utilizaron ramas individuales y Pull Requests.

Además, se creó una rama temporal de integración para comprobar la compatibilidad del código antes de incorporar los cambios a la rama principal.

Esta experiencia permitió comprender la importancia del control de versiones y de revisar los cambios antes de integrarlos.

### 3.4. VERIFICACIÓN DE REGLAS DE NEGOCIO

La implementación de las reglas de negocio también presentó desafíos, especialmente en los cálculos de precios, las promociones, las condiciones de pago y los porcentajes de devolución.

Fue necesario considerar situaciones como las diferencias de tarifa según la edad, la aplicación de descuentos no acumulables, el pago de señas y las cancelaciones realizadas con distintos plazos de anticipación.

Estas reglas se distribuyeron entre clases específicas para evitar concentrar toda la lógica en una única entidad.

---

## 4. DISTRIBUCIÓN DE TAREAS

El equipo organizó el trabajo de la siguiente manera:

| INTEGRANTE | RESPONSABILIDADES |
|---|---|
| Carlos Daniel Lazo | Implementación de las clases relacionadas con el catálogo de tours, tarifas, actividades, proveedores, empleados, guías, vehículos y operaciones de la agencia. |
| Bianca Chancalay | Implementación del módulo de reservas, pasajeros, cálculo de precios, promociones, pagos y cancelaciones. Desarrollo de las clases correspondientes y ajustes de integración. |
| Guadalupe Olivera | Implementación de salidas, administración general de la agencia, persistencia de datos, excepciones y componentes de consulta e interfaz. |

La división permitió desarrollar las funcionalidades en paralelo y posteriormente realizar ajustes para conectar los módulos.

---

## 5. PRUEBAS Y RESULTADOS

Durante el desarrollo se realizaron pruebas individuales de determinadas funcionalidades y una comprobación de compilación conjunta.

### 5.1. PRUEBAS DE PAGOS

Se verificó el cálculo de importes para pagos en efectivo y pagos con tarjeta.

| CASO | RESULTADO ESPERADO | RESULTADO OBTENIDO |
|---|---|---|
| Pago de $100.000 en efectivo | $100.000 | $100.000 |
| Pago de $100.000 con tarjeta en tres cuotas | $107.000 | $107.000 |
| Aplicación del recargo del 7% | $7.000 adicionales | $7.000 adicionales |

Los resultados obtenidos coincidieron con los valores esperados en los escenarios ejecutados.

### 5.2. REGLAS DE CANCELACIÓN

Se implementaron los porcentajes de devolución establecidos en las reglas de negocio.

| ANTICIPACIÓN | DEVOLUCIÓN PREVISTA |
|---|---|
| 30 días o más | 90% |
| Entre 15 y 29 días | 50% |
| Entre 7 y 14 días | 20% |
| Menos de 7 días | 0% |
| Cancelación por parte de la agencia | 100% |

Estos porcentajes forman parte de la lógica implementada y se validan mediante el flujo integrado del controlador de reservas.

### 5.3. COMPILACIÓN CONJUNTA

Se realizó una prueba de integración del código Java disponible utilizando el compilador de Java.

Inicialmente se detectaron errores de compatibilidad entre clases y referencias a paquetes.

Después de incorporar las correcciones correspondientes, la compilación finalizó sin errores.

Este resultado permitió comprobar la compatibilidad del código a nivel de compilación, aunque no constituye por sí solo una validación funcional completa.

### 5.4. PRUEBAS DE INTEGRACIÓN

La prueba `ControladorReservasTest` cubre el flujo de alta de una reserva en `Agencia`, retención de cupos, cálculo de precio, registro de pago, confirmación y cancelación con liberación de cupos. También se verificó la ejecución de la aplicación mediante el menú de consola y la compilación conjunta de fuentes y pruebas.

---

## 6. REFLEXIÓN DEL GRUPO

El desarrollo de esta etapa permitió aplicar los conceptos de programación orientada a objetos a un sistema con múltiples entidades y reglas de negocio relacionadas entre sí.

Uno de los aprendizajes más importantes fue comprender que no alcanza con implementar correctamente cada clase de manera individual. También es necesario definir cómo se comunican los distintos componentes y verificar que sus métodos, parámetros y responsabilidades sean compatibles.

La división de tareas permitió avanzar en paralelo, pero también puso de manifiesto la importancia de mantener una comunicación constante entre los integrantes y de acordar previamente las interfaces entre módulos.

El uso de Git y GitHub resultó útil para organizar las modificaciones, conservar versiones del código y revisar los aportes antes de incorporarlos al proyecto principal.

En conjunto, esta etapa permitió adquirir experiencia tanto en la implementación de un sistema orientado a objetos como en la resolución de problemas habituales del desarrollo colaborativo.

---

## 7. CONCLUSIÓN

Durante la segunda etapa del proyecto Rumbo Sur se avanzó en la implementación de las clases correspondientes a los principales módulos del sistema.

Se desarrollaron funcionalidades vinculadas con tours, salidas, reservas, pasajeros, pagos, cancelaciones y operaciones de la agencia.

Asimismo, se realizaron ajustes respecto del diseño inicial para resolver incompatibilidades detectadas durante la integración.

Como resultado, se consiguió una compilación conjunta satisfactoria del código Java disponible.

Para completar la entrega, resta ejecutar y documentar las pruebas funcionales integrales, verificar la persistencia y confirmar el funcionamiento de la aplicación completa.

El trabajo realizado constituye una base para finalizar la integración y validar los casos de uso definidos para el sistema.
