package persistencia;

import modelo.Agencia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Persistencia de la agencia mediante serializacion de objetos Java. */
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
        if (agencia == null) {
            throw new IllegalArgumentException("La agencia es obligatoria");
        }

        ObjectOutputStream salida = null;
        try {
            Path directorio = rutaArchivo.getParent();
            if (directorio != null) {
                Files.createDirectories(directorio);
            }
            salida = new ObjectOutputStream(Files.newOutputStream(rutaArchivo));
            salida.writeObject(agencia);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la agencia", e);
        } finally {
            if (salida != null) {
                try {
                    salida.close();
                } catch (IOException e) {
                    System.out.println("Advertencia: no se pudo cerrar el archivo de datos (" + e.getMessage() + ").");
                }
            }
        }
    }

    @Override
    public Agencia cargar() {
        if (!Files.exists(rutaArchivo)) {
            return new Agencia();
        }

        ObjectInputStream entrada = null;
        try {
            entrada = new ObjectInputStream(Files.newInputStream(rutaArchivo));
            Object objeto = entrada.readObject();
            if (!(objeto instanceof Agencia)) {
                throw new IllegalStateException("El archivo no contiene una agencia valida");
            }
            return (Agencia) objeto;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo cargar la agencia", e);
        } finally {
            if (entrada != null) {
                try {
                    entrada.close();
                } catch (IOException e) {
                    System.out.println("Advertencia: no se pudo cerrar el archivo de datos (" + e.getMessage() + ").");
                }
            }
        }
    }
}
