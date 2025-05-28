
package controller;

import java.io.*;
import java.util.*;

/**
 * 
 */
public class Camioneta extends Vehiculo {

    /**
     * Default constructor
     */
    public Camioneta() {
        super(0, "", "", TipoVehiculo.CAMIONETA, "", 0.0, "", new ArrayList<>(), "", "", true);
        this.cargaMaxima = 0.0;
        this.traccion4x4 = false;
        this.capacidadPasajeros = 5; // Default value
    }
    
    /**
     * Parameterized constructor
     */
    public Camioneta(int id, String marca, String modelo, String color, double precioBase, 
                     String caracteristicas, List<String> equipamientoOpcional, String chasis, 
                     String motor, boolean disponibleVenta, double cargaMaxima, boolean traccion4x4, int capacidadPasajeros) {
        super(id, marca, modelo, TipoVehiculo.CAMIONETA, color, precioBase, caracteristicas, 
              equipamientoOpcional, chasis, motor, disponibleVenta);
        this.cargaMaxima = cargaMaxima;
        this.traccion4x4 = traccion4x4;
        this.capacidadPasajeros = capacidadPasajeros;
    }

    /**
     * 
     */
    private double cargaMaxima;

    /**
     * 
     */
    private boolean traccion4x4;

    /**
     * 
     */
    private int capacidadPasajeros;

    /**
     * 
     */
    public double getCargaMaxima() {
        return this.cargaMaxima;
    }

    /**
     * 
     */
    public boolean getTraccion4x4() {
        return this.traccion4x4;
    }

    /**
     * 
     */
    public int getCapacidadPasajeros() {
        return this.capacidadPasajeros;
    }

    /**
     * 
     */
    public void setCargaMaxima(double cargaMaxima) {
        this.cargaMaxima = cargaMaxima;
    }

    /**
     * 
     */
    public void setTraccion4x4(boolean traccion4x4) {
        this.traccion4x4 = traccion4x4;
    }

    /**
     * 
     */
    public void setCapacidadPasajeros(int capacidadPasajeros) {
        this.capacidadPasajeros = capacidadPasajeros;
    }

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.CAMIONETA;
    }

}