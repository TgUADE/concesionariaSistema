package controller;

public class ChequeoFraudeHandler extends AbstractHandlerPedido {
    @Override
    public boolean procesar(Pedido pedido) {
        System.out.println("CoR: Realizando chequeo de fraude para el pedido " + pedido.getNumeroPedido() + "...");
        // Simulate fraud check logic
        boolean posibleFraude = false; // Example: pedido.getCliente().esSospechoso() || pedido.getCostoTotal() > 100000;
        if (posibleFraude) {
            System.err.println("CoR ALERTA: Posible fraude detectado para el pedido " + pedido.getNumeroPedido() + ".");
            // Could stop chain or just log and continue based on policy
            // return false; 
        }
        System.out.println("CoR: Chequeo de fraude completado para el pedido " + pedido.getNumeroPedido() + ".");
        return procesarSiguiente(pedido);
    }
}
