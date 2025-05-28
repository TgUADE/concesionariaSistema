package controller;

public class ConfirmacionPagoHandler extends AbstractHandlerPedido {
    @Override
    public boolean procesar(Pedido pedido) {
        System.out.println("CoR: Confirmando pago para el pedido " + pedido.getNumeroPedido() + "...");
        // Simulate payment confirmation
        if (pedido.getFormaPago() == null) { // Corrected to getFormaPago()
             System.err.println("CoR ERROR: No se ha especificado forma de pago para el pedido " + pedido.getNumeroPedido() + ".");
             return false; // Stop chain
        }
        // Actual payment processing (pedido.getFormaPago().procesarPago(...)) should ideally
        // happen before this handler if this is just a 'confirmation' step.
        // Or, if this handler IS responsible for processing:
        // pedido.getFormaPago().procesarPago(pedido.getCostoTotal());
        // String comprobante = pedido.getFormaPago().generarComprobante();
        // System.out.println("CoR: Comprobante generado: " + comprobante);
        
        // For this example, we'll assume it's just confirming that a payment method is set
        // and potentially checking its status if that was part of IFormaDePago.
        System.out.println("CoR: Pago confirmado (simulado) para el pedido " + pedido.getNumeroPedido() + " con forma de pago: " + pedido.getFormaPago().getClass().getSimpleName());
        return procesarSiguiente(pedido);
    }
}
