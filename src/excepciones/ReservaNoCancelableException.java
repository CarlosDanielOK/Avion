package excepciones;

/** Indica que la reserva no puede cancelarse según las reglas de negocio. */
public class ReservaNoCancelableException extends ReglaNegocioException {

    public ReservaNoCancelableException(String mensaje) {
        super(mensaje);
    }
}