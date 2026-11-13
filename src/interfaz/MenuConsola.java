package interfaz;

import controladores.ControladorOperaciones;
import controladores.GeneradorReportes;
import controladores.ControladorReservas;
import enumeraciones.CategoriaTour;
import enumeraciones.Temporada;
import enumeraciones.TipoVehiculo;
import enumeraciones.TipoProveedor;
import enumeraciones.TipoServicio;
import excepciones.ReglaNegocioException;
import modelo.Agencia;
import modelo.Actividad;
import modelo.Cancelacion;
import modelo.Cliente;
import modelo.Guia;
import modelo.Liquidacion;
import modelo.Pasajero;
import modelo.Proveedor;
import modelo.ServicioAdicional;
import modelo.Reserva;
import modelo.Salida;
import modelo.Tarifa;
import modelo.Tour;
import modelo.TourAventura;
import modelo.Pago;
import modelo.TourCultural;
import modelo.TourNaturaleza;
import modelo.Vehiculo;
import persistencia.RepositorioAgencia;
import enumeraciones.MedioPago;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Interfaz de consola. Solo muestra opciones, pide datos y delega en los
 * controladores y en el generador de reportes (no contiene reglas de negocio).
 */
public class MenuConsola {

    private final Agencia agencia;
    private final RepositorioAgencia repositorio;
    private final ControladorOperaciones operaciones;
    private final ControladorReservas reservas;
    private final GeneradorReportes reportes;
    private final Scanner entrada;

    public MenuConsola(Agencia agencia, RepositorioAgencia repositorio,
                       ControladorOperaciones operaciones, ControladorReservas reservas,
                       GeneradorReportes reportes, Scanner entrada) {
        this.agencia = agencia;
        this.repositorio = repositorio;
        this.operaciones = operaciones;
        this.reservas = reservas;
        this.reportes = reportes;
        this.entrada = entrada;
    }

    /** Bucle del menú principal. */
    public void mostrarMenu() {
        boolean salir = false;
        while (!salir) {
            System.out.println();
            System.out.println("========== RUMBO SUR - Gestión de agencia de turismo ==========");
            System.out.println("1. Tours y tarifas");
            System.out.println("2. Salidas (CU04 programar / CU05 estados)");
            System.out.println("3. Recursos y operación de salidas");
            System.out.println("4. Reservas");
            System.out.println("5. Reportes (CU24)");
            System.out.println("6. Cargar datos de ejemplo");
            System.out.println("0. Salir");
            switch (leerEntero("Opción: ")) {
                case 1: submenu(this::menuTours); break;
                case 2: submenu(this::menuSalidas); break;
                case 3: submenu(this::menuOperacion); break;
                case 4: submenu(this::menuReservas); break;
                case 5: submenu(this::menuReportes); break;
                case 6: submenu(this::cargarDatosEjemplo); break;
                case 0: salir = true; break;
                default: System.out.println("Opción inválida.");
            }
        }
        System.out.println("Los cambios se guardan automáticamente. ¡Hasta pronto!");
    }

    // ------------------------------ Tours ------------------------------

    private void menuTours() throws ReglaNegocioException {
        System.out.println("\n-- Catálogo de tours --");
        System.out.println("1. Listar tours\n2. Registrar tour\n3. Registrar tarifa\n"
                + "4. Modificar tour\n5. Agregar actividad al itinerario\n"
                + "6. Agregar servicio adicional\n7. Registrar proveedor\n8. Listar proveedores\n"
                + "9. Consultar itinerario y servicios de un tour\n0. Volver");
        switch (leerEntero("Opción: ")) {
            case 1:
                List<Tour> tours = operaciones.listarTours();
                if (tours.isEmpty()) System.out.println("No hay tours registrados.");
                for (Tour t : tours) {
                    System.out.println(t.getCodigo() + " | " + t.getNombre() + " | " + t.getCategoria()
                            + " | " + t.getDuracionDias() + " días | cupo " + t.getCupoMinimo() + "-" + t.getCupoMaximo()
                            + " | actividades: " + t.getItinerario().size() + " | tarifas: " + t.getTarifas().size());
                }
                break;
            case 2: registrarTour(); break;
            case 3: {
                String codigo = leerTexto("Código del tour: ");
                Temporada temporada = leerEnum("Temporada", Temporada.values());
                LocalDate desde = leerFecha("Vigencia desde");
                LocalDate hasta = leerFecha("Vigencia hasta");
                double precio = leerDecimal("Precio base por pasajero: ");
                operaciones.registrarTarifa(codigo, new Tarifa(temporada, desde, hasta, precio));
                System.out.println("Tarifa registrada.");
                break;
            }
            case 4: {
                String codigo = leerTexto("Código del tour a modificar: ");
                String nombre = leerTexto("Nuevo nombre: ");
                String descripcion = leerTexto("Nueva descripción: ");
                int dias = leerEntero("Duración en días: ");
                int minimo = leerEntero("Cupo mínimo: ");
                int maximo = leerEntero("Cupo máximo: ");
                operaciones.modificarTour(codigo, nombre, descripcion, dias, minimo, maximo);
                System.out.println("Tour modificado.");
                break;
            }
            case 5: registrarActividad(); break;
            case 6: registrarServicioAdicional(); break;
            case 7: registrarProveedor(); break;
            case 8:
                if (operaciones.listarProveedores().isEmpty()) System.out.println("No hay proveedores registrados.");
                for (Proveedor proveedor : operaciones.listarProveedores()) System.out.println(proveedor);
                break;
            case 9: consultarItinerario(); break;
            case 0: break;
            default: System.out.println("Opción inválida.");
        }
    }

    private void registrarActividad() throws ReglaNegocioException {
        String codigo = leerTexto("Código del tour: ");
        int dia = leerEntero("Día del itinerario: ");
        int orden = leerEntero("Orden de la actividad en el día: ");
        LocalTime horario;
        while (true) {
            try {
                horario = LocalTime.parse(leerTexto("Horario (HH:mm): "));
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Horario inválido. Use HH:mm, por ejemplo 09:30.");
            }
        }
        String descripcion = leerTexto("Descripción: ");
        String lugar = leerTexto("Lugar: ");
        String cuit = leerTexto("CUIT del proveedor (Enter si no corresponde): ");
        Proveedor proveedor = null;
        if (!cuit.isBlank()) {
            for (Proveedor p : operaciones.listarProveedores()) {
                if (p.getCuit().equals(cuit.replace("-", ""))) proveedor = p;
            }
            if (proveedor == null) throw new IllegalArgumentException("No existe un proveedor con ese CUIT.");
        }
        operaciones.agregarActividad(codigo, new Actividad(dia, orden, horario, descripcion, lugar, proveedor));
        System.out.println("Actividad agregada al itinerario.");
    }

    private void consultarItinerario() throws ReglaNegocioException {
        Tour tour = agencia.buscarTour(leerTexto("Código del tour: "));
        System.out.println("\nItinerario de " + tour.getNombre() + ":");
        if (tour.getItinerario().isEmpty()) System.out.println("No hay actividades cargadas.");
        for (Actividad actividad : tour.getItinerario()) System.out.println(" - " + actividad);
        System.out.println("Servicios adicionales:");
        if (tour.getServicios().isEmpty()) System.out.println("No hay servicios adicionales.");
        for (ServicioAdicional servicio : tour.getServicios()) {
            System.out.println(" - " + servicio.getNombre() + " | " + servicio.getTipo()
                    + " | $" + servicio.getPrecio() + (servicio.isObligatorio() ? " | obligatorio" : " | opcional"));
        }
    }

    private void registrarServicioAdicional() throws ReglaNegocioException {
        String codigo = leerTexto("Código del tour: ");
        String nombre = leerTexto("Nombre del servicio: ");
        double precio = leerDecimal("Precio por pasajero: ");
        TipoServicio tipo = leerEnum("Tipo de servicio", TipoServicio.values());
        boolean obligatorio = leerTexto("¿Es obligatorio? (s/n): ").equalsIgnoreCase("s");
        operaciones.agregarServicioAdicional(codigo, new ServicioAdicional(nombre, precio, tipo, obligatorio));
        System.out.println("Servicio adicional registrado.");
    }

    private void registrarProveedor() throws ReglaNegocioException {
        String nombre = leerTexto("Nombre del proveedor: ");
        String cuit = leerTexto("CUIT (11 dígitos): ");
        String contacto = leerTexto("Contacto: ");
        TipoProveedor tipo = leerEnum("Tipo de proveedor", TipoProveedor.values());
        operaciones.registrarProveedor(new Proveedor(nombre, cuit, contacto, tipo));
        System.out.println("Proveedor registrado.");
    }

    private void registrarTour() throws ReglaNegocioException {
        CategoriaTour categoria = leerEnum("Categoría", CategoriaTour.values());
        String codigo = leerTexto("Código: ");
        String nombre = leerTexto("Nombre: ");
        String descripcion = leerTexto("Descripción: ");
        int dias = leerEntero("Duración en días: ");
        int minimo = leerEntero("Cupo mínimo: ");
        int maximo = leerEntero("Cupo máximo: ");
        Tour tour;
        switch (categoria) {
            case AVENTURA:
                tour = new TourAventura(codigo, nombre, descripcion, dias, minimo, maximo,
                        leerEnum("Nivel de dificultad", enumeraciones.NivelDificultad.values()),
                        leerEntero("Edad mínima: "));
                break;
            case CULTURAL:
                tour = new TourCultural(codigo, nombre, descripcion, dias, minimo, maximo);
                break;
            default:
                tour = new TourNaturaleza(codigo, nombre, descripcion, dias, minimo, maximo);
        }
        operaciones.registrarTour(tour);
        System.out.println("Tour registrado.");
    }

    // ------------------------------ Salidas (CU04, CU05) ------------------------------

    private void menuSalidas() throws ReglaNegocioException {
        System.out.println("\n-- Salidas --");
        System.out.println("1. Listar salidas\n2. Programar salida (CU04)\n"
                + "3. Consultar y actualizar estado de una salida (CU05)\n0. Volver");
        switch (leerEntero("Opción: ")) {
            case 1: listarSalidas(); break;
            case 2:
                String codigo = leerTexto("Código del tour: ");
                LocalDate inicio = leerFecha("Fecha de inicio");
                LocalDate fin = leerFecha("Fecha de finalización");
                int cupo = leerEntero("Cupo de la salida: ");
                Salida salida = operaciones.programarSalida(codigo, inicio, fin, cupo);
                System.out.println("Salida #" + salida.getId() + " programada en estado " + salida.getEstado() + ".");
                break;
            case 3:
                long id = leerEntero("Número de salida: ");
                operaciones.actualizarEstadoSalida(id, LocalDate.now());
                mostrarSalida(agencia.buscarSalida(id));
                break;
            case 0: break;
            default: System.out.println("Opción inválida.");
        }
    }

    private void listarSalidas() {
        List<Salida> salidas = new ArrayList<>(agencia.getSalidas());
        if (salidas.isEmpty()) {
            System.out.println("No hay salidas registradas.");
            return;
        }
        Collections.sort(salidas);
        for (Salida s : salidas) mostrarSalida(s);
    }

    /** Muestra el estado y la disponibilidad de una salida (paso 5 de CU05). */
    private void mostrarSalida(Salida s) {
        LocalDate hoy = LocalDate.now();
        System.out.println("Salida #" + s.getId() + " | " + s.getTour().getNombre() + " | "
                + s.getFechaInicio() + " a " + s.getFechaFin() + " | Estado: " + s.getEstado()
                + " | Cupos: " + s.cuposOcupados(hoy) + "/" + s.getCupoMaximo()
                + " (disponibles: " + s.cuposDisponibles(hoy) + ")"
                + " | Guía: " + (s.getGuia() == null ? "sin asignar" : s.getGuia().obtenerNombreCompleto())
                + " | Vehículo: " + (s.getVehiculo() == null ? "sin asignar" : s.getVehiculo().getPatente()));
    }

    // ------------------------------ Operación ------------------------------

    private void menuOperacion() throws ReglaNegocioException {
        System.out.println("\n-- Recursos y operación --");
        System.out.println("1. Registrar vehículo\n2. Registrar guía\n3. Listar guías\n4. Listar vehículos\n"
                + "5. Asignar guía y vehículo\n6. Emitir lista de pasajeros\n7. Iniciar salida\n"
                + "8. Finalizar salida\n9. Liquidar honorarios del guía\n0. Volver");
        switch (leerEntero("Opción: ")) {
            case 1:
                operaciones.registrarVehiculo(new Vehiculo(leerTexto("Patente: "), leerEntero("Capacidad: "),
                        leerEnum("Tipo", TipoVehiculo.values())));
                System.out.println("Vehículo registrado.");
                break;
            case 2: registrarGuia(); break;
            case 3:
                if (operaciones.listarGuias().isEmpty()) System.out.println("No hay guías registrados.");
                for (Guia g : operaciones.listarGuias()) {
                    System.out.println(g + " | idiomas: " + g.getIdiomas() + " | categorías: "
                            + g.getCategoriasHabilitadas() + " | honorario diario: $" + g.getHonorarioPorDia());
                }
                break;
            case 4:
                if (operaciones.listarVehiculos().isEmpty()) System.out.println("No hay vehículos registrados.");
                for (Vehiculo v : operaciones.listarVehiculos()) {
                    System.out.println(v.getPatente() + " | " + v.getTipo() + " | capacidad " + v.getCapacidad());
                }
                break;
            case 5:
                operaciones.asignarRecursos(leerEntero("Número de salida: "), leerEntero("Legajo del guía: "),
                        leerTexto("Patente del vehículo: "));
                System.out.println("Recursos asignados.");
                break;
            case 6: {
                List<Pasajero> lista = operaciones.emitirListaPasajeros(leerEntero("Número de salida: "));
                System.out.println("Lista de pasajeros (" + lista.size() + "):");
                for (Pasajero pasajero : lista) {
                    System.out.println(" - " + pasajero.obtenerNombreCompleto() + " (" + pasajero.getDocumento() + ")");
                }
                break;
            }
            case 7:
                operaciones.iniciarSalida(leerEntero("Número de salida: "));
                System.out.println("Salida iniciada.");
                break;
            case 8:
                operaciones.finalizarSalida(leerEntero("Número de salida: "));
                System.out.println("Salida finalizada.");
                break;
            case 9: {
                Liquidacion liquidacion = operaciones.liquidarHonorarios(leerEntero("Número de salida: "), LocalDate.now());
                System.out.println("Honorarios liquidados a " + liquidacion.getGuia().obtenerNombreCompleto()
                        + ": $" + String.format(java.util.Locale.US, "%,.2f", liquidacion.getMonto()));
                break;
            }
            case 0: break;
            default: System.out.println("Opción inválida.");
        }
    }

    private void registrarGuia() throws ReglaNegocioException {
        int legajo = leerEntero("Legajo: ");
        String nombre = leerTexto("Nombre: ");
        String apellido = leerTexto("Apellido: ");
        String documento = leerTexto("Documento: ");
        String telefono = leerTexto("Teléfono: ");
        String idioma = leerTexto("Idioma principal: ");
        CategoriaTour categoria = leerEnum("Categoría habilitada", CategoriaTour.values());
        double honorario = leerDecimal("Honorario por día: ");
        operaciones.registrarGuia(new Guia(legajo, nombre, apellido, documento, telefono,
                java.util.Collections.singletonList(idioma), java.util.Collections.singletonList(categoria), honorario));
        System.out.println("Guía registrado.");
    }

    // ------------------------------ Reservas ------------------------------

    private void menuReservas() throws ReglaNegocioException {
        System.out.println("\n-- Reservas --");
        System.out.println("1. Listar reservas\n2. Registrar cliente y reserva\n3. Registrar pago\n"
                + "4. Cancelar reserva\n0. Volver");
        switch (leerEntero("Opción: ")) {
            case 1: listarReservas(); break;
            case 2: registrarReserva(); break;
            case 3: registrarPago(); break;
            case 4: cancelarReserva(); break;
            case 0: break;
            default: System.out.println("Opción inválida.");
        }
    }

    private void listarReservas() {
        if (agencia.getReservas().isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }
        for (Reserva r : agencia.getReservas()) {
            System.out.println("Reserva #" + r.getId() + " | " + r.getCliente().obtenerNombreCompleto()
                    + " | Salida #" + r.getSalida().getId() + " | " + r.getEstado()
                    + " | pasajeros: " + r.cantidadPasajeros()
                    + String.format(java.util.Locale.US, " | total $%,.2f | pagado $%,.2f | saldo $%,.2f",
                    r.getImporteTotal(), r.calcularTotalPagado(), r.calcularSaldoPendiente()));
        }
    }

    private void registrarReserva() throws ReglaNegocioException {
        String documento = leerTexto("Documento del cliente: ");
        Cliente cliente;
        try {
            cliente = agencia.buscarCliente(documento);
        } catch (ReglaNegocioException e) {
            cliente = reservas.registrarCliente(leerTexto("Nombre: "), leerTexto("Apellido: "),
                    documento, leerTexto("Teléfono: "), leerTexto("Correo: "), leerTexto("País: "));
        }
        long salidaId = leerEntero("Número de salida: ");
        int legajo = leerEntero("Legajo del vendedor: ");
        int cantidad = leerEntero("Cantidad de pasajeros: ");
        List<Pasajero> pasajeros = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            pasajeros.add(new Pasajero(leerTexto("Nombre del pasajero: "), leerTexto("Apellido: "),
                    leerTexto("Documento: "), leerTexto("Teléfono: "), leerFecha("Fecha de nacimiento"),
                    leerEnum("Tipo de pasajero", enumeraciones.TipoPasajero.values()),
                    leerTexto("Contacto de emergencia: "), leerTexto("Restricciones (Enter si no tiene): ")));
        }
        Reserva reserva = reservas.crearReserva(cliente.getDocumento(), salidaId, legajo, pasajeros);
        System.out.println("Reserva #" + reserva.getId() + " registrada en estado " + reserva.getEstado() + ".");
    }

    private void registrarPago() throws ReglaNegocioException {
        int id = leerEntero("Número de reserva: ");
        int idPago = leerEntero("Identificador del pago: ");
        double importe = leerDecimal("Importe: ");
        MedioPago medio = leerEnum("Medio de pago", MedioPago.values());
        int cuotas = medio == MedioPago.TARJETA ? leerEntero("Cuotas: ") : 1;
        Pago pago = reservas.registrarPago(id, idPago, importe, medio, cuotas);
        System.out.println("Pago registrado: $" + pago.calcularImporteFinal());
    }

    private void cancelarReserva() throws ReglaNegocioException {
        int id = leerEntero("Número de reserva: ");
        Cancelacion cancelacion = reservas.cancelarReserva(id, leerTexto("Motivo: "),
                LocalDate.now(), false);
        System.out.println("Cancelación registrada. Devolución: $" + cancelacion.getImporteDevuelto());
    }

    // ------------------------------ Reportes (CU24) ------------------------------

    private void menuReportes() {
        System.out.println("\n-- Reportes --");
        System.out.println("1. Ocupación por salida\n2. Ventas por vendedor\n3. Ingresos por tour\n4. Cancelaciones\n0. Volver");
        switch (leerEntero("Opción: ")) {
            case 1: System.out.println(reportes.reporteOcupacionPorSalida(LocalDate.now())); break;
            case 2: System.out.println(reportes.reporteVentasPorVendedor()); break;
            case 3: System.out.println(reportes.reporteIngresosPorTour()); break;
            case 4: System.out.println(reportes.reporteCancelaciones()); break;
            case 0: break;
            default: System.out.println("Opción inválida.");
        }
    }

    // ------------------------------ Datos de ejemplo ------------------------------

    private void cargarDatosEjemplo() throws ReglaNegocioException {
        if (!agencia.getTours().isEmpty() || !agencia.getSalidas().isEmpty()) {
            System.out.println("Ya hay datos cargados: los datos de ejemplo solo se cargan en una agencia vacía.");
            return;
        }
        DatosEjemplo.cargar(agencia, operaciones, LocalDate.now());
        repositorio.guardar(agencia);
        System.out.println("Datos de ejemplo cargados: tours, salidas, guías, vehículos, clientes y reservas.");
    }

    // ------------------------------ Entrada por consola ------------------------------

    /** Ejecuta una opción del menú sin que un error de carga o de negocio cierre el programa. */
    private void submenu(Accion accion) {
        try {
            accion.ejecutar();
        } catch (ReglaNegocioException e) {
            System.out.println("No se pudo completar la operación: " + e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Dato o estado inválido: " + e.getMessage());
        }
    }

    private interface Accion {
        void ejecutar() throws ReglaNegocioException;
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    private double leerDecimal(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).replace(',', '.');
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje + " (aaaa-mm-dd): ");
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida. Use el formato aaaa-mm-dd, por ejemplo 2026-12-20.");
            }
        }
    }

    private <E extends Enum<E>> E leerEnum(String nombre, E[] valores) {
        while (true) {
            StringBuilder opciones = new StringBuilder();
            for (int i = 0; i < valores.length; i++) {
                opciones.append(i + 1).append("=").append(valores[i]).append(i < valores.length - 1 ? ", " : "");
            }
            int opcion = leerEntero(nombre + " (" + opciones + "): ");
            if (opcion >= 1 && opcion <= valores.length) return valores[opcion - 1];
            System.out.println("Opción inválida.");
        }
    }
}
