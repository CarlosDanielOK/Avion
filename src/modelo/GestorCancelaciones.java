
package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class GestorCancelaciones {

    public double calcularPorcentajeDevolucion(
            LocalDate fechaCancelacion,
            LocalDate fechaSalida,
            boolean cancelacionAgencia) {

        if (fechaCancelacion == null || fechaSalida == null) {
            throw new IllegalArgumentException(
                    "Las fechas son obligatorias");
        }

        if (fechaCancelacion.isAfter(fechaSalida)) {
            throw new IllegalArgumentException(
                    "No se puede cancelar despues de la salida");
        }

        if (cancelacionAgencia) {
            return 1.0;
        }

        long dias = ChronoUnit.DAYS.between(
                fechaCancelacion, fechaSalida);

        if (dias >= 30) {
            return 0.90;
        }

        if (dias >= 15) {
            return 0.50;
        }

        if (dias >= 7) {
            return 0.20;
        }

        return 0.0;
    }

    public Cancelacion crearCancelacion(
            String motivo,
            LocalDate fechaSalida,
            double totalPagado,
            boolean cancelacionAgencia) {

        if (totalPagado < 0) {
            throw new IllegalArgumentException(
                    "El total pagado no puede ser negativo");
        }

        LocalDate fechaActual = LocalDate.now();

        double porcentaje = calcularPorcentajeDevolucion(
                fechaActual, fechaSalida, cancelacionAgencia);

        double importeDevuelto = totalPagado * porcentaje;

        return new Cancelacion(
                fechaActual,
                motivo,
                cancelacionAgencia,
                porcentaje,
                importeDevuelto);
    }
}
