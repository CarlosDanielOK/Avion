package modelo;

import enumeraciones.TipoServicio;
import java.io.Serializable;

/** Servicio extra que se contrata por pasajero (excursión, traslado, seguro). */
public class ServicioAdicional implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private double precio;
    private TipoServicio tipo;
    private boolean obligatorio;

    public ServicioAdicional(String nombre, double precio, TipoServicio tipo, boolean obligatorio) {
        if (nombre == null || nombre.isBlank() || tipo == null) {
            throw new IllegalArgumentException("Nombre y tipo del servicio son obligatorios");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.nombre = nombre.trim();
        this.precio = precio;
        this.tipo = tipo;
        this.obligatorio = obligatorio;
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public TipoServicio getTipo() { return tipo; }
    public boolean isObligatorio() { return obligatorio; }
}