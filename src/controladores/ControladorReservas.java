
package controladores;

import modelo.*;
import enumeraciones.EstadoReserva;
import enumeraciones.MedioPago;
import excepciones.EntidadNoEncontradaException;
import excepciones.ReglaNegocioException;
import persistencia.RepositorioAgencia;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class ControladorReservas {

    private final Agencia agencia;
    private final RepositorioAgencia repositorio;
    private final GestorPagos gestorPagos;
    private final GestorCancelaciones gestorCancelaciones;
    private final CalculadoraPrecioReserva calculadora;

    /** Constructor de compatibilidad para pruebas aisladas del controlador. */
    public ControladorReservas() {
        this(new Agencia(), null);
    }

    public ControladorReservas(Agencia agencia, RepositorioAgencia repositorio) {
        if (agencia == null) throw new IllegalArgumentException("La agencia es obligatoria");
        this.agencia = agencia;
        this.repositorio = repositorio;
        this.gestorPagos = new GestorPagos();
        this.gestorCancelaciones = new GestorCancelaciones();
        this.calculadora = new CalculadoraPrecioReserva();
    }

    public Cliente registrarCliente(String nombre, String apellido, String documento,
                                    String telefono, String correo, String pais) {
        try {
            return agencia.buscarCliente(documento);
        } catch (EntidadNoEncontradaException ignored) {
            Cliente cliente = new Cliente(nombre, apellido, documento, telefono, correo, pais);
            agencia.agregarCliente(cliente);
            guardar();
            return cliente;
        }
    }

    public Reserva crearReserva(String documentoCliente, long idSalida, int legajoVendedor,
                                List<Pasajero> pasajeros) throws ReglaNegocioException {
        if (pasajeros == null || pasajeros.isEmpty()) {
            throw new IllegalArgumentException("La reserva debe tener pasajeros");
        }
        Cliente cliente = agencia.buscarCliente(documentoCliente);
        Salida salida = agencia.buscarSalida(idSalida);
        Empleado empleado = agencia.buscarEmpleado(legajoVendedor);
        if (!(empleado instanceof Vendedor)) {
            throw new IllegalArgumentException("El legajo no corresponde a un vendedor");
        }
        if (salida.cuposDisponibles(LocalDate.now()) < pasajeros.size()) {
            throw new IllegalStateException("No hay cupos suficientes");
        }
        for (Pasajero pasajero : pasajeros) {
            if (!salida.puedeParticipar(new DatosPasajero(
                    pasajero.getNombre(), pasajero.getApellido(), pasajero.getDocumento(),
                    pasajero.getFechaNacimiento(), pasajero.getContactoEmergencia(),
                    pasajero.getRestricciones()))) {
                throw new IllegalArgumentException("Un pasajero no cumple los requisitos del tour");
            }
        }
        Reserva reserva = new Reserva((int) agencia.siguienteNumeroReserva(), cliente, salida, (Vendedor) empleado);
        for (Pasajero pasajero : pasajeros) reserva.agregarPasajero(pasajero);
        salida.agregarReserva(reserva);
        agencia.agregarReserva(reserva);
        guardar();
        return reserva;
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

        agencia.agregarReserva(reserva);
        guardar();
    }

    // BUSCAR RESERVA
    public Reserva buscarReserva(int id) {
        try {
            return agencia.buscarReserva(id);
        } catch (EntidadNoEncontradaException e) {
            return null;
        }
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

        reserva.actualizarServiciosAdicionales(totalAdicionales);
        reserva.actualizarImporteTotal(total);
        guardar();
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
        guardar();
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
        guardar();
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
        guardar();

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
        return agencia.getReservas();
    }

    // OBTENER RESERVA O INFORMAR ERROR
    private Reserva obtenerReserva(int id) {

        Reserva reserva = buscarReserva(id);

        if (reserva == null) {
            throw new IllegalArgumentException("No existe la reserva #" + id);
        }

        return reserva;
    }

    private void guardar() {
        if (repositorio != null) repositorio.guardar(agencia);
    }
}
