package controller;

public class DatosFacturacion {
    private final String nombreORazonSocial;
    private final String direccion;
    private final String cuitOCuil; // Could also have specific validation

    public DatosFacturacion(String nombreORazonSocial, String direccion, String cuitOCuil) {
        if (nombreORazonSocial == null || nombreORazonSocial.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre o Razón Social no puede ser nulo o vacío.");
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección no puede ser nula o vacía.");
        }
        if (cuitOCuil == null || cuitOCuil.trim().isEmpty()) {
            throw new IllegalArgumentException("CUIT/CUIL no puede ser nulo o vacío.");
        }
        this.nombreORazonSocial = nombreORazonSocial;
        this.direccion = direccion;
        this.cuitOCuil = cuitOCuil;
    }

    public String getNombreORazonSocial() {
        return nombreORazonSocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCuitOCuil() {
        return cuitOCuil;
    }

    // Consider overriding equals() and hashCode() for proper value object behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatosFacturacion that = (DatosFacturacion) o;
        return nombreORazonSocial.equals(that.nombreORazonSocial) &&
               direccion.equals(that.direccion) &&
               cuitOCuil.equals(that.cuitOCuil);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(nombreORazonSocial, direccion, cuitOCuil);
    }

    @Override
    public String toString() {
        return "DatosFacturacion{" +
               "nombreORazonSocial='" + nombreORazonSocial + '\'' +
               ", direccion='" + direccion + '\'' +
               ", cuitOCuil='" + cuitOCuil + '\'' +
               '}';
    }
}
