package controller.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import controller.*; // For entities and interfaces like IFormaDePago
import controller.Cliente; // Explicit import for Pedido change
import controller.Vehiculo;  // Explicit import for Pedido change
// Assuming all state classes are directly in 'controller' package for simplicity
// If they were in a subpackage like controller.state, the import would be controller.state.*;

public class GsonUtil {
    private static Gson gsonInstance;

    static {
        RuntimeTypeAdapterFactory<InterfazEstadoPedido> estadoAdapter =
            RuntimeTypeAdapterFactory.of(InterfazEstadoPedido.class, "_type")
                .registerSubtype(EstadoVentas.class, EstadoVentas.class.getSimpleName())
                .registerSubtype(EstadoCobranzas.class, EstadoCobranzas.class.getSimpleName())
                .registerSubtype(EstadoImpuestos.class, EstadoImpuestos.class.getSimpleName())
                .registerSubtype(EstadoEmbarque.class, EstadoEmbarque.class.getSimpleName())
                .registerSubtype(EstadoLogistica.class, EstadoLogistica.class.getSimpleName())
                .registerSubtype(EstadoEntrega.class, EstadoEntrega.class.getSimpleName());

        RuntimeTypeAdapterFactory<IFormaDePago> formaPagoAdapter =
            RuntimeTypeAdapterFactory.of(IFormaDePago.class, "_type")
                .registerSubtype(Contado.class, Contado.class.getSimpleName())
                .registerSubtype(TarjetaDeCredito.class, TarjetaDeCredito.class.getSimpleName())
                .registerSubtype(Transferencia.class, Transferencia.class.getSimpleName());
        
        // No adapters needed for ICliente and IVehiculo in Pedido due to the decision
        // to change Pedido to use concrete Cliente and Vehiculo fields for GSON persistence.

        gsonInstance = new GsonBuilder()
            .registerTypeAdapterFactory(estadoAdapter)
            .registerTypeAdapterFactory(formaPagoAdapter)
            .setPrettyPrinting()
            .serializeNulls() // Important if some fields can be null
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ") // Consistent date format
            .create();
    }

    public static Gson getGson() {
        return gsonInstance;
    }
}
