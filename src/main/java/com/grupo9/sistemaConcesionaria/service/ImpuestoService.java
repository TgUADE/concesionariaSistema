package com.grupo9.sistemaConcesionaria.service;

import com.grupo9.sistemaConcesionaria.model.TipoVehiculo;
import com.grupo9.sistemaConcesionaria.model.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ImpuestoService - Servicio coordinador del Strategy Pattern para impuestos
 * Gestiona todas las estrategias de cálculo de impuestos según el tipo de vehículo
 * Cumple con las consignas del sistema de cálculo de impuestos
 */
@Service
public class ImpuestoService {

    private static final Logger logger = LoggerFactory.getLogger(ImpuestoService.class);
    
    private final Map<TipoVehiculo, ImpuestoStrategy> strategies = new HashMap<>();

    /**
     * Constructor que inyecta todas las estrategias de impuestos
     */
    @Autowired
    public ImpuestoService(List<ImpuestoStrategy> impuestoStrategies) {
        // Registrar todas las estrategias disponibles
        for (ImpuestoStrategy strategy : impuestoStrategies) {
            strategies.put(strategy.getTipoVehiculo(), strategy);
            logger.info("Estrategia de impuestos registrada para: {}", strategy.getTipoVehiculo());
        }
    }

    /**
     * Calcula el impuesto total para un vehículo específico
     * @param vehiculo El vehículo para el cual calcular impuestos
     * @return Monto total de impuestos
     */
    public double calcularImpuesto(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo");
        }
        
        return calcularImpuesto(vehiculo.getPrecioBase(), vehiculo.getTipo());
    }

    /**
     * Calcula el impuesto para un precio base y tipo de vehículo específicos
     * @param precioBase Precio base del vehículo
     * @param tipoVehiculo Tipo de vehículo
     * @return Monto total de impuestos
     */
    public double calcularImpuesto(double precioBase, TipoVehiculo tipoVehiculo) {
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio base no puede ser negativo");
        }
        
        if (tipoVehiculo == null) {
            throw new IllegalArgumentException("El tipo de vehículo no puede ser nulo");
        }

        ImpuestoStrategy strategy = strategies.get(tipoVehiculo);
        if (strategy == null) {
            logger.warn("No se encontró estrategia para el tipo de vehículo: {}", tipoVehiculo);
            return 0.0;
        }

        double impuesto = strategy.calcularImpuesto(precioBase, tipoVehiculo);
        logger.debug("Impuesto calculado para {} (${:.2f}): ${:.2f}", 
                    tipoVehiculo, precioBase, impuesto);
        
        return impuesto;
    }

    /**
     * Obtiene el detalle de impuestos para un vehículo
     * @param vehiculo El vehículo
     * @return Detalle de impuestos en formato legible
     */
    public String getDetalleImpuestos(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo");
        }
        
        return getDetalleImpuestos(vehiculo.getPrecioBase(), vehiculo.getTipo());
    }

    /**
     * Obtiene el detalle de impuestos para un precio base y tipo específicos
     * @param precioBase Precio base del vehículo
     * @param tipoVehiculo Tipo de vehículo
     * @return Detalle de impuestos en formato legible
     */
    public String getDetalleImpuestos(double precioBase, TipoVehiculo tipoVehiculo) {
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio base no puede ser negativo");
        }
        
        if (tipoVehiculo == null) {
            throw new IllegalArgumentException("El tipo de vehículo no puede ser nulo");
        }

        ImpuestoStrategy strategy = strategies.get(tipoVehiculo);
        if (strategy == null) {
            return "No se encontró estrategia para el tipo de vehículo: " + tipoVehiculo;
        }

        return strategy.getDetalleImpuestos(precioBase, tipoVehiculo);
    }

    /**
     * Calcula el precio final (precio base + impuestos) para un vehículo
     * @param vehiculo El vehículo
     * @return Precio final incluyendo impuestos
     */
    public double calcularPrecioFinal(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo");
        }
        
        double precioBase = vehiculo.getPrecioBase();
        double impuesto = calcularImpuesto(vehiculo);
        return precioBase + impuesto;
    }

    /**
     * Calcula el precio final para un precio base y tipo específicos
     * @param precioBase Precio base del vehículo
     * @param tipoVehiculo Tipo de vehículo
     * @return Precio final incluyendo impuestos
     */
    public double calcularPrecioFinal(double precioBase, TipoVehiculo tipoVehiculo) {
        double impuesto = calcularImpuesto(precioBase, tipoVehiculo);
        return precioBase + impuesto;
    }

    /**
     * Obtiene el porcentaje total de impuestos para un tipo de vehículo
     * @param tipoVehiculo Tipo de vehículo
     * @return Porcentaje total de impuestos (como decimal, ej: 0.27 para 27%)
     */
    public double getPorcentajeImpuesto(TipoVehiculo tipoVehiculo) {
        if (tipoVehiculo == null) {
            throw new IllegalArgumentException("El tipo de vehículo no puede ser nulo");
        }

        // Calcular impuesto sobre una base de $100 para obtener el porcentaje
        double impuesto = calcularImpuesto(100.0, tipoVehiculo);
        return impuesto / 100.0;
    }

    /**
     * Verifica si está disponible una estrategia para el tipo de vehículo
     * @param tipoVehiculo Tipo de vehículo
     * @return true si hay estrategia disponible
     */
    public boolean tieneEstrategia(TipoVehiculo tipoVehiculo) {
        return tipoVehiculo != null && strategies.containsKey(tipoVehiculo);
    }

    /**
     * Obtiene un resumen de todos los porcentajes de impuestos por tipo
     * @return Mapa con porcentajes por tipo de vehículo
     */
    public Map<TipoVehiculo, Double> getResumenPorcentajes() {
        Map<TipoVehiculo, Double> resumen = new HashMap<>();
        for (TipoVehiculo tipo : TipoVehiculo.values()) {
            if (tieneEstrategia(tipo)) {
                resumen.put(tipo, getPorcentajeImpuesto(tipo));
            }
        }
        return resumen;
    }

    /**
     * Genera un reporte completo de impuestos para un vehículo
     * @param vehiculo El vehículo
     * @return Reporte completo con todos los detalles
     */
    public String generarReporteCompleto(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo");
        }

        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE IMPUESTOS ===\n");
        reporte.append("Vehículo: ").append(vehiculo.getMarca()).append(" ").append(vehiculo.getModelo()).append("\n");
        reporte.append("Tipo: ").append(vehiculo.getTipo().getDescripcion()).append("\n");
        reporte.append("Chasis: ").append(vehiculo.getNumeroChasis()).append("\n\n");
        
        reporte.append(getDetalleImpuestos(vehiculo)).append("\n\n");
        
        double precioFinal = calcularPrecioFinal(vehiculo);
        reporte.append("PRECIO FINAL: $").append(String.format("%.2f", precioFinal));
        
        return reporte.toString();
    }
} 