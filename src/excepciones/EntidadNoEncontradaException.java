package excepciones;

/**
 * Indica que no se encontró una entidad requerida por la operación.
 */
/** Indica que una entidad buscada por codigo, documento o identificador no existe. */
public class EntidadNoEncontradaException extends ReglaNegocioException {

    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}