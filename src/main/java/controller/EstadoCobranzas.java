package controller;

import java.util.Date; // Required for adding to history

public class EstadoCobranzas implements InterfazEstadoPedido {
    @Override
    public void avanzar(Pedido pedido, Object contexto) {
        System.out.println("Procesando desde Cobranzas...");
        // Perform validations/logic specific to Cobranzas
        // For example, check if payment has been processed or is valid.
        // This might involve checking pedido.getFormaPago().getEstado() or using servicioDePago.
        
        // For simplicity, we'll assume payment is okay if this state is reached and allowed to advance.
        // A real implementation would check:
        // if (pedido.realizarPagoConServicios()) { // Assuming this method now aligns with state logic
        //     System.out.println("Pago verificado en Cobranzas para pedido " + pedido.getNumeroPedido() + ". Avanzando a Impuestos.");
        //     pedido.setEstadoActual(new EstadoImpuestos());
        // } else {
        //     System.err.println("Error en Cobranzas: Pago no pudo ser procesado o verificado para el pedido " + pedido.getNumeroPedido());
        //     // Optionally, remain in Cobranzas or move to an error/hold state.
        // }
        
        System.out.println("Lógica de Cobranzas completada para pedido " + pedido.getNumeroPedido() + ". Avanzando a Impuestos.");
        pedido.setEstadoActual(new EstadoImpuestos());
    }

    @Override
    public void retroceder(Pedido pedido, Object contexto) {
        System.out.println("Retrocediendo de Cobranzas a Ventas para pedido " + pedido.getNumeroPedido());
        pedido.setEstadoActual(new EstadoVentas());
    }

    @Override
    public String getAreaResponsable() {
        return "Cobranzas";
    }
}
