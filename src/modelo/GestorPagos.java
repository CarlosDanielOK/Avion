package modelo;

import enumeraciones.EstadoReserva;
import enumeraciones.MedioPago;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class GestorPagos {

    private static final double PORCENTAJE_SENA = 0.30;
    private static final double RECARGO_TARJETA = 0.07;
    private static final double TOLERANCIA = 0.001;

    public Pago crearPago(
            int id,
            double importe,
            MedioPago medio,
            int cuotas) {

        if (medio == null) {
            throw new IllegalArgumentException(
                    "El medio de pago es obligatorio");
        }

        if (!Double.isFinite(importe) || importe <= 0) {
            throw new IllegalArgumentException(
                    "El importe debe ser mayor a cero");
        }

        if (cuotas < 1) {
            throw new IllegalArgumentException(
                    "Las cuotas deben ser al menos una");
        }

        if (medio != MedioPago.TARJETA && cuotas > 1) {
            throw new IllegalArgumentException(
                    "Solo se permiten cuotas con tarjeta");
        }

        Pago pago = new Pago(
                id, LocalDate.now(), importe, medio, cuotas);

        if (medio == MedioPago.TARJETA && cuotas > 1) {
            pago.aplicarRecargo(RECARGO_TARJETA);
        }

        return pago;
    }

    public double calcularSenaMinima(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria");
        }

        return reserva.getImporteTotal() * PORCENTAJE_SENA;
    }

    public boolean cumpleSena(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria");
        }

        return reserva.getImporteTotal() > 0
                && reserva.calcularTotalPagado()
                + TOLERANCIA >= calcularSenaMinima(reserva);
    }

    public double consultarSaldo(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria");
        }

        return reserva.calcularSaldoPendiente();
    }

    public boolean requierePagoTotal(LocalDate fechaSalida) {
        if (fechaSalida == null) {
            throw new IllegalArgumentException(
                    "La fecha de salida es obligatoria");
        }

        long dias = ChronoUnit.DAYS.between(
                LocalDate.now(), fechaSalida);

        if (dias < 0) {
            throw new IllegalArgumentException(
                    "La fecha de salida ya paso");
        }

        return dias <= 10;
    }

    public boolean puedeConfirmarse(
            Reserva reserva,
            LocalDate fechaSalida) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria");
        }

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            return false;
        }

        if (reserva.obtenerCantidadPasajeros() == 0
                || reserva.getImporteTotal() <= 0) {
            return false;
        }

        if (reserva.estaVencida(fechaSalida)) {
            return false;
        }

        if (requierePagoTotal(fechaSalida)) {
            return reserva.calcularSaldoPendiente()
                    <= TOLERANCIA;
        }

        return cumpleSena(reserva);
    }
}