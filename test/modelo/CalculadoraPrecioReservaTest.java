package modelo;

import java.time.LocalDate;
import java.util.Collections;

import enumeraciones.TipoPasajero;

/** Verifica que se pueda extender el cálculo incorporando una nueva promoción. */
public class CalculadoraPrecioReservaTest {

    public static void main(String[] args) {
        LocalDate fechaSalida = LocalDate.now().plusDays(90);
        TourCultural tour = new TourCultural("TEST", "Tour test", "Prueba", 1, 1, 5);
        Salida salida = new Salida(1, tour, fechaSalida, fechaSalida, 5);
        Cliente cliente = new Cliente("Ana", "Pérez", "100", "11", "ana@mail.com", "Argentina");
        Vendedor vendedor = new Vendedor(1, "Luis", "Gómez", "200", "22", 5);
        Reserva reserva = new Reserva(1, cliente, salida, vendedor);
        reserva.agregarPasajero(new Pasajero("Pablo", "Pérez", "300", "33",
                LocalDate.of(1990, 1, 1), TipoPasajero.ADULTO, "11", ""));

        Promocion promocionNueva = new Promocion() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean esAplicable(Reserva reserva, LocalDate fechaSalida) {
                return reserva != null && fechaSalida != null;
            }

            @Override
            public double calcularDescuento(double subtotalTour) {
                return subtotalTour * 0.20;
            }
        };

        CalculadoraPrecioReserva calculadora = new CalculadoraPrecioReserva(
                Collections.singletonList(promocionNueva));
        double total = calculadora.calcularTotal(reserva, 1000, fechaSalida, 0);
        verificar(total == 800, "la calculadora evalúa la promoción recibida por constructor");
        System.out.println("CalculadoraPrecioReservaTest: OK");
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError("Falló: " + mensaje);
    }
}
