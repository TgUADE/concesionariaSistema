package com.grupo9.sistemaConcesionaria.service;

import com.grupo9.sistemaConcesionaria.model.TipoVehiculo;

/**
 * Strategy Pattern para cálculo de impuestos
 * Según consignas:
 * - Impuesto Nacional: Autos 21%, Camionetas 10%, Motos/Camiones 0%
 * - Impuesto Provincial General: 5% (todos)
 * - Impuesto Provincial Adicional: Camiones/Camionetas 2%, Autos/Motos 1%
 */
public interface ImpuestoStrategy {

    /**
     * Calcula el impuesto total para un vehículo
     * @param precioBase Precio base del vehículo
     * @param tipoVehiculo Tipo de vehículo (Auto, Camioneta, Moto, Camión)
     * @return Monto total de impuestos
     */
    double calcularImpuesto(double precioBase, TipoVehiculo tipoVehiculo);

    /**
     * Obtiene el tipo de vehículo que maneja esta estrategia
     * @return Tipo de vehículo
     */
    TipoVehiculo getTipoVehiculo();

    /**
     * Obtiene el detalle de impuestos aplicados
     * @param precioBase Precio base del vehículo
     * @param tipoVehiculo Tipo de vehículo
     * @return Detalle de impuestos en formato legible
     */
    String getDetalleImpuestos(double precioBase, TipoVehiculo tipoVehiculo);
} 