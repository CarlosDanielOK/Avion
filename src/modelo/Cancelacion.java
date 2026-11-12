
package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Cancelacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private String motivo;
    private boolean cancelacionAgencia;
    private double porcentajeDevolucion;
    private double importeDevuelto;

    public Cancelacion(LocalDate fecha, String motivo,
                       boolean cancelacionAgencia,
                       double porcentajeDevolucion,
                       double importeDevuelto) {

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

    @Override
    public String toString() {
        return "Cancelacion: " + motivo
                + " - Devolucion: $" + importeDevuelto;
    }
}
