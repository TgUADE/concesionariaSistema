package com.concesionaria;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

// Placeholder/commented imports for when actual dependencies are used
// import controller.repository.InterfazClienteRepository;
// import controller.repository.GsonClienteRepositoryImpl;
// import controller.repository.InterfazVehiculoRepository;
// import controller.repository.GsonVehiculoRepositoryImpl;
// import controller.repository.InterfazPedidoRepository;
// import controller.repository.GsonPedidoRepositoryImpl;
// import controller.ConfiguracionConcesionaria;
// import controller.SistemaFacade;
import controller.repository.InterfazClienteRepository; 
import controller.repository.GsonClienteRepositoryImpl;   
import com.concesionaria.handler.ClienteHandler;      
import controller.repository.InterfazVehiculoRepository; 
import controller.repository.GsonVehiculoRepositoryImpl;   
import com.concesionaria.handler.VehiculoHandler;      
import controller.repository.InterfazPedidoRepository; // Added
import controller.repository.GsonPedidoRepositoryImpl;   // Added
import controller.ConfiguracionConcesionaria;          // Added
import controller.SistemaFacade;                       // Added
import com.concesionaria.handler.ReporteHandler;       // Added
// Imports for Gestor interfaces if needed for constructor, even if passing null
import controller.InterfazGestorDePedidos;
import controller.InterfazGestorDeUsuario;
import controller.InterfazGestorVehiculos;
import controller.InterfazGestorImpuestos;
import controller.GestorDeStock; // Concrete class used in facade constructor
import controller.InterfazDeNotificacion;
// import controller.util.GsonUtil; // For JSON responses, if more complex than simple string

public class MainApp {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Servidor iniciado en el puerto " + port + "...");

        // --- Initialization of Services and Repositories ---
        String dataPath = "data/"; // Example base path for JSON files
        // Ensure data directory exists for GSON repositories to write files
        new java.io.File(dataPath).mkdirs(); 
        
        final InterfazClienteRepository clienteRepo = new GsonClienteRepositoryImpl(dataPath + "clientes.json");
        final InterfazVehiculoRepository vehiculoRepo = new GsonVehiculoRepositoryImpl(dataPath + "vehiculos.json"); 
        final InterfazPedidoRepository pedidoRepo = new GsonPedidoRepositoryImpl(dataPath + "pedidos.json"); // Uncommented
        final ConfiguracionConcesionaria config = ConfiguracionConcesionaria.getInstance(); // Uncommented
        
        // Placeholder for Gestor interfaces - these would need concrete implementations or mocks
        // For this step, passing null as report methods don't use them.
        final InterfazGestorDePedidos gestorDePedidosMock = null; 
        final InterfazGestorDeUsuario gestorDeUsuarioMock = null;
        final InterfazGestorVehiculos gestorVehiculosMock = null;
        final InterfazGestorImpuestos gestorImpuestosMock = null;
        final GestorDeStock gestorDeStockMock = null; // Concrete type in facade
        final InterfazDeNotificacion notificacionesMock = null;

        final SistemaFacade sistemaFacade = new SistemaFacade( // Uncommented and initialized
            gestorDePedidosMock, gestorDeUsuarioMock, gestorVehiculosMock,
            gestorImpuestosMock, gestorDeStockMock, notificacionesMock,
            pedidoRepo, config
        );
        // --- End of Initialization ---


        // Basic test endpoint
        server.createContext("/api/test", exchange -> {
            String response = "{\"message\":\"Servidor de Concesionaria funcionando!\"}";
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        });

        // API Handlers
        server.createContext("/api/clientes", new ClienteHandler(clienteRepo));
        server.createContext("/api/vehiculos", new VehiculoHandler(vehiculoRepo)); 
        // server.createContext("/api/pedidos", new PedidoHandler(pedidoRepo, /* PedidoBuilder, services */));
        server.createContext("/api/reportes", new ReporteHandler(sistemaFacade)); // Added


        server.setExecutor(null); // Use default executor
        server.start();
    }
}
