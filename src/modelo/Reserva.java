
package modelo;

import enumeraciones.EstadoReserva;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final double TOLERANCIA = 0.001;

    private int id;
    private Cliente cliente;
    private Salida salida;
    private Vendedor vendedor;
    private List<Pasajero> pasajeros;
    private List<Pago> pagos;
    private List<NotaAjuste> notasAjuste;
    private LocalDateTime fechaCreacion;
    private EstadoReserva estado;
    private double importeTotal;
    private double importeServiciosAdicionales;
    private Cancelacion cancelacion;

    public Reserva(int id, Cliente cliente,
                   Salida salida, Vendedor vendedor) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de reserva debe ser positivo");
        }

        if (cliente == null || salida == null ||
                vendedor == null) {
            throw new IllegalArgumentException(
                    "Cliente, salida y vendedor son obligatorios");
        }

        this.id = id;
        this.cliente = cliente;
        this.salida = salida;
        this.vendedor = vendedor;
        this.pasajeros = new ArrayList<>();
        this.pagos = new ArrayList<>();
        this.notasAjuste = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoReserva.PENDIENTE;
        this.importeTotal = 0;
        this.cancelacion = null;
    }

    // AGREGAR PASAJERO
    public void agregarPasajero(Pasajero pasajero) {

        if (pasajero == null) {
            throw new IllegalArgumentException(
                    "El pasajero no puede ser nulo");
        }

        if (estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden agregar pasajeros a reservas pendientes");
        }

        for (Pasajero existente : pasajeros) {
            if (existente.getDocumento().equalsIgnoreCase(
                    pasajero.getDocumento())) {
                throw new IllegalArgumentException(
                        "El pasajero ya esta registrado en la reserva");
            }
        }

        if (!pagos.isEmpty()) {
            throw new IllegalStateException(
                    "No se pueden agregar pasajeros despues de registrar pagos");
        }

        pasajeros.add(pasajero);
        importeTotal = 0;
    }

    public int obtenerCantidadPasajeros() {
        return pasajeros.size();
    }

    public void incluirServiciosObligatorios() {
        verificarModificacion();
        for (Pasajero pasajero : pasajeros) {
            for (ServicioAdicional servicio : salida.getTour().obtenerServiciosObligatorios()) {
                if (!tieneServicio(pasajero, servicio)) {
                    pasajero.agregarServicioAdicional(servicio);
                }
            }
        }
        actualizarTotalServiciosAdicionales();
    }

    public void agregarServicioAdicional(Pasajero pasajero, ServicioAdicional servicio) {
        verificarModificacion();
        if (pasajero == null || !pasajeros.contains(pasajero)) {
            throw new IllegalArgumentException("El pasajero no pertenece a la reserva");
        }
        if (!salida.getTour().getServicios().contains(servicio)) {
            throw new IllegalArgumentException("El servicio no pertenece al tour de la reserva");
        }
        pasajero.agregarServicioAdicional(servicio);
        actualizarTotalServiciosAdicionales();
    }

    // REGISTRAR PAGO
    public void registrarPago(Pago pago) {

        if (pago == null) {
            throw new IllegalArgumentException(
                    "El pago no puede ser nulo");
        }

        if (estado == EstadoReserva.ANULADA ||
                estado == EstadoReserva.FINALIZADA) {
            throw new IllegalStateException(
                    "No se pueden registrar pagos en esta reserva");
        }

        if (importeTotal <= 0) {
            throw new IllegalStateException(
                    "Primero debe calcularse el importe de la reserva");
        }

        for (Pago existente : pagos) {
            if (existente.getId() == pago.getId()) {
                throw new IllegalArgumentException(
                        "Ya existe un pago con ese ID");
            }
        }

        double saldo = calcularSaldoPendiente();

        if (pago.getImporte() > saldo + TOLERANCIA) {
            throw new IllegalArgumentException(
                    "El pago supera el saldo pendiente");
        }

        pagos.add(pago);
    }

    // TOTAL PAGADO SIN RECARGOS
    public double calcularTotalPagado() {
        double total = 0;

        for (Pago pago : pagos) {
            total += pago.getImporte();
        }

        return total;
    }

    // TOTAL COBRADO CON RECARGOS
    public double calcularTotalCobrado() {
        double total = 0;

        for (Pago pago : pagos) {
            total += pago.calcularImporteFinal();
        }

        return total;
    }

    // SALDO PENDIENTE
    public double calcularSaldoPendiente() {
        return Math.max(
                0, importeTotal - calcularTotalPagado());
    }

    // ACTUALIZAR PRECIO
    public void actualizarImporteTotal(double importe) {

        if (!Double.isFinite(importe) || importe < 0) {
            throw new IllegalArgumentException(
                    "El importe no es valido");
        }

        if (estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se puede calcular el precio de una reserva pendiente");
        }

        if (!pagos.isEmpty()) {
            throw new IllegalStateException(
                    "No se puede modificar el importe con pagos registrados");
        }

        this.importeTotal = importe;
    }

    // CAMBIAR ESTADO
    public void cambiarEstado(EstadoReserva nuevoEstado) {

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "El estado no puede ser nulo");
        }

        if (estado == nuevoEstado) {
            return;
        }

        if (estado == EstadoReserva.ANULADA ||
                estado == EstadoReserva.FINALIZADA) {
            throw new IllegalStateException(
                    "No se puede modificar una reserva anulada o finalizada");
        }

        if (estado == EstadoReserva.PENDIENTE) {

            if (nuevoEstado != EstadoReserva.CONFIRMADA &&
                    nuevoEstado != EstadoReserva.ANULADA) {
                throw new IllegalStateException(
                        "Transicion de estado no permitida");
            }

            if (nuevoEstado == EstadoReserva.CONFIRMADA) {
                if (pasajeros.isEmpty() || importeTotal <= 0) {
                    throw new IllegalStateException(
                            "La reserva no tiene pasajeros o precio valido");
                }

                if (calcularTotalPagado() + TOLERANCIA
                        < importeTotal * 0.30) {
                    throw new IllegalStateException(
                            "No se alcanzo la sena minima del 30%");
                }
            }
        }

        if (estado == EstadoReserva.CONFIRMADA &&
                nuevoEstado != EstadoReserva.FINALIZADA &&
                nuevoEstado != EstadoReserva.ANULADA) {
            throw new IllegalStateException(
                    "Transicion de estado no permitida");
        }

        this.estado = nuevoEstado;
    }

    // REGISTRAR CANCELACION
    public void registrarCancelacion(Cancelacion cancelacion) {

        if (cancelacion == null) {
            throw new IllegalArgumentException(
                    "La cancelacion es obligatoria");
        }

        if (this.cancelacion != null) {
            throw new IllegalStateException(
                    "La reserva ya tiene una cancelacion registrada");
        }

        if (estado == EstadoReserva.ANULADA ||
                estado == EstadoReserva.FINALIZADA) {
            throw new IllegalStateException(
                    "La reserva no se puede cancelar");
        }

        cambiarEstado(EstadoReserva.ANULADA);
        this.cancelacion = cancelacion;
    }

    public void reemplazarPasajero(String documento, DatosPasajero datos, LocalDate hoy) {
        if (estado != EstadoReserva.PENDIENTE && estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("La reserva no admite reemplazos en su estado actual");
        }
        if (hoy == null || datos == null) {
            throw new IllegalArgumentException("Los datos y la fecha son obligatorios");
        }
        if (hoy.isAfter(salida.getFechaInicio().minusDays(3))) {
            throw new IllegalStateException("El reemplazo solo puede hacerse hasta 3 días antes de la salida");
        }
        if (!salida.puedeParticipar(datos)) {
            throw new IllegalArgumentException("El pasajero no cumple los requisitos del tour");
        }
        int indice = indicePasajero(documento);
        for (Pasajero pasajero : pasajeros) {
            if (pasajero.getDocumento().equalsIgnoreCase(datos.getDocumento())) {
                throw new IllegalArgumentException("El pasajero reemplazante ya está en la reserva");
            }
        }
        Pasajero reemplazante = new Pasajero(datos.getNombre(), datos.getApellido(),
                datos.getDocumento(), pasajeros.get(indice).getTelefono(),
                datos.getFechaNacimiento(), datos.getTipoPasajero(),
                datos.getTelefonoEmergencia(), datos.getRestricciones());
        for (ServicioAdicional servicio : pasajeros.get(indice).getServiciosAdicionales()) {
            reemplazante.agregarServicioAdicional(servicio);
        }
        pasajeros.set(indice, reemplazante);
    }

    public NotaAjuste registrarAjuste(String motivo, double importe, LocalDate fecha) {
        if (fecha == null || motivo == null || motivo.isBlank() || importe == 0) {
            throw new IllegalArgumentException("Fecha, motivo e importe válido son obligatorios");
        }
        double nuevoTotal = importeTotal + importe;
        if (nuevoTotal < calcularTotalPagado()) {
            throw new IllegalArgumentException("El ajuste no puede dejar un saldo cobrado superior al total");
        }
        int id = notasAjuste.size() + 1;
        NotaAjuste nota = new NotaAjuste(id, fecha, motivo, importe, this);
        notasAjuste.add(nota);
        importeTotal = nuevoTotal;
        return nota;
    }

    // VERIFICAR VENCIMIENTO
    public boolean estaVencida(LocalDate fechaSalida) {

        if (fechaSalida == null) {
            throw new IllegalArgumentException(
                    "La fecha de salida es obligatoria");
        }

        if (estado != EstadoReserva.PENDIENTE) {
            return false;
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime vencimiento48Horas =
                fechaCreacion.plusHours(48);

        // Si se reserva a 10 dias o menos,
        // se exige pago total al crear la reserva.
        // No se aplica un vencimiento anterior
        // a la propia fecha de creacion.
        LocalDateTime limiteSaldo =
                fechaSalida.minusDays(10).atStartOfDay();

        LocalDateTime vencimiento;

        if (!limiteSaldo.isAfter(fechaCreacion)) {
            vencimiento = fechaCreacion;
        } else {
            vencimiento = vencimiento48Horas.isBefore(limiteSaldo)
                    ? vencimiento48Horas
                    : limiteSaldo;
        }

        // Una reserva totalmente pagada no vence
        // por falta de pago.
        if (importeTotal > 0 &&
                calcularSaldoPendiente() <= TOLERANCIA) {
            return false;
        }

        return !ahora.isBefore(vencimiento);
    }

    // ANULAR SI ESTA VENCIDA
    public boolean anularSiVencida(LocalDate fechaSalida) {

        if (estaVencida(fechaSalida)) {
            cambiarEstado(EstadoReserva.ANULADA);
            return true;
        }

        return false;
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Salida getSalida() {
        return salida;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public List<Pasajero> getPasajeros() {
        return Collections.unmodifiableList(pasajeros);
    }

    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
    }

    public List<NotaAjuste> getNotasAjuste() {
        return Collections.unmodifiableList(notasAjuste);
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public Cancelacion getCancelacion() {
        return cancelacion;
    }

    public int getNumero() {
        return getId();
    }

    public int cantidadPasajeros() {
        return obtenerCantidadPasajeros();
    }

    public boolean ocupaCupos(LocalDate hoy) {
        if (hoy == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria");
        }

        if (estado == EstadoReserva.CONFIRMADA) {
            return true;
        }

        return estado == EstadoReserva.PENDIENTE
                && !estaVencida(salida.getFechaInicio());
    }

    public void anularPorVencimiento() {
        if (estado != EstadoReserva.PENDIENTE) {
            return;
        }

        if (!estaVencida(salida.getFechaInicio())) {
            throw new IllegalStateException(
                    "La reserva todavia no esta vencida");
        }

        cambiarEstado(EstadoReserva.ANULADA);
    }

    public void finalizar() {
        if (estado == EstadoReserva.CONFIRMADA) {
            cambiarEstado(EstadoReserva.FINALIZADA);
        }
    }

    public boolean puedeCancelarse() {
        return estado == EstadoReserva.PENDIENTE
                || estado == EstadoReserva.CONFIRMADA;
    }

    public void actualizarServiciosAdicionales(double importe) {
        if (!Double.isFinite(importe) || importe < 0) {
            throw new IllegalArgumentException(
                    "El importe de adicionales no es valido");
        }

        if (estado != EstadoReserva.PENDIENTE || !pagos.isEmpty()) {
            throw new IllegalStateException(
                    "No se pueden modificar los adicionales");
        }

        this.importeServiciosAdicionales = importe;
    }

    public double totalServiciosAdicionales() {
        double total = 0;
        for (Pasajero pasajero : pasajeros) {
            total += pasajero.totalServiciosAdicionales();
        }
        return total > 0 ? total : importeServiciosAdicionales;
    }

    private void verificarModificacion() {
        if (estado != EstadoReserva.PENDIENTE || !pagos.isEmpty()) {
            throw new IllegalStateException("La reserva ya no admite modificaciones");
        }
    }

    private boolean tieneServicio(Pasajero pasajero, ServicioAdicional servicio) {
        for (ServicioAdicional existente : pasajero.getServiciosAdicionales()) {
            if (existente.getNombre().equalsIgnoreCase(servicio.getNombre())) return true;
        }
        return false;
    }

    private int indicePasajero(String documento) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        for (int i = 0; i < pasajeros.size(); i++) {
            if (pasajeros.get(i).getDocumento().equalsIgnoreCase(documento)) return i;
        }
        throw new IllegalArgumentException("No existe el pasajero indicado");
    }

    private void actualizarTotalServiciosAdicionales() {
        importeServiciosAdicionales = totalServiciosAdicionales();
    }
}

