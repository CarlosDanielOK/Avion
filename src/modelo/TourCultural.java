package modelo;

import enumeraciones.CategoriaTour;

/** Especializacion cultural: usa las reglas comunes de Tour sin restricciones adicionales. */
public class TourCultural extends Tour {
    private static final long serialVersionUID = 1L;

    public TourCultural(String codigo, String nombre, String descripcion, int duracionDias,
                  int cupoMinimo, int cupoMaximo) {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    @Override
    public CategoriaTour getCategoria() { return CategoriaTour.CULTURAL; }
}