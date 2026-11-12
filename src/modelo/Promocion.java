
package modelo;

import java.io.Serializable;

public interface Promocion extends Serializable {

    boolean esAplicable(Reserva reserva);

    double calcularDescuento(double subtotalTour);
}
