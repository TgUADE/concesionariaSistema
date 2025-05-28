package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Assuming ICliente, IVehiculo, IFormaDePago, DatosFacturacion,
// ServicioDePago, ServicioDeNotificacion, Observador, InterfazEstadoPedido,
// and all concrete state classes (EstadoVentas, EstadoCobranzas, etc.)
// are in the 'controller' package or properly imported.

@ExtendWith(MockitoExtension.class)
public class PedidoStateTransitionTest {

    @Mock
    ICliente mockCliente;
    @Mock
    IVehiculo mockVehiculo;
    @Mock
    IFormaDePago mockFormaPago; // Can be null for initial states
    @Mock
    DatosFacturacion mockDatosFacturacion; // Can be null for initial states
    @Mock
    ServicioDePago mockServicioPago;
    @Mock
    ServicioDeNotificacion mockServicioNotificacion;
    @Mock
    Observador mockObservador; // For verifying notifications

    Pedido pedido;

    private Pedido createTestPedido() {
        // Using default values for non-essential fields in state transition tests
        return new Pedido(
            101,                // numeroPedido
            new Date(),         // fechaCreacion
            mockCliente,
            mockVehiculo,
            mockFormaPago,      // formaPago (can be null/set later)
            mockDatosFacturacion, // datosFacturacion (can be null/set later)
            "VendedorTest",     // vendedorNombre
            "vtest@example.com",// vendedorCorreo
            mockServicioPago,
            mockServicioNotificacion
        );
    }

    @BeforeEach
    void setUp() {
        pedido = createTestPedido();
        pedido.registrarObservador(mockObservador); // Register the mock observer
    }

    @Test
    void testEstadoInicialEsVentas() {
        assertTrue(pedido.getEstadoActual() instanceof EstadoVentas, "El estado inicial del pedido debe ser Ventas.");
        assertEquals(1, pedido.getHistorialEstados().size(), "El historial debe tener una entrada para el estado inicial.");
        RegistroHistorialEstado historialInicial = pedido.getHistorialEstados().get(0);
        assertEquals("EstadoVentas", historialInicial.getEstado(), "El estado en el historial inicial es incorrecto.");
        assertEquals("Ventas", historialInicial.getAreaResponsable(), "El área responsable en el historial inicial es incorrecta.");
        
        // Verify that the observer was notified of the initial state setting
        // This happens inside Pedido constructor via setEstadoActual -> agregarRegistroHistorial -> notificarObservadores
        // Note: The initial state notification check might be tricky if it happens *during* constructor.
        // The current Pedido constructor sets initial state and then initializes subject.
        // Let's adjust Pedido to initialize subject *before* setting initial state, or test notification *after* first transition.
        // For now, assuming notification for initial state is not explicitly tested here, but after first transition.
        // If Pedido.setEstadoActual is called by constructor *after* subject initialization, then verify(mockObservador, times(1)).actualizar(... for EstadoVentas);
    }

    @Test
    void testVentasAvanzarACobranzas() {
        assertTrue(pedido.getEstadoActual() instanceof EstadoVentas, "Estado inicial debe ser Ventas");
        int historialInicialSize = pedido.getHistorialEstados().size();

        pedido.avanzarPedido(null); // 'contexto' can be null if not used by this transition

        assertTrue(pedido.getEstadoActual() instanceof EstadoCobranzas, "Debería transicionar a Cobranzas");
        assertEquals(historialInicialSize + 1, pedido.getHistorialEstados().size(), "Debería agregarse una entrada al historial");
        RegistroHistorialEstado ultimoHistorial = pedido.getHistorialEstados().get(historialInicialSize);
        assertEquals("EstadoCobranzas", ultimoHistorial.getEstado(), "El estado en el historial es incorrecto");
        assertEquals("Cobranzas", ultimoHistorial.getAreaResponsable(), "El área en el historial es incorrecta");

        verify(mockObservador, times(1)).actualizar(eq(pedido), eq("EstadoCobranzas"), eq("Cobranzas"));
    }

    @Test
    void testVentasRetrocederLanzaExcepcion() {
        assertTrue(pedido.getEstadoActual() instanceof EstadoVentas, "Estado inicial debe ser Ventas");
        
        ExcepcionProceso exception = assertThrows(ExcepcionProceso.class, () -> {
            pedido.retrocederPedido(null);
        });
        assertEquals("No se puede retroceder desde el estado Ventas para el pedido " + pedido.getNumeroPedido() + ".", exception.getMessage());
        
        assertTrue(pedido.getEstadoActual() instanceof EstadoVentas, "El estado no debería cambiar después de la excepción.");
        // Verify observer was NOT called for a non-transition
        verify(mockObservador, never()).actualizar(any(), eq("EstadoVentas"), anyString()); // Or more specific if initial notification is verified
    }

    @Test
    void testCobranzasAvanzarAImpuestos() {
        pedido.setEstadoActual(new EstadoCobranzas()); // Setup state
        pedido.getHistorialEstados().clear(); // Clear history for this specific test flow if needed, or manage size
        pedido.registrarObservador(mockObservador); // Re-register if cleared, or ensure setup handles it.

        int historialInicialSize = pedido.getHistorialEstados().size(); // Should be 1 due to setEstadoActual
        
        pedido.avanzarPedido(null);

        assertTrue(pedido.getEstadoActual() instanceof EstadoImpuestos, "Debería transicionar a Impuestos");
        assertEquals(historialInicialSize + 1, pedido.getHistorialEstados().size());
        RegistroHistorialEstado ultimoHistorial = pedido.getHistorialEstados().get(historialInicialSize);
        assertEquals("EstadoImpuestos", ultimoHistorial.getEstado());
        assertEquals("Impuestos", ultimoHistorial.getAreaResponsable());

        verify(mockObservador, times(1)).actualizar(eq(pedido), eq("EstadoImpuestos"), eq("Impuestos"));
    }

    @Test
    void testCobranzasRetrocederAVentas() {
        pedido.setEstadoActual(new EstadoCobranzas());
        pedido.getHistorialEstados().clear();
        pedido.registrarObservador(mockObservador);
        int historialInicialSize = pedido.getHistorialEstados().size();

        pedido.retrocederPedido(null);

        assertTrue(pedido.getEstadoActual() instanceof EstadoVentas, "Debería transicionar a Ventas");
        assertEquals(historialInicialSize + 1, pedido.getHistorialEstados().size());
        RegistroHistorialEstado ultimoHistorial = pedido.getHistorialEstados().get(historialInicialSize);
        assertEquals("EstadoVentas", ultimoHistorial.getEstado());
        assertEquals("Ventas", ultimoHistorial.getAreaResponsable());
        
        verify(mockObservador, times(1)).actualizar(eq(pedido), eq("EstadoVentas"), eq("Ventas"));
    }

    // TODO: Add similar tests for:
    // - EstadoImpuestos.avanzar() -> EstadoEmbarque
    // - EstadoImpuestos.retroceder() -> EstadoCobranzas
    // - EstadoEmbarque.avanzar() -> EstadoLogistica
    // - EstadoEmbarque.retroceder() -> EstadoImpuestos
    // - EstadoLogistica.avanzar() -> EstadoEntrega
    // - EstadoLogistica.retroceder() -> EstadoEmbarque
    // - EstadoEntrega.avanzar() (should remain in EstadoEntrega or specific "Completed" state, no error)
    // - EstadoEntrega.retroceder() -> EstadoLogistica
}
