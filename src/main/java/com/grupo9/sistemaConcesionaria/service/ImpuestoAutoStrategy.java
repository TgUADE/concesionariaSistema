package com.grupo9.sistemaConcesionaria.service;

import com.grupo9.sistemaConcesionaria.model.TipoVehiculo;
import org.springframework.stereotype.Component;

/**
 * Estrategia de cálculo de impuestos para Autos
 * Según consignas:
 * - Impuesto Nacional: 21%
 * - Impuesto Provincial General: 5%
 * - Impuesto Provincial Adicional: 1%
 * Total: 27%
 */
@Component
public class ImpuestoAutoStrategy implements ImpuestoStrategy {

    private static final double IMPUESTO_NACIONAL = 0.21;        // 21%
    private static final double IMPUESTO_PROVINCIAL_GENERAL = 0.05;  // 5%
    private static final double IMPUESTO_PROVINCIAL_ADICIONAL = 0.01; // 1%

    @Override
    public double calcularImpuesto(double precioBase, TipoVehiculo tipoVehiculo) {
        if (tipoVehiculo != TipoVehiculo.AUTO) {
            return 0.0;
        }

        double impuestoNacional = IMPUESTO_NACIONAL * precioBase;
        double impuestoProvincialGeneral = IMPUESTO_PROVINCIAL_GENERAL * precioBase;
        double impuestoProvincialAdicional = IMPUESTO_PROVINCIAL_ADICIONAL * precioBase;

        return impuestoNacional + impuestoProvincialGeneral + impuestoProvincialAdicional;
    }

    @Override
    public TipoVehiculo getTipoVehiculo() {
        return TipoVehiculo.AUTO;
    }

    @Override
    public String getDetalleImpuestos(double precioBase, TipoVehiculo tipoVehiculo) {
        if (tipoVehiculo != TipoVehiculo.AUTO) {
            return "No aplica para este tipo de vehículo";
        }

        double impuestoNacional = IMPUESTO_NACIONAL * precioBase;
        double impuestoProvincialGeneral = IMPUESTO_PROVINCIAL_GENERAL * precioBase;
        double impuestoProvincialAdicional = IMPUESTO_PROVINCIAL_ADICIONAL * precioBase;
        double total = impuestoNacional + impuestoProvincialGeneral + impuestoProvincialAdicional;

        return String.format(
            "DETALLE IMPUESTOS - AUTO\n" +
            "Precio Base: $%.2f\n" +
            "Impuesto Nacional (21%%): $%.2f\n" +
            "Impuesto Provincial General (5%%): $%.2f\n" +
            "Impuesto Provincial Adicional (1%%): $%.2f\n" +
            "TOTAL IMPUESTOS: $%.2f",
            precioBase, impuestoNacional, impuestoProvincialGeneral, 
            impuestoProvincialAdicional, total
        );
    }
} 