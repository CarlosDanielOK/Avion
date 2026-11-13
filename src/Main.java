import controladores.ControladorOperaciones;
import controladores.ControladorReservas;
import controladores.GeneradorReportes;
import interfaz.MenuConsola;
import modelo.Agencia;
import modelo.GestorCancelaciones;
import persistencia.RepositorioAgencia;
import persistencia.RepositorioAgenciaArchivo;

import java.util.Scanner;

/**
 * Punto de entrada: arma las dependencias (repositorio, agencia, controlador y
 * reportes) y abre el menú de consola. Los datos se guardan en datos/agencia.dat.
 */
public class Main {

    private static final String ARCHIVO_DATOS = "datos/agencia.dat";

    public static void main(String[] args) {
        RepositorioAgencia repositorio = new RepositorioAgenciaArchivo(ARCHIVO_DATOS);
        Agencia agencia;
        try {
            agencia = repositorio.cargar();
        } catch (IllegalStateException e) {
            System.out.println("ERROR: no se pudieron leer los datos guardados (" + e.getMessage() + ").");
            System.out.println("Para evitar perder información, el sistema no iniciará ni sobrescribirá el archivo.");
            return;
        }

        ControladorOperaciones operaciones = new ControladorOperaciones(agencia, repositorio, new GestorCancelaciones());
        ControladorReservas reservas = new ControladorReservas(agencia, repositorio);
        GeneradorReportes reportes = new GeneradorReportes(agencia);

        Scanner entrada = new Scanner(System.in);
        try {
            new MenuConsola(agencia, repositorio, operaciones, reservas, reportes, entrada).mostrarMenu();
        } finally {
            System.out.println("Sesion finalizada.");
            entrada.close();
        }
    }
}