package persistencia;

import modelo.Agencia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementacion concreta de persistencia mediante serializacion binaria.
 * Guarda el objeto raiz Agencia y, por la conexion entre objetos, conserva
 * todo el grafo de datos. Se usa java.nio.file para rutas/directorios y
 * java.io para los flujos ObjectOutputStream/ObjectInputStream.
 */
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
            // Crea datos/ si no existe. Path evita depender de Windows o Linux.
            Path directorio = rutaArchivo.getParent();
            if (directorio != null) {
                Files.createDirectories(directorio);
            }
            salida = new ObjectOutputStream(Files.newOutputStream(rutaArchivo));
            // Serializa la raiz y todos los objetos alcanzables desde ella.
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
        // Si no hay archivo previo, comienza con una agencia nueva y limpia.
        if (!Files.exists(rutaArchivo)) {
            return new Agencia();
        }

        ObjectInputStream entrada = null;
        try {
            entrada = new ObjectInputStream(Files.newInputStream(rutaArchivo));
            Object objeto = entrada.readObject();
            // readObject devuelve Object: hay que validar y hacer cast.
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
