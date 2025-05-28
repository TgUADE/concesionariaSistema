
import java.io.*;
import java.util.*;

/**
 * 
 */
public class Cliente implements ICliente {

    /**
     * Default constructor
     */
    public Cliente() {
    }

    /**
     * 
     */
    private String nombre;
    // Assuming other fields like email, direccion might exist or be added later.
    // For now, only implementing what's in ICliente.

    /**
     * 
     */
    public void verEstadoPedidos() {
        // TODO implement here
    }

    /**
     * 
     */
    public void consultarCatalogoVehiculos() {
        // TODO implement here
    }

    /**
     * 
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * 
     */
    @Override
    public String getNombre() {
        return nombre;
    }

}