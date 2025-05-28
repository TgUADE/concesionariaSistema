package controller;

import java.util.Date; // Required for adding to history

public class EstadoLogistica implements InterfazEstadoPedido {
    @Override
    public void avanzar(Pedido pedido, Object contexto) {
        System.out.println("Procesando desde Logística...");
        // Perform validations/logic specific to Logística
        // For example, coordinate shipping, generate tracking numbers.
        System.out.println("Coordinación logística completada para pedido " + pedido.getNumeroPedido() + ". Avanzando a Entrega.");
        pedido.setEstadoActual(new EstadoEntrega());
    }

    @Override
    public void retroceder(Pedido pedido, Object contexto) {
        System.out.println("Retrocediendo de Logística a Embarque para pedido " + pedido.getNumeroPedido());
        pedido.setEstadoActual(new EstadoEmbarque());
    }

    @Override
    public String getAreaResponsable() {
        return "Logística";
    }
}
