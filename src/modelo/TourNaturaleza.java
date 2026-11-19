package modelo;

import enumeraciones.CategoriaTour;

/** Especializacion de naturaleza: usa las reglas comunes de Tour. */
public class TourNaturaleza extends Tour {
    private static final long serialVersionUID = 1L;

    public TourNaturaleza(String codigo, String nombre, String descripcion, int duracionDias,
                  int cupoMinimo, int cupoMaximo) {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    @Override
    public CategoriaTour getCategoria() { return CategoriaTour.NATURALEZA; }
}