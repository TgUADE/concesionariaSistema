package controller;

/**
 * 
 */
public class ImpuestoCamioneta implements InterfazImpuestoStrategy {

    /**
     * Default constructor
     */
    public ImpuestoCamioneta() {
    }

    /**
     * 
     */
    @Override
    public double calcularImpuesto(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return 0.0;
        }
        // Implement tax calculation logic for Camioneta
        // For example, 10% of vehicle's base price
        return vehiculo.getPrecioBase() * 0.10;
    }

}