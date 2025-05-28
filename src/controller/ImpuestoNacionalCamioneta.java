package controller;

public class ImpuestoNacionalCamioneta implements InterfazImpuestoStrategy {
    private static final double TASA_IMPUESTO_NACIONAL_CAMIONETA = 0.10; // 10%

    @Override
    public double calcularImpuesto(Vehiculo vehiculo) {
        if (vehiculo != null && vehiculo.getTipo() == Vehiculo.TipoVehiculo.CAMIONETA) {
            return vehiculo.getPrecioBase() * TASA_IMPUESTO_NACIONAL_CAMIONETA;
        }
        return 0.0;
    }
}
