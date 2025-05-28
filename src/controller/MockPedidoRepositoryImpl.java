package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockPedidoRepositoryImpl implements InterfazPedidoRepository {

    private List<Pedido> mockPedidos = new ArrayList<>();

    public MockPedidoRepositoryImpl() {
        // Create some mock data
        // For simplicity, using null for complex objects or creating very basic mocks.
        
        // Mock Services (can be null if Pedido handles null services gracefully for reporting)
        ServicioDePago mockServicioPago = (formaDePago, monto) -> true; // Simple mock
        ServicioDeNotificacion mockServicioNotificacion = (mensaje, cliente) -> {}; // Simple mock

        // Mock Cliente
        ICliente mockCliente1 = new Cliente(1, "Cliente", "Mock", "12345678", "cliente@mock.com", "555-0101");
        
        // Mock Vehiculo
        IVehiculo mockVehiculo1 = new Vehiculo(1, "MarcaMock", "ModeloMock", 
                                            Vehiculo.TipoVehiculo.AUTO, "Rojo", 20000, 
                                            "Caracteristicas Mock", new ArrayList<>(), 
                                            "CHASIS001", "MOTOR001");

        // Mock FormaDePago
        IFormaDePago mockFormaPagoContado = new Contado("USD");
        ((FormaDePago)mockFormaPagoContado).setMonto(25000.0); // Assuming monto is set during processing
        ((FormaDePago)mockFormaPagoContado).setEstado("Pagado");


        // Mock Pedido 1
        Pedido pedido1 = new Pedido(
            101, new Date(System.currentTimeMillis() - 86400000 * 5), // 5 days ago
            mockCliente1, mockVehiculo1, mockFormaPagoContado,
            null, // DatosFacturacion
            "VendedorUno", "vendedor1@example.com",
            mockServicioPago, mockServicioNotificacion
        );
        pedido1.agregarConfiguracionAdicional(new ConfiguracionAdicional("LlantasDeportivas", 500));
        pedido1.agregarImpuestoAplicado(new ImpuestoAplicado("IVA", 0.21, 4200)); // Example tax
        pedido1.recalcularCostoTotal(); // Ensure costoTotal is set
        pedido1.setEstadoActual(new EstadoEntrega()); // Example final state
        mockPedidos.add(pedido1);

        // Mock Pedido 2 (more recent, different state)
         ICliente mockCliente2 = new Cliente(2, "OtroCliente", "Mock", "87654321", "otro@mock.com", "555-0202");
         IVehiculo mockVehiculo2 = new Vehiculo(2, "MarcaOtra", "ModeloOtro", 
                                             Vehiculo.TipoVehiculo.CAMIONETA, "Azul", 35000, 
                                             "Full equip", new ArrayList<>(), 
                                             "CHASIS002", "MOTOR002");
        IFormaDePago mockFormaPagoTarjeta = new TarjetaDeCredito(3, "BancoMock");
        ((FormaDePago)mockFormaPagoTarjeta).setMonto(38000.0);
        ((FormaDePago)mockFormaPagoTarjeta).setEstado("Pagado");

        Pedido pedido2 = new Pedido(
            102, new Date(System.currentTimeMillis() - 86400000 * 2), // 2 days ago
            mockCliente2, mockVehiculo2, mockFormaPagoTarjeta,
            new DatosFacturacion("OtroCliente SA", "Calle Falsa 123", "20-87654321-5"),
            "VendedorDos", "vendedor2@example.com",
            mockServicioPago, mockServicioNotificacion
        );
        pedido2.agregarImpuestoAplicado(new ImpuestoAplicado("IVA", 0.21, 7350));
        pedido2.recalcularCostoTotal();
        pedido2.setEstadoActual(new EstadoCobranzas()); // Example intermediate state
        mockPedidos.add(pedido2);
    }

    @Override
    public List<Pedido> findPedidosByFechaAndEstado(Date fechaDesde, Date fechaHasta, String estadoActualNombre) {
        List<Pedido> resultados = new ArrayList<>();
        for (Pedido p : mockPedidos) {
            boolean coincideFecha = true;
            if (fechaDesde != null && p.getFechaCreacion().before(fechaDesde)) {
                coincideFecha = false;
            }
            if (fechaHasta != null && p.getFechaCreacion().after(fechaHasta)) {
                coincideFecha = false;
            }

            boolean coincideEstado = true;
            if (estadoActualNombre != null && !estadoActualNombre.isEmpty()) {
                if (p.getEstadoActual() == null || !p.getEstadoActual().getClass().getSimpleName().equalsIgnoreCase(estadoActualNombre)) {
                    coincideEstado = false;
                }
            }

            if (coincideFecha && coincideEstado) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    @Override
    public List<Pedido> findAllPedidos() {
        return new ArrayList<>(mockPedidos); // Return a copy
    }

    @Override
    public List<Pedido> findPedidosByFechaAndEstadoAndCliente(Date fechaDesde, Date fechaHasta, String estadoActualNombre, int clienteId) {
        List<Pedido> resultados = new ArrayList<>();
        for (Pedido p : mockPedidos) {
            boolean coincideFecha = true;
            if (fechaDesde != null && p.getFechaCreacion().before(fechaDesde)) {
                coincideFecha = false;
            }
            if (fechaHasta != null && p.getFechaCreacion().after(fechaHasta)) {
                coincideFecha = false;
            }

            boolean coincideEstado = true;
            if (estadoActualNombre != null && !estadoActualNombre.isEmpty()) {
                if (p.getEstadoActual() == null || !p.getEstadoActual().getClass().getSimpleName().equalsIgnoreCase(estadoActualNombre)) {
                    coincideEstado = false;
                }
            }
            
            boolean coincideCliente = true;
            // Assuming Cliente implements ICliente and has getId() method.
            // And ICliente interface has getId() method.
            // For now, we rely on Pedido.getCliente() returning an object that we can cast
            // or that ICliente directly has getId().
            // Let's assume ICliente has getId() for this example.
            if (p.getCliente() != null && p.getCliente() instanceof Cliente && ((Cliente)p.getCliente()).getId() == clienteId) {
                // matches
            } else if (p.getCliente() != null && p.getCliente().getNombre().equals("ClienteMock") && clienteId == 1 && mockPedidos.indexOf(p) == 0 ) { 
                // Simplified check for mock data if getId() is not on ICliente directly.
                // This part needs to be robust based on actual ICliente/Cliente structure.
                // For this example, we assume Cliente has getId() and ICliente might not.
                // If ICliente has getId(), the instanceof check is not needed.
                // The task implies Cliente.java has getId()
                // matches
            }
             else if (p.getCliente() != null && p.getCliente().getNombre().equals("OtroCliente") && clienteId == 2 && mockPedidos.indexOf(p) == 1 ) {
                // matches
             }
            else {
                coincideCliente = false;
            }


            if (coincideFecha && coincideEstado && coincideCliente) {
                resultados.add(p);
            }
        }
        return resultados;
    }
}
