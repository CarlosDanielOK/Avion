package modelo;

import excepciones.EntidadNoEncontradaException;

/** Pruebas de las búsquedas de Agencia: encuentra lo registrado y falla con una excepción de negocio si no existe. */
public class AgenciaTest {

    public static void main(String[] args) throws Exception {
        Agencia agencia = new Agencia();
        agencia.agregarTour(new TourCultural("CUL01", "Buenos Aires Histórica", "Recorrido", 2, 2, 10));
        agencia.agregarCliente(new Cliente("Carla", "Ruiz", "222", "1", "c@mail.com", "Argentina"));

        verificar(agencia.buscarTour("cul01").getNombre().equals("Buenos Aires Histórica"), "busca tour sin distinguir mayúsculas");
        verificar(agencia.buscarCliente("222").getApellido().equals("Ruiz"), "busca cliente por documento");
        verificar(agencia.siguienteNumeroReserva() == 1, "sin reservas el siguiente número es 1");

        try {
            agencia.buscarTour("NOEXISTE");
            throw new AssertionError("Debía lanzar EntidadNoEncontradaException");
        } catch (EntidadNoEncontradaException esperado) {
            verificar(esperado.getMessage().contains("NOEXISTE"), "el mensaje indica qué no se encontró");
        }
        System.out.println("AgenciaTest: OK");
    }

    private static void verificar(boolean condicion, String descripcion) {
        if (!condicion) throw new AssertionError("Falló: " + descripcion);
    }
}
