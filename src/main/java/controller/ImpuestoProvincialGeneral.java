package controller;

public class ImpuestoProvincialGeneral implements InterfazImpuestoStrategy {
    private static final double TASA_IMPUESTO_PROVINCIAL_GENERAL = 0.05; // 5%

    @Override
    public double calcularImpuesto(Vehiculo vehiculo) {
        if (vehiculo != null) {
            return vehiculo.getPrecioBase() * TASA_IMPUESTO_PROVINCIAL_GENERAL;
        }
        return 0.0;
    }
}
