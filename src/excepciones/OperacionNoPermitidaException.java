package excepciones;

/**
 * Indica que la operación solicitada no está permitida.
 */
public class OperacionNoPermitidaException extends ReglaNegocioException {

    public OperacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
