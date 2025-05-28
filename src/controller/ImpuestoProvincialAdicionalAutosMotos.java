package controller;

public class ImpuestoProvincialAdicionalAutosMotos implements InterfazImpuestoStrategy {
    private static final double TASA_IMPUESTO_PROVINCIAL_ADICIONAL_AM = 0.01; // 1%

    @Override
    public double calcularImpuesto(Vehiculo vehiculo) {
        if (vehiculo != null && (vehiculo.getTipo() == Vehiculo.TipoVehiculo.AUTO || vehiculo.getTipo() == Vehiculo.TipoVehiculo.MOTO)) {
            return vehiculo.getPrecioBase() * TASA_IMPUESTO_PROVINCIAL_ADICIONAL_AM;
        }
        return 0.0;
    }
}
