package modelo;

import enumeraciones.EstadoReserva;
import enumeraciones.EstadoSalida;
import enumeraciones.MedioPago;
import enumeraciones.TipoPasajero;

import java.time.LocalDate;

/** Pruebas de Salida: CU04 (validaciones al programar) y CU05 (estados y cupos). */
public class SalidaTest {

    private static final LocalDate HOY = LocalDate.now();

    public static void main(String[] args) throws Exception {
        rechazaFechasInconsistentes();
        rechazaCupoFueraDeLimitesDelTour();
        nuevaSalidaQuedaProgramada();
        confirmaAlAlcanzarElCupoMinimo();
        quedaCompletaAlAlcanzarElCupoMaximo();
        sePuedeCancelarPorCupoMinimoAUnaSemana();
        System.out.println("SalidaTest: OK");
    }

    // ---- CU04 ----

    private static void rechazaFechasInconsistentes() {
        try {
            new Salida(1, tour(), HOY.plusDays(30), HOY.plusDays(29), 10);
            throw new AssertionError("Debía rechazar una fecha de fin anterior al inicio");
        } catch (IllegalArgumentException esperado) {
            // correcto
        }
    }

    private static void rechazaCupoFueraDeLimitesDelTour() {
        Tour tour = tour(); // cupo mínimo 2, máximo 10
        for (int cupo : new int[]{1, 11}) {
            try {
                new Salida(1, tour, HOY.plusDays(30), HOY.plusDays(31), cupo);
                throw new AssertionError("Debía rechazar el cupo " + cupo);
            } catch (IllegalArgumentException esperado) {
                // correcto
            }
        }
    }

    private static void nuevaSalidaQuedaProgramada() {
        Salida salida = new Salida(1, tour(), HOY.plusDays(30), HOY.plusDays(31), 10);
        verificar(salida.getEstado() == EstadoSalida.PROGRAMADA, "una salida nueva queda programada");
        verificar(salida.cuposDisponibles(HOY) == 10, "sin reservas todos los cupos están disponibles");
    }

    // ---- CU05 ----

    private static void confirmaAlAlcanzarElCupoMinimo() throws Exception {
        Salida salida = new Salida(1, tour(), HOY.plusDays(30), HOY.plusDays(31), 10);
        agregarReservaConfirmada(salida, 1, 2);
        salida.actualizarEstado(HOY);
        verificar(salida.getEstado() == EstadoSalida.CONFIRMADA, "con el cupo mínimo la salida se confirma");
        verificar(salida.cuposDisponibles(HOY) == 8, "se descuentan los cupos ocupados");
    }

    private static void quedaCompletaAlAlcanzarElCupoMaximo() throws Exception {
        Salida salida = new Salida(1, tour(), HOY.plusDays(30), HOY.plusDays(31), 2);
        agregarReservaConfirmada(salida, 1, 2);
        salida.actualizarEstado(HOY);
        verificar(salida.getEstado() == EstadoSalida.COMPLETA, "con el cupo máximo la salida queda completa");
        verificar(salida.cuposDisponibles(HOY) == 0, "una salida completa no tiene cupos");
    }

    private static void sePuedeCancelarPorCupoMinimoAUnaSemana() {
        Salida salida = new Salida(1, tour(), HOY.plusDays(5), HOY.plusDays(6), 10);
        salida.actualizarEstado(HOY);
        verificar(salida.getEstado() == EstadoSalida.CANCELADA,
                "a 7 días o menos sin cupo mínimo la salida se cancela");
    }

    // ---- auxiliares ----

    private static Tour tour() {
        return new TourCultural("CUL01", "Buenos Aires Histórica", "Recorrido", 2, 2, 10);
    }

    private static void agregarReservaConfirmada(Salida salida, int id, int cantidadPasajeros) throws Exception {
        Vendedor vendedor = new Vendedor(1, "Ana", "Pérez", "111", "1", 5);
        Cliente cliente = new Cliente("Carla", "Ruiz", "222", "1", "c@mail.com", "Argentina");
        Reserva reserva = new Reserva(id, cliente, salida, vendedor);
        for (int i = 0; i < cantidadPasajeros; i++) {
            reserva.agregarPasajero(new Pasajero("Pas" + i, "Test", "9" + id + i, "1",
                    LocalDate.of(1990, 1, 1), TipoPasajero.ADULTO, "Contacto", ""));
        }
        reserva.actualizarImporteTotal(1000);
        reserva.registrarPago(new Pago(id, HOY, 1000, MedioPago.EFECTIVO, 1));
        reserva.cambiarEstado(EstadoReserva.CONFIRMADA);
        salida.agregarReserva(reserva);
    }

    private static void verificar(boolean condicion, String descripcion) {
        if (!condicion) throw new AssertionError("Falló: " + descripcion);
    }
}
