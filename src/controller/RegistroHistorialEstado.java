package controller;

import java.util.Date;

public class RegistroHistorialEstado {
    private final Date fecha;
    private final String areaResponsable;
    private final String estado; // Could be an Enum if states are predefined

    public RegistroHistorialEstado(Date fecha, String areaResponsable, String estado) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }
        if (areaResponsable == null || areaResponsable.trim().isEmpty()) {
            throw new IllegalArgumentException("El área responsable no puede ser nula o vacía.");
        }
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede ser nulo o vacío.");
        }
        this.fecha = (Date) fecha.clone(); // Defensive copy for mutable Date
        this.areaResponsable = areaResponsable;
        this.estado = estado;
    }

    public Date getFecha() {
        return (Date) fecha.clone(); // Defensive copy
    }

    public String getAreaResponsable() {
        return areaResponsable;
    }

    public String getEstado() {
        return estado;
    }

    // Consider overriding equals() and hashCode() for proper value object behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroHistorialEstado that = (RegistroHistorialEstado) o;
        return fecha.equals(that.fecha) &&
               areaResponsable.equals(that.areaResponsable) &&
               estado.equals(that.estado);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(fecha, areaResponsable, estado);
    }

    @Override
    public String toString() {
        return "RegistroHistorialEstado{" +
               "fecha=" + fecha +
               ", areaResponsable='" + areaResponsable + '\'' +
               ", estado='" + estado + '\'' +
               '}';
    }
}
