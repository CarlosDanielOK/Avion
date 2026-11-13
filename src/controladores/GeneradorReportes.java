package controladores;

import enumeraciones.EstadoReserva;
import modelo.Agencia;
import modelo.Cancelacion;
import modelo.Reserva;
import modelo.Salida;
import modelo.Tour;
import modelo.Vendedor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CU24 – Generar reportes. Fabricación pura: solo consulta la agencia y arma
 * el texto de cada reporte; no modifica ningún dato ni imprime por su cuenta.
 */
public class GeneradorReportes {

    private static final String SEPARADOR = "--------------------------------------------------------------";

    private final Agencia agencia;

    public GeneradorReportes(Agencia agencia) {
        if (agencia == null) throw new IllegalArgumentException("La agencia es obligatoria");
        this.agencia = agencia;
    }

    /** Igual que {@link #reporteOcupacionPorSalida(LocalDate)} usando la fecha actual. */
    public String reporteOcupacionPorSalida() {
        return reporteOcupacionPorSalida(LocalDate.now());
    }

    /** Cupos ocupados sobre el cupo máximo de cada salida. */
    public String reporteOcupacionPorSalida(LocalDate hoy) {
        StringBuilder sb = titulo("REPORTE: OCUPACIÓN POR SALIDA");
        List<Salida> salidas = new ArrayList<>(agencia.getSalidas());
        if (salidas.isEmpty()) return sb.append(vacio("No hay salidas registradas.")).toString();

        Collections.sort(salidas);
        sb.append(String.format("%-4s %-26s %-11s %-11s %-9s %s%n",
                "ID", "Tour", "Inicio", "Estado", "Ocupados", "Ocupación"));
        for (Salida salida : salidas) {
            int ocupados = 0;
            for (Reserva reserva : salida.getReservas()) {
                if (reserva.ocupaCupos(hoy)) ocupados += reserva.cantidadPasajeros();
            }
            double porcentaje = salida.getCupoMaximo() == 0 ? 0 : ocupados * 100.0 / salida.getCupoMaximo();
            sb.append(String.format(Locale.US, "%-4d %-26s %-11s %-11s %3d/%-5d %.0f%%%n",
                    salida.getId(), recortar(salida.getTour().getNombre(), 26), salida.getFechaInicio(),
                    salida.getEstado(), ocupados, salida.getCupoMaximo(), porcentaje));
        }
        return sb.toString();
    }

    /** Reservas, pasajeros e importe vendido por cada vendedor (sin contar anuladas). */
    public String reporteVentasPorVendedor() {
        StringBuilder sb = titulo("REPORTE: VENTAS POR VENDEDOR");
        Map<Integer, double[]> acumulado = new LinkedHashMap<>(); // [reservas, pasajeros, importe]
        Map<Integer, Vendedor> vendedores = new LinkedHashMap<>();

        for (Reserva reserva : agencia.getReservas()) {
            if (reserva.getEstado() == EstadoReserva.ANULADA) continue;
            Vendedor vendedor = reserva.getVendedor();
            double[] datos = acumulado.computeIfAbsent(vendedor.getLegajo(), k -> new double[3]);
            vendedores.put(vendedor.getLegajo(), vendedor);
            datos[0]++;
            datos[1] += reserva.cantidadPasajeros();
            datos[2] += reserva.getImporteTotal();
        }
        if (acumulado.isEmpty()) return sb.append(vacio("No hay ventas registradas.")).toString();

        sb.append(String.format("%-8s %-24s %-9s %-10s %s%n", "Legajo", "Vendedor", "Reservas", "Pasajeros", "Importe vendido"));
        double total = 0;
        for (Map.Entry<Integer, double[]> fila : acumulado.entrySet()) {
            double[] d = fila.getValue();
            sb.append(String.format(Locale.US, "%-8d %-24s %-9d %-10d %s%n", fila.getKey(),
                    recortar(vendedores.get(fila.getKey()).obtenerNombreCompleto(), 24),
                    (int) d[0], (int) d[1], dinero(d[2])));
            total += d[2];
        }
        sb.append(SEPARADOR).append(System.lineSeparator());
        sb.append("Total vendido: ").append(dinero(total)).append(System.lineSeparator());
        return sb.toString();
    }

    /** Pagos cobrados, devoluciones e ingreso neto por tour. */
    public String reporteIngresosPorTour() {
        StringBuilder sb = titulo("REPORTE: INGRESOS POR TOUR");
        Map<Tour, double[]> acumulado = new LinkedHashMap<>(); // [cobrado, devuelto]

        for (Reserva reserva : agencia.getReservas()) {
            double cobrado = reserva.calcularTotalPagado();
            Cancelacion cancelacion = reserva.getCancelacion();
            double devuelto = cancelacion == null ? 0 : cancelacion.getImporteDevuelto();
            if (cobrado == 0 && devuelto == 0) continue;
            double[] datos = acumulado.computeIfAbsent(reserva.getSalida().getTour(), k -> new double[2]);
            datos[0] += cobrado;
            datos[1] += devuelto;
        }
        if (acumulado.isEmpty()) return sb.append(vacio("No hay pagos registrados.")).toString();

        sb.append(String.format("%-8s %-26s %-16s %-16s %s%n", "Código", "Tour", "Cobrado", "Devuelto", "Neto"));
        double totalNeto = 0;
        for (Map.Entry<Tour, double[]> fila : acumulado.entrySet()) {
            double neto = fila.getValue()[0] - fila.getValue()[1];
            sb.append(String.format("%-8s %-26s %-16s %-16s %s%n", fila.getKey().getCodigo(),
                    recortar(fila.getKey().getNombre(), 26), dinero(fila.getValue()[0]),
                    dinero(fila.getValue()[1]), dinero(neto)));
            totalNeto += neto;
        }
        sb.append(SEPARADOR).append(System.lineSeparator());
        sb.append("Ingreso neto total: ").append(dinero(totalNeto)).append(System.lineSeparator());
        sb.append("(Importes pagados sin recargos de financiación.)").append(System.lineSeparator());
        return sb.toString();
    }

    /** Reservas canceladas con su motivo, quién canceló y el importe devuelto. */
    public String reporteCancelaciones() {
        StringBuilder sb = titulo("REPORTE: CANCELACIONES");
        List<Reserva> canceladas = new ArrayList<>();
        for (Reserva reserva : agencia.getReservas()) {
            if (reserva.getCancelacion() != null) canceladas.add(reserva);
        }
        if (canceladas.isEmpty()) return sb.append(vacio("No hay reservas canceladas.")).toString();

        sb.append(String.format("%-8s %-11s %-9s %-6s %-16s %s%n", "Reserva", "Fecha", "Origen", "Dev.", "Importe devuelto", "Motivo"));
        double total = 0;
        for (Reserva reserva : canceladas) {
            Cancelacion c = reserva.getCancelacion();
            sb.append(String.format(Locale.US, "%-8d %-11s %-9s %-6s %-16s %s%n", reserva.getId(), c.getFecha(),
                    c.isCancelacionAgencia() ? "Agencia" : "Cliente",
                    String.format(Locale.US, "%.0f%%", c.getPorcentajeDevolucion() * 100),
                    dinero(c.getImporteDevuelto()), c.getMotivo()));
            total += c.getImporteDevuelto();
        }
        sb.append(SEPARADOR).append(System.lineSeparator());
        sb.append("Cancelaciones: ").append(canceladas.size()).append(" - Total devuelto: ")
                .append(dinero(total)).append(System.lineSeparator());
        return sb.toString();
    }

    // ---------------------------- auxiliares ----------------------------

    private StringBuilder titulo(String texto) {
        return new StringBuilder(SEPARADOR).append(System.lineSeparator())
                .append(texto).append(System.lineSeparator())
                .append(SEPARADOR).append(System.lineSeparator());
    }

    /** Flujo alternativo de CU24: reporte vacío con una aclaración. */
    private String vacio(String aclaracion) {
        return aclaracion + " El reporte está vacío." + System.lineSeparator();
    }

    private String dinero(double importe) {
        return String.format(Locale.US, "$%,.2f", importe);
    }

    private String recortar(String texto, int largo) {
        return texto.length() <= largo ? texto : texto.substring(0, largo - 1) + "…";
    }
}
