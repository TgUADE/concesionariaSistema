package controller; // Using controller package as specified for now

public interface InterfazPedidoService {
    Pedido crearPedido(Pedido pedido) throws ExcepcionDuplicado, ExcepcionValidacion;
    // Using Pedido as input assumes builder has already run, or service uses builder.
}
