package controller.repository;

import controller.Pedido;
import controller.ExcepcionDuplicado;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface InterfazPedidoRepository {
    Pedido save(Pedido pedido) throws ExcepcionDuplicado; // Add or update
    Optional<Pedido> findByNumeroPedido(int numeroPedido);
    // For duplicate check: cliente + vehiculo + fecha. This is tricky with object refs and Date precision.
    // May require a specific method or logic in the save method.
    List<Pedido> findPedidosByFechaBetween(Date fechaDesde, Date fechaHasta);
    List<Pedido> findPedidosByEstado(String estadoClassName); // estadoClassName is e.g., "EstadoVentas"
    List<Pedido> findPedidosByClienteId(int clienteId);
    List<Pedido> findAll();
    void deleteByNumeroPedido(int numeroPedido);
    
    // Métodos adicionales para compatibilidad con MockPedidoRepositoryImpl
    List<Pedido> findAllPedidos();
    List<Pedido> findPedidosByFechaAndEstado(Date fechaDesde, Date fechaHasta, String estado);
    List<Pedido> findPedidosByFechaAndEstadoAndCliente(Date fechaDesde, Date fechaHasta, String estado, int clienteId);
}
