package controller;

public class ValidacionMontoHandler extends AbstractHandlerPedido {
    private double montoMinimoPermitido = 10.0; // Example threshold

    @Override
    public boolean procesar(Pedido pedido) { // Removed throws
        System.out.println("CoR: Validando monto del pedido " + pedido.getNumeroPedido() + "...");
        if (pedido.getCostoTotal() < montoMinimoPermitido) {
            throw new RuntimeException("El monto del pedido " + pedido.getNumeroPedido() + " es demasiado bajo (" + pedido.getCostoTotal() + ").");
        }
        System.out.println("CoR: Monto del pedido " + pedido.getNumeroPedido() + " validado.");
        return procesarSiguiente(pedido); // Continue to next handler
    }
}
