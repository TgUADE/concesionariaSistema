package controller.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
// Removed RuntimeTypeAdapterFactory import - using basic Gson instead
import controller.*; // For entities and interfaces like IFormaDePago
import controller.Cliente; // Explicit import for Pedido change
import controller.Vehiculo;  // Explicit import for Pedido change
// Assuming all state classes are directly in 'controller' package for simplicity
// If they were in a subpackage like controller.state, the import would be controller.state.*;

public class GsonUtil {
    private static Gson gsonInstance;

    static {
        // Simplified Gson configuration without RuntimeTypeAdapterFactory
        gsonInstance = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setPrettyPrinting()
            .create();
    }
    
    public static Gson getGson() {
        return gsonInstance;
    }
}
