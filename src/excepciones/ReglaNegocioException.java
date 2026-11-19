package excepciones;

/**
 * Excepción base para las reglas de negocio del sistema.
 */
/** Base checked de las excepciones propias que representan una regla incumplida. */
public class ReglaNegocioException extends Exception {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}