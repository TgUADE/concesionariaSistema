package controller;

// Context object to pass data through the chain, could be Pedido itself or a more specific context
// For this example, let's assume it operates on a Pedido.
public interface InterfazHandlerPedido {
    void setSiguiente(InterfazHandlerPedido siguiente);
    // 'procesar' returns true if processing should continue down the chain,
    // false if this handler completes the necessary action or a condition stops further processing.
    // Or it could throw an exception to stop the chain on error.
    // For simplicity, let's use a boolean return for now.
    boolean procesar(Pedido pedido);
}
