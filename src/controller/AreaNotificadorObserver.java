package controller;

public class AreaNotificadorObserver implements Observador {
    private String nombreAreaObservada; // Or a more complex representation of the area/department

    public AreaNotificadorObserver(String nombreAreaObservada) {
        this.nombreAreaObservada = nombreAreaObservada;
    }

    @Override
    public void actualizar(Pedido pedido, String estadoInfo, String areaResponsable) {
        System.out.println(String.format(
            "OBSERVER [%s]: Pedido #%d ha cambiado al estado '%s' (responsable: '%s').",
            this.nombreAreaObservada, // Name of this observer instance (e.g., "LogisticaNotifier")
            pedido.getNumeroPedido(),
            estadoInfo,               // e.g., "EstadoLogistica"
            areaResponsable           // e.g., "Logística"
        ));

        // Example: If this observer is specifically for when a Pedido enters the "Logística" area
        if (this.nombreAreaObservada.equalsIgnoreCase("LogisticaNotifier") && "Logística".equalsIgnoreCase(areaResponsable)) {
            System.out.println(String.format(
                "OBSERVER [%s]: Pedido #%d está listo para procesamiento en Logística.",
                this.nombreAreaObservada,
                pedido.getNumeroPedido()
            ));
            // Here, logic could be added to:
            // - Place the Pedido in a specific queue for the Logística department.
            // - Call an external service to notify the Logística system.
            // - Trigger other business processes related to this state change.
        }
        
        // Another example: Generic notification that a state change occurred
        // System.out.println(String.format(
        //    "OBSERVER [%s]: Received update for Pedido #%d. New state: '%s', Area: '%s'.",
        //    this.nombreAreaObservada,
        //    pedido.getNumeroPedido(),
        //    estadoInfo,
        //    areaResponsable
        // ));
    }
}
