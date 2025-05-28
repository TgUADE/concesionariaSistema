package controller;

import java.util.Date; // Required for adding to history

public class EstadoImpuestos implements InterfazEstadoPedido {
    @Override
    public void avanzar(Pedido pedido, Object contexto) {
        System.out.println("Procesando desde Impuestos...");
        // Perform validations/logic specific to Impuestos
        // For example, calculate taxes using pedido.getVehiculo() and impuestoStrategy
        // and add them to pedido.getImpuestosAplicados() and update pedido.getCostoTotal().
        // For now, assume this logic is done.
        System.out.println("Cálculo de impuestos completado para pedido " + pedido.getNumeroPedido() + ". Avanzando a Embarque.");
        pedido.setEstadoActual(new EstadoEmbarque());
    }

    @Override
    public void retroceder(Pedido pedido, Object contexto) {
        System.out.println("Retrocediendo de Impuestos a Cobranzas para pedido " + pedido.getNumeroPedido());
        pedido.setEstadoActual(new EstadoCobranzas());
    }

    @Override
    public String getAreaResponsable() {
        return "Impuestos";
    }
}
