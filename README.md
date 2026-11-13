# Rumbo Sur – Sistema de Gestión para Agencia de Turismo

**Trabajo Práctico Obligatorio – Programación Orientada a Objetos**

| | |
|---|---|
| **Propuesta elegida** | B – Agencia de Turismo |
| **Lenguaje** | Java 17 o superior |
| **Modelado** | UML |
| **Estado actual** | Etapas 1 y 2 implementadas y verificadas |
| **Entrega Etapa 1** | 25/10/2026 (tag `etapa-1`) |
| **Entrega Etapa 2** | 12/11/2026 (tag `etapa-2`) |
| **Defensa** | Según cronograma de la cátedra |

---

## Tabla de contenidos

1. [Descripción del proyecto](#1-descripción-del-proyecto)
2. [Alcance funcional](#2-alcance-funcional)
3. [Actores del sistema](#3-actores-del-sistema)
4. [Diseño de la solución](#4-diseño-de-la-solución)
5. [Documentación](#5-documentación)
6. [Estructura del repositorio](#6-estructura-del-repositorio)
7. [Tecnologías y herramientas](#7-tecnologías-y-herramientas)
8. [Instalación y ejecución](#8-instalación-y-ejecución)
9. [Metodología de trabajo](#9-metodología-de-trabajo)
10. [Plan de entregas y estado](#10-plan-de-entregas-y-estado)
11. [Integrantes](#11-integrantes)

---

## 1. Descripción del proyecto

**Rumbo Sur** es una agencia de turismo receptivo que organiza tours y excursiones con salidas programadas. Actualmente administra reservas, cupos y pagos mediante planillas, lo que dificulta el control de la información y el seguimiento de la operación.

Este proyecto propone un sistema de software que centraliza la venta, la operación de cada salida y la liquidación a guías, aplicando los conceptos de Programación Orientada a Objetos vistos en la materia: modelado con UML, principios SOLID, patrones GRASP y construcción, prueba y documentación de un programa en Java.

## 2. Alcance funcional

El alcance funcional previsto incluye los siguientes procesos:

- Administración del catálogo de tours (itinerario, duración y tarifas) y programación de salidas con fecha y cupo.
- Registro de clientes y creación de reservas con uno o más pasajeros.
- Cálculo del precio según tipo de pasajero, temporada y promociones; registro de la seña y de los pagos del saldo.
- Contratación de servicios adicionales (excursiones opcionales, seguro de viaje, traslados).
- Cancelación y modificación de reservas aplicando la política de cancelación.
- Asignación de guía y vehículo a cada salida y emisión de la lista de pasajeros.
- Liquidación de honorarios de guías.
- Reportes: ocupación por salida, ventas por vendedor, ingresos por tour y cancelaciones.

El detalle completo se encuentra en los documentos de análisis (ver [sección 5](#5-documentación)): 24 requerimientos funcionales (RF01–RF24), 33 reglas de negocio (RN01–RN33), 22 supuestos (S01–S22) y 24 casos de uso (CU01–CU24).

## 3. Actores del sistema

| Actor | Responsabilidad principal |
|---|---|
| **Administrador** | Catálogo de tours, itinerarios, tarifas, proveedores y reportes. |
| **Vendedor** | Clientes, reservas, precios, pagos y cancelaciones. |
| **Coordinador de operaciones** | Salidas, asignación de recursos, listas de pasajeros y liquidación de honorarios. |

El cliente, el guía de turismo y los proveedores participan de los procesos del negocio, pero no interactúan directamente con el software en esta versión.

## 4. Diseño de la solución

### 4.1. Modelado UML

- **Casos de uso:** cuatro diagramas por módulo (administración, reservas, pagos y operaciones), con relaciones «include» y una relación «extend» (los servicios adicionales opcionales, CU11, extienden la creación de una reserva, CU08), y escenarios alternativos descriptos en las fichas.
- **Diagrama de clases inicial:** incluye las jerarquías `Persona` (`Empleado`, `Cliente`, `Pasajero`) y `Tour` (`TourAventura`, `TourCultural`, `TourNaturaleza`), con sobreescritura de métodos.
- **Diagramas de secuencia:** CU08 *Crear reserva* (con flujos alternativos por cupos insuficientes y requisitos no cumplidos) y CU15 *Cancelar reserva* (con política de devolución y reserva no cancelable).
- **Diagrama de clases final:** incorpora los cambios derivados de la aplicación de patrones, con su nota de cambios.

### 4.2. Principios y patrones propuestos en el diseño

| Tipo | Aplicación | Clases principales |
|---|---|---|
| SOLID – SRP | Se separan las responsabilidades de reserva, cálculo de precios, pagos y cancelaciones. | `Reserva`, `CalculadoraPrecioReserva`, `GestorPagos`, `GestorCancelaciones` |
| SOLID – OCP | Las promociones se incorporan sin modificar el algoritmo de cálculo. | `Promocion`, `PromocionAnticipada`, `PromocionGrupo` |
| SOLID – DIP | La lógica depende de una abstracción de almacenamiento y no de archivos concretos. | `RepositorioAgencia`, `RepositorioAgenciaArchivo`, `Agencia` |
| GRASP – Experto en Información | Cada clase resuelve lo que conoce (cupos en `Salida`, saldo en `Reserva`). | `Salida`, `Reserva` |
| GRASP – Creador | `Reserva` incorpora y administra a sus pasajeros. | `Reserva`, `Pasajero` |
| GRASP – Controlador | Coordina las operaciones de los casos de uso y delega responsabilidades. | `ControladorReservas`, `ControladorOperaciones` |
| GRASP – Fabricación Pura | Los cálculos y políticas que no pertenecen a una clase del negocio se ubican en clases propias. | `CalculadoraPrecioReserva`, `GestorPagos`, `GestorCancelaciones` |

La justificación completa de cada decisión (problema detectado, decisión adoptada y ventaja) se encuentra en [`docs/diseno/solid-grasp.md`](docs/diseno/solid-grasp.md).

## 5. Documentación

### Análisis y requerimientos

| Documento | Contenido |
|---|---|
| [`docs/analisis/analisis-enunciado.md`](docs/analisis/analisis-enunciado.md) | Análisis del enunciado, actores y alcance. |
| [`docs/analisis/requerimientos-funcionales.md`](docs/analisis/requerimientos-funcionales.md) | Requerimientos funcionales RF01–RF24. |
| [`docs/analisis/reglas-negocio.md`](docs/analisis/reglas-negocio.md) | Reglas de negocio RN01–RN33. |
| [`docs/analisis/supuestos-adoptados.md`](docs/analisis/supuestos-adoptados.md) | Supuestos S01–S22. |
| [`docs/analisis/casos-de-uso.md`](docs/analisis/casos-de-uso.md) | Diagramas y fichas de casos de uso, y matriz de trazabilidad. |

### Diseño y diagramas

| Documento | Contenido |
|---|---|
| [`docs/diseno/solid-grasp.md`](docs/diseno/solid-grasp.md) | Aplicación y fundamentación de SOLID y GRASP. |
| [`docs/uml/`](docs/uml) | Diagramas UML (imagen exportada y fuente PlantUML) y nota de cambios. |
| [`docs/Documento de diseño.pdf`](docs/Documento%20de%20dise%C3%B1o.pdf) | Entregable de diseño en formato PDF. |

## 6. Estructura del repositorio

```text
.
├── docs/                 Documentación del proyecto
│   ├── analisis/         Análisis, requerimientos, reglas, supuestos y casos de uso
│   ├── diseno/           Fundamentación de SOLID y GRASP
│   ├── uml/              Diagramas UML (.puml, .png, .svg y .pdf)
│   └── Documento de diseño.pdf
├── src/                  Código fuente Java (Etapa 2)
├── test/                 Pruebas unitarias (Etapa 2)
└── README.md
```

Las carpetas `src/` y `test/` contienen la implementación y las pruebas de la Etapa 2.

## 7. Tecnologías y herramientas

- **Lenguaje:** Java 17 o superior.
- **Paradigma:** Programación Orientada a Objetos.
- **Modelado:** UML con PlantUML.
- **Persistencia:** lectura y escritura de un archivo mediante serialización de objetos Java (S07).
- **Interfaz:** consola con menú, tal como confirmó la profesora.
- **Control de versiones:** Git y GitHub.

## 8. Instalación y ejecución

Se requiere JDK 17 o superior. Desde la carpeta raíz del proyecto:

**PowerShell (Windows):**

```powershell
New-Item -ItemType Directory -Force out
javac -encoding UTF-8 -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object FullName)
java -cp out Main
```

**Pruebas:**

```powershell
New-Item -ItemType Directory -Force out-test
javac -encoding UTF-8 -cp out -d out-test (Get-ChildItem -Path test -Recurse -Filter *.java | ForEach-Object FullName)
java -cp "out;out-test" modelo.AgenciaTest
java -cp "out;out-test" modelo.SalidaTest
java -cp "out;out-test" persistencia.RepositorioAgenciaArchivoTest
java -cp "out;out-test" controladores.GeneradorReportesTest
java -cp "out;out-test" controladores.ControladorOperacionesTest
java -cp "out;out-test" controladores.ControladorReservasTest
```

Los cambios de catálogo y operación se guardan mediante el repositorio de archivos en `datos/agencia.dat`. Si el archivo existente no puede leerse, `Main` detiene el inicio para evitar sobrescribirlo accidentalmente.

## 9. Metodología de trabajo

El proyecto se desarrolla de forma colaborativa en GitHub:

- La rama `main` contiene únicamente versiones revisadas.
- Los cambios se desarrollan en ramas de trabajo y se integran mediante *pull requests* revisados por al menos otro integrante.
- Los *commits* tienen mensajes descriptivos.
- Para identificar cada entrega, se prevé crear un *tag* (`etapa-1`, `etapa-2`).

## 10. Plan de entregas y estado

| Etapa | Contenido | Entrega | Estado |
|---|---|---|---|
| **Etapa 1** | Análisis, casos de uso, diagramas de clases y secuencia, SOLID y GRASP. | 25/10/2026 | Finalizada |
| **Etapa 2** | Codificación, pruebas unitarias y funcionales, documentación final. | 12/11/2026 | Implementada y verificada |
| **Defensa** | Presentación oral y demostración del sistema. | Según cronograma de la cátedra | Pendiente |

## 11. Integrantes

- Bianca Chancalay
- Carlos Daniel Lazo
- Guadalupe Olivera


## 12. Implementación y pruebas — Etapa 2

### Implementación

Rumbo Sur está implementado en Java 17. El código se organiza en `modelo`, `controladores`, `enumeraciones`, `excepciones`, `interfaz` y `persistencia`. La interfaz de consola permite administrar catálogo, tarifas, itinerarios, proveedores, guías, vehículos, salidas, operación y reportes.

La persistencia utiliza serialización de objetos Java en `datos/agencia.dat`. Los cambios realizados mediante los controladores se guardan automáticamente. Si el archivo no puede leerse, el programa detiene el inicio para evitar sobrescribir datos dañados.

### Pruebas automatizadas ejecutadas

| Prueba | Resultado |
|---|---|
| `AgenciaTest` | OK |
| `SalidaTest` | OK |
| `RepositorioAgenciaArchivoTest` | OK |
| `GeneradorReportesTest` | OK |
| `ControladorOperacionesTest` | OK |
| `ControladorReservasTest` | OK |

Además, se ejecutó una prueba de humo del menú: carga de datos de ejemplo, generación de los cuatro reportes y comprobación de que el archivo de persistencia se crea correctamente.

### Estado de integración

La compilación conjunta no presenta errores. Los casos CU04, CU05 y CU24 cuentan con implementación y pruebas específicas. La interfaz también incorpora opciones para completar el catálogo y administrar recursos operativos.

El `ControladorReservas` utiliza la raíz `Agencia` y el mismo `RepositorioAgencia` que el módulo operativo. El menú permite registrar clientes y reservas, calcular precios, registrar pagos, cancelar reservas y consultar sus saldos.
