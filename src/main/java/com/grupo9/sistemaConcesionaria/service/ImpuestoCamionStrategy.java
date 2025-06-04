package com.grupo9.sistemaConcesionaria.service;

import com.grupo9.sistemaConcesionaria.model.TipoVehiculo;
import org.springframework.stereotype.Component;

/**
 * Estrategia de cálculo de impuestos para Camiones
 * Según consignas:
 * - Impuesto Nacional: 0%
 * - Impuesto Provincial General: 5%
 * - Impuesto Provincial Adicional: 2%
 * Total: 7%
 */
@Component
public class ImpuestoCamionStrategy implements ImpuestoStrategy {

    private static final double IMPUESTO_NACIONAL = 0.00;        // 0%
    private static final double IMPUESTO_PROVINCIAL_GENERAL = 0.05;  // 5%
    private static final double IMPUESTO_PROVINCIAL_ADICIONAL = 0.02; // 2%

    @Override
    public double calcularImpuesto(double precioBase, TipoVehiculo tipoVehiculo) {
        if (tipoVehiculo != TipoVehiculo.CAMION) {
            return 0.0;
        }

        double impuestoNacional = IMPUESTO_NACIONAL * precioBase;
        double impuestoProvincialGeneral = IMPUESTO_PROVINCIAL_GENERAL * precioBase;
        double impuestoProvincialAdicional = IMPUESTO_PROVINCIAL_ADICIONAL * precioBase;

        return impuestoNacional + impuestoProvincialGeneral + impuestoProvincialAdicional;
    }

    @Override
    public TipoVehiculo getTipoVehiculo() {
        return TipoVehiculo.CAMION;
    }

    @Override
    public String getDetalleImpuestos(double precioBase, TipoVehiculo tipoVehiculo) {
        if (tipoVehiculo != TipoVehiculo.CAMION) {
            return "No aplica para este tipo de vehículo";
        }

        double impuestoNacional = IMPUESTO_NACIONAL * precioBase;
        double impuestoProvincialGeneral = IMPUESTO_PROVINCIAL_GENERAL * precioBase;
        double impuestoProvincialAdicional = IMPUESTO_PROVINCIAL_ADICIONAL * precioBase;
        double total = impuestoNacional + impuestoProvincialGeneral + impuestoProvincialAdicional;

        return String.format(
            "DETALLE IMPUESTOS - CAMIÓN\n" +
            "Precio Base: $%.2f\n" +
            "Impuesto Nacional (0%%): $%.2f\n" +
            "Impuesto Provincial General (5%%): $%.2f\n" +
            "Impuesto Provincial Adicional (2%%): $%.2f\n" +
            "TOTAL IMPUESTOS: $%.2f",
            precioBase, impuestoNacional, impuestoProvincialGeneral, 
            impuestoProvincialAdicional, total
        );
    }
} 