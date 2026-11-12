package persistencia;

import modelo.Agencia;

/** Abstracción de persistencia para que el modelo no dependa de un archivo. */
public interface RepositorioAgencia {
    void guardar(Agencia agencia);
    Agencia cargar();
}
