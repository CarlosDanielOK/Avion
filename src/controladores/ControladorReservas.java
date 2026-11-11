
package controladores;

import modelo.*;
import enumeraciones.EstadoReserva;
import enumeraciones.MedioPago;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControladorReservas {

    private final List<Reserva> reservas;
    private final GestorPagos gestorPagos;
    private final GestorCancelaciones gestorCancelaciones;
    private final CalculadoraPrecioReserva calculadora;

    public ControladorReservas() {
        this.reservas = new ArrayList<>();
        this.gestorPagos = new GestorPagos();
        this.gestorCancelaciones = new GestorCancelaciones();
        this.calculadora = new CalculadoraPrecioReserva();
    }

    // REGISTRAR RESERVA
    public void registrarReserva(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva es obligatoria");
        }

        if (reserva.obtenerCantidadPasajeros() == 0) {
            throw new IllegalArgumentException(
                    "La reserva debe tener pasajeros");
        }

        if (buscarReserva(reserva.getId()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una reserva con ese ID");
        }

        reservas.add(reserva);
    }

    // BUSCAR RESERVA
    public Reserva buscarReserva(int id) {
        for (Reserva reserva : reservas) {
            if (reserva.getId() == id) {
                return reserva;
            }
        }
        return null;
    }

    // CALCULAR PRECIO
    public double calcularPrecio(
            int idReserva,
            double tarifaBase,
            LocalDate fechaSalida,
            double totalAdicionales) {

        Reserva reserva = obtenerReserva(idReserva);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se calcula el precio de reservas pendientes");
        }

        double total = calculadora.calcularTotal(
                reserva, tarifaBase,
                fechaSalida, totalAdicionales);

        reserva.actualizarImporteTotal(total);
        return total;
    }

    // PREPARAR PAGO
    public Pago prepararPago(
            int idReserva,
            int idPago,
            double importe,
            MedioPago medio,
            int cuotas) {

        Reserva reserva = obtenerReserva(idReserva);

        if (reserva.getEstado() == EstadoReserva.ANULADA ||
                reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new IllegalStateException(
                    "No se admiten pagos en esta reserva");
        }

        return gestorPagos.crearPago(
                idPago, importe, medio, cuotas);
    }

    // REGISTRAR PAGO
    public Pago registrarPago(
            int idReserva,
            int idPago,
            double importe,
            MedioPago medio,
            int cuotas) {

        Reserva reserva = obtenerReserva(idReserva);

        Pago pago = prepararPago(
                idReserva, idPago,
                importe, medio, cuotas);

        reserva.registrarPago(pago);
        return pago;
    }

    // CONSULTAR SALDO
    public double consultarSaldo(int idReserva) {
        Reserva reserva = obtenerReserva(idReserva);
        return gestorPagos.consultarSaldo(reserva);
    }

    // VERIFICAR CONFIRMACION
    public boolean puedeConfirmar(
            int idReserva,
            LocalDate fechaSalida) {

        Reserva reserva = obtenerReserva(idReserva);

        return gestorPagos.puedeConfirmarse(
                reserva, fechaSalida);
    }

    // CONFIRMAR RESERVA
    public void confirmarReserva(
            int idReserva,
            LocalDate fechaSalida) {

        Reserva reserva = obtenerReserva(idReserva);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "La reserva no esta pendiente");
        }

        if (reserva.obtenerCantidadPasajeros() == 0) {
            throw new IllegalStateException(
                    "La reserva debe tener pasajeros");
        }

        if (reserva.getImporteTotal() <= 0) {
            throw new IllegalStateException(
                    "Primero debe calcularse el precio");
        }

        if (!gestorPagos.puedeConfirmarse(
                reserva, fechaSalida)) {
            throw new IllegalStateException(
                    "No se cumplen las condiciones de pago o vencimiento");
        }

        reserva.cambiarEstado(EstadoReserva.CONFIRMADA);
    }

    // CALCULAR CANCELACION
    public Cancelacion calcularCancelacion(
            int idReserva,
            String motivo,
            LocalDate fechaSalida,
            boolean cancelacionAgencia) {

        Reserva reserva = obtenerReserva(idReserva);

        validarCancelacion(reserva);

        return gestorCancelaciones.crearCancelacion(
                motivo,
                fechaSalida,
                reserva.calcularTotalPagado(),
                cancelacionAgencia);
    }

    // CANCELAR RESERVA
    public Cancelacion cancelarReserva(
            int idReserva,
            String motivo,
            LocalDate fechaSalida,
            boolean cancelacionAgencia) {

        Reserva reserva = obtenerReserva(idReserva);

        validarCancelacion(reserva);

        Cancelacion cancelacion =
                gestorCancelaciones.crearCancelacion(
                        motivo,
                        fechaSalida,
                        reserva.calcularTotalPagado(),
                        cancelacionAgencia);

        reserva.registrarCancelacion(cancelacion);

        return cancelacion;
    }

    // VALIDAR CANCELACION
    private void validarCancelacion(Reserva reserva) {

        if (reserva.getEstado() == EstadoReserva.ANULADA ||
                reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new IllegalStateException(
                    "La reserva no se puede cancelar");
        }

        if (reserva.getCancelacion() != null) {
            throw new IllegalStateException(
                    "La reserva ya fue cancelada");
        }
    }

    // VERIFICAR VENCIMIENTO
    public boolean verificarVencimiento(
            int idReserva,
            LocalDate fechaSalida) {

        Reserva reserva = obtenerReserva(idReserva);

        return reserva.anularSiVencida(fechaSalida);
    }

    // LISTAR RESERVAS
    public List<Reserva> listarReservas() {
        return Collections.unmodifiableList(reservas);
    }

    // OBTENER RESERVA O INFORMAR ERROR
    private Reserva obtenerReserva(int id) {

        Reserva reserva = buscarReserva(id);

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "No existe la reserva #" + id);
        }

        return reserva;
    }
}
