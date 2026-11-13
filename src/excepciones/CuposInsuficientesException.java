package excepciones;

/** Indica que la operación supera los cupos disponibles de una salida. */
public class CuposInsuficientesException extends ReglaNegocioException {

    public CuposInsuficientesException(String mensaje) {
        super(mensaje);
    }
}