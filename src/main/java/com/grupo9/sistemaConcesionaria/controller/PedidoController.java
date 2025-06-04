package com.grupo9.sistemaConcesionaria.controller;

import com.grupo9.sistemaConcesionaria.model.*;
import com.grupo9.sistemaConcesionaria.service.PedidoService;
import com.grupo9.sistemaConcesionaria.service.ClienteService;
import com.grupo9.sistemaConcesionaria.service.VehiculoService;
import com.grupo9.sistemaConcesionaria.exception.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * PedidoController - Controlador REST para gestión de pedidos
 * Demuestra el funcionamiento completo del sistema con todos los patrones implementados
 */
@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VehiculoService vehiculoService;

    /**
     * Crea un nuevo pedido y lo procesa a través de todo el flujo
     * Demuestra: Chain of Responsibility + Observer + Strategy + Factory Method
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearPedido(@RequestBody Map<String, Object> request) {
        try {
            Long clienteId = Long.valueOf(request.get("clienteId").toString());
            Long vehiculoId = Long.valueOf(request.get("vehiculoId").toString());
            String formaPagoStr = request.get("formaPago").toString();
            
            FormaDePago formaPago = FormaDePago.valueOf(formaPagoStr.toUpperCase());
            
            Pedido pedido = pedidoService.crearPedido(clienteId, vehiculoId, formaPago);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido creado y procesado exitosamente",
                "pedido", Map.of(
                    "id", pedido.getIdPedido(),
                    "numero", pedido.getNumeroDePedido(),
                    "estado", pedido.getEstadoActual().name(),
                    "area", pedido.getAreaResponsableActual(),
                    "costoTotal", pedido.getCostoTotal(),
                    "cliente", pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido(),
                    "vehiculo", pedido.getVehiculo().getMarca() + " " + pedido.getVehiculo().getModelo(),
                    "formaPago", pedido.getFormaDePago().name(),
                    "historialCount", pedido.getHistorialEstados().size()
                )
            ));
            
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cliente no encontrado: " + e.getMessage()));
        } catch (VehiculoNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vehículo no encontrado: " + e.getMessage()));
        } catch (PedidoNotFoundException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error en procesamiento: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error en datos: " + e.getMessage()));
        }
    }

    /**
     * Obtiene todos los pedidos
     */
    @GetMapping
    public ResponseEntity<List<Pedido>> getTodosPedidos() {
        return ResponseEntity.ok(pedidoService.getTodosPedidos());
    }

    /**
     * Obtiene un pedido por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.getPedido(id);
            return ResponseEntity.ok(pedido);
        } catch (PedidoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene pedidos por estado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> getPedidosPorEstado(@PathVariable String estado) {
        try {
            EstadoPedido estadoPedido = EstadoPedido.valueOf(estado.toUpperCase());
            return ResponseEntity.ok(pedidoService.getPedidosPorEstado(estadoPedido));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene estadísticas del sistema
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> getEstadisticas() {
        return ResponseEntity.ok(Map.of(
            "resumen", pedidoService.getEstadisticas(),
            "totalPedidos", pedidoService.getTodosPedidos().size(),
            "estadosPorTipo", Map.of(
                "ventas", pedidoService.getPedidosPorEstado(EstadoPedido.VENTAS).size(),
                "cobranzas", pedidoService.getPedidosPorEstado(EstadoPedido.COBRANZAS).size(),
                "impuestos", pedidoService.getPedidosPorEstado(EstadoPedido.IMPUESTOS).size()
            )
        ));
    }

    /**
     * Obtiene clientes disponibles para crear pedidos
     */
    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> getClientes() {
        return ResponseEntity.ok(clienteService.getTodosLosClientes());
    }

    /**
     * Obtiene vehículos disponibles para crear pedidos
     */
    @GetMapping("/vehiculos")
    public ResponseEntity<List<Vehiculo>> getVehiculos() {
        return ResponseEntity.ok(vehiculoService.getTodosLosVehiculos());
    }

    /**
     * Endpoint de prueba rápida - Crea un pedido con datos predeterminados
     */
    @PostMapping("/test")
    public ResponseEntity<?> crearPedidoPrueba() {
        try {
            // Usar datos de prueba: Cliente ID 1, Vehículo ID 1, Pago CONTADO
            Pedido pedido = pedidoService.crearPedido(1L, 1L, FormaDePago.CONTADO);
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "🎉 PEDIDO DE PRUEBA CREADO EXITOSAMENTE",
                "descripcion", "Se demostró el funcionamiento completo del sistema:",
                "patronesImplementados", List.of(
                    "✅ Observer Pattern (Notificaciones SMS + Email)",
                    "✅ Chain of Responsibility (Ventas → Cobranzas → Impuestos)",
                    "✅ Strategy Pattern (Cálculo de impuestos por tipo de vehículo)",
                    "✅ Singleton Pattern (Servicios Spring)",
                    "✅ Factory Method Pattern (Formas de pago)"
                ),
                "pedido", Map.of(
                    "numero", pedido.getNumeroDePedido(),
                    "estadoFinal", pedido.getEstadoActual().name(),
                    "costoTotal", String.format("$%.2f", pedido.getCostoTotal()),
                    "etapasProcesadas", pedido.getHistorialEstados().size()
                )
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error en prueba: " + e.getMessage()
            ));
        }
    }

    /**
     * Obtiene estadísticas financieras del sistema
     */
    @GetMapping("/estadisticas/financieras")
    public ResponseEntity<?> getEstadisticasFinancieras() {
        return ResponseEntity.ok(Map.of(
            "financiero", pedidoService.getEstadisticasFinancieras(),
            "totalPedidos", pedidoService.getCantidadTotal(),
            "totalClientes", clienteService.getCantidadTotal(),
            "totalVehiculos", vehiculoService.getCantidadTotal()
        ));
    }

    /**
     * Endpoint para demostrar persistencia JSON
     */
    @GetMapping("/persistencia/info")
    public ResponseEntity<?> getInfoPersistencia() {
        return ResponseEntity.ok(Map.of(
            "mensaje", "💾 SISTEMA DE PERSISTENCIA JSON ACTIVO",
            "descripcion", "Todos los datos se guardan automáticamente en archivos JSON",
            "archivos", List.of(
                "data/pedidos.json - Almacena todos los pedidos",
                "data/clientes.json - Almacena todos los clientes", 
                "data/vehiculos.json - Almacena todos los vehículos"
            ),
            "caracteristicas", List.of(
                "✅ Persistencia automática en cada operación",
                "✅ Formato JSON legible y editable",
                "✅ Carga automática al iniciar la aplicación",
                "✅ Búsquedas optimizadas en memoria",
                "✅ Backup automático de datos"
            ),
            "estadisticas", Map.of(
                "pedidosEnArchivo", pedidoService.getCantidadTotal(),
                "clientesEnArchivo", clienteService.getCantidadTotal(),
                "vehiculosEnArchivo", vehiculoService.getCantidadTotal(),
                "directorioData", "data/"
            )
        ));
    }

    /**
     * Actualiza un pedido existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPedido(@PathVariable Long id, @RequestBody Pedido pedido) {
        try {
            pedido.setIdPedido(id);
            Pedido pedidoActualizado = pedidoService.actualizarPedido(pedido);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido actualizado exitosamente",
                "pedido", pedidoActualizado
            ));
        } catch (PedidoNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Elimina un pedido permanentemente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPedido(@PathVariable Long id) {
        boolean eliminado = pedidoService.eliminarPedido(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Pedido eliminado exitosamente"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 