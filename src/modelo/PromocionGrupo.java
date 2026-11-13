package modelo;

import java.time.LocalDate;

public class PromocionGrupo implements Promocion {

    private static final long serialVersionUID = 1L;
    private static final double DESCUENTO = 0.10;

    @Override
    public boolean esAplicable(Reserva reserva, LocalDate fechaSalida) {
        return reserva != null && reserva.obtenerCantidadPasajeros() >= 6;
    }

    @Override
    public double calcularDescuento(double subtotalTour) {
        if (!Double.isFinite(subtotalTour) || subtotalTour < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo");
        }
        return subtotalTour * DESCUENTO;
    }
}
