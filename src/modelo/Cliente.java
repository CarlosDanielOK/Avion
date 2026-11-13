package modelo;

public class Cliente extends Persona {
    private static final long serialVersionUID = 1L;

    private String correo;
    private String paisResidencia;

    public Cliente(String nombre, String apellido, String documento, String telefono,
                   String correo, String paisResidencia) {
        super(nombre, apellido, documento, telefono);
        this.correo = correo;
        this.paisResidencia = paisResidencia;
    }

    public String getCorreo() { return correo; }
    public String getPaisResidencia() { return paisResidencia; }

    @Override
    public String toString() {
        return "Cliente " + obtenerNombreCompleto() + " (" + documento + ")";
    }
}