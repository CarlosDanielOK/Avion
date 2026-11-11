package modelo;

import enumeraciones.CategoriaTour;
import enumeraciones.NivelDificultad;
import java.time.LocalDate;
import java.time.Period;

/** Tour de aventura: exige edad mínima, indica dificultad y requiere seguro. */
public class TourAventura extends Tour {
    private static final long serialVersionUID = 1L;

    private NivelDificultad nivelDificultad;
    private int edadMinima;

    public TourAventura(String codigo, String nombre, String descripcion, int duracionDias,
                        int cupoMinimo, int cupoMaximo, NivelDificultad nivelDificultad, int edadMinima) {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
        if (nivelDificultad == null || edadMinima < 0) {
            throw new IllegalArgumentException("Nivel de dificultad obligatorio y edad mínima no negativa");
        }
        this.nivelDificultad = nivelDificultad;
        this.edadMinima = edadMinima;
    }

    @Override
    public CategoriaTour getCategoria() { return CategoriaTour.AVENTURA; }

    @Override
    public boolean requiereSeguro() { return true; }

    /** Sobrescribe el método base: además valida la edad del pasajero a la fecha de la salida (S03). */
    @Override
    public boolean puedeParticipar(DatosPasajero datos, LocalDate fecha) {
        int edad = Period.between(datos.getFechaNacimiento(), fecha).getYears();
        return edad >= edadMinima;
    }

    public NivelDificultad getNivelDificultad() { return nivelDificultad; }
    public int getEdadMinima() { return edadMinima; }
}
