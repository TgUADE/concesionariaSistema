package controller;

/**
 * 
 */
public class ImpuestoMoto implements InterfazImpuestoStrategy {

    /**
     * Default constructor
     */
    public ImpuestoMoto() {
    }

    /**
     * 
     */
    @Override
    public double calcularImpuesto(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return 0.0;
        }
        // Implement tax calculation logic for Moto
        // For example, 0% national tax for motorcycles
        return 0.0;
    }

}