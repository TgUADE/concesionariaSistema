package com.grupo9.sistemaConcesionaria.service;

import com.grupo9.sistemaConcesionaria.model.TipoVehiculo;
import org.springframework.stereotype.Component;

/**
 * Estrategia de cálculo de impuestos para Camionetas
 * Según consignas:
 * - Impuesto Nacional: 10%
 * - Impuesto Provincial General: 5%
 * - Impuesto Provincial Adicional: 2%
 * Total: 17%
 */
@Component
public class ImpuestoCamionetaStrategy implements ImpuestoStrategy {

    private static final double IMPUESTO_NACIONAL = 0.10;        // 10%
    private static final double IMPUESTO_PROVINCIAL_GENERAL = 0.05;  // 5%
    private static final double IMPUESTO_PROVINCIAL_ADICIONAL = 0.02; // 2%

    @Override
    public double calcularImpuesto(double precioBase, TipoVehiculo tipoVehiculo) {
        if (tipoVehiculo != TipoVehiculo.CAMIONETA) {
            return 0.0;
        }

        double impuestoNacional = IMPUESTO_NACIONAL * precioBase;
        double impuestoProvincialGeneral = IMPUESTO_PROVINCIAL_GENERAL * precioBase;
        double impuestoProvincialAdicional = IMPUESTO_PROVINCIAL_ADICIONAL * precioBase;

        return impuestoNacional + impuestoProvincialGeneral + impuestoProvincialAdicional;
    }

    @Override
    public TipoVehiculo getTipoVehiculo() {
        return TipoVehiculo.CAMIONETA;
    }

    @Override
    public String getDetalleImpuestos(double precioBase, TipoVehiculo tipoVehiculo) {
        if (tipoVehiculo != TipoVehiculo.CAMIONETA) {
            return "No aplica para este tipo de vehículo";
        }

        double impuestoNacional = IMPUESTO_NACIONAL * precioBase;
        double impuestoProvincialGeneral = IMPUESTO_PROVINCIAL_GENERAL * precioBase;
        double impuestoProvincialAdicional = IMPUESTO_PROVINCIAL_ADICIONAL * precioBase;
        double total = impuestoNacional + impuestoProvincialGeneral + impuestoProvincialAdicional;

        return String.format(
            "DETALLE IMPUESTOS - CAMIONETA\n" +
            "Precio Base: $%.2f\n" +
            "Impuesto Nacional (10%%): $%.2f\n" +
            "Impuesto Provincial General (5%%): $%.2f\n" +
            "Impuesto Provincial Adicional (2%%): $%.2f\n" +
            "TOTAL IMPUESTOS: $%.2f",
            precioBase, impuestoNacional, impuestoProvincialGeneral, 
            impuestoProvincialAdicional, total
        );
    }
} 