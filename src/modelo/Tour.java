package modelo;

import enumeraciones.CategoriaTour;
import excepciones.EntidadNoEncontradaException;
import excepciones.OperacionNoPermitidaException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Tour del catálogo. Las subclases definen la categoría y reglas propias. */
public abstract class Tour implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nombre;
    private String descripcion;
    private int duracionDias;
    private int cupoMinimo;
    private int cupoMaximo;

    // Composición: estos objetos viven y mueren con el tour
    private final List<Actividad> itinerario = new ArrayList<>();
    private final List<Tarifa> tarifas = new ArrayList<>();
    private final List<ServicioAdicional> servicios = new ArrayList<>();

    protected Tour(String codigo, String nombre, String descripcion, int duracionDias,
            int cupoMinimo, int cupoMaximo) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Código y nombre son obligatorios");
        }
        this.codigo = codigo.trim().toUpperCase();
        this.nombre = nombre.trim();
        modificarDatos(this.nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    public abstract CategoriaTour getCategoria();

    /** Por defecto un tour no exige seguro; TourAventura lo sobrescribe. */
    public boolean requiereSeguro() {
        return false;
    }

    /**
     * Por defecto cualquier pasajero puede participar; TourAventura agrega la edad
     * mínima.
     */
    public boolean puedeParticipar(DatosPasajero datos, LocalDate fecha) {
        return true;
    }

    /** Devuelve la tarifa vigente en la fecha indicada (RN12). */
    public Tarifa obtenerTarifa(LocalDate fecha) throws EntidadNoEncontradaException {
        for (Tarifa t : tarifas) {
            if (t.estaVigente(fecha)) {
                return t;
            }
        }
        throw new EntidadNoEncontradaException("El tour " + codigo + " no tiene tarifa vigente al " + fecha);
    }

    public List<ServicioAdicional> obtenerServiciosObligatorios() {
        List<ServicioAdicional> obligatorios = new ArrayList<>();
        for (ServicioAdicional s : servicios) {
            if (s.isObligatorio()) {
                obligatorios.add(s);
            }
        }
        return obligatorios;
    }

    // ---------- Mantenimiento del tour (CU01, CU02, CU03) ----------

    public final void modificarDatos(String nombre, String descripcion, int duracionDias,
            int cupoMinimo, int cupoMaximo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (duracionDias < 1 || cupoMinimo < 1 || cupoMaximo < cupoMinimo) {
            throw new IllegalArgumentException(
                    "La duración y el cupo mínimo deben ser positivos y el cupo máximo no puede ser menor al mínimo");
        }
        this.nombre = nombre.trim();
        this.descripcion = descripcion;
        this.duracionDias = duracionDias;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
    }

    /** CU02: el día de la actividad no puede exceder la duración del tour. */
    public void agregarActividad(Actividad actividad) throws OperacionNoPermitidaException {
        if (actividad.getDia() > duracionDias) {
            throw new OperacionNoPermitidaException("El día " + actividad.getDia()
                    + " excede la duración del tour (" + duracionDias + " días)");
        }
        for (Actividad existente : itinerario) {
            if (existente.compareTo(actividad) == 0) {
                throw new OperacionNoPermitidaException("Ya existe una actividad en el día "
                        + actividad.getDia() + " con orden " + actividad.getOrden());
            }
        }
        itinerario.add(actividad);
        Collections.sort(itinerario); // orden natural (Comparable): día y orden
    }

    /** CU03: no se admiten períodos de vigencia superpuestos. */
    public void agregarTarifa(Tarifa tarifa) throws OperacionNoPermitidaException {
        for (Tarifa existente : tarifas) {
            if (existente.seSuperponeCon(tarifa)) {
                throw new OperacionNoPermitidaException("La vigencia de la tarifa se superpone con otra ya registrada");
            }
        }
        tarifas.add(tarifa);
    }

    public void agregarServicioAdicional(ServicioAdicional servicio) throws OperacionNoPermitidaException {
        for (ServicioAdicional existente : servicios) {
            if (existente.getNombre().equalsIgnoreCase(servicio.getNombre())) {
                throw new OperacionNoPermitidaException(
                        "El servicio '" + servicio.getNombre() + "' ya existe en el tour");
            }
        }
        servicios.add(servicio);
    }

    /**
     * Verifica que haya un seguro obligatorio cuando el tour lo exige (RN19, S21).
     */
    public boolean tieneSeguroObligatorio() {
        for (ServicioAdicional s : servicios) {
            if (s.isObligatorio() && s.getTipo() == enumeraciones.TipoServicio.SEGURO) {
                return true;
            }
        }
        return false;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public int getCupoMinimo() {
        return cupoMinimo;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public List<Actividad> getItinerario() {
        return Collections.unmodifiableList(itinerario);
    }

    public List<Tarifa> getTarifas() {
        return Collections.unmodifiableList(tarifas);
    }

    public List<ServicioAdicional> getServicios() {
        return Collections.unmodifiableList(servicios);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " [" + getCategoria() + ", " + duracionDias + " días]";
    }
}
