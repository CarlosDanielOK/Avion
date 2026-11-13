
package modelo;

import enumeraciones.TipoPasajero;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pasajero extends Persona {

    private static final long serialVersionUID = 1L;

    private LocalDate fechaNacimiento;
    private TipoPasajero tipoPasajero;
    private String contactoEmergencia;
    private String restricciones;
    private final List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();

    public Pasajero(String nombre, String apellido,
                    String documento, String telefono,
                    LocalDate fechaNacimiento,
                    TipoPasajero tipoPasajero,
                    String contactoEmergencia,
                    String restricciones) {

        super(nombre, apellido, documento, telefono);

        if (fechaNacimiento == null ||
                fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de nacimiento no es valida");
        }

        if (tipoPasajero == null) {
            throw new IllegalArgumentException(
                    "El tipo de pasajero es obligatorio");
        }

        this.fechaNacimiento = fechaNacimiento;
        this.tipoPasajero = tipoPasajero;
        this.contactoEmergencia = contactoEmergencia;
        this.restricciones = restricciones;
    }

    public int calcularEdad(LocalDate fechaSalida) {
        if (fechaSalida == null ||
                fechaSalida.isBefore(fechaNacimiento)) {
            throw new IllegalArgumentException(
                    "Fecha de salida invalida");
        }

        return Period.between(
                fechaNacimiento, fechaSalida).getYears();
    }

    public double calcularPorcentajeTarifa(LocalDate fechaSalida) {

        int edad = calcularEdad(fechaSalida);

        if (edad < 3) {
            return 0.0;
        }

        if (edad < 12) {
            return 0.60;
        }

        if (tipoPasajero == TipoPasajero.JUBILADO) {
            return 0.90;
        }

        return 1.0;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public TipoPasajero getTipoPasajero() {
        return tipoPasajero;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }

    public String getRestricciones() {
        return restricciones;
    }

    public void agregarServicioAdicional(ServicioAdicional servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio adicional es obligatorio");
        }
        for (ServicioAdicional existente : serviciosAdicionales) {
            if (existente.getNombre().equalsIgnoreCase(servicio.getNombre())) {
                throw new IllegalArgumentException("El pasajero ya tiene contratado ese servicio");
            }
        }
        serviciosAdicionales.add(servicio);
    }

    public List<ServicioAdicional> getServiciosAdicionales() {
        return Collections.unmodifiableList(serviciosAdicionales);
    }

    public double totalServiciosAdicionales() {
        double total = 0;
        for (ServicioAdicional servicio : serviciosAdicionales) {
            total += servicio.getPrecio();
        }
        return total;
    }

    @Override
    public String toString() {
        return obtenerNombreCompleto()
                + " - " + tipoPasajero;
    }
}
