package persistencia;

import modelo.Agencia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Persistencia de la agencia mediante serialización de objetos Java. */
public class RepositorioAgenciaArchivo implements RepositorioAgencia {
    private final Path rutaArchivo;

    public RepositorioAgenciaArchivo(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }
        this.rutaArchivo = Path.of(rutaArchivo);
    }

    @Override
    public void guardar(Agencia agencia) {
        if (agencia == null) throw new IllegalArgumentException("La agencia es obligatoria");
        try {
            Path directorio = rutaArchivo.getParent();
            if (directorio != null) Files.createDirectories(directorio);
            try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(rutaArchivo))) {
                salida.writeObject(agencia);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la agencia", e);
        }
    }

    @Override
    public Agencia cargar() {
        if (!Files.exists(rutaArchivo)) return new Agencia();
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(rutaArchivo))) {
            Object objeto = entrada.readObject();
            if (!(objeto instanceof Agencia)) {
                throw new IllegalStateException("El archivo no contiene una agencia válida");
            }
            return (Agencia) objeto;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo cargar la agencia", e);
        }
    }
}