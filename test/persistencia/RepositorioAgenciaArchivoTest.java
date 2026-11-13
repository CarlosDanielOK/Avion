package persistencia;

import modelo.Agencia;

import java.nio.file.Files;
import java.nio.file.Path;

public class RepositorioAgenciaArchivoTest {
    public static void main(String[] args) throws Exception {
        Path archivo = Files.createTempFile("agencia-", ".dat");
        try {
            RepositorioAgenciaArchivo repo = new RepositorioAgenciaArchivo(archivo.toString());
            repo.guardar(new Agencia());
            if (repo.cargar() == null) throw new AssertionError("La agencia cargada no puede ser nula");
            System.out.println("RepositorioAgenciaArchivoTest: OK");
        } finally {
            Files.deleteIfExists(archivo);
        }
    }
}
