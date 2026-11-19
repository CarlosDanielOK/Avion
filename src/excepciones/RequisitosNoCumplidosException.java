package excepciones;

/**
 * Indica que no se cumplen los requisitos necesarios para una operación.
 */
/** Indica que los pasajeros o recursos no cumplen las condiciones necesarias. */
public class RequisitosNoCumplidosException extends ReglaNegocioException {

    public RequisitosNoCumplidosException(String mensaje) {
        super(mensaje);
    }
}