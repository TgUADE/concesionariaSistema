package controller;

import java.util.Date; // Required for adding to history

public class EstadoEmbarque implements InterfazEstadoPedido {
    @Override
    public void avanzar(Pedido pedido, Object contexto) {
        System.out.println("Procesando desde Embarque...");
        // Perform validations/logic specific to Embarque
        // For example, check if vehicle is ready, documents are in order.
        System.out.println("Preparativos de embarque completados para pedido " + pedido.getNumeroPedido() + ". Avanzando a Logística.");
        pedido.setEstadoActual(new EstadoLogistica());
    }

    @Override
    public void retroceder(Pedido pedido, Object contexto) {
        System.out.println("Retrocediendo de Embarque a Impuestos para pedido " + pedido.getNumeroPedido());
        pedido.setEstadoActual(new EstadoImpuestos());
    }

    @Override
    public String getAreaResponsable() {
        return "Embarque";
    }
}
