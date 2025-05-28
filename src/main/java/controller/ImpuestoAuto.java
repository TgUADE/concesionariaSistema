package controller;

/**
 * 
 */
public class ImpuestoAuto implements InterfazImpuestoStrategy {

    /**
     * Default constructor
     */
    public ImpuestoAuto() {
    }

    /**
     * 
     */
    @Override
    public double calcularImpuesto(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return 0.0;
        }
        // Implement tax calculation logic for Auto
        // For example, 21% of vehicle's base price
        return vehiculo.getPrecioBase() * 0.21;
    }

}