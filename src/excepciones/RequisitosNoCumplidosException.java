package excepciones;

/**
 * Indica que no se cumplen los requisitos necesarios para una operación.
 */
public class RequisitosNoCumplidosException extends ReglaNegocioException {

    public RequisitosNoCumplidosException(String mensaje) {
        super(mensaje);
    }
}