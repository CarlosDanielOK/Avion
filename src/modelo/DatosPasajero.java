
package modelo;

import enumeraciones.TipoPasajero;
import java.io.Serializable;
import java.time.LocalDate;

public class DatosPasajero implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final String apellido;
    private final String documento;
    private final LocalDate fechaNacimiento;
    private final TipoPasajero tipoPasajero;
    private final String telefonoEmergencia;
    private final String restricciones;

    public DatosPasajero(
            String nombre,
            String apellido,
            String documento,
            LocalDate fechaNacimiento,
            String telefonoEmergencia,
            String restricciones) {
        this(nombre, apellido, documento, fechaNacimiento, TipoPasajero.ADULTO,
                telefonoEmergencia, restricciones);
    }

    public DatosPasajero(
            String nombre,
            String apellido,
            String documento,
            LocalDate fechaNacimiento,
            TipoPasajero tipoPasajero,
            String telefonoEmergencia,
            String restricciones) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio");
        }

        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException(
                    "El apellido es obligatorio");
        }

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento es obligatorio");
        }

        if (fechaNacimiento == null ||
                fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de nacimiento no es valida");
        }
        if (tipoPasajero == null) {
            throw new IllegalArgumentException("El tipo de pasajero es obligatorio");
        }

        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
        this.documento = documento.trim();
        this.fechaNacimiento = fechaNacimiento;
        this.tipoPasajero = tipoPasajero;
        this.telefonoEmergencia = telefonoEmergencia;
        this.restricciones = restricciones;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public TipoPasajero getTipoPasajero() {
        return tipoPasajero;
    }

    public String getTelefonoEmergencia() {
        return telefonoEmergencia;
    }

    public String getRestricciones() {
        return restricciones;
    }
}
