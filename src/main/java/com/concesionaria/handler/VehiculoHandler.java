package com.concesionaria.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.ExcepcionDuplicado;
import controller.ExcepcionValidacion;
import controller.RolUsuario; // Assuming RolUsuario is available for simulated role check
import controller.Vehiculo;
import controller.repository.InterfazVehiculoRepository;
import controller.util.GsonUtil;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class VehiculoHandler implements HttpHandler {
    private InterfazVehiculoRepository vehiculoRepository;
    private com.google.gson.Gson gson;

    public VehiculoHandler(InterfazVehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.gson = GsonUtil.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";
        int statusCode = 200;

        // Simulate role for protected endpoints - In a real app, this would come from an auth layer
        RolUsuario rolSolicitante = RolUsuario.ADMINISTRADOR; // Default to ADMIN for simplicity here

        try {
            if ("GET".equals(method)) {
                if (path.equals("/api/vehiculos")) { // Get available vehicles for sale
                    List<Vehiculo> vehiculos = vehiculoRepository.findByDisponibleVenta(true);
                    response = gson.toJson(vehiculos);
                } else if (path.equals("/api/vehiculos/catalogo")) { // Get all vehicles (admin view)
                    if (rolSolicitante == RolUsuario.ADMINISTRADOR) {
                        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
                        response = gson.toJson(vehiculos);
                    } else {
                        statusCode = 403; // Forbidden
                        response = "{\"error\":\"Acceso denegado. Se requiere rol ADMINISTRADOR.\"}";
                    }
                } else if (path.matches("/api/vehiculos/\\d+")) { // Get vehicle by ID
                    int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                    Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findById(id);
                    if (vehiculoOpt.isPresent()) {
                        response = gson.toJson(vehiculoOpt.get());
                    } else {
                        statusCode = 404;
                        response = "{\"error\":\"Vehículo no encontrado\"}";
                    }
                } else {
                    statusCode = 404;
                    response = "{\"error\":\"Ruta GET no encontrada\"}";
                }
            } else if ("POST".equals(method) && path.equals("/api/vehiculos")) { // Create vehicle (admin)
                if (rolSolicitante == RolUsuario.ADMINISTRADOR) {
                    InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    Vehiculo nuevoVehiculo = gson.fromJson(reader, Vehiculo.class);
                    
                    // Basic validation for required fields of Vehiculo
                    if (nuevoVehiculo.getMarca() == null || nuevoVehiculo.getMarca().trim().isEmpty() ||
                        nuevoVehiculo.getModelo() == null || nuevoVehiculo.getModelo().trim().isEmpty() ||
                        nuevoVehiculo.getChasis() == null || nuevoVehiculo.getChasis().trim().isEmpty() ||
                        nuevoVehiculo.getTipo() == null) {
                        throw new ExcepcionValidacion("Marca, modelo, chasis y tipo son requeridos para crear un vehículo.");
                    }
                    // Note: vehiculoRepository.save might throw ExcepcionValidacion if factory has more checks
                    Vehiculo vehiculoCreado = vehiculoRepository.save(nuevoVehiculo);
                    response = gson.toJson(vehiculoCreado);
                    statusCode = 201; // Created
                } else {
                    statusCode = 403; // Forbidden
                    response = "{\"error\":\"Acceso denegado. Se requiere rol ADMINISTRADOR para crear vehículos.\"}";
                }
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
            response = "{\"error\":\"Vehículo duplicado: " + e.getMessage() + "\"}";
        } catch (NumberFormatException e) {
            statusCode = 400; // Bad Request
            response = "{\"error\":\"ID de vehículo inválido en la URL.\"}";
        } catch (Exception e) {
            statusCode = 500; // Internal Server Error
            response = "{\"error\":\"Error interno del servidor: " + e.getMessage() + "\"}";
            e.printStackTrace(); // Log error for server-side debugging
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
