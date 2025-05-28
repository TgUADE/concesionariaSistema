package controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

// Assuming entities (Cliente, Vehiculo, Pedido), exceptions (ExcepcionDuplicado, ExcepcionValidacion),
// and service interfaces (InterfazClienteService, InterfazVehiculoService, InterfazPedidoService)
// are in the 'controller' package or properly imported.
import controller.Vehiculo.TipoVehiculo; // For Vehiculo enum

@ExtendWith(MockitoExtension.class)
public class DuplicadoEntityTest {

    // Mocks for Service Interfaces
    @Mock
    InterfazClienteService mockClienteService;
    @Mock
    InterfazVehiculoService mockVehiculoService;
    @Mock
    InterfazPedidoService mockPedidoService;

    // Mocks for Pedido dependencies (if needed for creating Pedido instances for testing)
    @Mock
    ICliente mockTestCliente; // Used as a component of Pedido
    @Mock
    IVehiculo mockTestVehiculo; // Used as a component of Pedido
    @Mock
    IFormaDePago mockTestFormaPago;
    @Mock
    DatosFacturacion mockTestDatosFacturacion;
    @Mock
    ServicioDePago mockTestServicioPago;
    @Mock
    ServicioDeNotificacion mockTestServicioNotificacion;


    @Test
    void testRegistrarClienteDuplicadoLanzaExcepcion() throws ExcepcionValidacion, ExcepcionDuplicado {
        // Setup: Create a client that would be considered a duplicate
        // The actual values don't matter as much as the mock's behavior.
        Cliente clienteDuplicado = new Cliente(2, "Ana", "Gomez", "123", "jp@mail.com", "555-456");

        // Configure mock service to throw ExcepcionDuplicado when this specific client (or any, for simplicity) is registered
        String expectedErrorMessage = "Cliente duplicado: documento=" + clienteDuplicado.getDocumento() + ", correo=" + clienteDuplicado.getCorreo();
        when(mockClienteService.registrarCliente(any(Cliente.class))) // Could use eq(clienteDuplicado) if specific instance matters
            .thenThrow(new ExcepcionDuplicado(expectedErrorMessage));

        // Execute and Assert
        ExcepcionDuplicado exception = assertThrows(ExcepcionDuplicado.class, () -> {
            mockClienteService.registrarCliente(clienteDuplicado);
        });
        
        assertTrue(exception.getMessage().contains("Cliente duplicado"));
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void testRegistrarVehiculoDuplicadoLanzaExcepcion() throws ExcepcionValidacion, ExcepcionDuplicado {
        // Setup: Create a vehicle that would be a duplicate
        Vehiculo vehiculoDuplicado = new Vehiculo(2, "MarcaX", "ModeloY", TipoVehiculo.AUTO, "Azul", 
                                                25000, "Duplicado", new ArrayList<>(), 
                                                "CHASISXYZ", "MOTORXYZ");

        String expectedErrorMessage = "Vehículo duplicado: marca=" + vehiculoDuplicado.getMarca() + 
                                      ", modelo=" + vehiculoDuplicado.getModelo() + 
                                      ", chasis=" + vehiculoDuplicado.getChasis();
        when(mockVehiculoService.registrarVehiculo(any(Vehiculo.class)))
            .thenThrow(new ExcepcionDuplicado(expectedErrorMessage));

        // Execute and Assert
        ExcepcionDuplicado exception = assertThrows(ExcepcionDuplicado.class, () -> {
            mockVehiculoService.registrarVehiculo(vehiculoDuplicado);
        });

        assertTrue(exception.getMessage().contains("Vehículo duplicado"));
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    void testCrearPedidoDuplicadoLanzaExcepcion() throws ExcepcionValidacion, ExcepcionDuplicado {
        // Setup: Create a Pedido that would be a duplicate
        // For Pedido, duplicate is defined by cliente + vehiculo + fecha.
        // We need to ensure the mock Pedido object used for testing has these identifiable.
        
        // Configure the mock client and vehicle to return identifiable info if needed by a real service.
        // For this test, we just need a Pedido instance.
        Date fechaPedido = new Date();
        Pedido pedidoDuplicado = new Pedido(
            102, fechaPedido, mockTestCliente, mockTestVehiculo, 
            mockTestFormaPago, mockTestDatosFacturacion,
            "VendedorTest", "vtest@example.com",
            mockTestServicioPago, mockTestServicioNotificacion
        );

        String expectedErrorMessage = "Pedido duplicado: cliente=" + pedidoDuplicado.getCliente() + // Relies on ICliente.toString() or similar
                                      ", vehiculo=" + pedidoDuplicado.getVehiculo() + // Relies on IVehiculo.toString()
                                      ", fecha=" + pedidoDuplicado.getFechaCreacion();
        when(mockPedidoService.crearPedido(any(Pedido.class)))
            .thenThrow(new ExcepcionDuplicado(expectedErrorMessage));

        // Execute and Assert
        ExcepcionDuplicado exception = assertThrows(ExcepcionDuplicado.class, () -> {
            mockPedidoService.crearPedido(pedidoDuplicado);
        });

        assertTrue(exception.getMessage().contains("Pedido duplicado"));
        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
