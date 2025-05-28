package controller;

import java.util.HashMap;
import java.util.Map;

public class GestorDeStock {
    private Map<Integer, Integer> stockVehiculos; // ID del vehículo -> cantidad en stock
    
    public GestorDeStock() {
        this.stockVehiculos = new HashMap<>();
    }
    
    public void agregarStock(int idVehiculo, int cantidad) {
        stockVehiculos.put(idVehiculo, stockVehiculos.getOrDefault(idVehiculo, 0) + cantidad);
    }
    
    public boolean verificarDisponibilidad(int idVehiculo) {
        return stockVehiculos.getOrDefault(idVehiculo, 0) > 0;
    }
    
    public boolean descontarStock(int idVehiculo) {
        int stockActual = stockVehiculos.getOrDefault(idVehiculo, 0);
        if (stockActual > 0) {
            stockVehiculos.put(idVehiculo, stockActual - 1);
            return true;
        }
        return false;
    }
    
    public int obtenerStock(int idVehiculo) {
        return stockVehiculos.getOrDefault(idVehiculo, 0);
    }
}
