package com.concesionaria;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// Import controller classes
import controller.*;

public class MainApp {
    private static GestorDePedidosSingleton gestorPedidos;
    private static SistemaFacade sistemaFacade;
    private static List<VehiculoData> vehiculosCreados = new ArrayList<>();
    private static List<PedidoData> pedidosCreados = new ArrayList<>();
    private static List<Integer> vehiculosEliminados = new ArrayList<>(); // Track deleted default vehicles
    private static List<Integer> pedidosEliminados = new ArrayList<>(); // Track deleted default orders
    private static java.util.Map<Integer, String> estadosPedidosDefecto = new java.util.HashMap<>(); // Track states of default orders
    private static java.util.Map<Integer, VehiculoData> vehiculosDefectoModificados = new java.util.HashMap<>(); // Track modified default vehicles
    private static AtomicInteger vehiculoIdCounter = new AtomicInteger(1); // Start from 1
    private static AtomicInteger pedidoIdCounter = new AtomicInteger(1); // Start from 1
    
    // Simple data class for vehicles
    static class VehiculoData {
        public int id;
        public String tipo;
        public String marca;
        public String modelo;
        public int año;
        public double precio;
        public boolean disponible;
        public String color;
        
        // Atributos específicos por tipo de vehículo
        public Integer puertas; // Para Auto
        public String tipoMoto; // Para Moto
        public Integer cilindrada; // Para Moto
        public Double cargaMaximaCamioneta; // Para Camioneta
        public Boolean traccion4x4; // Para Camioneta
        public Integer capacidadPasajeros; // Para Camioneta
        public Double cargaMaximaCamion; // Para Camión
        public Integer numeroEjes; // Para Camión
        
        public VehiculoData(int id, String tipo, String marca, String modelo, int año, double precio, boolean disponible, String color) {
            this.id = id;
            this.tipo = tipo;
            this.marca = marca;
            this.modelo = modelo;
            this.año = año;
            this.precio = precio;
            this.disponible = disponible;
            this.color = color;
        }
    }
    
    // Simple data class for orders
    static class PedidoData {
        public int id;
        public ClienteData cliente;
        public VehiculoData vehiculo;
        public String estado;
        public double total;
        public String fecha;
        
        public PedidoData(int id, ClienteData cliente, VehiculoData vehiculo, String estado, double total, String fecha) {
            this.id = id;
            this.cliente = cliente;
            this.vehiculo = vehiculo;
            this.estado = estado;
            this.total = total;
            this.fecha = fecha;
        }
    }
    
    // Simple data class for client
    static class ClienteData {
        public String nombre;
        public String email;
        
        public ClienteData(String nombre, String email) {
            this.nombre = nombre;
            this.email = email;
        }
    }
    
    public static void main(String[] args) throws IOException {
        int port = 8081;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Sistema de Concesionaria iniciado en el puerto " + port + "...");
        
        // Initialize system components
        initializeSystem();
        
        // Basic test endpoint
        server.createContext("/api/test", exchange -> {
            String response = "{\"message\":\"Sistema de Concesionaria funcionando correctamente!\",\"status\":\"OK\"}";
            sendJsonResponse(exchange, response);
        });
         // Vehiculos endpoint
        server.createContext("/api/vehiculos", exchange -> {
            try {
                String method = exchange.getRequestMethod();
                
                if ("GET".equals(method)) {
                    String response = getVehiculosJson();
                    sendJsonResponse(exchange, response);
                } else if ("POST".equals(method)) {
                    // Handle POST request to create new vehicle
                    String response = handleCreateVehiculo(exchange);
                    sendJsonResponse(exchange, response);
                } else if ("DELETE".equals(method)) {
                    // Handle DELETE request to remove vehicle
                    String response = handleDeleteVehiculo(exchange);
                    sendJsonResponse(exchange, response);
                } else if ("PUT".equals(method)) {
                    // Handle PUT request to update vehicle
                    String response = handleUpdateVehiculo(exchange);
                    sendJsonResponse(exchange, response);
                } else {
                    // Method not allowed
                    exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                String errorResponse = "{\"error\":\"" + e.getMessage() + "\"}";
                sendJsonResponse(exchange, errorResponse);
            }
        });

        // Pedidos endpoint
        server.createContext("/api/pedidos", exchange -> {
            try {
                String method = exchange.getRequestMethod();
                
                if ("GET".equals(method)) {
                    String response = getPedidosJson();
                    sendJsonResponse(exchange, response);
                } else if ("POST".equals(method)) {
                    String response = handleCreatePedido(exchange);
                    sendJsonResponse(exchange, response);
                } else if ("DELETE".equals(method)) {
                    String response = handleDeletePedido(exchange);
                    sendJsonResponse(exchange, response);
                } else if ("PUT".equals(method)) {
                    String response = handleUpdatePedido(exchange);
                    sendJsonResponse(exchange, response);
                } else {
                    // Method not allowed
                    exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                String errorResponse = "{\"error\":\"" + e.getMessage() + "\"}";
                sendJsonResponse(exchange, errorResponse);
            }
        });

        // Status endpoint
        server.createContext("/api/status", exchange -> {
            String response = getSystemStatusJson();
            sendJsonResponse(exchange, response);
        });

        // Static files endpoint
        server.createContext("/", exchange -> {
            try {
                String path = exchange.getRequestURI().getPath();
                if (path.equals("/") || path.equals("/index.html")) {
                    path = "/index.html";
                }
                
                InputStream resourceStream = MainApp.class.getResourceAsStream("/static" + path);
                if (resourceStream != null) {
                    byte[] content = resourceStream.readAllBytes();
                    
                    // Set content type based on file extension
                    String contentType = "text/html";
                    if (path.endsWith(".css")) contentType = "text/css";
                    else if (path.endsWith(".js")) contentType = "application/javascript";
                    else if (path.endsWith(".png")) contentType = "image/png";
                    else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
                    
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    exchange.sendResponseHeaders(200, content.length);
                    
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(content);
                    }
                } else {
                    // File not found
                    exchange.sendResponseHeaders(404, -1);
                }
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
            }
        });

        server.setExecutor(null); // Use default executor
        server.start();
        
        System.out.println("Endpoints disponibles:");
        System.out.println("- http://localhost:" + port + "/api/test");
        System.out.println("- http://localhost:" + port + "/api/vehiculos");
        System.out.println("- http://localhost:" + port + "/api/status");
        System.out.println("- http://localhost:" + port + "/api/pedidos");
        System.out.println("\nSistema listo para recibir peticiones...");
    }
    
    private static void initializeSystem() {
        try {
            System.out.println("Inicializando componentes del sistema...");
            
            // Initialize Singleton
            gestorPedidos = new GestorDePedidosSingleton();
            
            // Initialize repositories (commented out to avoid warnings)
            // InterfazPedidoRepository pedidoRepository = new MockPedidoRepositoryImpl();
            
            // Initialize other components (commented out to avoid warnings)
            // GestorDeStock gestorDeStock = new GestorDeStock();
            
            System.out.println("✓ Componentes inicializados correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al inicializar sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String handleCreateVehiculo(HttpExchange exchange) throws IOException {
        // Read request body
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        
        try {
            // Parse JSON manually (simple approach)
            String tipo = extractJsonValue(body, "tipo");
            String marca = extractJsonValue(body, "marca");
            String modelo = extractJsonValue(body, "modelo");
            int año = Integer.parseInt(extractJsonValue(body, "año"));
            double precio = Double.parseDouble(extractJsonValue(body, "precio"));
            String color = extractJsonValue(body, "color");
            
            // Create new vehicle
            int newId = vehiculoIdCounter.getAndIncrement();
            VehiculoData nuevoVehiculo = new VehiculoData(newId, tipo, marca, modelo, año, precio, true, color);
            
            // Extraer atributos específicos según el tipo de vehículo
            switch (tipo.toUpperCase()) {
                case "AUTO":
                    nuevoVehiculo.puertas = extractJsonValueAsInteger(body, "puertas");
                    break;
                case "MOTO":
                    nuevoVehiculo.tipoMoto = extractJsonValue(body, "tipoMoto");
                    nuevoVehiculo.cilindrada = extractJsonValueAsInteger(body, "cilindrada");
                    break;
                case "CAMIONETA":
                    nuevoVehiculo.cargaMaximaCamioneta = extractJsonValueAsDouble(body, "cargaMaximaCamioneta");
                    nuevoVehiculo.traccion4x4 = extractJsonValueAsBoolean(body, "traccion4x4");
                    nuevoVehiculo.capacidadPasajeros = extractJsonValueAsInteger(body, "capacidadPasajeros");
                    break;
                case "CAMION":
                    nuevoVehiculo.cargaMaximaCamion = extractJsonValueAsDouble(body, "cargaMaximaCamion");
                    nuevoVehiculo.numeroEjes = extractJsonValueAsInteger(body, "numeroEjes");
                    break;
            }
            
            vehiculosCreados.add(nuevoVehiculo);
            
            System.out.println("Nuevo vehículo creado: " + marca + " " + modelo + " (" + tipo + ") - Color: " + color);
            
            return "{\"success\":true,\"message\":\"Vehículo creado exitosamente\",\"id\":" + newId + "}";
            
        } catch (Exception e) {
            System.err.println("Error al crear vehículo: " + e.getMessage());
            return "{\"success\":false,\"error\":\"Error al procesar los datos del vehículo: " + e.getMessage() + "\"}";
        }
    }
    
    private static String handleDeleteVehiculo(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");
        
        if (segments.length >= 4) {
            try {
                int vehiculoId = Integer.parseInt(segments[3]);
                
                // Try to remove from dynamically created vehicles first
                boolean removed = vehiculosCreados.removeIf(v -> v.id == vehiculoId);
                
                // If not found in dynamic vehicles, check if it's a default vehicle (ID 1-4)
                if (!removed && vehiculoId >= 1 && vehiculoId <= 4) {
                    // Add to deleted list to hide it from display
                    if (!vehiculosEliminados.contains(vehiculoId)) {
                        vehiculosEliminados.add(vehiculoId);
                        removed = true;
                    }
                }
                
                if (removed) {
                    System.out.println("Vehículo eliminado: ID " + vehiculoId);
                    return "{\"success\":true,\"message\":\"Vehículo eliminado exitosamente\"}";
                } else {
                    return "{\"success\":false,\"error\":\"Vehículo no encontrado o no se puede eliminar\"}";
                }
            } catch (NumberFormatException e) {
                return "{\"success\":false,\"error\":\"ID de vehículo inválido\"}";
            }
        } else {
            return "{\"success\":false,\"error\":\"Falta ID del vehículo\"}";
        }
    }
    
    private static String handleUpdateVehiculo(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");
        
        if (segments.length >= 4) {
            try {
                int vehiculoId = Integer.parseInt(segments[3]);
                
                // Read request body
                InputStream requestBody = exchange.getRequestBody();
                String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                
                // Parse JSON data
                String tipo = extractJsonValue(body, "tipo");
                String marca = extractJsonValue(body, "marca");
                String modelo = extractJsonValue(body, "modelo");
                String color = extractJsonValue(body, "color");
                int año = Integer.parseInt(extractJsonValue(body, "año"));
                double precio = Double.parseDouble(extractJsonValue(body, "precio"));
                boolean disponible = Boolean.parseBoolean(extractJsonValue(body, "disponible"));
                
                // Try to update in dynamically created vehicles
                for (VehiculoData vehiculo : vehiculosCreados) {
                    if (vehiculo.id == vehiculoId) {
                        vehiculo.tipo = tipo;
                        vehiculo.marca = marca;
                        vehiculo.modelo = modelo;
                        vehiculo.color = color;
                        vehiculo.año = año;
                        vehiculo.precio = precio;
                        vehiculo.disponible = disponible;
                        
                        // Actualizar atributos específicos según el tipo de vehículo
                        // Primero limpiar todos los atributos específicos
                        vehiculo.puertas = null;
                        vehiculo.tipoMoto = null;
                        vehiculo.cilindrada = null;
                        vehiculo.cargaMaximaCamioneta = null;
                        vehiculo.traccion4x4 = null;
                        vehiculo.capacidadPasajeros = null;
                        vehiculo.cargaMaximaCamion = null;
                        vehiculo.numeroEjes = null;
                        
                        // Luego asignar los específicos del nuevo tipo
                        switch (tipo.toUpperCase()) {
                            case "AUTO":
                                vehiculo.puertas = extractJsonValueAsInteger(body, "puertas");
                                break;
                            case "MOTO":
                                vehiculo.tipoMoto = extractJsonValue(body, "tipoMoto");
                                vehiculo.cilindrada = extractJsonValueAsInteger(body, "cilindrada");
                                break;
                            case "CAMIONETA":
                                vehiculo.cargaMaximaCamioneta = extractJsonValueAsDouble(body, "cargaMaximaCamioneta");
                                vehiculo.traccion4x4 = extractJsonValueAsBoolean(body, "traccion4x4");
                                vehiculo.capacidadPasajeros = extractJsonValueAsInteger(body, "capacidadPasajeros");
                                break;
                            case "CAMION":
                                vehiculo.cargaMaximaCamion = extractJsonValueAsDouble(body, "cargaMaximaCamion");
                                vehiculo.numeroEjes = extractJsonValueAsInteger(body, "numeroEjes");
                                break;
                        }
                        
                        System.out.println("Vehículo actualizado: ID " + vehiculoId + " - " + marca + " " + modelo);
                        return "{\"success\":true,\"message\":\"Vehículo actualizado exitosamente\"}";
                    }
                }
                
                // Check if it's a default vehicle (ID 1-4) that can be updated
                if (vehiculoId >= 1 && vehiculoId <= 4) {
                    // Create updated vehicle data
                    VehiculoData vehiculoActualizado = new VehiculoData(vehiculoId, tipo, marca, modelo, año, precio, disponible, color);
                    
                    // Store the modified default vehicle
                    vehiculosDefectoModificados.put(vehiculoId, vehiculoActualizado);
                    
                    System.out.println("Vehículo por defecto actualizado: ID " + vehiculoId + " - " + marca + " " + modelo);
                    return "{\"success\":true,\"message\":\"Vehículo actualizado exitosamente\"}";
                }
                
                // If vehicle ID not found
                return "{\"success\":false,\"error\":\"Vehículo no encontrado\"}";
                
            } catch (NumberFormatException e) {
                return "{\"success\":false,\"error\":\"ID de vehículo inválido\"}";
            } catch (Exception e) {
                return "{\"success\":false,\"error\":\"Error al procesar los datos: " + e.getMessage() + "\"}";
            }
        } else {
            return "{\"success\":false,\"error\":\"Falta ID del vehículo\"}";
        }
    }
    
    private static String extractJsonValue(String json, String key) {
        // Simple JSON value extraction (not a robust parser)
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return "";
        
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return "";
        
        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && (json.charAt(valueStart) == ' ' || json.charAt(valueStart) == '\t')) {
            valueStart++;
        }
        
        int valueEnd;
        if (json.charAt(valueStart) == '"') {
            // String value
            valueStart++; // Skip opening quote
            valueEnd = json.indexOf('"', valueStart);
        } else {
            // Number value
            valueEnd = json.indexOf(',', valueStart);
            if (valueEnd == -1) valueEnd = json.indexOf('}', valueStart);
        }
        
        if (valueEnd == -1) valueEnd = json.length();
        
        return json.substring(valueStart, valueEnd).trim();
    }
    
    private static Integer extractJsonValueAsInteger(String json, String key) {
        String value = extractJsonValue(json, key);
        if (value.isEmpty()) return null;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private static Double extractJsonValueAsDouble(String json, String key) {
        String value = extractJsonValue(json, key);
        if (value.isEmpty()) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private static Boolean extractJsonValueAsBoolean(String json, String key) {
        String value = extractJsonValue(json, key);
        if (value.isEmpty()) return null;
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return null;
        }
    }
    
    private static String getVehiculosJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\"vehiculos\":[");
        
        boolean first = true;
        
        // Only add dynamically created vehicles (no default vehicles)
        for (VehiculoData vehiculo : vehiculosCreados) {
            if (!vehiculosEliminados.contains(vehiculo.id)) { // Check if not eliminated
                if (!first) json.append(",");
                json.append("{\"id\":").append(vehiculo.id)
                    .append(",\"tipo\":\"").append(vehiculo.tipo)
                    .append("\",\"marca\":\"").append(vehiculo.marca)
                    .append("\",\"modelo\":\"").append(vehiculo.modelo)
                    .append("\",\"año\":").append(vehiculo.año)
                    .append(",\"precio\":").append(vehiculo.precio)
                    .append(",\"disponible\":").append(vehiculo.disponible)
                    .append(",\"color\":\"").append(vehiculo.color)
                    .append("\"");
                
                // Agregar atributos específicos según el tipo de vehículo
                switch (vehiculo.tipo.toUpperCase()) {
                    case "AUTO":
                        if (vehiculo.puertas != null) {
                            json.append(",\"puertas\":").append(vehiculo.puertas);
                        }
                        break;
                    case "MOTO":
                        if (vehiculo.tipoMoto != null) {
                            json.append(",\"tipoMoto\":\"").append(vehiculo.tipoMoto).append("\"");
                        }
                        if (vehiculo.cilindrada != null) {
                            json.append(",\"cilindrada\":").append(vehiculo.cilindrada);
                        }
                        break;
                    case "CAMIONETA":
                        if (vehiculo.cargaMaximaCamioneta != null) {
                            json.append(",\"cargaMaximaCamioneta\":").append(vehiculo.cargaMaximaCamioneta);
                        }
                        if (vehiculo.traccion4x4 != null) {
                            json.append(",\"traccion4x4\":").append(vehiculo.traccion4x4);
                        }
                        if (vehiculo.capacidadPasajeros != null) {
                            json.append(",\"capacidadPasajeros\":").append(vehiculo.capacidadPasajeros);
                        }
                        break;
                    case "CAMION":
                        if (vehiculo.cargaMaximaCamion != null) {
                            json.append(",\"cargaMaximaCamion\":").append(vehiculo.cargaMaximaCamion);
                        }
                        if (vehiculo.numeroEjes != null) {
                            json.append(",\"numeroEjes\":").append(vehiculo.numeroEjes);
                        }
                        break;
                }
                
                json.append("}");
                first = false;
            }
        }
        
        // Count only dynamically created vehicles (excluding deleted ones)
        long totalVehiculos = vehiculosCreados.stream().filter(v -> !vehiculosEliminados.contains(v.id)).count();
        
        json.append("],\"total\":").append(totalVehiculos).append("}");
        return json.toString();
    }
    
    private static String getSystemStatusJson() {
        boolean gestorOk = gestorPedidos != null;
        boolean facadeOk = sistemaFacade != null;
        
        return "{\"sistema\":\"activo\"," +
               "\"compilacion\":\"exitosa\"," +
               "\"errores\":\"0\"," +
               "\"gestorPedidos\":\"" + (gestorOk ? "OK" : "ERROR") + "\"," +
               "\"sistemaFacade\":\"" + (facadeOk ? "OK" : "NO_INITIALIZED") + "\"," +
               "\"timestamp\":\"" + System.currentTimeMillis() + "\"," +
               "\"version\":\"1.0.0\"}";
    }
    
    private static String getPedidosJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\"pedidos\":[");
        
        boolean first = true;
        
        // Add dynamically created orders
        for (PedidoData pedido : pedidosCreados) {
            if (!pedidosEliminados.contains(pedido.id)) { // Check if not eliminated
                if (!first) json.append(",");
                json.append("{\"id\":").append(pedido.id)
                    .append(",\"cliente\":{\"nombre\":\"").append(pedido.cliente.nombre)
                    .append("\",\"email\":\"").append(pedido.cliente.email)
                    .append("\"},\"vehiculo\":{\"tipo\":\"").append(pedido.vehiculo.tipo != null ? pedido.vehiculo.tipo : "N/A")
                    .append("\",\"marca\":\"").append(pedido.vehiculo.marca)
                    .append("\",\"modelo\":\"").append(pedido.vehiculo.modelo)
                    .append("\"},\"estado\":\"").append(pedido.estado)
                    .append("\",\"total\":").append(pedido.total)
                    .append(",\"fecha\":\"").append(pedido.fecha.substring(0, 10)) // Asegúrate que la fecha no sea null
                    .append("\"}");
                first = false;
            }
        }
        
        // Count total orders (excluding deleted ones)
        long totalPedidos = pedidosCreados.stream().filter(p -> !pedidosEliminados.contains(p.id)).count();
        json.append("],\"total\":").append(totalPedidos).append("}");
        return json.toString();
    }
    
    private static String handleCreatePedido(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        
        try {
            String nombreCliente = extractJsonValue(body, "nombreCliente");
            String emailCliente = extractJsonValue(body, "emailCliente");
            String marca = extractJsonValue(body, "marca");
            String modelo = extractJsonValue(body, "modelo");
            String tipo = extractJsonValue(body, "tipo"); // Extraer el tipo de vehículo
            double precio = Double.parseDouble(extractJsonValue(body, "precio"));
            
            // Create client and vehicle data
            ClienteData cliente = new ClienteData(nombreCliente, emailCliente);
            VehiculoData vehiculo = new VehiculoData(0, tipo.isEmpty() ? "N/A" : tipo, marca, modelo, 0, precio, true, "N/A");
            
            // Create new order
            int newId = pedidoIdCounter.getAndIncrement();
            String fecha = java.time.LocalDateTime.now().toString();
            PedidoData nuevoPedido = new PedidoData(newId, cliente, vehiculo, "VENTAS", precio, fecha);
            pedidosCreados.add(nuevoPedido);
            
            System.out.println("Nuevo pedido creado: " + nombreCliente + " - " + tipo + " " + marca + " " + modelo);
            
            return "{\"success\":true,\"message\":\"Pedido creado exitosamente\",\"id\":" + newId + "}";
            
        } catch (Exception e) {
            System.err.println("Error al crear pedido: " + e.getMessage());
            return "{\"success\":false,\"error\":\"Error al procesar los datos del pedido: " + e.getMessage() + "\"}";
        }
    }
    
    private static String handleDeletePedido(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");
        
        if (segments.length >= 4) {
            try {
                int pedidoId = Integer.parseInt(segments[3]);
                
                // Try to remove from dynamically created orders first
                boolean removed = pedidosCreados.removeIf(p -> p.id == pedidoId);
                
                // If not found in dynamic orders, check if it's a default order (ID 1-2)
                if (!removed && pedidoId >= 1 && pedidoId <= 2) {
                    // Add to deleted list to hide it from display
                    if (!pedidosEliminados.contains(pedidoId)) {
                        pedidosEliminados.add(pedidoId);
                        removed = true;
                    }
                }
                
                if (removed) {
                    System.out.println("Pedido cancelado: ID " + pedidoId);
                    return "{\"success\":true,\"message\":\"Pedido cancelado exitosamente\"}";
                } else {
                    return "{\"success\":false,\"error\":\"Pedido no encontrado\"}";
                }
            } catch (NumberFormatException e) {
                return "{\"success\":false,\"error\":\"ID de pedido inválido\"}";
            }
        } else {
            return "{\"success\":false,\"error\":\"Falta ID del pedido\"}";
        }
    }
    
    private static String handleUpdatePedido(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");
        
        if (segments.length >= 4) {
            try {
                int pedidoId = Integer.parseInt(segments[3]);
                
                InputStream requestBody = exchange.getRequestBody();
                String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                
                // Extract fields from request body
                String nuevoEstado = extractJsonValue(body, "estado");
                String nombreCliente = extractJsonValue(body, "nombreCliente");
                String emailCliente = extractJsonValue(body, "emailCliente");
                
                // Find and update the order in dynamic orders first
                for (PedidoData pedido : pedidosCreados) {
                    if (pedido.id == pedidoId) {
                        // Update estado if provided
                        if (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) {
                            pedido.estado = nuevoEstado;
                            System.out.println("Pedido estado actualizado: ID " + pedidoId + " -> " + nuevoEstado);
                        }
                        
                        // Update cliente data if provided
                        if (nombreCliente != null && !nombreCliente.trim().isEmpty()) {
                            pedido.cliente.nombre = nombreCliente;
                            System.out.println("Pedido cliente nombre actualizado: ID " + pedidoId + " -> " + nombreCliente);
                        }
                        
                        if (emailCliente != null && !emailCliente.trim().isEmpty()) {
                            pedido.cliente.email = emailCliente;
                            System.out.println("Pedido cliente email actualizado: ID " + pedidoId + " -> " + emailCliente);
                        }
                        
                        return "{\"success\":true,\"message\":\"Pedido actualizado exitosamente\"}";
                    }
                }
                
                // If not found in dynamic orders, check if it's a default order (ID 1-2)
                if (pedidoId >= 1 && pedidoId <= 2 && !pedidosEliminados.contains(pedidoId)) {
                    // For default orders, we only handle estado updates for now
                    if (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) {
                        estadosPedidosDefecto.put(pedidoId, nuevoEstado);
                        System.out.println("Pedido por defecto actualizado: ID " + pedidoId + " -> " + nuevoEstado);
                        return "{\"success\":true,\"message\":\"Pedido actualizado exitosamente\"}";
                    } else {
                        return "{\"success\":false,\"error\":\"Los pedidos por defecto solo permiten actualizar el estado\"}";
                    }
                }
                
                return "{\"success\":false,\"error\":\"Pedido no encontrado\"}";
                
            } catch (NumberFormatException e) {
                return "{\"success\":false,\"error\":\"ID de pedido inválido\"}";
            }
        } else {
            return "{\"success\":false,\"error\":\"Falta ID del pedido\"}";
        }
    }
    

    
    private static void sendJsonResponse(HttpExchange exchange, String jsonResponse) throws IOException {
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, responseBytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
