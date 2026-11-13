package controladores;

import modelo.*;
import enumeraciones.EstadoReserva;
import enumeraciones.MedioPago;
import excepciones.ReglaNegocioException;
import persistencia.RepositorioAgencia;

import java.time.LocalDate;
import java.util.List;

public class ControladorReservas {

    private final Agencia agencia;
    private final RepositorioAgencia repositorio;
    private final GestorPagos gestorPagos;
    private final GestorCancelaciones gestorCancelaciones;
    private final CalculadoraPrecioReserva calculadora;

    /**
     * Constructor de compatibilidad para pruebas aisladas. Las reservas se
     * mantienen en una agencia en memoria y no se persisten.
     */
    public ControladorReservas() {
        this(new Agencia(), null);
    }

    public ControladorReservas(Agencia agencia, RepositorioAgencia repositorio) {
        this(agencia, repositorio, new CalculadoraPrecioReserva());
    }

    public ControladorReservas(Agencia agencia, RepositorioAgencia repositorio,
                               CalculadoraPrecioReserva calculadora) {
        if (agencia == null || calculadora == null) {
            throw new IllegalArgumentException("La agencia y la calculadora son obligatorias");
        }
        this.agencia = agencia;
        this.repositorio = repositorio;
        this.gestorPagos = new GestorPagos();
        this.gestorCancelaciones = new GestorCancelaciones();
        this.calculadora = calculadora;
    }

    // REGISTRAR RESERVA
    public void registrarReserva(Reserva reserva) throws ReglaNegocioException {

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

        Salida salida = reserva.getSalida();
        if (salida.getEstado() == enumeraciones.EstadoSalida.CANCELADA
                || salida.getEstado() == enumeraciones.EstadoSalida.FINALIZADA
                || salida.getEstado() == enumeraciones.EstadoSalida.EN_CURSO) {
            throw new IllegalStateException("No se pueden crear reservas para una salida " + salida.getEstado());
        }
        for (Pasajero pasajero : reserva.getPasajeros()) {
            DatosPasajero datos = new DatosPasajero(pasajero.getNombre(),
                    pasajero.getApellido(), pasajero.getDocumento(),
                    pasajero.getFechaNacimiento(), pasajero.getTipoPasajero(),
                    pasajero.getContactoEmergencia(), pasajero.getRestricciones());
            if (!salida.puedeParticipar(datos)) {
                throw new IllegalArgumentException("Un pasajero no cumple los requisitos del tour");
            }
            if (!pasajero.tipoEsCompatible(salida.getFechaInicio())) {
                throw new IllegalArgumentException("El tipo de pasajero no coincide con su edad");
            }
        }
        if (salida.cuposDisponibles(LocalDate.now()) < reserva.obtenerCantidadPasajeros()) {
            throw new IllegalStateException("No hay cupos suficientes para la reserva");
        }

        enumeraciones.EstadoSalida estadoAnterior = salida.getEstado();
        reserva.incluirServiciosObligatorios();
        salida.agregarReserva(reserva);
        agencia.agregarReserva(reserva);
        procesarCancelacionAutomaticaSiCorresponde(salida, estadoAnterior, LocalDate.now());
        guardar();
    }

    // BUSCAR RESERVA
    public Reserva buscarReserva(int id) {
        try {
            return agencia.buscarReserva(id);
        } catch (excepciones.EntidadNoEncontradaException e) {
            return null;
        }
    }

    // CALCULAR PRECIO usando tarifa vigente y adicionales reales del tour/sus pasajeros
    public double calcularPrecio(int idReserva) throws ReglaNegocioException {
        Reserva reserva = actualizarVigenciaReserva(idReserva, LocalDate.now());
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException("Solo se calcula el precio de reservas pendientes");
        }
        Tarifa tarifa = reserva.getSalida().obtenerTarifa();
        LocalDate fechaSalida = reserva.getSalida().getFechaInicio();
        double totalAdicionales = reserva.totalServiciosAdicionales();
        double total = calculadora.calcularTotal(reserva, tarifa.getPrecioBase(),
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

        Reserva reserva = actualizarVigenciaReserva(idReserva, LocalDate.now());

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
        Reserva reserva = actualizarVigenciaReserva(idReserva, LocalDate.now());
        return gestorPagos.consultarSaldo(reserva);
    }

    // VERIFICAR CONFIRMACION
    public boolean puedeConfirmar(
            int idReserva,
            LocalDate fechaSalida) {

        Reserva reserva = actualizarVigenciaReserva(idReserva, LocalDate.now());

        return gestorPagos.puedeConfirmarse(
                reserva, reserva.getSalida().getFechaInicio());
    }

    // CONFIRMAR RESERVA
    public void confirmarReserva(int idReserva) {
        confirmarReserva(idReserva, LocalDate.now());
    }

    /** Sobrecarga con fecha de operación explícita para probar vencimientos. */
    public void confirmarReserva(int idReserva, LocalDate hoy) {
        Reserva reserva = actualizarVigenciaReserva(idReserva, hoy);
        LocalDate fechaSalida = reserva.getSalida().getFechaInicio();

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "La reserva no esta pendiente o ya vencio");
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

        Reserva reserva = actualizarVigenciaReserva(idReserva, LocalDate.now());

        validarCancelacion(reserva);

        return gestorCancelaciones.crearCancelacion(
                reserva, motivo, cancelacionAgencia, LocalDate.now());
    }

    // CANCELAR RESERVA
    public Cancelacion cancelarReserva(int idReserva, String motivo,
                                       boolean cancelacionAgencia) {
        Reserva reserva = actualizarVigenciaReserva(idReserva, LocalDate.now());

        validarCancelacion(reserva);

        Cancelacion cancelacion = gestorCancelaciones.crearCancelacion(
                reserva, motivo, cancelacionAgencia, LocalDate.now());

        enumeraciones.EstadoSalida estadoAnterior = reserva.getSalida().getEstado();
        reserva.registrarCancelacion(cancelacion);
        reserva.getSalida().actualizarEstado(LocalDate.now());
        procesarCancelacionAutomaticaSiCorresponde(reserva.getSalida(), estadoAnterior, LocalDate.now());
        guardar();

        return cancelacion;
    }

    /** Compatibilidad: se usa la fecha de salida almacenada, no la ingresada. */
    public Cancelacion cancelarReserva(int idReserva, String motivo,
                                       LocalDate fechaSalida, boolean cancelacionAgencia) {
        return cancelarReserva(idReserva, motivo, cancelacionAgencia);
    }

    // VALIDAR CANCELACION
    private void validarCancelacion(Reserva reserva) {

        if (!reserva.puedeCancelarse()) {
            throw new IllegalStateException("La reserva no se puede cancelar en el estado actual de la salida");
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

        boolean anulada = reserva.anularSiVencida(fechaSalida);
        if (anulada) guardar();
        return anulada;
    }

    public void agregarServicioAdicional(int idReserva, String documentoPasajero,
                                         ServicioAdicional servicio) {
        Reserva reserva = actualizarVigenciaReserva(idReserva, LocalDate.now());
        Pasajero pasajero = buscarPasajero(reserva, documentoPasajero);
        reserva.agregarServicioAdicional(pasajero, servicio);
        guardar();
    }

    public void reemplazarPasajero(int idReserva, String documento,
                                   DatosPasajero datos, LocalDate hoy) {
        Reserva reserva = actualizarVigenciaReserva(idReserva, hoy);
        reserva.reemplazarPasajero(documento, datos, hoy);
        guardar();
    }

    public NotaAjuste registrarAjuste(int idReserva, String motivo,
                                      double importe, LocalDate fecha) {
        Reserva reserva = actualizarVigenciaReserva(idReserva, LocalDate.now());
        NotaAjuste nota = reserva.registrarAjuste(motivo, importe, fecha);
        guardar();
        return nota;
    }

    // LISTAR RESERVAS
    public List<Reserva> listarReservas() {
        return agencia.getReservas();
    }

    /**
     * S09: cada operación sobre una reserva refresca vencimientos y el estado
     * de su salida. Si la salida se cancela por cupo mínimo, procesa las
     * devoluciones completas de las reservas que sigan vigentes.
     */
    private Reserva actualizarVigenciaReserva(int idReserva, LocalDate hoy) {
        if (hoy == null) throw new IllegalArgumentException("La fecha es obligatoria");
        Reserva reserva = obtenerReserva(idReserva);
        Salida salida = reserva.getSalida();
        enumeraciones.EstadoSalida estadoAnterior = salida.getEstado();
        salida.verificarVencimientos(hoy);
        salida.actualizarEstado(hoy);
        procesarCancelacionAutomaticaSiCorresponde(salida, estadoAnterior, hoy);
        guardar();
        return reserva;
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

    private Pasajero buscarPasajero(Reserva reserva, String documento) {
        for (Pasajero pasajero : reserva.getPasajeros()) {
            if (pasajero.getDocumento().equalsIgnoreCase(documento)) return pasajero;
        }
        throw new IllegalArgumentException("No existe el pasajero indicado");
    }

    private void procesarCancelacionAutomaticaSiCorresponde(Salida salida,
                                                               enumeraciones.EstadoSalida estadoAnterior,
                                                               LocalDate hoy) {
        if (estadoAnterior != enumeraciones.EstadoSalida.CANCELADA
                && salida.getEstado() == enumeraciones.EstadoSalida.CANCELADA) {
            for (Reserva otra : salida.getReservas()) {
                if (otra.puedeCancelarse()) {
                    Cancelacion devolucion = gestorCancelaciones.cancelar(otra,
                            "Salida cancelada por no alcanzar el cupo mínimo", true, hoy);
                    // La cancelación de agencia es una simulación; no se conecta a un medio de pago.
                }
            }
        }
    }

    private void guardar() {
        if (repositorio != null) {
            repositorio.guardar(agencia);
        }
    }
}