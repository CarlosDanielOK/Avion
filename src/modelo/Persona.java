package modelo;

import java.io.Serializable;

/** Clase abstracta base de todas las personas del sistema. */
public abstract class Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    // protected: las subclases (Empleado, Cliente, Pasajero) acceden directamente
    protected String nombre;
    protected String apellido;
    protected String documento;
    protected String telefono;

    protected Persona(String nombre, String apellido, String documento, String telefono) {
        if (nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank()
                || documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("Nombre, apellido y documento son obligatorios");
        }
        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
        this.documento = documento.trim();
        this.telefono = telefono;
    }

    public String obtenerNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
}