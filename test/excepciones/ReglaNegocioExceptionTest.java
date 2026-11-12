package excepciones;

/** Prueba simple y ejecutable sin librerías externas. */
public class ReglaNegocioExceptionTest {

    public static void main(String[] args) {
        ReglaNegocioException excepcion = new ReglaNegocioException("Mensaje de prueba");

        if (!"Mensaje de prueba".equals(excepcion.getMessage())) {
            throw new AssertionError("La excepción debe conservar el mensaje recibido");
        }

        System.out.println("ReglaNegocioExceptionTest: OK");
    }
}
