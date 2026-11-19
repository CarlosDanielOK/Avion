package excepciones;

/**
 * Indica que la operación solicitada no está permitida.
 */
/** Indica que el estado actual impide realizar la operacion solicitada. */
public class OperacionNoPermitidaException extends ReglaNegocioException {

    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}