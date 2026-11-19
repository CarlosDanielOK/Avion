package excepciones;

/** Indica que la reserva no puede cancelarse según las reglas de negocio. */
/** Indica que una reserva ya finalizo, fue anulada o no puede cancelarse. */
public class ReservaNoCancelableException extends ReglaNegocioException {

    public ReservaNoCancelableException(String mensaje) {
        super(mensaje);
    }
}