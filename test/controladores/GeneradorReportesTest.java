package controladores;

import enumeraciones.EstadoReserva;
import enumeraciones.MedioPago;
import enumeraciones.TipoPasajero;
import modelo.*;

import java.time.LocalDate;

/** Pruebas de GeneradorReportes (CU24). Ejecutable sin librerías externas. */
public class GeneradorReportesTest {

    public static void main(String[] args) throws Exception {
        reportesVaciosAclaranQueNoHayDatos();
        reportesConDatos();
        System.out.println("GeneradorReportesTest: OK");
    }

    private static void reportesVaciosAclaranQueNoHayDatos() {
        GeneradorReportes reportes = new GeneradorReportes(new Agencia());
        verificar(reportes.reporteOcupacionPorSalida().contains("vacío"), "ocupación vacía");
        verificar(reportes.reporteVentasPorVendedor().contains("vacío"), "ventas vacías");
        verificar(reportes.reporteIngresosPorTour().contains("vacío"), "ingresos vacíos");
        verificar(reportes.reporteCancelaciones().contains("vacío"), "cancelaciones vacías");
    }

    private static void reportesConDatos() throws Exception {
        LocalDate hoy = LocalDate.now();
        Agencia agencia = new Agencia();
        TourCultural tour = new TourCultural("CUL01", "Buenos Aires Histórica", "Recorrido", 2, 2, 10);
        Salida salida = new Salida(1, tour, hoy.plusDays(30), hoy.plusDays(31), 10);
        agencia.agregarTour(tour);
        agencia.agregarSalida(salida);
        Vendedor vendedor = new Vendedor(1, "Ana", "Pérez", "111", "1", 5);
        Cliente cliente = new Cliente("Carla", "Ruiz", "222", "1", "c@mail.com", "Argentina");

        Reserva vigente = reserva(1, cliente, salida, vendedor, 2000, "A");
        vigente.registrarPago(new Pago(1, hoy, 2000, MedioPago.EFECTIVO, 1));
        vigente.cambiarEstado(EstadoReserva.CONFIRMADA);

        Reserva cancelada = reserva(2, cliente, salida, vendedor, 1000, "B");
        cancelada.registrarPago(new Pago(2, hoy, 1000, MedioPago.EFECTIVO, 1));
        cancelada.cambiarEstado(EstadoReserva.CONFIRMADA);
        cancelada.registrarCancelacion(new Cancelacion(hoy, "Motivo de prueba", false, 0.5, 500));

        for (Reserva r : new Reserva[]{vigente, cancelada}) {
            salida.agregarReserva(r);
            agencia.agregarReserva(r);
        }

        GeneradorReportes reportes = new GeneradorReportes(agencia);
        verificar(reportes.reporteOcupacionPorSalida(hoy).contains("1/10"), "solo la reserva vigente ocupa cupos");
        verificar(reportes.reporteVentasPorVendedor().contains("$2,000.00"), "las anuladas no cuentan como venta");
        verificar(reportes.reporteIngresosPorTour().contains("$2,500.00"), "neto = 3000 cobrado - 500 devuelto");
        verificar(reportes.reporteCancelaciones().contains("Motivo de prueba"), "se informa el motivo");
    }

    private static Reserva reserva(int id, Cliente cliente, Salida salida, Vendedor vendedor, double importe, String doc) {
        Reserva reserva = new Reserva(id, cliente, salida, vendedor);
        reserva.agregarPasajero(new Pasajero("Pas", doc, "9" + doc + id, "1", LocalDate.of(1990, 1, 1),
                TipoPasajero.ADULTO, "Contacto", ""));
        reserva.actualizarImporteTotal(importe);
        return reserva;
    }

    private static void verificar(boolean condicion, String descripcion) {
        if (!condicion) throw new AssertionError("Falló: " + descripcion);
    }
}
