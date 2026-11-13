package modelo;

public class Vendedor extends Empleado {
    private static final long serialVersionUID = 1L;

    private double comision; // porcentaje; el sistema no liquida comisiones (S14)

    public Vendedor(int legajo, String nombre, String apellido, String documento,
                    String telefono, double comision) {
        super(legajo, nombre, apellido, documento, telefono);
        if (comision < 0) {
            throw new IllegalArgumentException("La comisión no puede ser negativa");
        }
        this.comision = comision;
    }

    @Override
    public String getRol() { return "Vendedor"; }

    public double getComision() { return comision; }
}