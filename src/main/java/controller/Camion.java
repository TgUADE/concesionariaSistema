
package controller;

import java.io.*;
import java.util.*;

/**
 * 
 */
public class Camion extends Vehiculo {

    /**
     * Default constructor
     */
    public Camion() {
        super(0, "", "", TipoVehiculo.CAMION, "", 0.0, "", new ArrayList<>(), "", "", true);
        this.cargaMaxima = 0.0;
        this.numeroEjes = 2; // Default value
    }
    
    /**
     * Parameterized constructor
     */
    public Camion(int id, String marca, String modelo, String color, double precioBase, 
                  String caracteristicas, List<String> equipamientoOpcional, String chasis, 
                  String motor, boolean disponibleVenta, double cargaMaxima, int numeroEjes) {
        super(id, marca, modelo, TipoVehiculo.CAMION, color, precioBase, caracteristicas, 
              equipamientoOpcional, chasis, motor, disponibleVenta);
        this.cargaMaxima = cargaMaxima;
        this.numeroEjes = numeroEjes;
    }

    /**
     * 
     */
    private double cargaMaxima;

    /**
     * 
     */
    private int numeroEjes;

    /**
     * 
     */
    public double getCargaMaxima() {
        return this.cargaMaxima;
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
    public int getNumeroEjes() {
        return this.numeroEjes;
    }

    /**
     * 
     */
    public void setNumeroEjes(int numeroEjes) {
        this.numeroEjes = numeroEjes;
    }

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.CAMION;
    }

}