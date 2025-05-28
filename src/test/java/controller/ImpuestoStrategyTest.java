package controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
// Assuming TipoVehiculo is public enum within Vehiculo
import controller.Vehiculo.TipoVehiculo;


public class ImpuestoStrategyTest {

    // Test Data Provider Method
    static Stream<Arguments> vehiculoImpuestoProvider() {
        // Using ArrayList<>() for equipamientoOpcional as defined in Vehiculo constructor
        Vehiculo auto = new Vehiculo(1, "MarcaA", "ModeloA", TipoVehiculo.AUTO, "Rojo", 10000, "Auto", new ArrayList<>(), "chasisA", "motorA");
        Vehiculo camioneta = new Vehiculo(2, "MarcaB", "ModeloB", TipoVehiculo.CAMIONETA, "Azul", 20000, "Camioneta", new ArrayList<>(), "chasisB", "motorB");
        Vehiculo moto = new Vehiculo(3, "MarcaC", "ModeloC", TipoVehiculo.MOTO, "Negro", 5000, "Moto", new ArrayList<>(), "chasisC", "motorC");
        Vehiculo camion = new Vehiculo(4, "MarcaD", "ModeloD", TipoVehiculo.CAMION, "Blanco", 30000, "Camion", new ArrayList<>(), "chasisD", "motorD");
        
        // Edge case: Vehiculo with precioBase = 0
        Vehiculo autoPrecioCero = new Vehiculo(5, "MarcaE", "ModeloE", TipoVehiculo.AUTO, "Verde", 0, "Auto Precio Cero", new ArrayList<>(), "chasisE", "motorE");

        return Stream.of(
            // Auto: Nacional 21%, Provincial General 5%, Provincial Adicional 1%
            Arguments.of(auto, 10000 * 0.21, 10000 * 0.05, 10000 * 0.01, 10000 * (0.21 + 0.05 + 0.01)),
            // Camioneta: Nacional 10%, Provincial General 5%, Provincial Adicional 2%
            Arguments.of(camioneta, 20000 * 0.10, 20000 * 0.05, 20000 * 0.02, 20000 * (0.10 + 0.05 + 0.02)),
            // Moto: Nacional 0%, Provincial General 5%, Provincial Adicional 1%
            Arguments.of(moto, 0.0, 5000 * 0.05, 5000 * 0.01, 5000 * (0.05 + 0.01)),
            // Camion: Nacional 0%, Provincial General 5%, Provincial Adicional 2%
            Arguments.of(camion, 0.0, 30000 * 0.05, 30000 * 0.02, 30000 * (0.05 + 0.02)),
            // Auto con precio 0: Todos los impuestos deben ser 0
            Arguments.of(autoPrecioCero, 0.0, 0.0, 0.0, 0.0)
        );
    }

    // Parameterized Test Method
    @ParameterizedTest
    @MethodSource("vehiculoImpuestoProvider")
    void testCalculoImpuestosPorTipoVehiculo(Vehiculo vehiculo, double expectedNacional, double expectedProvGeneral, double expectedProvAdicional, double expectedTotalImpuesto) {
        InterfazImpuestoStrategy impuestoNacional = null;
        InterfazImpuestoStrategy impuestoProvAdicional = null;

        switch (vehiculo.getTipo()) {
            case AUTO:
                impuestoNacional = new ImpuestoNacionalAuto();
                impuestoProvAdicional = new ImpuestoProvincialAdicionalAutosMotos();
                break;
            case CAMIONETA:
                impuestoNacional = new ImpuestoNacionalCamioneta();
                impuestoProvAdicional = new ImpuestoProvincialAdicionalCamionesCamionetas();
                break;
            case MOTO:
                impuestoNacional = new ImpuestoNacionalNulo(); // 0%
                impuestoProvAdicional = new ImpuestoProvincialAdicionalAutosMotos();
                break;
            case CAMION:
                impuestoNacional = new ImpuestoNacionalNulo(); // 0%
                impuestoProvAdicional = new ImpuestoProvincialAdicionalCamionesCamionetas();
                break;
            default:
                // Should not happen if provider only gives these types
                throw new IllegalArgumentException("Tipo de vehículo no manejado en el test: " + vehiculo.getTipo());
        }

        InterfazImpuestoStrategy impuestoProvGeneral = new ImpuestoProvincialGeneral();

        double actualNacional = impuestoNacional.calcularImpuesto(vehiculo);
        double actualProvGeneral = impuestoProvGeneral.calcularImpuesto(vehiculo);
        double actualProvAdicional = impuestoProvAdicional.calcularImpuesto(vehiculo);
        double actualTotalImpuesto = actualNacional + actualProvGeneral + actualProvAdicional;

        assertEquals(expectedNacional, actualNacional, 0.001, "Impuesto Nacional incorrecto para " + vehiculo.getTipo());
        assertEquals(expectedProvGeneral, actualProvGeneral, 0.001, "Impuesto Provincial General incorrecto para " + vehiculo.getTipo());
        assertEquals(expectedProvAdicional, actualProvAdicional, 0.001, "Impuesto Provincial Adicional incorrecto para " + vehiculo.getTipo());
        assertEquals(expectedTotalImpuesto, actualTotalImpuesto, 0.001, "Suma total de impuestos incorrecta para " + vehiculo.getTipo());
    }

    // Test for null Vehiculo object
    @Test
    void testImpuestoConVehiculoNulo() {
        InterfazImpuestoStrategy impuestoNacionalAuto = new ImpuestoNacionalAuto();
        InterfazImpuestoStrategy impuestoNacionalCamioneta = new ImpuestoNacionalCamioneta();
        InterfazImpuestoStrategy impuestoNacionalNulo = new ImpuestoNacionalNulo();
        InterfazImpuestoStrategy impuestoProvGeneral = new ImpuestoProvincialGeneral();
        InterfazImpuestoStrategy impuestoProvAdicionalAM = new ImpuestoProvincialAdicionalAutosMotos();
        InterfazImpuestoStrategy impuestoProvAdicionalCC = new ImpuestoProvincialAdicionalCamionesCamionetas();

        assertEquals(0.0, impuestoNacionalAuto.calcularImpuesto(null), 0.001, "ImpuestoNacionalAuto con null vehiculo");
        assertEquals(0.0, impuestoNacionalCamioneta.calcularImpuesto(null), 0.001, "ImpuestoNacionalCamioneta con null vehiculo");
        assertEquals(0.0, impuestoNacionalNulo.calcularImpuesto(null), 0.001, "ImpuestoNacionalNulo con null vehiculo");
        assertEquals(0.0, impuestoProvGeneral.calcularImpuesto(null), 0.001, "ImpuestoProvincialGeneral con null vehiculo");
        assertEquals(0.0, impuestoProvAdicionalAM.calcularImpuesto(null), 0.001, "ImpuestoProvincialAdicionalAutosMotos con null vehiculo");
        assertEquals(0.0, impuestoProvAdicionalCC.calcularImpuesto(null), 0.001, "ImpuestoProvincialAdicionalCamionesCamionetas con null vehiculo");
    }
}
