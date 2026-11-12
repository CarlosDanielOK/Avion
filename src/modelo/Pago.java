
package modelo;

import enumeraciones.MedioPago;
import java.io.Serializable;
import java.time.LocalDate;

public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDate fecha;
    private double importe;
    private MedioPago medioPago;
    private int cuotas;
    private double recargo;

    public Pago(int id, LocalDate fecha, double importe,
                MedioPago medioPago, int cuotas) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador debe ser positivo");
        }

        if (fecha == null || medioPago == null) {
            throw new IllegalArgumentException(
                    "La fecha y el medio de pago son obligatorios");
        }

        if (importe <= 0) {
            throw new IllegalArgumentException(
                    "El importe debe ser mayor a cero");
        }

        if (cuotas < 1) {
            throw new IllegalArgumentException(
                    "La cantidad de cuotas debe ser al menos uno");
        }

        this.id = id;
        this.fecha = fecha;
        this.importe = importe;
        this.medioPago = medioPago;
        this.cuotas = cuotas;
        this.recargo = 0;
    }

    public double calcularImporteFinal() {
        return importe + recargo;
    }

    public void aplicarRecargo(double porcentaje) {
        if (porcentaje < 0) {
            throw new IllegalArgumentException(
                    "El porcentaje no puede ser negativo");
        }

        this.recargo = importe * porcentaje;
    }

    public int getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public int getCuotas() {
        return cuotas;
    }

    public double getRecargo() {
        return recargo;
    }

    @Override
    public String toString() {
        return "Pago #" + id
                + " - Importe: $" + importe
                + " - Medio: " + medioPago
                + " - Cuotas: " + cuotas
                + " - Recargo: $" + recargo;
    }
}
