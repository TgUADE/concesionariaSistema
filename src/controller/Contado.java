
import java.io.*;
import java.util.*;

/**
 * 
 */
public class Contado extends FormaDePago {

    /**
     * 
     */
    private String moneda;

    /**
     * Default constructor
     */
    public Contado(String moneda) {
        this.moneda = moneda;
    }

    /**
     * Default constructor for factory.
     */
    public Contado() {
        // Moneda might be set later via setMoneda() or defaults to null/default value
        this.moneda = "N/A"; // Default placeholder if not set
    }

    /**
     * 
     */
    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    /**
     * 
     */
    public String getMoneda() {
        return moneda;
    }

    /**
     * @param montoTotal
     */
    @Override
    public void procesarPago(double montoTotal) {
        // TODO implement here
        System.out.println("Procesando pago en efectivo: " + montoTotal + " en " + this.moneda);
        setEstado("Pagado con Contado"); // Example
    }

    @Override
    public String generarComprobante() {
        // In a real application, this would generate a more detailed receipt.
        return "Comprobante de pago Contado generado. Moneda: " + (this.moneda != null ? this.moneda : "N/A") + ", Monto: " + getMonto();
    }
}