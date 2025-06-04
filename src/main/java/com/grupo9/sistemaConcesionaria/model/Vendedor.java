package com.grupo9.sistemaConcesionaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entidad Vendedor - Hereda de Usuario
 * Representa a los vendedores del sistema con acceso a ventas y catálogo
 */
@Entity
@Table(name = "vendedores")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Vendedor extends Usuario {

    @Min(value = 0, message = "Las ventas totales no pueden ser negativas")
    @Column(columnDefinition = "integer default 0")
    @JsonProperty("ventasTotales")
    private Integer ventasTotales = 0;

    @Column(length = 100)
    @JsonProperty("sucursal")
    private String sucursal;

    // Constructors
    public Vendedor() {
        super();
        setRol(RolUsuario.VENDEDOR);
    }

    public Vendedor(String nombre, String apellido, String mail, String sucursal) {
        super(nombre, apellido, mail, RolUsuario.VENDEDOR);
        this.sucursal = sucursal;
        this.ventasTotales = 0;
    }

    // Business methods
    public void consultarCatalogoDeVehiculos() {
        System.out.println("Vendedor " + getNombre() + ": Consultando catálogo de vehículos...");
    }

    public void gestionarVentas() {
        System.out.println("Vendedor " + getNombre() + ": Gestionando ventas...");
    }

    public void incrementarVentas(int cantidad) {
        this.ventasTotales += cantidad;
        System.out.println("Vendedor " + getNombre() + ": Ventas incrementadas. Total: " + ventasTotales);
    }

    public void registrarVenta() {
        incrementarVentas(1);
    }

    // Getters and Setters
    public Integer getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(Integer ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    @Override
    public String toString() {
        return "Vendedor{" +
                "idUsuario=" + getIdUsuario() +
                ", nombre='" + getNombre() + '\'' +
                ", apellido='" + getApellido() + '\'' +
                ", mail='" + getMail() + '\'' +
                ", ventasTotales=" + ventasTotales +
                ", sucursal='" + sucursal + '\'' +
                '}';
    }
} 