
package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PromocionAnticipada implements Promocion {

    private static final long serialVersionUID = 1L;

    private static final double DESCUENTO = 0.15;

    private LocalDate fechaSalida;

    public PromocionAnticipada(LocalDate fechaSalida) {
        if (fechaSalida == null) {
            throw new IllegalArgumentException(
                    "La fecha de salida es obligatoria");
        }
        this.fechaSalida = fechaSalida;
    }

    @Override
    public boolean esAplicable(Reserva reserva) {
        if (reserva == null) {
            return false;
        }

        long dias = ChronoUnit.DAYS.between(
                reserva.getFechaCreacion().toLocalDate(),
                fechaSalida
        );

        return dias > 60;
    }

    @Override
    public double calcularDescuento(double subtotalTour) {
        if (subtotalTour < 0) {
            throw new IllegalArgumentException(
                    "El subtotal no puede ser negativo");
        }

        return subtotalTour * DESCUENTO;
    }
}
