package controller;

public class ConfiguracionAdicional {
    private final String nombreItem;
    private final double precioItem;

    public ConfiguracionAdicional(String nombreItem, double precioItem) {
        if (nombreItem == null || nombreItem.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del item no puede ser nulo o vacío.");
        }
        if (precioItem < 0) {
            throw new IllegalArgumentException("El precio del item no puede ser negativo.");
        }
        this.nombreItem = nombreItem;
        this.precioItem = precioItem;
    }

    public String getNombreItem() {
        return nombreItem;
    }

    public double getPrecioItem() {
        return precioItem;
    }

    // Consider overriding equals() and hashCode() for proper value object behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguracionAdicional that = (ConfiguracionAdicional) o;
        return Double.compare(that.precioItem, precioItem) == 0 &&
               nombreItem.equals(that.nombreItem);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(nombreItem, precioItem);
    }

    @Override
    public String toString() {
        return "ConfiguracionAdicional{" +
               "nombreItem='" + nombreItem + '\'' +
               ", precioItem=" + precioItem +
               '}';
    }
}
