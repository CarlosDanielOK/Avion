package modelo;

/** Personal de la agencia. Cada subclase define su rol. */
public abstract class Empleado extends Persona {
    private static final long serialVersionUID = 1L;

    protected int legajo;

    protected Empleado(int legajo, String nombre, String apellido, String documento, String telefono) {
        super(nombre, apellido, documento, telefono);
        if (legajo <= 0) {
            throw new IllegalArgumentException("El legajo debe ser positivo");
        }
        this.legajo = legajo;
    }

    public abstract String getRol();

    public int getLegajo() { return legajo; }

    @Override
    public String toString() {
        return getRol() + " #" + legajo + " - " + obtenerNombreCompleto();
    }
}