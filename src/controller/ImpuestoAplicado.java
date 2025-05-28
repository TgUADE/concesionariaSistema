package controller;

public class ImpuestoAplicado {
    private final String nombreImpuesto;
    private final double porcentajeAplicado; // e.g., 0.21 for 21%
    private final double montoCalculado;

    public ImpuestoAplicado(String nombreImpuesto, double porcentajeAplicado, double montoCalculado) {
        if (nombreImpuesto == null || nombreImpuesto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del impuesto no puede ser nulo o vacío.");
        }
        if (porcentajeAplicado < 0) { // Could also check for > 1 if it's strictly a percentage factor
            throw new IllegalArgumentException("El porcentaje aplicado no puede ser negativo.");
        }
        if (montoCalculado < 0) {
            throw new IllegalArgumentException("El monto calculado no puede ser negativo.");
        }
        this.nombreImpuesto = nombreImpuesto;
        this.porcentajeAplicado = porcentajeAplicado;
        this.montoCalculado = montoCalculado;
    }

    public String getNombreImpuesto() {
        return nombreImpuesto;
    }

    public double getPorcentajeAplicado() {
        return porcentajeAplicado;
    }

    public double getMontoCalculado() {
        return montoCalculado;
    }

    // Consider overriding equals() and hashCode() for proper value object behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImpuestoAplicado that = (ImpuestoAplicado) o;
        return Double.compare(that.porcentajeAplicado, porcentajeAplicado) == 0 &&
               Double.compare(that.montoCalculado, montoCalculado) == 0 &&
               nombreImpuesto.equals(that.nombreImpuesto);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(nombreImpuesto, porcentajeAplicado, montoCalculado);
    }

    @Override
    public String toString() {
        return "ImpuestoAplicado{" +
               "nombreImpuesto='" + nombreImpuesto + '\'' +
               ", porcentajeAplicado=" + porcentajeAplicado +
               ", montoCalculado=" + montoCalculado +
               '}';
    }
}
