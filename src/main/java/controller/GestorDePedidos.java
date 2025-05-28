package controller;
import java.io.*;
import java.util.*;

/**
 * 
 */
public class GestorDePedidos implements InterfazGestorDePedidos {

    /**
     * Default constructor
     */
    private GestorDePedidos() {
    }

    private static GestorDePedidos instancia;

    public static GestorDePedidos getInstancia() {
        if (instancia == null) {
            instancia = new GestorDePedidos();
        }
        return instancia;
    }

    /**
     * 
     */
    private HashMap<Integer, Pedido> pedidos;




    /**
     * 
     */
    public void procesarPedidos() {
        // TODO implement here
    }

    /**
     * 
     */
    public void generarInforme() {
        // TODO implement here
    }

    /**
     * 
     */
    public void getPedio() {
        // TODO implement here
    }

}
