package controladores;

import enumeraciones.EstadoReserva;
import enumeraciones.MedioPago;
import enumeraciones.TipoPasajero;
import enumeraciones.TipoServicio;
import modelo.Agencia;
import modelo.Cliente;
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
        tour.agregarServicioAdicional(traslado);
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
        controlador.calcularPrecio(1, 1000, salidaFecha, reserva.totalServiciosAdicionales());
        controlador.registrarPago(1, 1, 1100, MedioPago.EFECTIVO, 1);
        controlador.confirmarReserva(1, salidaFecha);
        verificar(reserva.getEstado() == EstadoReserva.CONFIRMADA, "la reserva se confirma con el pago");

        controlador.reemplazarPasajero(1, "789",
                new DatosPasajero("Pedro", "Ruiz", "790", LocalDate.of(1990, 1, 1),
                        TipoPasajero.ADULTO, "11", ""), LocalDate.now());
        verificar(reserva.getPasajeros().get(0).getDocumento().equals("790"),
                "se reemplaza el pasajero sin modificar el precio");
        controlador.registrarAjuste(1, "Diferencia administrativa", 50, LocalDate.now());
        verificar(reserva.getNotasAjuste().size() == 1 && reserva.getImporteTotal() == 1150,
                "la nota de ajuste modifica el total sin borrar pagos");

        controlador.cancelarReserva(1, "Motivo de prueba", salidaFecha, false);
        verificar(reserva.getEstado() == EstadoReserva.ANULADA, "la reserva se cancela");
        verificar(salida.cuposDisponibles(LocalDate.now()) == 4, "la cancelación libera el cupo");
        System.out.println("ControladorReservasTest: OK");
    }

    private static void verificar(boolean condicion, String descripcion) {
        if (!condicion) throw new AssertionError("Falló: " + descripcion);
    }
}
