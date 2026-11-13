package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import enumeraciones.MedioPago;

public class Cancelacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private String motivo;
    private boolean cancelacionAgencia;
    private double porcentajeDevolucion;
    private double importeDevuelto;
    private MedioPago medioDevolucion;

    public Cancelacion(LocalDate fecha, String motivo,
                       boolean cancelacionAgencia,
                       double porcentajeDevolucion,
                       double importeDevuelto) {
        this(fecha, motivo, cancelacionAgencia, porcentajeDevolucion, importeDevuelto, null);
    }

    public Cancelacion(LocalDate fecha, String motivo,
                       boolean cancelacionAgencia,
                       double porcentajeDevolucion,
                       double importeDevuelto,
                       MedioPago medioDevolucion) {

        if (fecha == null || motivo == null ||
                motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "La fecha y el motivo son obligatorios");
        }

        if (porcentajeDevolucion < 0 ||
                porcentajeDevolucion > 1 ||
                importeDevuelto < 0) {
            throw new IllegalArgumentException(
                    "Los valores de devolucion son invalidos");
        }

        this.fecha = fecha;
        this.motivo = motivo;
        this.cancelacionAgencia = cancelacionAgencia;
        this.porcentajeDevolucion = porcentajeDevolucion;
        this.importeDevuelto = importeDevuelto;
        this.medioDevolucion = medioDevolucion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public boolean isCancelacionAgencia() {
        return cancelacionAgencia;
    }

    public double getPorcentajeDevolucion() {
        return porcentajeDevolucion;
    }

    public double getImporteDevuelto() {
        return importeDevuelto;
    }

    public MedioPago getMedioDevolucion() {
        return medioDevolucion;
    }

    @Override
    public String toString() {
        return "Cancelacion: " + motivo
                + " - Devolucion: $" + importeDevuelto;
    }
}