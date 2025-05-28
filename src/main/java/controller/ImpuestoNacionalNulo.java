package controller;

public class ImpuestoNacionalNulo implements InterfazImpuestoStrategy {
    @Override
    public double calcularImpuesto(Vehiculo vehiculo) {
        // Applies to MOTO and CAMION for National Tax, or any other case needing zero tax.
        // Condition can be more specific if needed, e.g.,
        // if (vehiculo != null && (vehiculo.getTipo() == Vehiculo.TipoVehiculo.MOTO || vehiculo.getTipo() == Vehiculo.TipoVehiculo.CAMION)) {
        //     return 0.0;
        // }
        return 0.0;
    }
}
