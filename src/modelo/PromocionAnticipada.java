package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PromocionAnticipada implements Promocion {

    private static final long serialVersionUID = 1L;
    private static final double DESCUENTO = 0.15;

    @Override
    public boolean esAplicable(Reserva reserva, LocalDate fechaSalida) {
        if (reserva == null || fechaSalida == null) return false;
        long dias = ChronoUnit.DAYS.between(
                reserva.getFechaCreacion().toLocalDate(), fechaSalida);
        return dias > 60;
    }

    @Override
    public double calcularDescuento(double subtotalTour) {
        if (!Double.isFinite(subtotalTour) || subtotalTour < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo");
        }
        return subtotalTour * DESCUENTO;
    }
}
