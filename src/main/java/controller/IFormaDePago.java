package controller;

public interface IFormaDePago {
    void procesarPago(double montoTotal);
    String getEstado();
    String generarComprobante();
    // Add other methods that Pedido might need from FormaDePago
    // For example:
    // int getIdFormaDePago();
    // double getMonto();
}
