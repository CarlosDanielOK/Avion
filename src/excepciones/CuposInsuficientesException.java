package excepciones;

/** Indica que la operación supera los cupos disponibles de una salida. */
/** Indica que la reserva excede los cupos actualmente disponibles. */
public class CuposInsuficientesException extends ReglaNegocioException {

    public CuposInsuficientesException(String mensaje) {
        super(mensaje);
    }
}