package persistencia;

import enumeraciones.EstadoReserva;
import enumeraciones.TipoPasajero;
import modelo.Agencia;
import modelo.Cliente;
import modelo.Pasajero;
import modelo.Reserva;
import modelo.Salida;
import modelo.TourCultural;
import modelo.Vendedor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/** Pruebas de ExportadorTxt y de Agencia.buscarReservasDeCliente (CU25 y CU26). */
public class ExportadorTxtTest {

    public static void main(String[] args) throws Exception {
        Path carpeta = Files.createTempDirectory("rumbosur");
        ExportadorTxt exportador = new ExportadorTxt(carpeta.toString());

        Agencia agencia = new Agencia();
        TourCultural tour = new TourCultural("CUL01", "Buenos Aires Histórica", "Recorrido", 2, 2, 10);
        Salida salida = new Salida(1, tour, LocalDate.now().plusDays(30), LocalDate.now().plusDays(31), 10);
        Cliente carla = new Cliente("Carla", "Ruiz", "222", "1", "c@mail.com", "Argentina");
        Cliente diego = new Cliente("Diego", "López", "333", "1", "d@mail.com", "Argentina");
        Vendedor vendedor = new Vendedor(1, "Luis", "Pérez", "444", "22", 5);
        agencia.agregarCliente(carla);
        agencia.agregarCliente(diego);

        Reserva r1 = new Reserva(1, carla, salida, vendedor);
        r1.agregarPasajero(new Pasajero("Ana", "Ruiz", "111", "1", LocalDate.of(1990, 1, 1),
                TipoPasajero.ADULTO, "Contacto 99", "celíaca"));
        Reserva r2 = new Reserva(2, diego, salida, vendedor);
        r2.agregarPasajero(new Pasajero("Beto", "López", "112", "1", LocalDate.of(1985, 1, 1),
                TipoPasajero.ADULTO, "Contacto 98", ""));
        r1.actualizarImporteTotal(100000); // sin pagos: queda con saldo
        agencia.agregarReserva(r1);
        agencia.agregarReserva(r2);

        // Agencia: solo devuelve las reservas del cliente pedido.
        List<Reserva> deCarla = agencia.buscarReservasDeCliente("222");
        verificar(deCarla.size() == 1 && deCarla.get(0) == r1, "filtra las reservas por cliente");
        verificar(agencia.buscarReservasDeCliente("999").isEmpty(), "cliente sin reservas devuelve lista vacía");

        // CU25: lista de pasajeros.
        Path lista = exportador.exportarListaPasajeros(salida, r1.getPasajeros());
        List<String> lineasLista = Files.readAllLines(lista);
        verificar(lista.getFileName().toString().equals("lista_pasajeros_salida_1.txt"), "nombre del archivo de lista");
        verificar(lineasLista.get(0).contains("LISTA DE PASAJEROS"), "título de la lista");
        verificar(contiene(lineasLista, "DNI: 111") && contiene(lineasLista, "Restricciones: celíaca"),
                "incluye documento y restricciones (con tildes)");
        verificar(contiene(lineasLista, "Guía: sin asignar"), "salida sin guía asignado");

        // CU26: historial con una reserva pendiente.
        Path historial = exportador.exportarHistorialCliente(carla, deCarla);
        List<String> lineasHist = Files.readAllLines(historial);
        verificar(contiene(lineasHist, "Realizados: 0 | Pendientes: 1 | Anuladas: 0"), "cuenta las reservas por estado");
        verificar(contiene(lineasHist, "CON SALDO"), "indica que la reserva todavía tiene saldo");

        // CU26: una reserva anulada se cuenta como anulada y no lleva etiqueta de pago.
        r2.cambiarEstado(EstadoReserva.ANULADA);
        List<String> lineasDiego = Files.readAllLines(exportador.exportarHistorialCliente(diego, agencia.buscarReservasDeCliente("333")));
        verificar(contiene(lineasDiego, "Anuladas: 1"), "cuenta la reserva anulada");
        verificar(!contiene(lineasDiego, "PAGADA") && !contiene(lineasDiego, "CON SALDO"),
                "la reserva anulada no muestra etiqueta de pago");

        // CU26 flujo alternativo: cliente sin reservas.
        Cliente nuevo = new Cliente("Nora", "Paz", "444", "1", "n@mail.com", "Chile");
        List<String> vacio = Files.readAllLines(exportador.exportarHistorialCliente(nuevo, agencia.buscarReservasDeCliente("444")));
        verificar(contiene(vacio, "no tiene reservas registradas"), "historial vacío con aclaración");

        // Error de escritura: la "carpeta" es en realidad un archivo.
        Path archivoComun = Files.createTempFile("rumbosur", ".txt");
        try {
            new ExportadorTxt(archivoComun.toString()).exportarListaPasajeros(salida, r1.getPasajeros());
            throw new AssertionError("Debía lanzar IllegalStateException");
        } catch (IllegalStateException esperado) {
            verificar(esperado.getMessage().contains("No se pudo escribir"), "mensaje claro si falla la escritura");
        }
        System.out.println("ExportadorTxtTest: OK");
    }

    private static boolean contiene(List<String> lineas, String texto) {
        for (String linea : lineas) {
            if (linea.contains(texto)) return true;
        }
        return false;
    }

    private static void verificar(boolean condicion, String descripcion) {
        if (!condicion) throw new AssertionError("Falló: " + descripcion);
    }
}
