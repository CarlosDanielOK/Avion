package excepciones;

/**
 * Indica que no se encontró una entidad requerida por la operación.
 */
public class EntidadNoEncontradaException extends ReglaNegocioException {

    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
