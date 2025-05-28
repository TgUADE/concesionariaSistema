package controller;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Pedido { // No longer implements InterfazPedidoBuilder or other specific interfaces from previous plan for now

    private int numeroPedido;
    private Date fechaCreacion;
    private Cliente cliente; // Changed to concrete class
    private Vehiculo vehiculo; // Changed to concrete class
    private List<ConfiguracionAdicional> configuracionesAdicionales;
    private IFormaDePago formaPago; // Using interface
    private List<ImpuestoAplicado> impuestosAplicados;
    private double costoTotal; // This will likely be calculated
    private DatosFacturacion datosFacturacion;
    private String vendedorNombre;
    private String vendedorCorreo;
    private InterfazEstadoPedido estadoActual; // Using the State interface
    private List<RegistroHistorialEstado> historialEstados;
    private InterfazSubjectObservadorEstado subjectObservadorEstado; // Observer Subject

    // Fields from previous refactoring (Step 4), to be integrated or potentially superseded
    private ServicioDePago servicioDePago; 
    private ServicioDeNotificacion servicioDeNotificacion;

    // Constructor: Simplified for now, focusing on essential new fields.
    // The Builder pattern will handle more complex construction.
    // The service dependencies might be injected differently with a Builder or a Service Locator.
    public Pedido(int numeroPedido, Date fechaCreacion, Cliente cliente, Vehiculo vehiculo,  // Changed types
                    IFormaDePago formaPago, DatosFacturacion datosFacturacion, 
                    String vendedorNombre, String vendedorCorreo,
                    ServicioDePago servicioDePago, ServicioDeNotificacion servicioDeNotificacion) {
        if (fechaCreacion == null) throw new IllegalArgumentException("Fecha de creación no puede ser nula.");
        if (cliente == null) throw new IllegalArgumentException("Cliente no puede ser nulo.");
        if (vehiculo == null) throw new IllegalArgumentException("Vehículo no puede ser nulo.");
        
        this.numeroPedido = numeroPedido;
        this.fechaCreacion = (Date) fechaCreacion.clone();
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.formaPago = formaPago; // Can be null initially
        this.datosFacturacion = datosFacturacion; // Can be null initially
        this.vendedorNombre = vendedorNombre;
        this.vendedorCorreo = vendedorCorreo;
        
        this.configuracionesAdicionales = new ArrayList<>();
        this.impuestosAplicados = new ArrayList<>();
        this.historialEstados = new ArrayList<>();
        // this.estadoActualString = "Iniciado"; // Default initial state - REMOVED
        this.costoTotal = 0; // To be calculated
        
        // Set initial state
        this.estadoActual = new EstadoVentas(); // Default initial state
        // Record initial state in history
        agregarRegistroHistorial(new Date(), this.estadoActual.getAreaResponsable(), this.estadoActual.getClass().getSimpleName());
        
        this.subjectObservadorEstado = new SubjectObservadorEstadoImpl(); // Initialize subject


        // Service dependencies
        this.servicioDePago = servicioDePago;
        this.servicioDeNotificacion = servicioDeNotificacion;
    }

    // Getters
    public int getNumeroPedido() {
        return numeroPedido;
    }

    public Date getFechaCreacion() {
        return (Date) fechaCreacion.clone();
    }

    public Cliente getCliente() { // Changed return type
        return cliente;
    }

    public Vehiculo getVehiculo() { // Changed return type
        return vehiculo;
    }

    public List<ConfiguracionAdicional> getConfiguracionesAdicionales() {
        return new ArrayList<>(configuracionesAdicionales); // Return copy
    }

    public IFormaDePago getFormaPago() {
        return formaPago;
    }

    public List<ImpuestoAplicado> getImpuestosAplicados() {
        return new ArrayList<>(impuestosAplicados); // Return copy
    }

    public double getCostoTotal() {
        // In a real scenario, this would be calculated based on vehiculo.precioBase,
        // configuracionesAdicionales, and impuestosAplicados.
        // For now, just returning the stored value.
        return costoTotal;
    }
    
    public void recalcularCostoTotal() {
        double nuevoTotal = 0;
        if (this.vehiculo instanceof Vehiculo) { // Check if it's the new Vehiculo class with getPrecioBase()
            nuevoTotal += ((Vehiculo)this.vehiculo).getPrecioBase();
        }
        for (ConfiguracionAdicional item : this.configuracionesAdicionales) {
            nuevoTotal += item.getPrecioItem();
        }
        for (ImpuestoAplicado impuesto : this.impuestosAplicados) {
            // This assumes montoCalculado is the actual tax amount to add.
            // If it's a rate, logic would differ.
            nuevoTotal += impuesto.getMontoCalculado(); 
        }
        this.costoTotal = nuevoTotal;
    }


    public DatosFacturacion getDatosFacturacion() {
        return datosFacturacion;
    }

    public String getVendedorNombre() {
        return vendedorNombre;
    }

    public String getVendedorCorreo() {
        return vendedorCorreo;
    }

    public InterfazEstadoPedido getEstadoActual() { // Corrected return type
        return estadoActual;
    }

    public List<RegistroHistorialEstado> getHistorialEstados() {
        return new ArrayList<>(historialEstados); // Return copy
    }
    
    public ServicioDePago getServicioDePago() {
        return servicioDePago;
    }

    public ServicioDeNotificacion getServicioDeNotificacion() {
        return servicioDeNotificacion;
    }

    // Setters (for fields that are mutable post-construction)
    public void setNumeroPedido(int numeroPedido) {
        this.numeroPedido = numeroPedido;
    }
    
    public void setFechaCreacion(Date fechaCreacion) {
        if (fechaCreacion == null) throw new IllegalArgumentException("Fecha de creación no puede ser nula.");
        this.fechaCreacion = (Date) fechaCreacion.clone();
    }

    public void setCliente(Cliente cliente) { // Changed parameter type
        if (cliente == null) throw new IllegalArgumentException("Cliente no puede ser nulo.");
        this.cliente = cliente;
    }

    public void setVehiculo(Vehiculo vehiculo) { // Changed parameter type
        if (vehiculo == null) throw new IllegalArgumentException("Vehículo no puede ser nulo.");
        this.vehiculo = vehiculo;
    }
    
    public void setFormaPago(IFormaDePago formaPago) {
        this.formaPago = formaPago;
    }

    public void setCostoTotal(double costoTotal) { // Typically calculated, but setter might be needed for adjustments
        this.costoTotal = costoTotal;
    }

    public void setDatosFacturacion(DatosFacturacion datosFacturacion) {
        this.datosFacturacion = datosFacturacion;
    }

    public void setVendedorNombre(String vendedorNombre) {
        this.vendedorNombre = vendedorNombre;
    }

    public void setVendedorCorreo(String vendedorCorreo) {
        this.vendedorCorreo = vendedorCorreo;
    }

    public void setEstadoActual(InterfazEstadoPedido nuevoEstado) {
        if (nuevoEstado == null) throw new IllegalArgumentException("El nuevo estado no puede ser nulo.");
        this.estadoActual = nuevoEstado;
        String nuevoEstadoNombre = nuevoEstado.getClass().getSimpleName();
        String areaResponsable = nuevoEstado.getAreaResponsable();
        
        // Add to history
        this.agregarRegistroHistorial(new Date(), areaResponsable, nuevoEstadoNombre);
        System.out.println("Pedido " + getNumeroPedido() + " ha transicionado a: " + nuevoEstadoNombre);
        
        // Notify observers
        this.subjectObservadorEstado.notificarObservadores(this, nuevoEstadoNombre, areaResponsable);
    }
    
    public void setServicioDePago(ServicioDePago servicioDePago) {
        this.servicioDePago = servicioDePago;
    }

    public void setServicioDeNotificacion(ServicioDeNotificacion servicioDeNotificacion) {
        this.servicioDeNotificacion = servicioDeNotificacion;
    }

    // Methods to manage collections
    public void agregarConfiguracionAdicional(ConfiguracionAdicional item) {
        if (item != null) {
            this.configuracionesAdicionales.add(item);
            recalcularCostoTotal();
        }
    }

    public void quitarConfiguracionAdicional(ConfiguracionAdicional item) {
        if (item != null && this.configuracionesAdicionales.remove(item)) {
            recalcularCostoTotal();
        }
    }

    public void agregarImpuestoAplicado(ImpuestoAplicado impuesto) {
        if (impuesto != null) {
            this.impuestosAplicados.add(impuesto);
            recalcularCostoTotal();
        }
    }
    
    public void quitarImpuestoAplicado(ImpuestoAplicado impuesto) {
         if (impuesto != null && this.impuestosAplicados.remove(impuesto)) {
            recalcularCostoTotal();
        }
    }

    public void agregarRegistroHistorial(Date fecha, String areaResponsable, String estado) {
        if (fecha != null && areaResponsable != null && !areaResponsable.trim().isEmpty() && estado != null && !estado.trim().isEmpty()) {
            this.historialEstados.add(new RegistroHistorialEstado(fecha, areaResponsable, estado));
        }
    }
    
    // Placeholder for business logic from previous refactoring (Step 4)
    // These might be refactored further when integrating with a Builder or specific services.
    public void crearPedidoConServicios() {
        System.out.println("Pedido (nueva entidad) creado para: " + (cliente != null ? cliente.getNombre() : "N/A") + " Vehiculo: " + (vehiculo != null ? vehiculo.getMarca() : "N/A"));
        if (servicioDeNotificacion != null && cliente != null) { // Ensure cliente is not null for notification
             servicioDeNotificacion.enviarNotificacion("Pedido N°" + numeroPedido + " creado.", this.cliente);
        }
        // History is now added when initial state is set in constructor
    }

    public boolean realizarPagoConServicios() {
        if (this.formaPago == null) {
            System.out.println("Forma de pago no especificada para pedido N°" + numeroPedido);
            return false;
        }
        if (this.servicioDePago != null) {
            // Recalculate total just before payment to ensure it's up-to-date
            recalcularCostoTotal(); 
            boolean pagoExitoso = this.servicioDePago.procesarPago(this.formaPago, this.costoTotal);
            if (pagoExitoso) {
                // State transition should be handled by the State object, e.g., after Cobranzas.avanzar()
                // For now, if direct payment success implies a state change handled here:
                // this.setEstadoActual(new EstadoImpuestos()); // Or whatever the next state is
                // The above line is an example, proper state change should go through avanzar/retroceder
                System.out.println("Pago exitoso para pedido N°" + numeroPedido + ". El estado del pedido debe ser actualizado por el objeto Estado correspondiente.");
                 if (servicioDeNotificacion != null) {
                    servicioDeNotificacion.enviarNotificacion("Su pago para el pedido N°" + numeroPedido + " ha sido procesado.", this.cliente);
                }
            } else {
                // Similar to above, state update should be handled by state objects.
                System.out.println("Error de pago para pedido N°" + numeroPedido + ". El estado del pedido debe ser actualizado por el objeto Estado correspondiente.");
                if (servicioDeNotificacion != null) {
                    servicioDeNotificacion.enviarNotificacion("Hubo un error al procesar su pago para el pedido N°" + numeroPedido + ".", this.cliente);
                }
            }
            return pagoExitoso;
        }
        System.out.println("Servicio de pago no configurado para pedido N°" + numeroPedido);
        return false;
    }

    // Methods to delegate to the current state object
    public void avanzarPedido(Object contexto) {
        if (this.estadoActual != null) {
            this.estadoActual.avanzar(this, contexto);
        } else {
            System.err.println("Estado actual del pedido no está definido. No se puede avanzar.");
        }
    }

    public void retrocederPedido(Object contexto) {
        if (this.estadoActual != null) {
            this.estadoActual.retroceder(this, contexto);
        } else {
            System.err.println("Estado actual del pedido no está definido. No se puede retroceder.");
        }
    }

    // Methods to manage observers
    public void registrarObservador(Observador observador) {
        this.subjectObservadorEstado.adjuntar(observador);
    }

    public void quitarObservador(Observador observador) {
        this.subjectObservadorEstado.quitar(observador);
    }
}
