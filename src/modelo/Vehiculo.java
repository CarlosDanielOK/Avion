package modelo;

import enumeraciones.TipoVehiculo;
import java.io.Serializable;

public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String patente;
    private int capacidad;
    private TipoVehiculo tipo;

    public Vehiculo(String patente, int capacidad, TipoVehiculo tipo) {
        if (patente == null || patente.isBlank() || tipo == null) {
            throw new IllegalArgumentException("Patente y tipo son obligatorios");
        }
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva");
        }
        this.patente = patente.trim().toUpperCase();
        this.capacidad = capacidad;
        this.tipo = tipo;
    }

    public String getPatente() { return patente; }
    public int getCapacidad() { return capacidad; }
    public TipoVehiculo getTipo() { return tipo; }

    @Override
    public String toString() {
        return tipo + " " + patente + " (capacidad " + capacidad + ")";
    }
}
