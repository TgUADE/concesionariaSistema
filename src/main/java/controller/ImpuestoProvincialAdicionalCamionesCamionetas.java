package controller;

public class ImpuestoProvincialAdicionalCamionesCamionetas implements InterfazImpuestoStrategy {
    private static final double TASA_IMPUESTO_PROVINCIAL_ADICIONAL_CC = 0.02; // 2%

    @Override
    public double calcularImpuesto(Vehiculo vehiculo) {
        if (vehiculo != null && (vehiculo.getTipo() == Vehiculo.TipoVehiculo.CAMION || vehiculo.getTipo() == Vehiculo.TipoVehiculo.CAMIONETA)) {
            return vehiculo.getPrecioBase() * TASA_IMPUESTO_PROVINCIAL_ADICIONAL_CC;
        }
        return 0.0;
    }
}
