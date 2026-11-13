package controladores;

import enumeraciones.EstadoSalida;
import excepciones.EntidadNoEncontradaException;
import excepciones.OperacionNoPermitidaException;
import excepciones.ReglaNegocioException;
import excepciones.RequisitosNoCumplidosException;
import modelo.Actividad;
import modelo.Agencia;
import modelo.Empleado;
import modelo.Guia;
import modelo.Liquidacion;
import modelo.Pasajero;
import modelo.Proveedor;
import modelo.Reserva;
import modelo.Salida;
import modelo.ServicioAdicional;
import modelo.Tarifa;
import modelo.Tour;
import modelo.Vehiculo;
import persistencia.RepositorioAgencia;
import modelo.GestorCancelaciones;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * Controlador GRASP de la gestión del catálogo y la operación (CU01-03 y
 * CU18-23).
 * Recibe las órdenes del menú, delega la lógica en las clases del modelo
 * y persiste los cambios a través de RepositorioAgencia.
 */
public class ControladorOperaciones {

    private final Agencia agencia;
    private final RepositorioAgencia repositorio;
    private final GestorCancelaciones gestorCancelaciones;

    public ControladorOperaciones(Agencia agencia, RepositorioAgencia repositorio,
                                  GestorCancelaciones gestorCancelaciones) {
        this.agencia = agencia;
        this.repositorio = repositorio;
        this.gestorCancelaciones = gestorCancelaciones;
    }

    // ===================== Catálogo (CU01, CU02, CU03, CU23) =====================

    /** CU01: alta de tour. El código no puede repetirse. */
    public void registrarTour(Tour tour) throws ReglaNegocioException {
        if (existeTour(tour.getCodigo())) {
            throw new OperacionNoPermitidaException("Ya existe un tour con el código " + tour.getCodigo());
        }
        agencia.agregarTour(tour);
        guardar();
    }

    /**
     * CU01: modificación. Si el tour ya tiene salidas, no se pueden cambiar
     * duración ni cupos.
     */
    public void modificarTour(String codigo, String nombre, String descripcion, int duracionDias,
                              int cupoMinimo, int cupoMaximo) throws ReglaNegocioException {
        Tour tour = agencia.buscarTour(codigo);
        boolean cambiaOperacion = duracionDias != tour.getDuracionDias()
                || cupoMinimo != tour.getCupoMinimo() || cupoMaximo != tour.getCupoMaximo();
        if (cambiaOperacion && tieneSalidas(tour)) {
            throw new OperacionNoPermitidaException(
                    "El tour tiene salidas programadas: solo se pueden modificar nombre y descripción");
        }
        tour.modificarDatos(nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
        guardar();
    }

    /** CU02: agrega una actividad al itinerario. */
    public void agregarActividad(String codigoTour, Actividad actividad) throws ReglaNegocioException {
        agencia.buscarTour(codigoTour).agregarActividad(actividad);
        guardar();
    }

    /** CU03: registra una tarifa por temporada. */
    public void registrarTarifa(String codigoTour, Tarifa tarifa) throws ReglaNegocioException {
        agencia.buscarTour(codigoTour).agregarTarifa(tarifa);
        guardar();
    }

    /** Servicios adicionales que ofrece el tour (S21). */
    public void agregarServicioAdicional(String codigoTour, ServicioAdicional servicio) throws ReglaNegocioException {
        agencia.buscarTour(codigoTour).agregarServicioAdicional(servicio);
        guardar();
    }

    /** CU23: alta de proveedor. El CUIT no puede repetirse. */
    public void registrarProveedor(Proveedor proveedor) throws ReglaNegocioException {
        for (Proveedor p : agencia.getProveedores()) {
            if (p.getCuit().equals(proveedor.getCuit())) {
                throw new OperacionNoPermitidaException("Ya existe un proveedor con el CUIT " + proveedor.getCuit());
            }
        }
        agencia.agregarProveedor(proveedor);
        guardar();
    }

    // ===================== Recursos (CU18) =====================

    public void registrarGuia(Guia guia) throws ReglaNegocioException {
        for (Empleado e : agencia.getEmpleados()) {
            if (e.getLegajo() == guia.getLegajo()) {
                throw new OperacionNoPermitidaException("Ya existe un empleado con el legajo " + guia.getLegajo());
            }
        }
        agencia.agregarEmpleado(guia);
        guardar();
    }

    public void registrarVehiculo(Vehiculo vehiculo) throws ReglaNegocioException {
        if (existeVehiculo(vehiculo.getPatente())) {
            throw new OperacionNoPermitidaException("Ya existe un vehículo con la patente " + vehiculo.getPatente());
        }
        agencia.agregarVehiculo(vehiculo);
        guardar();
    }

    // ========= Salidas y operación (CU04/05 por Salida, CU19-CU22) =========

    /** Crea una salida validando las condiciones del tour (S18, S20, RN19). */
    public Salida programarSalida(String codigoTour, LocalDate inicio, LocalDate fin, int cupo)
            throws ReglaNegocioException {
        if (inicio == null || fin == null || fin.isBefore(inicio) || inicio.isBefore(LocalDate.now())) {
            throw new OperacionNoPermitidaException("Las fechas deben ser válidas y la salida no puede comenzar en el pasado");
        }
        Tour tour = agencia.buscarTour(codigoTour);
        long dias = ChronoUnit.DAYS.between(inicio, fin) + 1;
        if (dias != tour.getDuracionDias()) {
            throw new OperacionNoPermitidaException("El tour dura " + tour.getDuracionDias()
                    + " días pero las fechas indicadas abarcan " + dias);
        }
        if (cupo < tour.getCupoMinimo() || cupo > tour.getCupoMaximo()) {
            throw new RequisitosNoCumplidosException("El cupo debe estar entre " + tour.getCupoMinimo()
                    + " y " + tour.getCupoMaximo() + " (límites del tour)");
        }
        tour.obtenerTarifa(inicio); // lanza excepción si no hay tarifa vigente en esa fecha (S20)
        if (tour.requiereSeguro() && !tour.tieneSeguroObligatorio()) {
            throw new RequisitosNoCumplidosException(
                    "El tour de aventura necesita un seguro de viaje obligatorio entre sus servicios");
        }
        Salida salida = new Salida(siguienteIdSalida(), tour, inicio, fin, cupo);
        agencia.agregarSalida(salida);
        guardar();
        return salida;
    }

    /**
     * CU05: actualiza el estado de la salida. Si queda cancelada por no alcanzar el
     * cupo mínimo,
     * cancela sus reservas vigentes como cancelación de la agencia, con devolución
     * total (S19).
     */
    public void actualizarEstadoSalida(long idSalida, LocalDate hoy) throws ReglaNegocioException {
        Salida salida = agencia.buscarSalida(idSalida);
        salida.verificarVencimientos(hoy);
        EstadoSalida antes = salida.getEstado();
        salida.actualizarEstado(hoy);
        if (antes != EstadoSalida.CANCELADA && salida.getEstado() == EstadoSalida.CANCELADA) {
            for (Reserva reserva : new ArrayList<>(salida.getReservas())) {
                if (reserva.puedeCancelarse()) {
                    gestorCancelaciones.cancelar(reserva, "Salida cancelada por cupo mínimo insuficiente", true, hoy);
                }
            }
        }
        guardar();
    }

    /**
     * CU19: el guía y el vehículo se validan dentro de Salida (experto en sus
     * fechas, tour y cupo).
     */
    public void asignarRecursos(long idSalida, int legajoGuia, String patente) throws ReglaNegocioException {
        Salida salida = agencia.buscarSalida(idSalida);
        Empleado empleado = agencia.buscarEmpleado(legajoGuia);
        if (!(empleado instanceof Guia)) {
            throw new RequisitosNoCumplidosException("El legajo " + legajoGuia + " no corresponde a un guía");
        }
        Vehiculo vehiculo = agencia.buscarVehiculo(patente);
        Guia guia = (Guia) empleado;
        // Se valida el vehículo antes de modificar la asociación del guía,
        // para evitar una asignación parcial si el vehículo no está disponible.
        if (vehiculo.getCapacidad() < salida.getCupoMaximo()) {
            throw new RequisitosNoCumplidosException("El vehículo no alcanza la capacidad requerida");
        }
        for (Salida otra : agencia.getSalidas()) {
            if (otra == salida || otra.getEstado() == EstadoSalida.CANCELADA) continue;
            boolean seSuperpone = !otra.getFechaFin().isBefore(salida.getFechaInicio())
                    && !otra.getFechaInicio().isAfter(salida.getFechaFin());
            if (seSuperpone && otra.getVehiculo() != null
                    && otra.getVehiculo().getPatente().equalsIgnoreCase(vehiculo.getPatente())) {
                throw new RequisitosNoCumplidosException("El vehículo ya está asignado a otra salida en esas fechas");
            }
        }
        salida.asignarGuia(guia);
        salida.asignarVehiculo(vehiculo);
        guardar();
    }

    /** CU20 */
    public List<Pasajero> emitirListaPasajeros(long idSalida) throws ReglaNegocioException {
        return emitirListaPasajeros(idSalida, LocalDate.now());
    }

    /**
     * Antes de emitir la lista se actualizan vencimientos y el mínimo de pasajeros.
     * Así no se emite una lista para una salida que ya debe cancelarse (RN06/S19).
     */
    public List<Pasajero> emitirListaPasajeros(long idSalida, LocalDate hoy) throws ReglaNegocioException {
        actualizarEstadoSalida(idSalida, hoy);
        Salida salida = agencia.buscarSalida(idSalida);
        if (salida.getEstado() == EstadoSalida.CANCELADA) {
            throw new OperacionNoPermitidaException("No se puede emitir la lista: la salida está cancelada");
        }
        List<Pasajero> lista = salida.emitirLista();
        guardar();
        return lista;
    }

    /**
     * CU21: actualiza primero el estado de negocio y luego valida la lista,
     * el guía habilitado y el vehículo con capacidad (RN06, RN30).
     */
    public void iniciarSalida(long idSalida) throws ReglaNegocioException {
        iniciarSalida(idSalida, LocalDate.now());
    }

    /** Sobrecarga con fecha explícita para que las reglas temporales sean comprobables. */
    public void iniciarSalida(long idSalida, LocalDate hoy) throws ReglaNegocioException {
        actualizarEstadoSalida(idSalida, hoy);
        agencia.buscarSalida(idSalida).iniciar();
        guardar();
    }

    /** CU21: pasa la salida a finalizada y finaliza sus reservas vigentes. */
    public void finalizarSalida(long idSalida) throws ReglaNegocioException {
        agencia.buscarSalida(idSalida).finalizar();
        guardar();
    }

    /**
     * CU22: honorarios del guía = días x honorario diario + 5 % de los adicionales
     * (RN32).
     */
    public Liquidacion liquidarHonorarios(long idSalida, LocalDate hoy) throws ReglaNegocioException {
        Salida salida = agencia.buscarSalida(idSalida);
        if (salida.getEstado() != EstadoSalida.FINALIZADA) {
            throw new OperacionNoPermitidaException("Solo se puede liquidar una salida finalizada");
        }
        Guia guia = salida.getGuia();
        if (guia == null) {
            throw new OperacionNoPermitidaException("La salida no tiene guía asignado");
        }
        if (salida.getLiquidacion() != null) {
            throw new OperacionNoPermitidaException("La salida ya fue liquidada");
        }
        double monto = guia.calcularHonorarios(salida);
        Liquidacion liquidacion = new Liquidacion(salida, guia, hoy, salida.getTour().getDuracionDias(), monto);
        salida.registrarLiquidacion(liquidacion);
        guardar();
        return liquidacion;
    }

    // ===================== Consultas para el menú =====================

    /** Tours ordenados por nombre: ordenamiento personalizado con Comparator. */
    public List<Tour> listarTours() {
        List<Tour> tours = new ArrayList<>(agencia.getTours());
        Collections.sort(tours, new Comparator<Tour>() {
            @Override
            public int compare(Tour primero, Tour segundo) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        primero.getNombre(), segundo.getNombre());
            }
        });
        return tours;
    }

    public List<Guia> listarGuias() {
        List<Guia> guias = new ArrayList<>();
        for (Empleado e : agencia.getEmpleados()) {
            if (e instanceof Guia) {
                guias.add((Guia) e);
            }
        }
        return guias;
    }

    public List<Vehiculo> listarVehiculos() {
        return new ArrayList<>(agencia.getVehiculos());
    }

    public List<Proveedor> listarProveedores() {
        return new ArrayList<>(agencia.getProveedores());
    }

    // ===================== Auxiliares privados =====================

    private boolean existeTour(String codigo) {
        try {
            agencia.buscarTour(codigo);
            return true;
        } catch (EntidadNoEncontradaException e) {
            return false;
        }
    }

    private boolean existeVehiculo(String patente) {
        try {
            agencia.buscarVehiculo(patente);
            return true;
        } catch (EntidadNoEncontradaException e) {
            return false;
        }
    }

    private boolean tieneSalidas(Tour tour) {
        for (Salida s : agencia.getSalidas()) {
            if (s.getTour() == tour) {
                return true;
            }
        }
        return false;
    }

    private long siguienteIdSalida() {
        long maximo = 0;
        for (Salida s : agencia.getSalidas()) {
            maximo = Math.max(maximo, s.getId());
        }
        return maximo + 1;
    }

    /**
     * Persiste el estado de la agencia. Un fallo de E/S se informa como error de
     * ejecución.
     */
    private void guardar() {
        try {
            repositorio.guardar(agencia);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudieron guardar los datos: " + e.getMessage(), e);
        }
    }
}