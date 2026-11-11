package modelo;

public class Administrador extends Empleado {
    private static final long serialVersionUID = 1L;

    public Administrador(int legajo, String nombre, String apellido, String documento, String telefono) {
        super(legajo, nombre, apellido, documento, telefono);
    }

    @Override
    public String getRol() { return "Administrador"; }
}
