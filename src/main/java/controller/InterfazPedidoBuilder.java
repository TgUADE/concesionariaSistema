package controller;

import java.util.Date;
import java.util.List;

public interface InterfazPedidoBuilder {
    InterfazPedidoBuilder setNumeroPedido(int numeroPedido); // Or allow auto-generation
    InterfazPedidoBuilder setFechaCreacion(Date fechaCreacion); // Or set automatically
    InterfazPedidoBuilder setCliente(ICliente cliente);
    InterfazPedidoBuilder setVehiculo(IVehiculo vehiculo);
    InterfazPedidoBuilder agregarConfiguracionAdicional(ConfiguracionAdicional configuracion);
    InterfazPedidoBuilder setConfiguracionesAdicionales(List<ConfiguracionAdicional> configuraciones);
    InterfazPedidoBuilder setFormaDePago(IFormaDePago formaPago);
    InterfazPedidoBuilder setDatosFacturacion(DatosFacturacion datosFacturacion);
    InterfazPedidoBuilder setVendedor(String nombre, String correo);

    // Methods to set services required by Pedido
    InterfazPedidoBuilder setServicioDePago(ServicioDePago servicioDePago);
    InterfazPedidoBuilder setServicioDeNotificacion(ServicioDeNotificacion servicioDeNotificacion);
    
    // Method to set initial state explicitly if needed, otherwise builder can set default
    // InterfazPedidoBuilder setInitialState(InterfazEstadoPedido estadoInicial);

    Pedido build();
}
