package interfaz;

import controladores.ControladorOperaciones;
import enumeraciones.CategoriaTour;
import enumeraciones.MedioPago;
import enumeraciones.NivelDificultad;
import enumeraciones.EstadoReserva;
import enumeraciones.Temporada;
import enumeraciones.TipoPasajero;
import enumeraciones.TipoServicio;
import enumeraciones.TipoVehiculo;
import excepciones.ReglaNegocioException;
import modelo.Agencia;
import modelo.Cancelacion;
import modelo.Cliente;
import modelo.Guia;
import modelo.Pago;
import modelo.Pasajero;
import modelo.Reserva;
import modelo.Salida;
import modelo.ServicioAdicional;
import modelo.Tarifa;
import modelo.Tour;
import modelo.TourAventura;
import modelo.TourCultural;
import modelo.Vehiculo;
import modelo.Vendedor;

import java.time.LocalDate;
import java.util.Arrays;

/** Datos de prueba para la demostración: permiten ver los reportes y la operación sin cargar todo a mano. */
final class DatosEjemplo {

    private DatosEjemplo() {
    }

    static void cargar(Agencia agencia, ControladorOperaciones operaciones, LocalDate hoy)
            throws ReglaNegocioException {
        // Catálogo: una aventura (con seguro obligatorio) y un cultural.
        Tour aventura = new TourAventura("AVE01", "Trekking Cerro Catedral", "Ascenso con guía", 3, 4, 12,
                NivelDificultad.MEDIO, 12);
        Tour cultural = new TourCultural("CUL01", "Buenos Aires Histórica", "Recorrido por los barrios", 2, 4, 15);
        operaciones.registrarTour(aventura);
        operaciones.registrarTour(cultural);
        for (Tour tour : Arrays.asList(aventura, cultural)) {
            operaciones.registrarTarifa(tour.getCodigo(),
                    new Tarifa(Temporada.MEDIA, hoy.minusDays(30), hoy.plusDays(365), tour == aventura ? 150000 : 80000));
        }
        operaciones.agregarServicioAdicional("AVE01", new ServicioAdicional("Seguro de viaje", 12000, TipoServicio.SEGURO, true));

        // Recursos y personal.
        operaciones.registrarGuia(new Guia(101, "Martín", "Sosa", "30111222", "1155550001",
                Arrays.asList("Español", "Inglés"), Arrays.asList(CategoriaTour.AVENTURA, CategoriaTour.CULTURAL), 40000));
        operaciones.registrarVehiculo(new Vehiculo("AB123CD", 20, TipoVehiculo.COMBI));
        Vendedor ana = new Vendedor(201, "Ana", "Pérez", "28333444", "1155550002", 5);
        Vendedor luis = new Vendedor(202, "Luis", "Gómez", "29555666", "1155550003", 5);
        agencia.agregarEmpleado(ana);
        agencia.agregarEmpleado(luis);

        // Salidas (CU04).
        Salida salidaAventura = operaciones.programarSalida("AVE01", hoy.plusDays(40), hoy.plusDays(42), 12);
        Salida salidaCultural = operaciones.programarSalida("CUL01", hoy.plusDays(30), hoy.plusDays(31), 15);

        // Clientes y reservas con distinto estado.
        Cliente c1 = nuevoCliente(agencia, "Carla", "Ruiz", "35000001");
        Cliente c2 = nuevoCliente(agencia, "Diego", "López", "35000002");
        Cliente c3 = nuevoCliente(agencia, "Elena", "Mora", "35000003");

        Reserva r1 = nuevaReserva(agencia, 1, c1, salidaAventura, ana, 600000, 4, "36000001");
        pagar(r1, 1, 600000, hoy);
        r1.cambiarEstado(EstadoReserva.CONFIRMADA);

        Reserva r2 = nuevaReserva(agencia, 2, c2, salidaCultural, luis, 160000, 2, "36000011");
        pagar(r2, 2, 80000, hoy);
        r2.cambiarEstado(EstadoReserva.CONFIRMADA);

        Reserva r3 = nuevaReserva(agencia, 3, c3, salidaCultural, ana, 80000, 1, "36000021");
        pagar(r3, 3, 80000, hoy);
        r3.cambiarEstado(EstadoReserva.CONFIRMADA);
        r3.registrarCancelacion(new Cancelacion(hoy, "El cliente no puede viajar", false, 0.5, 40000));

        operaciones.actualizarEstadoSalida(salidaAventura.getId(), hoy);
        operaciones.actualizarEstadoSalida(salidaCultural.getId(), hoy);
    }

    private static Cliente nuevoCliente(Agencia agencia, String nombre, String apellido, String documento) {
        Cliente cliente = new Cliente(nombre, apellido, documento, "1100000000", nombre.toLowerCase() + "@mail.com", "Argentina");
        agencia.agregarCliente(cliente);
        return cliente;
    }

    private static Reserva nuevaReserva(Agencia agencia, int id, Cliente cliente, Salida salida, Vendedor vendedor,
                                        double importe, int cantidadPasajeros, String documentoBase) throws ReglaNegocioException {
        Reserva reserva = new Reserva(id, cliente, salida, vendedor);
        long base = Long.parseLong(documentoBase);
        for (int i = 0; i < cantidadPasajeros; i++) {
            reserva.agregarPasajero(new Pasajero("Pasajero" + (i + 1), cliente.getApellido(), String.valueOf(base + i),
                    "1100000000", LocalDate.of(1990, 5, 10), TipoPasajero.ADULTO, "Contacto 1100000001", ""));
        }
        reserva.actualizarImporteTotal(importe);
        salida.agregarReserva(reserva);
        agencia.agregarReserva(reserva);
        return reserva;
    }

    private static void pagar(Reserva reserva, int idPago, double importe, LocalDate hoy) {
        reserva.registrarPago(new Pago(idPago, hoy, importe, MedioPago.TRANSFERENCIA, 1));
    }
}