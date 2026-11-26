import controladores.ControladorOperaciones;
import controladores.ControladorReservas;
import controladores.GeneradorReportes;
import interfaz.MenuConsola;
import modelo.Agencia;
import modelo.GestorCancelaciones;
import controladores.ControladorExportaciones;
import persistencia.ExportadorTxt;
import persistencia.RepositorioAgencia;
import persistencia.RepositorioAgenciaArchivo;

import java.util.Scanner;

/**
 * Punto de entrada: arma las dependencias (repositorio, agencia, controlador y
 * reportes) y abre el menú de consola. Los datos se guardan en datos/agencia.dat.
 *
 * El flujo es: cargar los datos serializados, construir los controladores con
 * la misma agencia y repositorio, abrir el menú y cerrar el Scanner al salir.
 */
public class Main {

    private static final String ARCHIVO_DATOS = "datos/agencia.dat";

    public static void main(String[] args) {
        // Construye la puerta de persistencia concreta; los controladores usan
        // la interfaz RepositorioAgencia y no necesitan conocer el archivo.
        RepositorioAgencia repositorio = new RepositorioAgenciaArchivo(ARCHIVO_DATOS);
        Agencia agencia;
        try {
            // Lee el grafo completo de objetos. Si falla, no inicia el menú
            // para no arriesgar una sobrescritura accidental.
            agencia = repositorio.cargar();
        } catch (IllegalStateException e) {
            System.out.println("ERROR: no se pudieron leer los datos guardados (" + e.getMessage() + ").");
            System.out.println("Para evitar perder información, el sistema no iniciará ni sobrescribirá el archivo.");
            return;
        }

        ControladorOperaciones operaciones = new ControladorOperaciones(agencia, repositorio, new GestorCancelaciones());
        ControladorReservas reservas = new ControladorReservas(agencia, repositorio);
        GeneradorReportes reportes = new GeneradorReportes(agencia);
        ControladorExportaciones exportaciones = new ControladorExportaciones(agencia, operaciones, new ExportadorTxt("exportaciones"));

        Scanner entrada = new Scanner(System.in);
        try {
            new MenuConsola(agencia, repositorio, operaciones, reservas, reportes, exportaciones, entrada).mostrarMenu();
        } finally {
            // finally garantiza el mensaje de cierre y libera el Scanner.
            System.out.println("Sesion finalizada.");
            entrada.close();
        }
    }
}
