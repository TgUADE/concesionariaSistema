
import java.io.*;
import java.util.*;

/**
 * 
 */
public class EstadoPedido ChainOfResponsability implements InterfazObervadorEstado {

    /**
     * Default constructor
     */
    public EstadoPedido ChainOfResponsability() {
    }

    /**
     * 
     */
    private String nombre;

    /**
     * 
     */
    private EstadoPedido siguienteEstado;

    /**
     * 
     */
    private List<Observadores> seguidores;


    /**
     * 
     */
    public void manejarPedido() {
        // TODO implement here
    }

    /**
     * 
     */
    public void actualizarEstado() {
        // TODO implement here
    }

    /**
     * 
     */
    public void setSiguienteEstado() {
        // TODO implement here
    }

    /**
     * 
     */
    public void adjuntar() {
        // TODO implement InterfazObervadorEstado.adjuntar() here
    }

    /**
     * 
     */
    public void notificar() {
        // TODO implement InterfazObervadorEstado.notificar() here
    }

}