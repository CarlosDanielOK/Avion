package persistencia;

import modelo.Agencia;

/**
 * Contrato de persistencia.
 * GRASP DIP: el programa depende de esta abstraccion y no de una implementacion
 * concreta de archivo.
 */
public interface RepositorioAgencia {
    void guardar(Agencia agencia);
    Agencia cargar();
}