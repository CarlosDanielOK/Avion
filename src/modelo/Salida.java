package modelo;

import enumeraciones.EstadoSalida;
import excepciones.OperacionNoPermitidaException;
import excepciones.RequisitosNoCumplidosException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Salida programada de un tour; conoce sus cupos, recursos y reservas. */
public class Salida implements Serializable, Comparable<Salida> {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final Tour tour;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final int cupoMaximo;
    private EstadoSalida estado;
    private boolean listaEmitida;
    private Guia guia;
    private Vehiculo vehiculo;
    private Liquidacion liquidacion;
    private final List<Reserva> reservas = new ArrayList<>();

    public Salida(long id, Tour tour, LocalDate fechaInicio, LocalDate fechaFin, int cupoMaximo) {
        if (id <= 0 || tour == null || fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Id, tour y fechas son obligatorios");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de finalización no puede ser anterior al inicio");
        }
        if (cupoMaximo < tour.getCupoMinimo() || cupoMaximo > tour.getCupoMaximo()) {
            throw new IllegalArgumentException("El cupo debe respetar los límites del tour");
        }
        this.id = id;
        this.tour = tour;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupoMaximo = cupoMaximo;
        this.estado = EstadoSalida.PROGRAMADA;
    }

    /** Incluye reservas pendientes vigentes y confirmadas, pero no anuladas. */
    public int cuposOcupados(LocalDate hoy) {
        verificarVencimientos(hoy);
        int ocupados = 0;
        for (Reserva reserva : reservas) {
            if (reserva.ocupaCupos(hoy)) ocupados += reserva.cantidadPasajeros();
        }
        return ocupados;
    }

    public int cuposDisponibles(LocalDate hoy) {
        return Math.max(0, cupoMaximo - cuposOcupados(hoy));
    }

    /** Libera los cupos retenidos por reservas cuyo plazo de pago ya venció. */
    public void verificarVencimientos(LocalDate hoy) {
        if (hoy == null) throw new IllegalArgumentException("La fecha es obligatoria");
        for (Reserva reserva : reservas) {
            if (reserva.estaVencida(fechaInicio, hoy)) {
                reserva.anularPorVencimiento(fechaInicio, hoy);
                listaEmitida = false;
            }
        }
    }

    public boolean puedeParticipar(DatosPasajero datos) {
        return tour.puedeParticipar(datos, fechaInicio);
    }

    public Tarifa obtenerTarifa() throws excepciones.EntidadNoEncontradaException {
        return tour.obtenerTarifa(fechaInicio);
    }

    public long diasHastaInicio(LocalDate hoy) {
        return ChronoUnit.DAYS.between(hoy, fechaInicio);
    }

    public boolean exigePagoTotal(LocalDate hoy) {
        return diasHastaInicio(hoy) <= 10;
    }

    public double totalServiciosAdicionales() {
        double total = 0;
        for (Reserva reserva : reservas) {
            if (reserva.getEstado() == enumeraciones.EstadoReserva.CONFIRMADA
                    || reserva.getEstado() == enumeraciones.EstadoReserva.FINALIZADA) {
                total += reserva.totalServiciosAdicionales();
            }
        }
        return total;
    }

    public void agregarReserva(Reserva reserva) throws OperacionNoPermitidaException {
        if (reserva == null) throw new IllegalArgumentException("La reserva es obligatoria");
        if (reserva.getSalida() != this) {
            throw new OperacionNoPermitidaException("La reserva pertenece a otra salida");
        }
        if (reservas.contains(reserva)) {
            throw new OperacionNoPermitidaException("La reserva ya está asociada a esta salida");
        }
        LocalDate hoy = LocalDate.now();
        verificarVencimientos(hoy);
        if (estado == EstadoSalida.CANCELADA || estado == EstadoSalida.FINALIZADA
                || estado == EstadoSalida.EN_CURSO) {
            throw new OperacionNoPermitidaException("No se admiten reservas para una salida " + estado);
        }
        if (estado == EstadoSalida.COMPLETA) {
            // Si se liberó un lugar, vuelve a calcular el estado antes de admitir otra reserva.
            actualizarEstado(hoy);
            if (estado == EstadoSalida.CANCELADA) {
                throw new OperacionNoPermitidaException("La salida fue cancelada por no alcanzar el cupo mínimo");
            }
        }
        if (reserva.cantidadPasajeros() > Math.max(0, cupoMaximo - cuposOcupados(hoy))) {
            throw new OperacionNoPermitidaException("No hay cupos suficientes para la reserva");
        }
        reservas.add(reserva);
        listaEmitida = false;
        actualizarEstado(hoy);
    }

    public void asignarGuia(Guia guia) throws RequisitosNoCumplidosException {
        if (guia == null || !guia.estaHabilitadoPara(tour.getCategoria())) {
            throw new RequisitosNoCumplidosException("El guía no está habilitado para la categoría del tour");
        }
        if (!guia.estaDisponible(fechaInicio, fechaFin)) {
            throw new RequisitosNoCumplidosException("El guía ya tiene una salida superpuesta");
        }
        this.guia = guia;
        guia.agregarSalida(this);
    }

    public void asignarVehiculo(Vehiculo vehiculo) throws RequisitosNoCumplidosException {
        if (vehiculo == null || vehiculo.getCapacidad() < cupoMaximo) {
            throw new RequisitosNoCumplidosException("El vehículo no alcanza la capacidad requerida");
        }
        this.vehiculo = vehiculo;
    }

    /** Emite la lista con las reservas que mantienen una retención vigente o están confirmadas. */
    public List<Pasajero> emitirLista() throws OperacionNoPermitidaException {
        return emitirLista(LocalDate.now());
    }

    /** Sobrecarga con fecha explícita para comprobar los vencimientos sin depender del reloj. */
    public List<Pasajero> emitirLista(LocalDate hoy) throws OperacionNoPermitidaException {
        if (hoy == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }

        verificarVencimientos(hoy);
        actualizarEstado(hoy);
        if (estado == EstadoSalida.CANCELADA || estado == EstadoSalida.EN_CURSO
                || estado == EstadoSalida.FINALIZADA) {
            throw new OperacionNoPermitidaException(
                    "No se puede emitir la lista de una salida " + estado);
        }

        List<Pasajero> pasajeros = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.ocupaCupos(hoy)) {
                pasajeros.addAll(reserva.getPasajeros());
            }
        }
        if (pasajeros.isEmpty()) {
            throw new OperacionNoPermitidaException(
                    "No hay reservas vigentes para emitir la lista");
        }
        listaEmitida = true;
        return pasajeros;
    }

    /** Implementa RN04 a RN07. Los estados terminales no cambian. */
    public void actualizarEstado(LocalDate hoy) {
        if (estado == EstadoSalida.CANCELADA || estado == EstadoSalida.FINALIZADA || estado == EstadoSalida.EN_CURSO) return;
        int ocupados = cuposOcupados(hoy);
        if (ocupados >= cupoMaximo) {
            estado = EstadoSalida.COMPLETA;
        } else if (diasHastaInicio(hoy) <= 7 && ocupados < tour.getCupoMinimo()) {
            estado = EstadoSalida.CANCELADA;
        } else if (ocupados >= tour.getCupoMinimo()) {
            estado = EstadoSalida.CONFIRMADA;
        } else {
            estado = EstadoSalida.PROGRAMADA;
        }
    }

    public void validarCondicionesInicio() throws RequisitosNoCumplidosException {
        if (!listaEmitida) throw new RequisitosNoCumplidosException("Debe emitirse la lista de pasajeros antes de iniciar");
        if (guia == null || !guia.estaHabilitadoPara(tour.getCategoria())) {
            throw new RequisitosNoCumplidosException("La salida debe tener un guía habilitado");
        }
        if (vehiculo == null || vehiculo.getCapacidad() < cupoMaximo) {
            throw new RequisitosNoCumplidosException("La salida debe tener un vehículo con capacidad suficiente");
        }
    }

    public void iniciar() throws RequisitosNoCumplidosException, OperacionNoPermitidaException {
        if (estado != EstadoSalida.CONFIRMADA && estado != EstadoSalida.COMPLETA) {
            throw new OperacionNoPermitidaException(
                    "Solo se puede iniciar una salida confirmada o completa; estado actual: " + estado);
        }
        validarCondicionesInicio();
        estado = EstadoSalida.EN_CURSO;
    }

    public void finalizar() throws OperacionNoPermitidaException {
        if (estado != EstadoSalida.EN_CURSO) {
            throw new OperacionNoPermitidaException("Solo se puede finalizar una salida en curso");
        }
        estado = EstadoSalida.FINALIZADA;
        for (Reserva reserva : reservas) reserva.finalizar();
    }

    /** Invalida la lista si cambian los pasajeros o las reservas asociadas. */
    void invalidarListaPasajeros() {
        listaEmitida = false;
    }

    public void registrarLiquidacion(Liquidacion liquidacion) { this.liquidacion = liquidacion; }

    @Override
    public int compareTo(Salida otra) {
        int porFecha = fechaInicio.compareTo(otra.fechaInicio);
        return porFecha != 0 ? porFecha : Long.compare(id, otra.id);
    }

    public long getId() { return id; }
    public Tour getTour() { return tour; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public int getCupoMaximo() { return cupoMaximo; }
    public EstadoSalida getEstado() { return estado; }
    public boolean isListaEmitida() { return listaEmitida; }
    public Guia getGuia() { return guia; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public Liquidacion getLiquidacion() { return liquidacion; }
    public List<Reserva> getReservas() { return Collections.unmodifiableList(reservas); }
}