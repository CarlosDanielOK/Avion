package modelo;

import java.io.Serializable;
import java.time.LocalDate;

/** Contrato común para agregar promociones sin modificar la calculadora. */
/**
 * Contrato polimorfico para promociones comerciales.
 * Permite agregar descuentos nuevos sin modificar la calculadora (OCP).
 */
public interface Promocion extends Serializable {

    boolean esAplicable(Reserva reserva, LocalDate fechaSalida);

    double calcularDescuento(double subtotalTour);
}
