
package controller;

import java.io.*;
import java.util.*;

/**
 * 
 */
public class Moto extends Vehiculo {

    /**
     * Default constructor
     */
    public Moto() {
        super(0, "", "", TipoVehiculo.MOTO, "", 0.0, "", new ArrayList<>(), "", "", true);
        this.tipoMoto = "";
        this.cilindrada = 0;
    }
    
    /**
     * Parameterized constructor
     */
    public Moto(int id, String marca, String modelo, String color, double precioBase, 
                String caracteristicas, List<String> equipamientoOpcional, String chasis, 
                String motor, boolean disponibleVenta, String tipoMoto, int cilindrada) {
        super(id, marca, modelo, TipoVehiculo.MOTO, color, precioBase, caracteristicas, 
              equipamientoOpcional, chasis, motor, disponibleVenta);
        this.tipoMoto = tipoMoto;
        this.cilindrada = cilindrada;
    }

    /**
     * 
     */
    private String tipoMoto;

    /**
     * 
     */
    private int cilindrada;

    /**
     * 
     */
    public TipoVehiculo getTipo() {
        return TipoVehiculo.MOTO;
    }

    /**
     * 
     */
    public String getTipoMoto() {
        return this.tipoMoto;
    }

    /**
     * 
     */
    public int getCilindrada() {
        return this.cilindrada;
    }

    /**
     * 
     */
    public void setTipoMoto(String tipoMoto) {
        this.tipoMoto = tipoMoto;
    }

    /**
     * 
     */
    public void setCilindrada(int cilindrada) {
        this.cilindrada = cilindrada;
    }

}