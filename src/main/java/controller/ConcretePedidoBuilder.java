package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConcretePedidoBuilder implements InterfazPedidoBuilder {
    private int numeroPedido;
    private Date fechaCreacion;
    private ICliente cliente;
    private IVehiculo vehiculo;
    private List<ConfiguracionAdicional> configuracionesAdicionales = new ArrayList<>();
    private IFormaDePago formaPago;
    // impuestosAplicados will be calculated, not set directly by builder usually
    // private List<ImpuestoAplicado> impuestosAplicados = new ArrayList<>();
    // costoTotal will be calculated
    // private double costoTotal;
    private DatosFacturacion datosFacturacion;
    private String vendedorNombre;
    private String vendedorCorreo;
    // estadoActual will be set to initial state by default in build() or Pedido constructor
    // private InterfazEstadoPedido estadoActual;
    // historialEstados will be managed by Pedido
    // private List<RegistroHistorialEstado> historialEstados = new ArrayList<>();

    // Required services for Pedido
    private ServicioDePago servicioDePago;
    private ServicioDeNotificacion servicioDeNotificacion;

    public ConcretePedidoBuilder() {
        // Default values or generate some, e.g.,
        this.fechaCreacion = new Date(); // Default to now
        // this.numeroPedido = generateNextNumeroPedido(); // If auto-generated
    }

    @Override
    public InterfazPedidoBuilder setNumeroPedido(int numeroPedido) {
        this.numeroPedido = numeroPedido;
        return this;
    }

    @Override
    public InterfazPedidoBuilder setFechaCreacion(Date fechaCreacion) {
        if (fechaCreacion != null) {
            this.fechaCreacion = (Date) fechaCreacion.clone();
        }
        return this;
    }

    @Override
    public InterfazPedidoBuilder setCliente(ICliente cliente) {
        this.cliente = cliente;
        return this;
    }

    @Override
    public InterfazPedidoBuilder setVehiculo(IVehiculo vehiculo) {
        this.vehiculo = vehiculo;
        return this;
    }

    @Override
    public InterfazPedidoBuilder agregarConfiguracionAdicional(ConfiguracionAdicional configuracion) {
        if (configuracion != null) {
            this.configuracionesAdicionales.add(configuracion);
        }
        return this;
    }
    
    @Override
    public InterfazPedidoBuilder setConfiguracionesAdicionales(List<ConfiguracionAdicional> configuraciones) {
        if (configuraciones != null) {
            this.configuracionesAdicionales = new ArrayList<>(configuraciones); // Use a copy
        } else {
            this.configuracionesAdicionales = new ArrayList<>();
        }
        return this;
    }

    @Override
    public InterfazPedidoBuilder setFormaDePago(IFormaDePago formaPago) {
        this.formaPago = formaPago;
        return this;
    }

    @Override
    public InterfazPedidoBuilder setDatosFacturacion(DatosFacturacion datosFacturacion) {
        this.datosFacturacion = datosFacturacion;
        return this;
    }

    @Override
    public InterfazPedidoBuilder setVendedor(String nombre, String correo) {
        this.vendedorNombre = nombre;
        this.vendedorCorreo = correo;
        return this;
    }

    @Override
    public InterfazPedidoBuilder setServicioDePago(ServicioDePago servicioDePago) {
        this.servicioDePago = servicioDePago;
        return this;
    }

    @Override
    public InterfazPedidoBuilder setServicioDeNotificacion(ServicioDeNotificacion servicioDeNotificacion) {
        this.servicioDeNotificacion = servicioDeNotificacion;
        return this;
    }

    @Override
    public Pedido build() { // Removed throws
        // Basic validation for required fields
        if (cliente == null || vehiculo == null || servicioDePago == null || servicioDeNotificacion == null || fechaCreacion == null) {
            throw new RuntimeException("Campos obligatorios no fueron establecidos para construir el Pedido. Cliente, Vehículo, ServicioDePago y ServicioDeNotificacion son requeridos.");
        }
        if (numeroPedido <= 0) { // Assuming numeroPedido should be positive
             // For now, let's default it if not set, or throw error.
             // Depending on requirements, it could be auto-generated here or in Pedido constructor if 0.
             System.err.println("Advertencia: numeroPedido no fue establecido o es inválido. Se usará un valor por defecto o podría fallar.");
             // For this example, let Pedido constructor handle it or throw error if it expects positive.
        }


        // The Pedido constructor now matches these parameters.
        // Convert ICliente to Cliente if needed
        Cliente clienteConcreto = (cliente instanceof Cliente) ? (Cliente) cliente : null;
        if (clienteConcreto == null) {
            throw new RuntimeException("Cliente debe ser una instancia de la clase Cliente");
        }
        
        // Convert IVehiculo to Vehiculo if needed
        Vehiculo vehiculoConcreto = (vehiculo instanceof Vehiculo) ? (Vehiculo) vehiculo : null;
        if (vehiculoConcreto == null) {
            throw new RuntimeException("Vehiculo debe ser una instancia de la clase Vehiculo");
        }
        
        // Initial state (EstadoVentas) is set within Pedido's constructor.
        Pedido pedido = new Pedido(
            numeroPedido,
            fechaCreacion,
            clienteConcreto,
            vehiculoConcreto,
            formaPago, // Can be null initially
            datosFacturacion, // Can be null initially
            vendedorNombre,
            vendedorCorreo,
            servicioDePago,
            servicioDeNotificacion
        );
        
        // Set collections
        if (this.configuracionesAdicionales != null && !this.configuracionesAdicionales.isEmpty()) {
            for(ConfiguracionAdicional ca : this.configuracionesAdicionales) {
                pedido.agregarConfiguracionAdicional(ca); // This will also trigger recalcularCostoTotal in Pedido
            }
        } else {
            // If no configuracionesAdicionales were added via builder, ensure Pedido's list is empty
            // and trigger initial cost calculation if needed (though Pedido's agregarConfiguracionAdicional already does)
            pedido.recalcularCostoTotal(); 
        }
        
        // If no configuraciones were added, an explicit call to recalcularCostoTotal ensures
        // the base price of the vehicle is set as the initial costoTotal.
        // If configuraciones were added, agregarConfiguracionAdicional in Pedido already calls it.
        // To be safe, one final call after all configurations are set.
        pedido.recalcularCostoTotal();

        return pedido;
    }
}
