package modelo;

import excepciones.EntidadNoEncontradaException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Raíz del modelo: concentra las colecciones que forman el estado de la agencia.
 */
public class Agencia implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Tour> tours = new ArrayList<>();
    private final List<Salida> salidas = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();
    private final List<Empleado> empleados = new ArrayList<>();
    private final List<Vehiculo> vehiculos = new ArrayList<>();
    private final List<Proveedor> proveedores = new ArrayList<>();

    public Tour buscarTour(String codigo) throws EntidadNoEncontradaException {
        for (Tour tour : tours) {
            if (tour.getCodigo().equalsIgnoreCase(codigo)) return tour;
        }
        throw new EntidadNoEncontradaException("No existe el tour con código " + codigo);
    }

    public Salida buscarSalida(long id) throws EntidadNoEncontradaException {
        for (Salida salida : salidas) {
            if (salida.getId() == id) return salida;
        }
        throw new EntidadNoEncontradaException("No existe la salida " + id);
    }

    public Cliente buscarCliente(String documento) throws EntidadNoEncontradaException {
        for (Cliente cliente : clientes) {
            if (cliente.getDocumento().equalsIgnoreCase(documento)) return cliente;
        }
        throw new EntidadNoEncontradaException("No existe el cliente con documento " + documento);
    }

    public Reserva buscarReserva(long numero) throws EntidadNoEncontradaException {
        for (Reserva reserva : reservas) {
            if (reserva.getNumero() == numero) return reserva;
        }
        throw new EntidadNoEncontradaException("No existe la reserva " + numero);
    }

    public Empleado buscarEmpleado(int legajo) throws EntidadNoEncontradaException {
        for (Empleado empleado : empleados) {
            if (empleado.getLegajo() == legajo) return empleado;
        }
        throw new EntidadNoEncontradaException("No existe el empleado con legajo " + legajo);
    }

    public Vehiculo buscarVehiculo(String patente) throws EntidadNoEncontradaException {
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getPatente().equalsIgnoreCase(patente)) return vehiculo;
        }
        throw new EntidadNoEncontradaException("No existe el vehículo con patente " + patente);
    }

    public long siguienteNumeroReserva() {
        long mayor = 0;
        for (Reserva reserva : reservas) mayor = Math.max(mayor, reserva.getNumero());
        return mayor + 1;
    }

    public void agregarTour(Tour tour) { tours.add(tour); }
    public void agregarSalida(Salida salida) { salidas.add(salida); }
    public void agregarCliente(Cliente cliente) { clientes.add(cliente); }
    public void agregarReserva(Reserva reserva) { reservas.add(reserva); }
    public void agregarEmpleado(Empleado empleado) { empleados.add(empleado); }
    public void agregarVehiculo(Vehiculo vehiculo) { vehiculos.add(vehiculo); }
    public void agregarProveedor(Proveedor proveedor) { proveedores.add(proveedor); }

    public List<Tour> getTours() { return Collections.unmodifiableList(tours); }
    public List<Salida> getSalidas() { return Collections.unmodifiableList(salidas); }
    public List<Cliente> getClientes() { return Collections.unmodifiableList(clientes); }
    public List<Reserva> getReservas() { return Collections.unmodifiableList(reservas); }
    public List<Empleado> getEmpleados() { return Collections.unmodifiableList(empleados); }
    public List<Vehiculo> getVehiculos() { return Collections.unmodifiableList(vehiculos); }
    public List<Proveedor> getProveedores() { return Collections.unmodifiableList(proveedores); }
}
