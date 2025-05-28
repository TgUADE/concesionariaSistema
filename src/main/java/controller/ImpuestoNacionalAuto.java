package controller;

public class ImpuestoNacionalAuto implements InterfazImpuestoStrategy {
    private static final double TASA_IMPUESTO_NACIONAL_AUTO = 0.21; // 21%

    @Override
    public double calcularImpuesto(Vehiculo vehiculo) {
        if (vehiculo != null && vehiculo.getTipo() == Vehiculo.TipoVehiculo.AUTO) {
            return vehiculo.getPrecioBase() * TASA_IMPUESTO_NACIONAL_AUTO;
        }
        return 0.0; // No tax if not applicable type or vehicle is null
    }
}
