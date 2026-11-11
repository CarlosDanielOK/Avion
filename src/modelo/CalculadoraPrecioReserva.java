
package modelo;

import java.time.LocalDate;

public class CalculadoraPrecioReserva {

    // CALCULAR PRECIO BASE DE LOS PASAJEROS
    public double calcularSubtotalTour(
            Reserva reserva,
            double tarifaBase,
            LocalDate fechaSalida) {

        validarDatos(reserva, tarifaBase, fechaSalida);

        double subtotal = 0;

        for (Pasajero pasajero : reserva.getPasajeros()) {
            subtotal += tarifaBase *
                    pasajero.calcularPorcentajeTarifa(fechaSalida);
        }

        return subtotal;
    }

    // ELEGIR LA MEJOR PROMOCION
    public double calcularMejorDescuento(
            Reserva reserva,
            double subtotalTour,
            LocalDate fechaSalida) {

        if (reserva == null || fechaSalida == null) {
            throw new IllegalArgumentException(
                    "La reserva y la fecha son obligatorias");
        }

        if (!Double.isFinite(subtotalTour) || subtotalTour < 0) {
            throw new IllegalArgumentException(
                    "El subtotal no es valido");
        }

        Promocion anticipada =
                new PromocionAnticipada(fechaSalida);

        Promocion grupo = new PromocionGrupo();

        double mejorDescuento = 0;

        if (anticipada.esAplicable(reserva)) {
            mejorDescuento =
                    anticipada.calcularDescuento(subtotalTour);
        }

        if (grupo.esAplicable(reserva)) {
            double descuentoGrupo =
                    grupo.calcularDescuento(subtotalTour);

            mejorDescuento = Math.max(
                    mejorDescuento, descuentoGrupo);
        }

        return mejorDescuento;
    }

    // CALCULAR PRECIO FINAL
    public double calcularTotal(
            Reserva reserva,
            double tarifaBase,
            LocalDate fechaSalida,
            double totalAdicionales) {

        validarDatos(reserva, tarifaBase, fechaSalida);

        if (!Double.isFinite(totalAdicionales)
                || totalAdicionales < 0) {
            throw new IllegalArgumentException(
                    "Los adicionales no son validos");
        }

        double subtotal = calcularSubtotalTour(
                reserva, tarifaBase, fechaSalida);

        double descuento = calcularMejorDescuento(
                reserva, subtotal, fechaSalida);

        return subtotal - descuento + totalAdicionales;
    }

    // VALIDACIONES GENERALES
    private void validarDatos(
            Reserva reserva,
            double tarifaBase,
            LocalDate fechaSalida) {

        if (reserva == null || fechaSalida == null) {
            throw new IllegalArgumentException(
                    "La reserva y la fecha son obligatorias");
        }

        if (reserva.obtenerCantidadPasajeros() == 0) {
            throw new IllegalStateException(
                    "La reserva debe tener al menos un pasajero");
        }

        if (!Double.isFinite(tarifaBase) || tarifaBase < 0) {
            throw new IllegalArgumentException(
                    "La tarifa base no es valida");
        }

        if (fechaSalida.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de salida ya paso");
        }
    }
}
