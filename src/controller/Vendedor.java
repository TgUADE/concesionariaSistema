
import java.io.*;
import java.util.*;

/**
 * 
 */
public class Vendedor extends Usuario {

    /**
     * 
     */
    private int ventasTotales; // Changed from Int to int

    /**
     * Constructor
     */
    public Vendedor(int idUsuario, String nombre, String apellido, String mail, int ventasTotales) {
        super(idUsuario, nombre, apellido, mail, RolUsuario.VENDEDOR);
        this.ventasTotales = ventasTotales;
    }

    // Default constructor might be needed if other parts of the system instantiate Vendedor without parameters.
    // For this task, focusing on the parameterized constructor for role setting.
    // If a default constructor is absolutely needed, it should call super() and potentially set RolUsuario.VENDEDOR.
    public Vendedor() {
        super(); // Calls Usuario's default constructor
        setRol(RolUsuario.VENDEDOR); // Ensure role is set even with default constructor
    }


    /**
     * 
     */
    public void consultarCatalogoDeVehiculos() {
        // TODO implement here
    }

    /**
     * 
     */
    public void gestionarVentas() {
        // TODO implement here
    }

    /**
     * 
     */
    public int getVentasTotales() {
        return ventasTotales;
    }

    /**
     * 
     */
    public void setVentasTotales(int ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

}