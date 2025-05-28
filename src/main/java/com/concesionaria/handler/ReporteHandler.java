package com.concesionaria.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.RolUsuario; 
import controller.SistemaFacade;
import controller.ExcepcionValidacion; // Assuming this exists from previous steps
import controller.util.GsonUtil; 

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReporteHandler implements HttpHandler {
    private SistemaFacade sistemaFacade;
    private com.google.gson.Gson gson; // For error responses

    public ReporteHandler(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
        this.gson = GsonUtil.getGson(); // Assuming GsonUtil is set up
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = queryToMap(query);

        String responseBody = "";
        int statusCode = 200;
        String contentType = "text/plain; charset=UTF-8";

        // Simulate role and ID for now - In a real app, this would come from an auth layer
        RolUsuario rolSolicitante = RolUsuario.ADMINISTRADOR; 
        int idSolicitante = 1; // Example admin/test user ID

        // Basic role simulation from hypothetical headers (more robust auth needed for real app)
        String headerRol = exchange.getRequestHeaders().getFirst("X-User-Role");
        String headerId = exchange.getRequestHeaders().getFirst("X-User-Id");
        if (headerRol != null) { try { rolSolicitante = RolUsuario.valueOf(headerRol.toUpperCase()); } catch (IllegalArgumentException e) { /* keep default */ } }
        if (headerId != null) { try { idSolicitante = Integer.parseInt(headerId); } catch (NumberFormatException e) { /* keep default */ } }


        try {
            if (!"GET".equals(method)) {
                // Using ExcepcionProceso as per Step 22 for process flow errors
                throw new controller.ExcepcionProceso("Método no permitido para reportes. Solo GET es soportado.");
            }

            if (path.equals("/api/reportes/pedidos")) {
                String fechaDesdeStr = params.get("fechaDesde");
                String fechaHastaStr = params.get("fechaHasta");
                // estadoPedido can be null, handled by facade
                String estadoPedido = params.get("estadoPedido"); 

                if (fechaDesdeStr == null || fechaHastaStr == null) {
                    throw new ExcepcionValidacion("Parámetros fechaDesde y fechaHasta son requeridos.");
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false); // Disallow lenient date parsing
                Date fechaDesde = sdf.parse(fechaDesdeStr);
                Date fechaHasta = sdf.parse(fechaHastaStr);

                responseBody = sistemaFacade.generarReporteListadoPedidos(fechaDesde, fechaHasta, estadoPedido, rolSolicitante, idSolicitante);

            } else if (path.equals("/api/reportes/totales")) {
                String incluirImpuestosStr = params.getOrDefault("incluirImpuestos", "true");
                boolean incluirImpuestos = Boolean.parseBoolean(incluirImpuestosStr);
                responseBody = sistemaFacade.generarReporteTotales(incluirImpuestos, rolSolicitante);
            } else {
                statusCode = 404;
                responseBody = gson.toJson(Map.of("error", "Ruta de reporte no encontrada"));
                contentType = "application/json; charset=UTF-8";
            }
        } catch (controller.ExcepcionProceso e) { // Catching the specific process error
            statusCode = 405; // Method not allowed
            responseBody = gson.toJson(Map.of("error", e.getMessage()));
            contentType = "application/json; charset=UTF-8";
        } catch (ExcepcionValidacion e) {
            statusCode = 400;
            responseBody = gson.toJson(Map.of("error", e.getMessage()));
            contentType = "application/json; charset=UTF-8";
        } catch (ParseException e) {
            statusCode = 400;
            responseBody = gson.toJson(Map.of("error", "Formato de fecha inválido. Usar YYYY-MM-DD."));
            contentType = "application/json; charset=UTF-8";
        }
        catch (SecurityException e) { // Catch SecurityException from facade if role access is denied
            statusCode = 403; // Forbidden
            responseBody = gson.toJson(Map.of("error", e.getMessage()));
            contentType = "application/json; charset=UTF-8";
        }
        catch (Exception e) {
            statusCode = 500;
            responseBody = gson.toJson(Map.of("error", "Error interno del servidor: " + e.getMessage()));
            contentType = "application/json; charset=UTF-8";
            e.printStackTrace(); // Log for server-side debugging
        }

        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private Map<String, String> queryToMap(String query) {
        if (query == null || query.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                try {
                    result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8.name()), URLDecoder.decode(entry[1], StandardCharsets.UTF_8.name()));
                } catch (java.io.UnsupportedEncodingException e) { 
                    System.err.println("Error decodificando query param (ignorado): " + e.getMessage());
                }
            } else {
                try {
                    result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8.name()), "");
                } catch (java.io.UnsupportedEncodingException e) { 
                    System.err.println("Error decodificando query param (ignorado): " + e.getMessage());
                }
            }
        }
        return result;
    }
}
