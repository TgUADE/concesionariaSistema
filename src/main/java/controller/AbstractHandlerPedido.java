package controller;

public abstract class AbstractHandlerPedido implements InterfazHandlerPedido {
    protected InterfazHandlerPedido siguiente;

    @Override
    public void setSiguiente(InterfazHandlerPedido siguiente) {
        this.siguiente = siguiente;
    }

    // Default implementation to pass to the next handler if not overridden
    // Subclasses will override procesar and call procesarSiguiente if they wish to continue the chain.
    protected boolean procesarSiguiente(Pedido pedido) {
        if (siguiente != null) {
            return siguiente.procesar(pedido);
        }
        return true; // End of chain, processed successfully by default
    }
    
    // Abstract procesar method to be implemented by concrete handlers
    @Override
    public abstract boolean procesar(Pedido pedido);
}
