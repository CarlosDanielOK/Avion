package controladores;

import enumeraciones.CategoriaTour;
import enumeraciones.Temporada;
import enumeraciones.TipoVehiculo;
import modelo.*;
import persistencia.RepositorioAgencia;

import java.time.LocalDate;
import java.util.Collections;

/** Pruebas de integración de asignación de recursos (CU19). */
public class ControladorOperacionesTest {

    public static void main(String[] args) throws Exception {
        rechazaVehiculoEnSalidasSuperpuestas();
        System.out.println("ControladorOperacionesTest: OK");
    }

    private static void rechazaVehiculoEnSalidasSuperpuestas() throws Exception {
        Agencia agencia = new Agencia();
        RepositorioAgencia repositorio = new RepositorioAgencia() {
            @Override public void guardar(Agencia ignorada) { }
            @Override public Agencia cargar() { return agencia; }
        };
        ControladorOperaciones controlador = new ControladorOperaciones(
                agencia, repositorio, new GestorCancelaciones());

        LocalDate inicio = LocalDate.now().plusDays(30);
        LocalDate fin = inicio.plusDays(1);
        TourCultural primero = new TourCultural("CUL01", "Tour uno", "Descripción", 2, 1, 10);
        TourCultural segundo = new TourCultural("CUL02", "Tour dos", "Descripción", 2, 1, 10);
        controlador.registrarTour(primero);
        controlador.registrarTour(segundo);
        Tarifa tarifa1 = new Tarifa(Temporada.MEDIA, inicio.minusDays(1), fin.plusDays(1), 1000);
        Tarifa tarifa2 = new Tarifa(Temporada.MEDIA, inicio.minusDays(1), fin.plusDays(1), 1000);
        controlador.registrarTarifa("CUL01", tarifa1);
        controlador.registrarTarifa("CUL02", tarifa2);

        Guia guia1 = new Guia(1, "Ana", "Uno", "111", "1",
                Collections.singletonList("Español"), Collections.singletonList(CategoriaTour.CULTURAL), 1000);
        Guia guia2 = new Guia(2, "Luis", "Dos", "222", "2",
                Collections.singletonList("Español"), Collections.singletonList(CategoriaTour.CULTURAL), 1000);
        controlador.registrarGuia(guia1);
        controlador.registrarGuia(guia2);
        Vehiculo vehiculo = new Vehiculo("AA123AA", 10, TipoVehiculo.COMBI);
        controlador.registrarVehiculo(vehiculo);

        Salida salida1 = controlador.programarSalida("CUL01", inicio, fin, 10);
        Salida salida2 = controlador.programarSalida("CUL02", inicio, fin, 10);
        controlador.asignarRecursos(salida1.getId(), guia1.getLegajo(), vehiculo.getPatente());

        try {
            controlador.asignarRecursos(salida2.getId(), guia2.getLegajo(), vehiculo.getPatente());
            throw new AssertionError("No debía permitir reutilizar el vehículo en fechas superpuestas");
        } catch (excepciones.RequisitosNoCumplidosException esperado) {
            // comportamiento esperado
        }
        if (salida2.getVehiculo() != null || salida2.getGuia() != null) {
            throw new AssertionError("La asignación rechazada no debe dejar recursos parcialmente asignados");
        }
    }
}
