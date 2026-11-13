package excepciones;

/** Indica que se requiere un pago para continuar con una operación. */
public class PagoRequeridoException extends ReglaNegocioException {

    public PagoRequeridoException(String mensaje) {
        super(mensaje);
    }
}