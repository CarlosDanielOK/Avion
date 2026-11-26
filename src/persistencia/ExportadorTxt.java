package persistencia;

import enumeraciones.EstadoReserva;
import modelo.Cliente;
import modelo.Pasajero;
import modelo.Reserva;
import modelo.Salida;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Genera archivos de texto (.txt) con la lista de pasajeros de una salida y
 * con el historial de viajes de un cliente.
 *
 * Fabricación pura (GRASP) + SRP: es la única clase que escribe archivos de
 * texto; ni el modelo ni el menú saben cómo se arma o se guarda el archivo.
 */
public class ExportadorTxt {

    private final Path carpeta;

    public ExportadorTxt(String carpeta) {
        if (carpeta == null || carpeta.isBlank()) {
            throw new IllegalArgumentException("La carpeta de exportación es obligatoria");
        }
        this.carpeta = Path.of(carpeta);
    }

    /** Arma las líneas de la lista de pasajeros y las guarda en lista_pasajeros_salida_N.txt. */
    public Path exportarListaPasajeros(Salida salida, List<Pasajero> pasajeros) {
        List<String> lineas = new ArrayList<>();
        lineas.add("LISTA DE PASAJEROS - RUMBO SUR");
        lineas.add("Salida #" + salida.getId() + " - " + salida.getTour().getNombre());
        lineas.add("Fechas: " + salida.getFechaInicio() + " al " + salida.getFechaFin());
        lineas.add("Guía: " + (salida.getGuia() == null ? "sin asignar" : salida.getGuia().obtenerNombreCompleto()));
        lineas.add("Total de pasajeros: " + pasajeros.size());
        lineas.add("--------------------------------------------------");

        int numero = 1;
        for (Pasajero pasajero : pasajeros) {
            String restricciones = pasajero.getRestricciones();
            if (restricciones == null || restricciones.isBlank()) {
                restricciones = "ninguna";
            }
            lineas.add(numero + ". " + pasajero.obtenerNombreCompleto()
                    + " | DNI: " + pasajero.getDocumento()
                    + " | Emergencia: " + pasajero.getContactoEmergencia()
                    + " | Restricciones: " + restricciones);
            numero++;
        }
        return escribir("lista_pasajeros_salida_" + salida.getId() + ".txt", lineas);
    }

    /** Arma el historial del cliente (realizados, pendientes y anuladas) y lo guarda en historial_cliente_DNI.txt. */
    public Path exportarHistorialCliente(Cliente cliente, List<Reserva> reservas) {
        int realizados = 0;
        int pendientes = 0;
        int anuladas = 0;
        List<String> detalle = new ArrayList<>();

        for (Reserva reserva : reservas) {
            if (reserva.getEstado() == EstadoReserva.FINALIZADA) {
                realizados++;
            } else if (reserva.getEstado() == EstadoReserva.ANULADA) {
                anuladas++;
            } else {
                pendientes++; // PENDIENTE o CONFIRMADA: el viaje todavía no se realizó
            }
            double saldo = reserva.calcularSaldoPendiente();
            // En una reserva anulada no tiene sentido decir "pagada" o "con saldo".
            String situacionPago = "";
            if (reserva.getEstado() != EstadoReserva.ANULADA) {
                situacionPago = saldo < 0.01 ? " | PAGADA" : " | CON SALDO";
            }
            detalle.add("Reserva #" + reserva.getId()
                    + " | " + reserva.getSalida().getTour().getNombre()
                    + " | Salida #" + reserva.getSalida().getId() + " (" + reserva.getSalida().getFechaInicio() + ")"
                    + " | " + reserva.getEstado()
                    + " | Pasajeros: " + reserva.cantidadPasajeros()
                    + String.format(Locale.US, " | Total $%,.2f | Pagado $%,.2f | Saldo $%,.2f",
                    reserva.getImporteTotal(), reserva.calcularTotalPagado(), saldo)
                    + situacionPago);
        }

        List<String> lineas = new ArrayList<>();
        lineas.add("HISTORIAL DE VIAJES - RUMBO SUR");
        lineas.add("Cliente: " + cliente.obtenerNombreCompleto() + " (DNI " + cliente.getDocumento() + ")");
        lineas.add("Realizados: " + realizados + " | Pendientes: " + pendientes + " | Anuladas: " + anuladas);
        lineas.add("--------------------------------------------------");
        if (detalle.isEmpty()) {
            lineas.add("El cliente no tiene reservas registradas.");
        } else {
            lineas.addAll(detalle);
        }
        return escribir("historial_cliente_" + cliente.getDocumento() + ".txt", lineas);
    }

    /** Crea la carpeta si hace falta y escribe todas las líneas (Files.write abre y cierra el archivo solo). */
    private Path escribir(String nombreArchivo, List<String> lineas) {
        try {
            Files.createDirectories(carpeta);
            return Files.write(carpeta.resolve(nombreArchivo), lineas);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo escribir el archivo " + nombreArchivo, e);
        }
    }
}
