
package controller;

import java.io.*;
import java.util.*;

/**
 * 
 */
public class Auto extends Vehiculo {

    /**
     * Default constructor
     */
    public Auto() {
        super(0, "", "", TipoVehiculo.AUTO, "", 0.0, "", new ArrayList<>(), "", "", true);
        this.puertas = 4; // Default value
    }
    
    /**
     * Parameterized constructor
     */
    public Auto(int id, String marca, String modelo, String color, double precioBase, 
                String caracteristicas, List<String> equipamientoOpcional, String chasis, 
                String motor, boolean disponibleVenta, int puertas) {
        super(id, marca, modelo, TipoVehiculo.AUTO, color, precioBase, caracteristicas, 
              equipamientoOpcional, chasis, motor, disponibleVenta);
        this.puertas = puertas;
    }

    /**
     * 
     */
    private int puertas;

    /**
     * 
     */
    public int getPuertas() {
        return this.puertas;
    }

    /**
     * 
     */
    public void setPuertas(int puertas) {
        this.puertas = puertas;
    }

    /**
     * 
     */
    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.AUTO;
    }

}