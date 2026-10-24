# Rumbo Sur – Sistema de Gestión para Agencia de Turismo

**Trabajo Práctico Obligatorio – Programación Orientada a Objetos**

| | |
|---|---|
| **Propuesta elegida** | B – Agencia de Turismo |
| **Lenguaje** | Java 17 o superior |
| **Modelado** | UML |
| **Estado actual** | Etapa 1 finalizada – Etapa 2 en preparación |
| **Entrega Etapa 1** | 25/10/2026 (tag `etapa-1`) |
| **Entrega Etapa 2** | 04/11/2026 (tag `etapa-2`) |
| **Defensa** | 11/11/2026 |

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

El sistema cubre, como mínimo, los siguientes procesos definidos en la propuesta:

- Administración del catálogo de tours (itinerario, duración y tarifas) y programación de salidas con fecha y cupo.
- Registro de clientes y creación de reservas con uno o más pasajeros.
- Cálculo del precio según tipo de pasajero, temporada y promociones; registro de la seña y de los pagos del saldo.
- Contratación de servicios adicionales (excursiones opcionales, seguro de viaje, traslados).
- Cancelación y modificación de reservas aplicando la política de cancelación.
- Asignación de guía y vehículo a cada salida y emisión de la lista de pasajeros.
- Liquidación de honorarios de guías.
- Reportes: ocupación por salida, ventas por vendedor, ingresos por tour y cancelaciones.

El detalle completo se encuentra en los documentos de análisis (ver [sección 5](#5-documentación)): 26 requerimientos funcionales (RF01–RF26), 35 reglas de negocio (RN01–RN35), 17 supuestos (S01–S17) y 27 casos de uso (CU01–CU27).

## 3. Actores del sistema

| Actor | Responsabilidad principal |
|---|---|
| **Administrador** | Catálogo de tours, itinerarios, tarifas, proveedores y reportes. |
| **Vendedor** | Clientes, reservas, precios, pagos y cancelaciones. |
| **Coordinador de operaciones** | Salidas, asignación de recursos, listas de pasajeros y liquidación de honorarios. |
| **Guía de turismo** | Consulta de las salidas que tiene asignadas (supuesto S01). |

El cliente y los proveedores participan de los procesos del negocio, pero no interactúan directamente con el software en esta versión.

## 4. Diseño de la solución

### 4.1. Modelado UML

- **Casos de uso:** cuatro diagramas por módulo (administración, reservas, pagos y operaciones), con relaciones *include* y *extend*, y una ficha textual por cada caso de uso.
- **Diagrama de clases inicial:** incluye las jerarquías `Persona` (`Empleado`, `Cliente`, `Pasajero`) y `Tour` (`TourAventura`, `TourCultural`, `TourNaturaleza`), con sobreescritura de métodos.
- **Diagramas de secuencia:** CU08 *Crear reserva* (con flujos alternativos por cupos insuficientes y requisitos no cumplidos) y CU15 *Cancelar reserva* (con política de devolución y reserva no cancelable).
- **Diagrama de clases final:** incorpora los cambios derivados de la aplicación de patrones, con su nota de cambios.

### 4.2. Principios y patrones aplicados

| Tipo | Aplicación | Clases principales |
|---|---|---|
| SOLID – SRP | Se separan las responsabilidades de reserva, cálculo de precios, pagos y cancelaciones. | `Reserva`, `CalculadoraPrecioReserva`, `GestorPagos`, `GestorCancelaciones` |
| SOLID – OCP | Las promociones se incorporan sin modificar el algoritmo de cálculo. | `Promocion`, `PromocionAnticipada`, `PromocionGrupo` |
| SOLID – DIP | La lógica depende de una abstracción de almacenamiento y no de archivos concretos. | `RepositorioReservas`, `RepositorioReservasArchivo` |
| GRASP – Experto en Información | Cada clase resuelve lo que conoce (cupos en `Salida`, saldo en `Reserva`). | `Salida`, `Reserva` |
| GRASP – Creador | `Reserva` incorpora y administra a sus pasajeros. | `Reserva`, `Pasajero` |
| GRASP – Controlador | Se separa la interfaz de la lógica de los casos de uso. | `MenuConsola`, `ControladorReservas` |
| GRASP – Fabricación Pura | Clases creadas para ubicar cálculos y políticas que no pertenecen al dominio. | `CalculadoraPrecioReserva`, `GestorPagos`, `GestorCancelaciones` |

La justificación completa de cada decisión (problema detectado, decisión adoptada y ventaja) se encuentra en [`docs/solid-grasp.md`](docs/solid-grasp.md).

## 5. Documentación

| Documento | Contenido |
|---|---|
| [`docs/Documento_Diseno_Etapa1_RumboSur.pdf`](docs/Documento_Diseno_Etapa1_RumboSur.pdf) | Documento de diseño de la Etapa 1 (entregable). |
| [`docs/enunciado-propuesta-b.md`](docs/enunciado-propuesta-b.md) | Enunciado de la propuesta elegida (Propuesta B). |
| [`docs/analisis-enunciado.md`](docs/analisis-enunciado.md) | Análisis del enunciado, actores y alcance. |
| [`docs/requerimientos-funcionales.md`](docs/requerimientos-funcionales.md) | Requerimientos funcionales RF01–RF26. |
| [`docs/reglas-negocio.md`](docs/reglas-negocio.md) | Reglas de negocio RN01–RN35. |
| [`docs/supuestos-adoptados.md`](docs/supuestos-adoptados.md) | Supuestos S01–S17. |
| [`docs/casos-de-uso.md`](docs/casos-de-uso.md) | Diagramas y fichas de casos de uso, y matriz de trazabilidad. |
| [`docs/solid-grasp.md`](docs/solid-grasp.md) | Aplicación y fundamentación de SOLID y GRASP. |
| [`docs/nota-de-cambios.md`](docs/nota-de-cambios.md) | Justificación de los cambios entre el diagrama de clases inicial y el final. |
| [`docs/uml/`](docs/uml) | Diagramas UML (imagen exportada y fuente PlantUML) y nota de cambios. |

## 6. Estructura del repositorio

```text
.
├── docs/                 Documentación del proyecto
│   └── uml/              Diagramas UML (.puml y .png)
├── src/                  Código fuente Java (Etapa 2)
├── test/                 Pruebas unitarias (Etapa 2)
└── README.md
```

> Las carpetas `src/` y `test/` se incorporarán durante la Etapa 2.

## 7. Tecnologías y herramientas

- **Lenguaje:** Java 17 o superior.
- **Paradigma:** Programación Orientada a Objetos.
- **Modelado:** UML con PlantUML.
- **Persistencia:** lectura y escritura de archivos.
- **Interfaz:** consola con menú (a confirmar en la Etapa 2).
- **Control de versiones:** Git y GitHub.

## 8. Instalación y ejecución

> **Pendiente – Etapa 2.** Esta sección se completará con los requisitos, los pasos de compilación y ejecución, los datos de prueba y las instrucciones para correr las pruebas unitarias una vez que el código esté disponible.

## 9. Metodología de trabajo

El proyecto se desarrolla de forma colaborativa en GitHub:

- La rama `main` contiene únicamente versiones revisadas.
- Los cambios se desarrollan en ramas de trabajo y se integran mediante *pull requests* revisados por al menos otro integrante.
- Los *commits* tienen mensajes descriptivos.
- Cada etapa se entrega marcada con un *tag* (`etapa-1`, `etapa-2`).

## 10. Plan de entregas y estado

| Etapa | Contenido | Entrega | Estado |
|---|---|---|---|
| **Etapa 1** | Análisis, casos de uso, diagramas de clases y secuencia, SOLID y GRASP. | 25/10/2026 | Finalizada |
| **Etapa 2** | Codificación, pruebas unitarias y funcionales, documentación final. | 04/11/2026 | En preparación |
| **Defensa** | Presentación oral y demostración del sistema. | 11/11/2026 | Pendiente |

## 11. Integrantes

- Bianca Chancalay
- Carlos Daniel Lazo
- Guadalupe Olivera
