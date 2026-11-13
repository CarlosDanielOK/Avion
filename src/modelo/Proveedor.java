package modelo;

import enumeraciones.TipoProveedor;
import java.io.Serializable;

public class Proveedor implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String cuit; // guardado solo con dígitos
    private String contacto;
    private TipoProveedor tipoServicio;

    public Proveedor(String nombre, String cuit, String contacto, TipoProveedor tipoServicio) {
        if (nombre == null || nombre.isBlank() || tipoServicio == null) {
            throw new IllegalArgumentException("Nombre y tipo de servicio son obligatorios");
        }
        String soloDigitos = cuit == null ? "" : cuit.replace("-", "").trim();
        if (!soloDigitos.matches("\\d{11}")) {
            throw new IllegalArgumentException("El CUIT debe tener 11 dígitos");
        }
        this.nombre = nombre.trim();
        this.cuit = soloDigitos;
        this.contacto = contacto;
        this.tipoServicio = tipoServicio;
    }

    public String getNombre() { return nombre; }
    public String getCuit() { return cuit; }
    public String getContacto() { return contacto; }
    public TipoProveedor getTipoServicio() { return tipoServicio; }

    @Override
    public String toString() {
        return tipoServicio + " " + nombre + " (CUIT " + cuit + ")";
    }
}