package controller;

import java.util.List;
import java.util.ArrayList;

public class Vehiculo implements IVehiculo { // Retaining IVehiculo for Pedido compatibility

    public enum TipoVehiculo {
        AUTO, CAMIONETA, MOTO, CAMION
    }

    private int id;
    private String marca;
    private String modelo;
    private TipoVehiculo tipo;
    private String color;
    private double precioBase;
    private String caracteristicas; // general description
    private List<String> equipamientoOpcional;
    private String chasis;
    private String motor;
    private boolean disponibleVenta = true; // Default to true

    public Vehiculo(int id, String marca, String modelo, TipoVehiculo tipo, String color, 
                    double precioBase, String caracteristicas, List<String> equipamientoOpcional, 
                    String chasis, String motor, boolean disponibleVenta) { // Added disponibleVenta
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
        this.color = color;
        this.precioBase = precioBase;
        this.caracteristicas = caracteristicas;
        this.equipamientoOpcional = equipamientoOpcional != null ? new ArrayList<>(equipamientoOpcional) : new ArrayList<>();
        this.chasis = chasis;
        this.motor = motor;
        this.disponibleVenta = disponibleVenta; // Assign
    }

    // Getters
    public int getId() {
        return id;
    }

    @Override // From IVehiculo
    public String getMarca() {
        return marca;
    }

    @Override // From IVehiculo
    public String getModelo() {
        return modelo;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public String getColor() {
        return color;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public List<String> getEquipamientoOpcional() {
        // Return a copy to maintain encapsulation if the list is mutable externally
        return new ArrayList<>(equipamientoOpcional);
    }

    public String getChasis() {
        return chasis;
    }

    public String getMotor() {
        return motor;
    }

    public boolean isDisponibleVenta() {
        return disponibleVenta;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setTipo(TipoVehiculo tipo) {
        this.tipo = tipo;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public void setEquipamientoOpcional(List<String> equipamientoOpcional) {
        this.equipamientoOpcional = equipamientoOpcional != null ? new ArrayList<>(equipamientoOpcional) : new ArrayList<>();
    }
    
    public void addEquipamientoOpcional(String equipamiento) {
        if (equipamiento != null && !equipamiento.trim().isEmpty()) {
            this.equipamientoOpcional.add(equipamiento);
        }
    }

    public void setChasis(String chasis) {
        this.chasis = chasis;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public void setDisponibleVenta(boolean disponibleVenta) {
        this.disponibleVenta = disponibleVenta;
    }
}
