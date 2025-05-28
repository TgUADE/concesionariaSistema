package controller;

/**
 * 
 */
public class ImpuestoCamion implements InterfazImpuestoStrategy {

    /**
     * Default constructor
     */
    public ImpuestoCamion() {
    }

    /**
     * 
     */
    @Override
    public double calcularImpuesto(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return 0.0;
        }
        // Implement tax calculation logic for Camion
        // For example, 0% national tax for trucks
        return 0.0;
    }

}