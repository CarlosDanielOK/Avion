package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Calcula el precio y delega las reglas variables en las promociones recibidas. */
public class CalculadoraPrecioReserva {

    private final List<Promocion> promociones;

    /** Configuración normal del sistema, con las dos promociones del enunciado. */
    public CalculadoraPrecioReserva() {
        this(Arrays.asList(new PromocionAnticipada(), new PromocionGrupo()));
    }

    /** Permite incorporar promociones nuevas sin modificar esta calculadora. */
    public CalculadoraPrecioReserva(List<Promocion> promociones) {
        if (promociones == null || promociones.contains(null)) {
            throw new IllegalArgumentException("La lista de promociones no puede ser nula ni contener valores nulos");
        }
        this.promociones = Collections.unmodifiableList(new ArrayList<>(promociones));
    }

    public double calcularSubtotalTour(Reserva reserva, double tarifaBase, LocalDate fechaSalida) {
        validarDatos(reserva, tarifaBase, fechaSalida);
        double subtotal = 0;
        for (Pasajero pasajero : reserva.getPasajeros()) {
            subtotal += tarifaBase * pasajero.calcularPorcentajeTarifa(fechaSalida);
        }
        return subtotal;
    }

    public double calcularMejorDescuento(Reserva reserva, double subtotalTour, LocalDate fechaSalida) {
        if (reserva == null || fechaSalida == null) {
            throw new IllegalArgumentException("La reserva y la fecha son obligatorias");
        }
        if (!Double.isFinite(subtotalTour) || subtotalTour < 0) {
            throw new IllegalArgumentException("El subtotal no es válido");
        }
        double mejorDescuento = 0;
        for (Promocion promocion : promociones) {
            if (promocion.esAplicable(reserva, fechaSalida)) {
                mejorDescuento = Math.max(mejorDescuento,
                        promocion.calcularDescuento(subtotalTour));
            }
        }
        return mejorDescuento;
    }

    public double calcularTotal(Reserva reserva, double tarifaBase,
                                LocalDate fechaSalida, double totalAdicionales) {
        validarDatos(reserva, tarifaBase, fechaSalida);
        if (!Double.isFinite(totalAdicionales) || totalAdicionales < 0) {
            throw new IllegalArgumentException("Los adicionales no son válidos");
        }
        double subtotal = calcularSubtotalTour(reserva, tarifaBase, fechaSalida);
        double descuento = calcularMejorDescuento(reserva, subtotal, fechaSalida);
        return subtotal - descuento + totalAdicionales;
    }

    private void validarDatos(Reserva reserva, double tarifaBase, LocalDate fechaSalida) {
        if (reserva == null || fechaSalida == null) {
            throw new IllegalArgumentException("La reserva y la fecha son obligatorias");
        }
        if (reserva.obtenerCantidadPasajeros() == 0) {
            throw new IllegalStateException("La reserva debe tener al menos un pasajero");
        }
        if (!Double.isFinite(tarifaBase) || tarifaBase < 0) {
            throw new IllegalArgumentException("La tarifa base no es válida");
        }
        if (fechaSalida.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de salida ya pasó");
        }
    }
}
