
import java.io.*;
import java.util.*;

/**
 * 
 */
public class Pedido {

    /**
     * 
     */
    private int idPedido;

    /**
     * 
     */
    private int numeroDePedido;

    /**
     * 
     */
    private Date fechaDeCreacion;

    /**
     * 
     */
    private ICliente cliente;

    /**
     * 
     */
    private IVehiculo vehiculo;

    /**
     * 
     */
    private String estado;

    /**
     * 
     */
    private IFormaDePago formaDePago;

    /**
     * 
     */
    private InterfazImpuestoStrategy impuestoStrategy;

    /**
     * 
     */
    private double total;

    /**
     * 
     */
    private ServicioDePago servicioDePago;

    /**
     * 
     */
    private ServicioDeNotificacion servicioDeNotificacion;

    /**
     * Default constructor
     */
    public Pedido(int idPedido, int numeroDePedido, Date fechaDeCreacion, ICliente cliente, IVehiculo vehiculo, String estado, IFormaDePago formaDePago, InterfazImpuestoStrategy impuestoStrategy, double total, ServicioDePago servicioDePago, ServicioDeNotificacion servicioDeNotificacion) {
        this.idPedido = idPedido;
        this.numeroDePedido = numeroDePedido;
        this.fechaDeCreacion = fechaDeCreacion;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.estado = estado;
        this.formaDePago = formaDePago;
        this.impuestoStrategy = impuestoStrategy;
        this.total = total; // Assuming total is calculated or passed in initially.
        this.servicioDePago = servicioDePago;
        this.servicioDeNotificacion = servicioDeNotificacion;
    }


    /**
     * 
     */
    public void crearPedido() {
        // TODO: Logic to initialize a new order.
        // This might involve setting a default state, calculating initial total, etc.
        // For now, dependencies are injected, so it's about internal state.
        System.out.println("Pedido creado para: " + cliente.getNombre() + " Vehiculo: " + vehiculo.getMarca());
    }

    /**
     * 
     */
    public void actualizarEstadoPedido(String nuevoEstado) {
        this.estado = nuevoEstado;
        // TODO: Add any other logic related to state change.
        if (this.servicioDeNotificacion != null) {
            this.servicioDeNotificacion.enviarNotificacion("Su pedido ha sido actualizado a: " + this.estado, this.cliente);
        }
    }

    /**
     * 
     */
    public void calcularImpuestos() {
        // Assuming impuestoStrategy.calcularImpuesto() might modify total directly
        // or return a value. If it returns, we should use it:
        // double impuestoCalculado = this.impuestoStrategy.calcularImpuesto(this.vehiculo, this.total_base_sin_impuesto);
        // this.total += impuestoCalculado;
        // For now, calling it as if it might modify state or use its own data.
        if (this.impuestoStrategy != null) {
            this.impuestoStrategy.calcularImpuesto(); // Or: this.total += this.impuestoStrategy.calcularImpuesto(this.total);
            System.out.println("Impuestos calculados para el pedido.");
        }
    }

    /**
     * Processes the payment using the payment service.
     */
    public boolean realizarPago() {
        if (this.formaDePago == null) {
            System.out.println("Forma de pago no especificada.");
            return false;
        }
        if (this.servicioDePago != null) {
            boolean pagoExitoso = this.servicioDePago.procesarPago(this.formaDePago, this.total);
            if (pagoExitoso) {
                this.actualizarEstadoPedido("Pagado"); // Example state update
            } else {
                this.actualizarEstadoPedido("ErrorDePago"); // Example state update
            }
            return pagoExitoso;
        }
        System.out.println("Servicio de pago no configurado.");
        return false;
    }

    // Getters
    public int getIdPedido() {
        return idPedido;
    }

    public int getNumeroDePedido() {
        return numeroDePedido;
    }

    public Date getFechaDeCreacion() {
        return fechaDeCreacion;
    }

    public ICliente getCliente() {
        return cliente;
    }

    public IVehiculo getVehiculo() {
        return vehiculo;
    }

    public String getEstado() {
        return estado;
    }

    public IFormaDePago getFormaDePago() {
        return formaDePago;
    }

    public InterfazImpuestoStrategy getImpuestoStrategy() {
        return impuestoStrategy;
    }

    public double getTotal() {
        return total;
    }

    public ServicioDePago getServicioDePago() {
        return servicioDePago;
    }

    public ServicioDeNotificacion getServicioDeNotificacion() {
        return servicioDeNotificacion;
    }

    // Setters
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public void setNumeroDePedido(int numeroDePedido) {
        this.numeroDePedido = numeroDePedido;
    }

    public void setFechaDeCreacion(Date fechaDeCreacion) {
        this.fechaDeCreacion = fechaDeCreacion;
    }

    public void setCliente(ICliente cliente) {
        this.cliente = cliente;
    }

    public void setVehiculo(IVehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setFormaDePago(IFormaDePago formaDePago) {
        this.formaDePago = formaDePago;
    }

    public void setImpuestoStrategy(InterfazImpuestoStrategy impuestoStrategy) {
        this.impuestoStrategy = impuestoStrategy;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setServicioDePago(ServicioDePago servicioDePago) {
        this.servicioDePago = servicioDePago;
    }

    public void setServicioDeNotificacion(ServicioDeNotificacion servicioDeNotificacion) {
        this.servicioDeNotificacion = servicioDeNotificacion;
    }
}