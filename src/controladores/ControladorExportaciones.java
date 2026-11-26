package controladores;

import excepciones.ReglaNegocioException;
import modelo.Agencia;
import modelo.Cliente;
import modelo.Pasajero;
import modelo.Salida;
import persistencia.ExportadorTxt;

import java.nio.file.Path;
import java.util.List;

/**
 * Controlador GRASP de la exportación a TXT (CU25 y CU26).
 * Busca los datos en la agencia, reutiliza la emisión de lista del
 * ControladorOperaciones y delega la escritura en ExportadorTxt.
 */
public class ControladorExportaciones {

    private final Agencia agencia;
    private final ControladorOperaciones operaciones;
    private final ExportadorTxt exportador;

    public ControladorExportaciones(Agencia agencia, ControladorOperaciones operaciones, ExportadorTxt exportador) {
        this.agencia = agencia;
        this.operaciones = operaciones;
        this.exportador = exportador;
    }

    /** CU25: emite la lista de pasajeros (con sus reglas) y la exporta a un archivo de texto. */
    public Path exportarListaPasajeros(long idSalida) throws ReglaNegocioException {
        Salida salida = agencia.buscarSalida(idSalida);
        List<Pasajero> pasajeros = operaciones.emitirListaPasajeros(idSalida);
        return exportador.exportarListaPasajeros(salida, pasajeros);
    }

    /** CU26: busca al cliente por documento y exporta el historial de todas sus reservas. */
    public Path exportarHistorialCliente(String documento) throws ReglaNegocioException {
        Cliente cliente = agencia.buscarCliente(documento);
        return exportador.exportarHistorialCliente(cliente, agencia.buscarReservasDeCliente(documento));
    }
}
