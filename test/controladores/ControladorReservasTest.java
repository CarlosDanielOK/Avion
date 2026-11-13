package controladores;

import enumeraciones.EstadoReserva;
import enumeraciones.MedioPago;
import enumeraciones.TipoPasajero;
import enumeraciones.TipoServicio;
import modelo.Agencia;
import modelo.Cliente;
import modelo.Cancelacion;
import modelo.DatosPasajero;
import modelo.Pasajero;
import modelo.Reserva;
import modelo.Salida;
import modelo.ServicioAdicional;
import modelo.Tarifa;
import modelo.TourCultural;
import modelo.Vendedor;
import persistencia.RepositorioAgencia;

import java.time.LocalDate;

/** Prueba la integración de reservas con la raíz de la agencia y su repositorio. */
public class ControladorReservasTest {

    public static void main(String[] args) throws Exception {
        Agencia agencia = new Agencia();
        RepositorioAgencia repositorio = new RepositorioAgencia() {
            @Override
            public void guardar(Agencia agencia) { }

            @Override
            public Agencia cargar() { return agencia; }
        };
        ControladorReservas controlador = new ControladorReservas(agencia, repositorio);
        LocalDate salidaFecha = LocalDate.now().plusDays(30);
        TourCultural tour = new TourCultural("CUL01", "Tour cultural", "Descripción", 2, 1, 4);
        ServicioAdicional traslado = new ServicioAdicional("Traslado", 100, TipoServicio.TRASLADO, false);
        ServicioAdicional excursion = new ServicioAdicional("Excursión opcional", 200,
                TipoServicio.EXCURSION, false);
        tour.agregarServicioAdicional(traslado);
        tour.agregarServicioAdicional(excursion);
        tour.agregarTarifa(new Tarifa(enumeraciones.Temporada.MEDIA,
                LocalDate.now().minusDays(1), salidaFecha.plusDays(1), 1000));
        Salida salida = new Salida(1, tour, salidaFecha, salidaFecha.plusDays(1), 4);
        Cliente cliente = new Cliente("Ana", "Ruiz", "123", "11", "a@mail.com", "Argentina");
        Vendedor vendedor = new Vendedor(1, "Luis", "Pérez", "456", "22", 5);
        agencia.agregarSalida(salida);
        agencia.agregarCliente(cliente);
        agencia.agregarEmpleado(vendedor);

        Reserva reserva = new Reserva(1, cliente, salida, vendedor);
        reserva.agregarPasajero(new Pasajero("Pablo", "Ruiz", "789", "33",
                LocalDate.of(1990, 1, 1), TipoPasajero.ADULTO, "11", ""));
        controlador.registrarReserva(reserva);
        verificar(agencia.getReservas().contains(reserva), "la reserva se registra en la agencia");
        verificar(salida.cuposOcupados(LocalDate.now()) == 1, "la reserva retiene un cupo");

        controlador.agregarServicioAdicional(1, "789", traslado);
        double precioCalculado = controlador.calcularPrecio(1);
        verificar(precioCalculado == 1100.0, "el precio usa la tarifa registrada y los adicionales reales");
        controlador.agregarServicioAdicional(1, "789", excursion);
        verificar(reserva.getImporteTotal() == 0,
                "agregar un adicional invalida el precio anterior para obligar a recalcularlo");
        precioCalculado = controlador.calcularPrecio(1);
        verificar(precioCalculado == 1300.0, "el precio se recalcula con el nuevo adicional");
        controlador.registrarPago(1, 1, 1300, MedioPago.EFECTIVO, 1);
        controlador.confirmarReserva(1);
        verificar(reserva.getEstado() == EstadoReserva.CONFIRMADA, "la reserva se confirma con el pago");

        controlador.reemplazarPasajero(1, "789",
                new DatosPasajero("Pedro", "Ruiz", "790", LocalDate.of(1990, 1, 1),
                        TipoPasajero.ADULTO, "11", ""), LocalDate.now());
        verificar(reserva.getPasajeros().get(0).getDocumento().equals("790"),
                "se reemplaza el pasajero sin modificar el precio");
        controlador.registrarAjuste(1, "Diferencia administrativa", 50, LocalDate.now());
        verificar(reserva.getNotasAjuste().size() == 1 && reserva.getImporteTotal() == 1350,
                "la nota de ajuste modifica el total sin borrar pagos");
        controlador.registrarPago(1, 2, 50, MedioPago.TRANSFERENCIA, 1);

        Cancelacion cancelacion = controlador.cancelarReserva(1, "Motivo de prueba", false);
        verificar(reserva.getEstado() == EstadoReserva.ANULADA, "la reserva se cancela");
        verificar(cancelacion.getMedioDevolucion() == MedioPago.TRANSFERENCIA,
                "la devolución conserva el medio del último pago");
        verificar(salida.cuposDisponibles(LocalDate.now()) == 4, "la cancelación libera el cupo");
        rechazaAltaDeReservaEnSalidaCancelada();
        permiteReservaQueAlcanzaElMinimoAUnaSemana();
        anulaReservaVencidaAntesDeConfirmar();
        System.out.println("ControladorReservasTest: OK");
    }

    private static void rechazaAltaDeReservaEnSalidaCancelada() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(5);
        TourCultural tour = new TourCultural("CUL02", "Tour próximo", "Descripción", 1, 2, 4);
        Salida salida = new Salida(2, tour, fecha, fecha, 4);
        salida.actualizarEstado(LocalDate.now());
        Cliente cliente = new Cliente("Ana", "Ruiz", "321", "11", "a@mail.com", "Argentina");
        Vendedor vendedor = new Vendedor(2, "Luis", "Pérez", "654", "22", 5);
        Reserva reserva = new Reserva(2, cliente, salida, vendedor);
        reserva.agregarPasajero(new Pasajero("Pablo", "Ruiz", "987", "33",
                LocalDate.of(1990, 1, 1), TipoPasajero.ADULTO, "11", ""));
        try {
            salida.agregarReserva(reserva);
            throw new AssertionError("No debía permitir reservas para una salida cancelada");
        } catch (excepciones.OperacionNoPermitidaException esperado) {
            // correcto
        }
    }

    private static void permiteReservaQueAlcanzaElMinimoAUnaSemana() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(5);
        TourCultural tour = new TourCultural("CUL03", "Tour próximo", "Descripción", 1, 2, 4);
        Salida salida = new Salida(3, tour, fecha, fecha, 4);
        Agencia agencia = new Agencia();
        agencia.agregarSalida(salida);
        ControladorReservas controlador = new ControladorReservas(agencia, null);
        Cliente cliente = new Cliente("Ana", "Ruiz", "541", "11", "a@mail.com", "Argentina");
        Vendedor vendedor = new Vendedor(3, "Luis", "Pérez", "654", "22", 5);
        Reserva reserva = new Reserva(3, cliente, salida, vendedor);
        for (int i = 0; i < 2; i++) {
            reserva.agregarPasajero(new Pasajero("P" + i, "Ruiz", "543" + i, "33",
                    LocalDate.of(1990, 1, 1), TipoPasajero.ADULTO, "11", ""));
        }
        controlador.registrarReserva(reserva);
        verificar(salida.getEstado() == enumeraciones.EstadoSalida.CONFIRMADA,
                "una reserva que alcanza el cupo mínimo a cinco días confirma la salida");
        verificar(reserva.getEstado() == EstadoReserva.PENDIENTE,
                "la reserva queda pendiente de seña aunque la salida esté confirmada");
    }

    private static void anulaReservaVencidaAntesDeConfirmar() throws Exception {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaSalida = hoy.plusDays(30);
        Agencia agencia = new Agencia();
        ControladorReservas controlador = new ControladorReservas(agencia, null);
        TourCultural tour = new TourCultural("CUL04", "Tour con reserva vencible", "Descripción", 1, 1, 4);
        tour.agregarTarifa(new Tarifa(enumeraciones.Temporada.MEDIA,
                hoy.minusDays(1), fechaSalida.plusDays(1), 1000));
        Salida salida = new Salida(4, tour, fechaSalida, fechaSalida, 4);
        agencia.agregarSalida(salida);
        Cliente cliente = new Cliente("Ana", "Ruiz", "741", "11", "a@mail.com", "Argentina");
        Vendedor vendedor = new Vendedor(4, "Luis", "Pérez", "742", "22", 5);
        Reserva reserva = new Reserva(4, cliente, salida, vendedor);
        reserva.agregarPasajero(new Pasajero("Pablo", "Ruiz", "743", "33",
                LocalDate.of(1990, 1, 1), TipoPasajero.ADULTO, "11", ""));
        controlador.registrarReserva(reserva);

        try {
            // La reserva fue creada hoy; al operar tres días después ya superó S04.
            controlador.confirmarReserva(4, hoy.plusDays(3));
            throw new AssertionError("No debía confirmar una reserva vencida");
        } catch (IllegalStateException esperado) {
            // La verificación de vigencia debe anularla antes de confirmar.
        }
        verificar(reserva.getEstado() == EstadoReserva.ANULADA,
                "una operación sobre la reserva detecta y aplica el vencimiento S09");
        verificar(salida.cuposDisponibles(hoy.plusDays(3)) == 4,
                "la reserva vencida libera los cupos");
    }

    private static void verificar(boolean condicion, String descripcion) {
        if (!condicion) throw new AssertionError("Falló: " + descripcion);
    }
}