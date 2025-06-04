package com.grupo9.sistemaConcesionaria.service;

import com.grupo9.sistemaConcesionaria.model.*;
import com.grupo9.sistemaConcesionaria.exception.PedidoNotFoundException;
import com.grupo9.sistemaConcesionaria.exception.VehiculoNotFoundException;
import com.grupo9.sistemaConcesionaria.exception.ClienteNotFoundException;
import com.grupo9.sistemaConcesionaria.repository.PedidoJsonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * PedidoService - Servicio principal para gestión de pedidos con persistencia JSON
 * Coordina el flujo completo de pedidos utilizando Chain of Responsibility,
 * Observer Pattern, Strategy Pattern (impuestos) y Factory Method (formas de pago)
 * Ahora con persistencia en archivo JSON
 */
@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);
    
    @Autowired
    private PedidoJsonRepository pedidoRepository;
    
    // Inyección de dependencias
    @Autowired
    private VehiculoService vehiculoService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ImpuestoService impuestoService;
    
    @Autowired
    private NotificacionService notificacionService;
    
    // Handlers para Chain of Responsibility
    @Autowired
    private VentasHandler ventasHandler;
    
    @Autowired
    private CobranzasHandler cobranzasHandler;
    
    @Autowired
    private ImpuestosHandler impuestosHandler;

    /**
     * Configura la cadena de responsabilidad en PostConstruct
     */
    @jakarta.annotation.PostConstruct
    public void configurarCadenaDeResponsabilidad() {
        // Configurar el flujo: Ventas → Cobranzas → Impuestos → Embarque → Logística → Entrega
        ventasHandler
            .setSiguiente(cobranzasHandler)
            .setSiguiente(impuestosHandler);
        
        logger.info("Cadena de responsabilidad configurada: Ventas → Cobranzas → Impuestos");
        logger.info("Sistema de persistencia JSON inicializado - Pedidos existentes: {}", pedidoRepository.count());
    }

    /**
     * Crea un nuevo pedido y lo procesa a través de la cadena
     * @param clienteId ID del cliente
     * @param vehiculoId ID del vehículo
     * @param formaDePago Forma de pago seleccionada
     * @return El pedido creado y procesado
     */
    public Pedido crearPedido(Long clienteId, Long vehiculoId, FormaDePago formaDePago) 
            throws ClienteNotFoundException, VehiculoNotFoundException, PedidoNotFoundException {
        
        logger.info("Creando nuevo pedido - Cliente: {}, Vehículo: {}, Pago: {}", 
                   clienteId, vehiculoId, formaDePago);

        // Obtener cliente y vehículo
        Cliente cliente = clienteService.getCliente(clienteId);
        if (cliente == null) {
            throw new ClienteNotFoundException("Cliente no encontrado: " + clienteId);
        }

        Vehiculo vehiculo = vehiculoService.getVehiculoPorId(vehiculoId);
        if (vehiculo == null) {
            throw new VehiculoNotFoundException("Vehículo no encontrado: " + vehiculoId);
        }

        // Crear el pedido
        Pedido pedido = new Pedido(cliente, vehiculo, formaDePago);
        
        // Calcular impuestos iniciales
        double impuestos = impuestoService.calcularImpuesto(vehiculo);
        pedido.setImpuestosAplicados(impuestos);
        pedido.calcularCostoTotal();

        // Guardar en JSON
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        logger.info("Pedido creado: {} - Costo total: ${:.2f}", 
                   pedidoGuardado.getNumeroDePedido(), pedidoGuardado.getCostoTotal());

        // Procesar a través de la cadena de responsabilidad
        procesarPedidoCompleto(pedidoGuardado);

        // Guardar el pedido procesado
        return pedidoRepository.save(pedidoGuardado);
    }

    /**
     * Procesa un pedido a través de toda la cadena de responsabilidad
     * @param pedido El pedido a procesar
     */
    public void procesarPedidoCompleto(Pedido pedido) throws PedidoNotFoundException {
        logger.info("Iniciando procesamiento completo del pedido: {}", pedido.getNumeroDePedido());
        
        try {
            // Iniciar la cadena desde el primer handler (Ventas)
            ventasHandler.procesar(pedido);
            
            logger.info("Procesamiento completo finalizado para pedido: {}", pedido.getNumeroDePedido());
            
        } catch (Exception e) {
            logger.error("Error en procesamiento de pedido {}: {}", 
                        pedido.getNumeroDePedido(), e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene un pedido por ID
     * @param id ID del pedido
     * @return El pedido encontrado
     */
    public Pedido getPedido(Long id) throws PedidoNotFoundException {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado: " + id));
    }

    /**
     * Obtiene un pedido por número
     * @param numeroPedido Número del pedido
     * @return El pedido encontrado
     */
    public Pedido getPedidoPorNumero(String numeroPedido) throws PedidoNotFoundException {
        return pedidoRepository.findByNumeroDePedido(numeroPedido)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido no encontrado: " + numeroPedido));
    }

    /**
     * Obtiene todos los pedidos
     * @return Lista de todos los pedidos
     */
    public List<Pedido> getTodosPedidos() {
        return pedidoRepository.findAll();
    }

    /**
     * Obtiene pedidos por estado
     * @param estado Estado del pedido
     * @return Lista de pedidos en el estado especificado
     */
    public List<Pedido> getPedidosPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstadoActual(estado);
    }

    /**
     * Obtiene pedidos por cliente
     * @param clienteId ID del cliente
     * @return Lista de pedidos del cliente
     */
    public List<Pedido> getPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    /**
     * Obtiene pedidos por vehículo
     * @param vehiculoChasis Número de chasis del vehículo
     * @return Lista de pedidos para el vehículo
     */
    public List<Pedido> getPedidosPorVehiculo(String vehiculoChasis) {
        return pedidoRepository.findByVehiculoChasis(vehiculoChasis);
    }

    /**
     * Obtiene pedidos por forma de pago
     * @param formaPago Forma de pago
     * @return Lista de pedidos con la forma de pago especificada
     */
    public List<Pedido> getPedidosPorFormaPago(FormaDePago formaPago) {
        return pedidoRepository.findByFormaDePago(formaPago);
    }

    /**
     * Actualiza un pedido existente
     * @param pedido Pedido a actualizar
     * @return Pedido actualizado
     */
    public Pedido actualizarPedido(Pedido pedido) throws PedidoNotFoundException {
        if (pedido.getIdPedido() == null || !pedidoRepository.existsById(pedido.getIdPedido())) {
            throw new PedidoNotFoundException("Pedido no existe para actualizar: " + pedido.getIdPedido());
        }
        
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        logger.info("Pedido actualizado: {}", pedidoActualizado.getNumeroDePedido());
        return pedidoActualizado;
    }

    /**
     * Cancela un pedido
     * @param id ID del pedido a cancelar
     */
    public void cancelarPedido(Long id) throws PedidoNotFoundException {
        Pedido pedido = getPedido(id);
        pedido.cambiarEstado(EstadoPedido.CANCELADO, "Sistema");
        
        pedidoRepository.save(pedido);
        
        // Notificar cancelación
        notificacionService.notificarCambioPedido(
            pedido.getIdPedido(), 
            "CANCELADO", 
            "Sistema"
        );
        
        logger.info("Pedido cancelado: {}", pedido.getNumeroDePedido());
    }

    /**
     * Elimina un pedido permanentemente
     * @param id ID del pedido a eliminar
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarPedido(Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            logger.info("Pedido eliminado permanentemente: {}", id);
            return true;
        }
        return false;
    }

    /**
     * Obtiene estadísticas de pedidos
     * @return Información de estadísticas
     */
    public String getEstadisticas() {
        long totalPedidos = pedidoRepository.count();
        long pedidosVentas = pedidoRepository.countByEstadoActual(EstadoPedido.VENTAS);
        long pedidosCobranzas = pedidoRepository.countByEstadoActual(EstadoPedido.COBRANZAS);
        long pedidosImpuestos = pedidoRepository.countByEstadoActual(EstadoPedido.IMPUESTOS);
        
        return String.format(
            "Estadísticas Pedidos - Total: %d | Ventas: %d | Cobranzas: %d | Impuestos: %d",
            totalPedidos, pedidosVentas, pedidosCobranzas, pedidosImpuestos
        );
    }

    /**
     * Obtiene estadísticas financieras
     * @return Información financiera
     */
    public String getEstadisticasFinancieras() {
        double totalVentas = pedidoRepository.sumCostoTotal();
        double ventasImpuestos = pedidoRepository.sumCostoTotalByEstado(EstadoPedido.IMPUESTOS);
        
        return String.format(
            "Estadísticas Financieras - Total Ventas: $%.2f | Completadas: $%.2f | Promedio: $%.2f",
            totalVentas, 
            ventasImpuestos, 
            pedidoRepository.count() > 0 ? totalVentas / pedidoRepository.count() : 0.0
        );
    }

    /**
     * Obtiene la cantidad total de pedidos
     * @return Número total de pedidos
     */
    public long getCantidadTotal() {
        return pedidoRepository.count();
    }
} 