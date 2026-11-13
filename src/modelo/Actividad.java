package modelo;

import java.io.Serializable;
import java.time.LocalTime;

/** Paso del itinerario de un tour. Orden natural: por día y luego por orden. */
public class Actividad implements Serializable, Comparable<Actividad> {
    private static final long serialVersionUID = 1L;

    private int dia;
    private int orden;
    private LocalTime horario;
    private String descripcion;
    private String lugar;
    private Proveedor proveedor; // opcional (0..1)

    public Actividad(int dia, int orden, LocalTime horario, String descripcion, String lugar, Proveedor proveedor) {
        if (dia < 1 || orden < 1) {
            throw new IllegalArgumentException("El día y el orden deben ser mayores o iguales a 1");
        }
        if (horario == null || descripcion == null || descripcion.isBlank() || lugar == null || lugar.isBlank()) {
            throw new IllegalArgumentException("Horario, descripción y lugar son obligatorios");
        }
        this.dia = dia;
        this.orden = orden;
        this.horario = horario;
        this.descripcion = descripcion.trim();
        this.lugar = lugar.trim();
        this.proveedor = proveedor;
    }

    @Override
    public int compareTo(Actividad otra) {
        int porDia = Integer.compare(dia, otra.dia);
        return porDia != 0 ? porDia : Integer.compare(orden, otra.orden);
    }

    public int getDia() { return dia; }
    public int getOrden() { return orden; }
    public LocalTime getHorario() { return horario; }
    public String getDescripcion() { return descripcion; }
    public String getLugar() { return lugar; }
    public Proveedor getProveedor() { return proveedor; }

    @Override
    public String toString() {
        return "Día " + dia + " #" + orden + " " + horario + " - " + descripcion + " (" + lugar + ")";
    }
}