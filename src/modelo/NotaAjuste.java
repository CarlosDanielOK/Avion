
package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class NotaAjuste implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDate fecha;
    private String motivo;
    private double importe;
    private Reserva reserva;

    public NotaAjuste(int id, LocalDate fecha,
                      String motivo, double importe,
                      Reserva reserva) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador debe ser positivo");
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "El motivo es obligatorio");
        }

        if (importe == 0) {
            throw new IllegalArgumentException(
                    "El importe no puede ser cero");
        }

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria");
        }

        this.id = id;
        this.fecha = fecha;
        this.motivo = motivo;
        this.importe = importe;
        this.reserva = reserva;
    }

    public int getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public double getImporte() {
        return importe;
    }

    public Reserva getReserva() {
        return reserva;
    }

    @Override
    public String toString() {
        return "Nota de ajuste #" + id
                + " - Motivo: " + motivo
                + " - Importe: $" + importe;
    }
}
