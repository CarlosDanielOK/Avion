package modelo;

import java.io.Serializable;
import java.time.LocalDate;

/** Registro de los honorarios pagados a un guía por una salida finalizada. */
public class Liquidacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private int dias;
    private double monto;
    private Salida salida;
    private Guia guia;

    public Liquidacion(Salida salida, Guia guia, LocalDate fecha, int dias, double monto) {
        if (salida == null || guia == null || fecha == null) {
            throw new IllegalArgumentException("Salida, guía y fecha son obligatorios");
        }
        this.salida = salida;
        this.guia = guia;
        this.fecha = fecha;
        this.dias = dias;
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getDias() {
        return dias;
    }

    public double getMonto() {
        return monto;
    }

    public Salida getSalida() {
        return salida;
    }

    public Guia getGuia() {
        return guia;
    }
}
