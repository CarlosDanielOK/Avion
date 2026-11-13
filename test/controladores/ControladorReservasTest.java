package controladores;

import enumeraciones.MedioPago;
import modelo.*;
import persistencia.RepositorioAgenciaArchivo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;

public class ControladorReservasTest {
    public static void main(String[] args) throws Exception {
        Path archivo = Files.createTempFile("agencia-reservas-", ".dat");
        try {
            Agencia agencia = new Agencia();
            RepositorioAgenciaArchivo repo = new RepositorioAgenciaArchivo(archivo.toString());
            ControladorOperaciones operaciones = new ControladorOperaciones(
                    agencia, repo, new GestorCancelaciones());
            Tour tour = new TourCultural("CUL", "Cultural", "Tour", 1, 1, 10);
            operaciones.registrarTour(tour);
            Cliente cliente = new Cliente("Ana", "Pérez", "1", "2", "a@a.com", "Argentina");
            Vendedor vendedor = new Vendedor(10, "V", "V", "3", "4", 5);
            agencia.agregarCliente(cliente);
            agencia.agregarEmpleado(vendedor);
            Salida salida = new Salida(1, tour, LocalDate.now().plusDays(20),
                    LocalDate.now().plusDays(20), 10);
            agencia.agregarSalida(salida);

            ControladorReservas reservas = new ControladorReservas(agencia, repo);
            Pasajero pasajero = new Pasajero("P", "P", "5", "6",
                    LocalDate.of(1990, 1, 1), enumeraciones.TipoPasajero.ADULTO, "7", "");
            Reserva reserva = reservas.crearReserva("1", 1, 10, Collections.singletonList(pasajero));
            reservas.calcularPrecio(reserva.getId(), 100, salida.getFechaInicio(), 0);
            reservas.registrarPago(reserva.getId(), 1, 30, MedioPago.EFECTIVO, 1);
            reservas.confirmarReserva(reserva.getId(), salida.getFechaInicio());
            comprobar(reserva.getEstado() == enumeraciones.EstadoReserva.CONFIRMADA,
                    "La reserva debe confirmarse con una seña del 30%");
            comprobar(repo.cargar().getReservas().size() == 1,
                    "La reserva debe persistirse en la agencia");
            System.out.println("ControladorReservasTest: OK");
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
