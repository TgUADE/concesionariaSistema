package com.concesionaria.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.Cliente;
import controller.ExcepcionDuplicado;
import controller.ExcepcionValidacion;
import controller.repository.InterfazClienteRepository;
import controller.util.GsonUtil;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import com.google.gson.JsonSyntaxException;

public class ClienteHandler implements HttpHandler {
    private InterfazClienteRepository clienteRepository;
    private com.google.gson.Gson gson;

    public ClienteHandler(InterfazClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
        this.gson = GsonUtil.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";
        int statusCode = 200;

        try {
            if ("GET".equals(method)) {
                if (path.equals("/api/clientes")) { // Get all clientes
                    List<Cliente> clientes = clienteRepository.findAll();
                    response = gson.toJson(clientes);
                } else if (path.matches("/api/clientes/\\d+")) { // Get cliente by ID
                    int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                    Optional<Cliente> clienteOpt = clienteRepository.findById(id);
                    if (clienteOpt.isPresent()) {
                        response = gson.toJson(clienteOpt.get());
                    } else {
                        statusCode = 404;
                        response = "{\"error\":\"Cliente no encontrado\"}";
                    }
                } else {
                    statusCode = 404;
                    response = "{\"error\":\"Ruta GET no encontrada\"}";
                }
            } else if ("POST".equals(method) && path.equals("/api/clientes")) { // Create cliente
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                Cliente nuevoCliente = gson.fromJson(reader, Cliente.class);
                // Basic validation for required fields in POST
                if (nuevoCliente.getDocumento() == null || nuevoCliente.getCorreo() == null || nuevoCliente.getNombre() == null) {
                    throw new ExcepcionValidacion("Documento, correo y nombre son requeridos para crear un cliente.");
                }
                Cliente clienteCreado = clienteRepository.save(nuevoCliente);
                response = gson.toJson(clienteCreado);
                statusCode = 201; // Created
            } else {
                statusCode = 405; // Method Not Allowed
                response = "{\"error\":\"Método no permitido\"}";
            }
        } catch (JsonSyntaxException e) {
            statusCode = 400; // Bad Request
            response = "{\"error\":\"JSON malformado: " + e.getMessage() + "\"}";
        } catch (ExcepcionValidacion e) {
            statusCode = 400; // Bad Request
            response = "{\"error\":\"Error de validación: " + e.getMessage() + "\"}";
        } catch (ExcepcionDuplicado e) {
            statusCode = 409; // Conflict
            response = "{\"error\":\"Cliente duplicado: " + e.getMessage() + "\"}";
        } catch (NumberFormatException e) {
            statusCode = 400; // Bad Request
            response = "{\"error\":\"ID de cliente inválido en la URL.\"}";
        }
         catch (Exception e) {
            statusCode = 500; // Internal Server Error
            response = "{\"error\":\"Error interno del servidor: " + e.getMessage() + "\"}";
            e.printStackTrace(); // Log error
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
