package controller;

import java.io.*;
import java.util.*;

/**
 * 
 */
public interface InterfazDeSistema {

    /**
     * 
     */
    public GestorDePedidos getGestorDePedidos();

    /**
     * 
     */
    public GestorDeUsuarios getGestorDeUsuario();

    /**
     * 
     */
    public GestorVehiculos getGestorDeVehiculo();




    /**
     * 
     */
    public void realizarPedido();

    /**
     * 
     */
    public void procesarPedido();

    /**
     * 
     */
    public void generarReporteDeVentas();

    /**
     * 
     */
    public void agregarUsuario();

    /**
     * 
     */
    public void eliminarUsuario();

    /**
     * 
     */
    public void actualizarUsuario();

    /**
     * 
     */
    public void agregarVehiculo();

    /**
     * 
     */
    public void actualizarVehiculo();

    /**
     * 
     */
    public void eliminarVehiculo();

}