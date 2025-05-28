package controller;

public interface InterfazEstadoPedido {
    // Attempts to advance the pedido to the next state.
    // 'contexto' can be a Map or a dedicated class to pass data needed by the next state or for validation.
    void avanzar(Pedido pedido, Object contexto);

    // Attempts to move the pedido to the previous state (if applicable and allowed).
    void retroceder(Pedido pedido, Object contexto);

    // Returns a string descriptor of the current area/state.
    String getAreaResponsable();

    // Optional: A method to perform actions specific to the current state upon entering it.
    // void onEnterState(Pedido pedido);
}
