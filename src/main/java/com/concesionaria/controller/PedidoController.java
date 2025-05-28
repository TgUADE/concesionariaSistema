package com.concesionaria.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import controller.*;
import java.util.*;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPedidos() {
        try {
            List<Map<String, Object>> pedidos = new ArrayList<>();
            
            // Crear algunos pedidos de ejemplo
            Map<String, Object> pedido1 = new HashMap<>();
            pedido1.put("id", 1);
            pedido1.put("cliente", Map.of("nombre", "Juan Pérez", "email", "juan@email.com"));
            pedido1.put("vehiculo", Map.of("marca", "Toyota", "modelo", "Corolla", "precio", 25000.0));
            pedido1.put("estado", "ACTIVO");
            pedido1.put("total", 25000.0);
            pedidos.add(pedido1);
            
            Map<String, Object> response = new HashMap<>();
            response.put("pedidos", pedidos);
            response.put("total", pedidos.size());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPedido(@RequestBody Map<String, Object> pedidoData) {
        try {
            // Crear pedido básico
            String nombreCliente = (String) pedidoData.get("nombreCliente");
            String emailCliente = (String) pedidoData.get("emailCliente");
            String marca = (String) pedidoData.get("marca");
            String modelo = (String) pedidoData.get("modelo");
            Double precio = null;
            Object precioObj = pedidoData.get("precio");
            if (precioObj instanceof Number) {
                precio = ((Number) precioObj).doubleValue();
            }

            Map<String, Object> pedido = new HashMap<>();
            pedido.put("id", System.currentTimeMillis()); // ID único
            pedido.put("cliente", Map.of(
                "nombre", nombreCliente != null ? nombreCliente : "Cliente",
                "email", emailCliente != null ? emailCliente : "cliente@email.com"
            ));
            pedido.put("vehiculo", Map.of(
                "marca", marca != null ? marca : "Toyota",
                "modelo", modelo != null ? modelo : "Corolla",
                "precio", precio != null ? precio : 25000.0
            ));
            pedido.put("estado", "CREADO");
            pedido.put("fechaCreacion", new Date());
            pedido.put("total", precio != null ? precio : 25000.0);

            Map<String, Object> response = new HashMap<>();
            response.put("pedido", pedido);
            response.put("message", "Pedido creado exitosamente");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPedidoById(@PathVariable Integer id) {
        try {
            Map<String, Object> pedido = new HashMap<>();
            pedido.put("id", id);
            pedido.put("cliente", Map.of("nombre", "Cliente " + id, "email", "cliente" + id + "@email.com"));
            pedido.put("vehiculo", Map.of("marca", "Toyota", "modelo", "Corolla", "precio", 25000.0));
            pedido.put("estado", "ACTIVO");
            pedido.put("total", 25000.0);
            
            Map<String, Object> response = new HashMap<>();
            response.put("pedido", pedido);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
