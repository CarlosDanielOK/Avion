package modelo;

import enumeraciones.CategoriaTour;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Guia extends Empleado {
    private static final long serialVersionUID = 1L;

    /** Porcentaje de las ventas de adicionales que cobra el guía (RN32). */
    private static final double PORCENTAJE_ADICIONALES = 0.05;

    private List<String> idiomas;
    private List<CategoriaTour> categoriasHabilitadas;
    private double honorarioPorDia;
    // Extremo de la asociación Guia 0..1 -- 0..* Salida: salidas que tiene
    // asignadas
    private final List<Salida> salidas = new ArrayList<>();

    public Guia(int legajo, String nombre, String apellido, String documento, String telefono,
            List<String> idiomas, List<CategoriaTour> categoriasHabilitadas, double honorarioPorDia) {
        super(legajo, nombre, apellido, documento, telefono);
        if (idiomas == null || idiomas.isEmpty() || categoriasHabilitadas == null || categoriasHabilitadas.isEmpty()) {
            throw new IllegalArgumentException("El guía debe tener al menos un idioma y una categoría habilitada");
        }
        if (honorarioPorDia <= 0) {
            throw new IllegalArgumentException("El honorario por día debe ser mayor a cero");
        }
        this.idiomas = new ArrayList<>(idiomas);
        this.categoriasHabilitadas = new ArrayList<>(categoriasHabilitadas);
        this.honorarioPorDia = honorarioPorDia;
    }

    @Override
    public String getRol() {
        return "Guía";
    }

    public boolean estaHabilitadoPara(CategoriaTour categoria) {
        return categoriasHabilitadas.contains(categoria);
    }

    /**
     * RN08: no puede tener dos salidas superpuestas. Las salidas canceladas no
     * cuentan.
     */
    public boolean estaDisponible(LocalDate inicio, LocalDate fin) {
        for (Salida s : salidas) {
            boolean activa = s.getEstado() != enumeraciones.EstadoSalida.CANCELADA;
            boolean seSuperpone = !s.getFechaFin().isBefore(inicio) && !s.getFechaInicio().isAfter(fin);
            if (activa && seSuperpone) {
                return false;
            }
        }
        return true;
    }

    /**
     * RN32: honorario fijo por día + 5 % de las ventas de adicionales de la salida.
     */
    public double calcularHonorarios(Salida salida) {
        int dias = salida.getTour().getDuracionDias();
        return dias * honorarioPorDia + PORCENTAJE_ADICIONALES * salida.totalServiciosAdicionales();
    }

    /** Lo invoca Salida.asignarGuia() una vez validada la asignación. */
    public void agregarSalida(Salida salida) {
        if (!salidas.contains(salida)) {
            salidas.add(salida);
        }
    }

    public List<String> getIdiomas() {
        return Collections.unmodifiableList(idiomas);
    }

    public List<CategoriaTour> getCategoriasHabilitadas() {
        return Collections.unmodifiableList(categoriasHabilitadas);
    }

    public double getHonorarioPorDia() {
        return honorarioPorDia;
    }

    public List<Salida> getSalidas() {
        return Collections.unmodifiableList(salidas);
    }
}