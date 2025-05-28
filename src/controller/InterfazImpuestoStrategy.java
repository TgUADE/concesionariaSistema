package controller;

// No specific imports needed if Vehiculo is in the same package.
// If Vehiculo is in a different package, import it.

public interface InterfazImpuestoStrategy {
    /**
     * Calculates tax amount based on the vehicle's attributes.
     * @param vehiculo The vehicle for which to calculate tax.
     * @return The calculated tax amount.
     */
    double calcularImpuesto(Vehiculo vehiculo);
}
