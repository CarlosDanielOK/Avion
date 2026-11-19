package excepciones;

/** Indica que se requiere un pago para continuar con una operación. */
/** Indica que falta el pago minimo o el pago total requerido por el plazo. */
public class PagoRequeridoException extends ReglaNegocioException {

    public PagoRequeridoException(String mensaje) {
        super(mensaje);
    }
}