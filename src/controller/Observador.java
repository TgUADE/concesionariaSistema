package controller;

public interface Observador {
    // 'estadoInfo' could be the simple state name, or a more complex object with Pedido context
    void actualizar(Pedido pedido, String estadoInfo, String areaResponsable);
}
