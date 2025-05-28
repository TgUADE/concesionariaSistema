package controller;

import java.util.List;
// Ensure TipoVehiculo is accessible. If it's nested in Vehiculo:
import controller.Vehiculo.TipoVehiculo;

public class VehiculoFactoryImpl implements InterfazVehiculoFactory {
    @Override
    public Vehiculo crearVehiculo(
                        int id,
                        String marca,
                        String modelo,
                        TipoVehiculo tipo,
                        String color,
                        double precioBase,
                        String caracteristicas,
                        List<String> equipamientoOpcional,
                        String chasis,
                        String motor,
                        boolean disponibleVenta) throws ExcepcionValidacion { // Added disponibleVenta and throws

        // Basic validation (can also be in Vehiculo constructor or a dedicated validation service)
        if (marca == null || marca.trim().isEmpty()) {
            throw new ExcepcionValidacion("Marca es obligatoria para crear un vehículo.");
        }
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new ExcepcionValidacion("Modelo es obligatorio para crear un vehículo.");
        }
        if (tipo == null) {
            throw new ExcepcionValidacion("Tipo es obligatorio para crear un vehículo.");
        }
        if (precioBase < 0) {
            throw new ExcepcionValidacion("Precio base no puede ser negativo.");
        }


        // Create the Vehiculo instance using its constructor
        Vehiculo vehiculo = new Vehiculo(id, marca, modelo, tipo, color, precioBase, caracteristicas, equipamientoOpcional, chasis, motor, disponibleVenta);
        
        // Any type-specific logic after instantiation can be added here if needed.
        // For example, if certain types had default 'caracteristicas' if not provided and not handled by constructor:
        // switch (tipo) {
        //    case AUTO:
        //        if (vehiculo.getCaracteristicas() == null || vehiculo.getCaracteristicas().isEmpty()) {
        //            vehiculo.setCaracteristicas("Auto estándar de pasajeros");
        //        }
        //        break;
        //    case CAMION:
        //        // camion-specific logic
        //        break;
        //    // ... other cases
        // }
        
        // For now, the constructor of Vehiculo handles all provided parameters.

        return vehiculo;
    }
}
