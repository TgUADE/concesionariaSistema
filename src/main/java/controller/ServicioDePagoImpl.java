package controller; // Or a dedicated service package

public class ServicioDePagoImpl implements ServicioDePago {
    @Override
    public boolean procesarPago(IFormaDePago formaDePago, double monto) {
        if (formaDePago == null) {
            System.err.println("ServicioDePago: Error - FormaDePago es null.");
            return false;
        }
        System.out.println("ServicioDePago: Procesando pago de " + monto + " via " + formaDePago.getClass().getSimpleName());
        
        // Set the monto on the FormaDePago object before processing,
        // as this was part of its state in some earlier designs.
        if (formaDePago instanceof FormaDePago) { // Check if it's our abstract class that has setMonto
             ((FormaDePago) formaDePago).setMonto(monto);
        }

        formaDePago.procesarPago(monto); // Delegate to actual payment method
        
        // Simulate success based on the state set by procesarPago
        // This assumes procesarPago implementations call setEstado("Pagado con...")
        if ("Pagado con Contado".equals(formaDePago.getEstado()) || 
            "Pagado con Tarjeta de Crédito".equals(formaDePago.getEstado()) ||
            "Pagado con Transferencia".equals(formaDePago.getEstado())) {
            System.out.println("ServicioDePago: Pago procesado exitosamente.");
            return true;
        } else {
            System.err.println("ServicioDePago: El estado del pago no es exitoso: " + formaDePago.getEstado());
            return false;
        }
    }
}
