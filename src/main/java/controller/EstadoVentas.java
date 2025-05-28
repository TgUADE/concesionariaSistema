package controller;

import java.util.Date; // Required for adding to history

public class EstadoVentas implements InterfazEstadoPedido {
    @Override
    public void avanzar(Pedido pedido, Object contexto) {
        System.out.println("Procesando desde Ventas...");
        // Perform validations/logic specific to Ventas
        // For example, check if cliente and vehiculo are valid.
        if (pedido.getCliente() == null || pedido.getVehiculo() == null) {
            System.err.println("Error en Ventas: Cliente o Vehículo no especificado para el pedido " + pedido.getNumeroPedido());
            // Potentially set an error state or throw an exception
            return; 
        }
        System.out.println("Pedido " + pedido.getNumeroPedido() + " validado en Ventas. Avanzando a Cobranzas.");
        pedido.setEstadoActual(new EstadoCobranzas());
        // Optionally call onEnterState for the new state: 
        // if (pedido.getEstadoActual() instanceof SomeStateWithOnEnter) {
        //    ((SomeStateWithOnEnter)pedido.getEstadoActual()).onEnterState(pedido);
        // }
    }

    @Override
    public void retroceder(Pedido pedido, Object contexto) {
        throw new RuntimeException("No se puede retroceder desde el estado Ventas para el pedido " + (pedido != null ? pedido.getNumeroPedido() : "N/A") + ".");
    }

    @Override
    public String getAreaResponsable() {
        return "Ventas";
    }
}
