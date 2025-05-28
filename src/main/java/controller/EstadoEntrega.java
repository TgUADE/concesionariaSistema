package controller;

import java.util.Date; // Required for adding to history

public class EstadoEntrega implements InterfazEstadoPedido {
    @Override
    public void avanzar(Pedido pedido, Object contexto) {
        System.out.println("Procesando desde Entrega...");
        // Perform validations/logic specific to Entrega
        // For example, confirm delivery, obtain signature.
        System.out.println("Pedido " + pedido.getNumeroPedido() + " ha sido entregado. Este es el estado final.");
        // No state transition further from Entrega in this simple model.
        // Potentially could transition to a "Completado" or "Archivado" state if needed.
        // For now, avanzar does nothing more.
    }

    @Override
    public void retroceder(Pedido pedido, Object contexto) {
        System.out.println("Retrocediendo de Entrega a Logística para pedido " + pedido.getNumeroPedido() + " (ej. entrega fallida y necesita re-coordinación).");
        pedido.setEstadoActual(new EstadoLogistica());
    }

    @Override
    public String getAreaResponsable() {
        return "Entrega";
    }
}
