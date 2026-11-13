package modelo;

import enumeraciones.Temporada;
import java.io.Serializable;
import java.time.LocalDate;

/** Precio base por pasajero de un tour durante un período de vigencia. */
public class Tarifa implements Serializable {
    private static final long serialVersionUID = 1L;

    private Temporada temporada;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private double precioBase;

    public Tarifa(Temporada temporada, LocalDate vigenciaDesde, LocalDate vigenciaHasta, double precioBase) {
        if (temporada == null || vigenciaDesde == null || vigenciaHasta == null) {
            throw new IllegalArgumentException("Temporada y fechas de vigencia son obligatorias");
        }
        if (vigenciaHasta.isBefore(vigenciaDesde)) {
            throw new IllegalArgumentException("La vigencia hasta no puede ser anterior a la vigencia desde");
        }
        if (precioBase <= 0) {
            throw new IllegalArgumentException("El precio base debe ser mayor a cero");
        }
        this.temporada = temporada;
        this.vigenciaDesde = vigenciaDesde;
        this.vigenciaHasta = vigenciaHasta;
        this.precioBase = precioBase;
    }

    /** Vigente si la fecha está dentro del período, extremos incluidos. */
    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isBefore(vigenciaDesde) && !fecha.isAfter(vigenciaHasta);
    }

    /** Dos períodos se superponen si ninguno termina antes de que empiece el otro. */
    public boolean seSuperponeCon(Tarifa otra) {
        return !vigenciaHasta.isBefore(otra.vigenciaDesde) && !otra.vigenciaHasta.isBefore(vigenciaDesde);
    }

    public Temporada getTemporada() { return temporada; }
    public LocalDate getVigenciaDesde() { return vigenciaDesde; }
    public LocalDate getVigenciaHasta() { return vigenciaHasta; }
    public double getPrecioBase() { return precioBase; }
}