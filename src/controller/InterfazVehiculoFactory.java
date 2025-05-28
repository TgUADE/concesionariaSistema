package controller;

import java.util.List; // For equipamientoOpcional
// Ensure TipoVehiculo is accessible. If it's nested in Vehiculo, this import might not be needed
// if Vehiculo itself is imported or in the same package.
// For clarity, if TipoVehiculo is nested: import controller.Vehiculo.TipoVehiculo;
import controller.Vehiculo.TipoVehiculo;


public interface InterfazVehiculoFactory {
    Vehiculo crearVehiculo(
        int id, // Or generate ID within factory/repository
        String marca,
        String modelo,
        TipoVehiculo tipo, // Enum from Vehiculo.java
        String color,
        double precioBase,
        String caracteristicas,
        List<String> equipamientoOpcional,
        String chasis,
        String motor,
        boolean disponibleVenta // Added
    );
}
