package com.concesionaria.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.*; // Entities, Exceptions, Services, Repositories, Factories
import controller.repository.*;
import controller.util.GsonUtil;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList; // For DTO's list

// DTO for POST /api/pedidos request
class PedidoRequestDTO {
    // Assuming these are provided in the request
    int clienteId;
    int vehiculoId;
    TipoFormaPago formaPagoTipo; // Enum: CONTADO, TARJETA_CREDITO, TRANSFERENCIA
    DatosFacturacion datosFacturacion; // Can be null
    String vendedorNombre;
    String vendedorCorreo;
    List<ConfiguracionAdicional> configuracionesAdicionales; // Can be null or empty

    // Default constructor for GSON
    public PedidoRequestDTO() {}
}


public class PedidoHandler implements HttpHandler {
    private InterfazPedidoRepository pedidoRepository;
    private InterfazClienteRepository clienteRepository;
    private InterfazVehiculoRepository vehiculoRepository;
    private InterfazFormaDePagoFactory formaDePagoFactory;
    private ServicioDePago servicioDePago;
    private ServicioDeNotificacion servicioDeNotificacion;
    private com.google.gson.Gson gson;

    public PedidoHandler(InterfazPedidoRepository pedidoRepository,
                         InterfazClienteRepository clienteRepository,
                         InterfazVehiculoRepository vehiculoRepository,
                         InterfazFormaDePagoFactory formaDePagoFactory,
                         ServicioDePago servicioDePago,
                         ServicioDeNotificacion servicioDeNotificacion) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.formaDePagoFactory = formaDePagoFactory;
        this.servicioDePago = servicioDePago;
        this.servicioDeNotificacion = servicioDeNotificacion;
        this.gson = GsonUtil.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";
        int statusCode = 200;
        
        // Simulated role and ID for testing GET by ID and other role-dependent operations
        // In a real app, this would come from an authentication/authorization layer.
        RolUsuario rolSolicitante = RolUsuario.ADMINISTRADOR; // Default to ADMIN for broad access
        int idSolicitante = 0; // Default, only relevant for COMPRADOR

        // Example: Extract user info from headers if available (very basic, not secure)
        // String headerRol = exchange.getRequestHeaders().getFirst("X-User-Role");
        // String headerId = exchange.getRequestHeaders().getFirst("X-User-Id");
        // if (headerRol != null) { try { rolSolicitante = RolUsuario.valueOf(headerRol.toUpperCase()); } catch (IllegalArgumentException e) { /* keep default */ } }
        // if (headerId != null) { try { idSolicitante = Integer.parseInt(headerId); } catch (NumberFormatException e) { /* keep default */ } }


        try {
            if ("POST".equals(method) && path.equals("/api/pedidos")) {
                // --- Create Pedido ---
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                PedidoRequestDTO requestDTO = gson.fromJson(reader, PedidoRequestDTO.class);

                if (requestDTO == null) {
                    throw new ExcepcionValidacion("Cuerpo de la solicitud está vacío o malformado.");
                }
                
                Optional<Cliente> clienteOpt = clienteRepository.findById(requestDTO.clienteId);
                if (!clienteOpt.isPresent()) {
                    statusCode = 404; // Not Found, but more like a validation error for POST
                    throw new ExcepcionValidacion("Cliente con ID " + requestDTO.clienteId + " no encontrado.");
                }
                Cliente cliente = clienteOpt.get();

                Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findById(requestDTO.vehiculoId);
                if (!vehiculoOpt.isPresent()) {
                    statusCode = 404;
                    throw new ExcepcionValidacion("Vehículo con ID " + requestDTO.vehiculoId + " no encontrado.");
                }
                Vehiculo vehiculo = vehiculoOpt.get();
                if (!vehiculo.isDisponibleVenta()) {
                     throw new ExcepcionValidacion("Vehículo con ID " + requestDTO.vehiculoId + " no está disponible para la venta.");
                }

                IFormaDePago formaDePago = null;
                if (requestDTO.formaPagoTipo != null) {
                     formaDePago = formaDePagoFactory.crearFormaDePago(requestDTO.formaPagoTipo);
                     // If formaDePago requires specifics (e.g. Contado needs moneda), they'd be set here or via DTO
                     if (formaDePago instanceof Contado && requestDTO.formaPagoTipo == TipoFormaPago.CONTADO) {
                        // ((Contado) formaDePago).setMoneda("USD"); // Example, this should come from DTO or config
                     }
                }


                InterfazPedidoBuilder builder = new ConcretePedidoBuilder();
                // numeroPedido is usually auto-generated by repository on save or by a sequence in DB
                // For now, Pedido constructor can handle 0 and repository might assign.
                // Or, builder can be responsible for it if needed pre-build.
                // Let's assume 0 means "to be generated".
                builder.setNumeroPedido(0) 
                       .setFechaCreacion(new Date()) // Or from DTO if provided
                       .setCliente(cliente)
                       .setVehiculo(vehiculo)
                       .setFormaDePago(formaDePago) // Can be null
                       .setDatosFacturacion(requestDTO.datosFacturacion) // Can be null
                       .setVendedor(requestDTO.vendedorNombre, requestDTO.vendedorCorreo)
                       .setServicioDePago(this.servicioDePago)
                       .setServicioDeNotificacion(this.servicioDeNotificacion);
                
                if (requestDTO.configuracionesAdicionales != null) {
                    for (ConfiguracionAdicional ca : requestDTO.configuracionesAdicionales) {
                        builder.agregarConfiguracionAdicional(ca);
                    }
                }
                
                Pedido nuevoPedido = builder.build(); // Throws ExcepcionValidacion if builder validation fails
                Pedido pedidoCreado = pedidoRepository.save(nuevoPedido); // Throws ExcepcionDuplicado

                // Mark vehicle as not available for sale
                vehiculo.setDisponibleVenta(false);
                vehiculoRepository.save(vehiculo); // Update the vehicle's availability

                response = gson.toJson(pedidoCreado);
                statusCode = 201; // Created

            } else if ("GET".equals(method) && path.matches("/api/pedidos/\\d+")) {
                // --- Get Pedido by ID ---
                int numeroPedido = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                Optional<Pedido> pedidoOpt = pedidoRepository.findByNumeroPedido(numeroPedido);

                if (pedidoOpt.isPresent()) {
                    Pedido pedido = pedidoOpt.get();
                    // Role Check
                    if (rolSolicitante == RolUsuario.ADMINISTRADOR) {
                        response = gson.toJson(pedido);
                    } else if (rolSolicitante == RolUsuario.COMPRADOR && pedido.getCliente() != null && pedido.getCliente().getId() == idSolicitante) {
                        response = gson.toJson(pedido);
                    } else {
                        statusCode = 403; // Forbidden
                        response = "{\"error\":\"Acceso denegado.\"}";
                    }
                } else {
                    statusCode = 404;
                    response = "{\"error\":\"Pedido no encontrado\"}";
                }
            } else if ("POST".equals(method) && path.matches("/api/pedidos/\\d+/avanzar")) {
                // --- Advance Pedido State ---
                if (rolSolicitante != RolUsuario.ADMINISTRADOR) {
                    statusCode = 403;
                    throw new SecurityException("Acceso denegado. Se requiere rol ADMINISTRADOR para avanzar pedidos.");
                }
                int numeroPedido = Integer.parseInt(path.split("/")[3]); // Path: /api/pedidos/{numeroPedido}/avanzar
                Optional<Pedido> pedidoOpt = pedidoRepository.findByNumeroPedido(numeroPedido);

                if (!pedidoOpt.isPresent()) {
                    statusCode = 404;
                    throw new ExcepcionValidacion("Pedido con número " + numeroPedido + " no encontrado.");
                }
                Pedido pedido = pedidoOpt.get();
                
                // Contexto for avanzar can be null or parsed from a minimal request body if needed
                // For this example, assuming null contexto
                Object contexto = null; 
                // Example: If contexto was expected:
                // InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                // Map<String, Object> contextoMap = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
                // contexto = contextoMap;


                pedido.avanzarPedido(contexto); // This method now updates history and notifies observers
                Pedido pedidoActualizado = pedidoRepository.save(pedido); // Save the updated state
                response = gson.toJson(pedidoActualizado);
                statusCode = 200;

            } else if ("GET".equals(method) && path.equals("/api/pedidos")) {
                // --- List Pedidos ---
                List<Pedido> pedidosFiltrados;
                // Basic query parameter extraction (more robust parsing might be needed for multiple params)
                String query = exchange.getRequestURI().getQuery();
                String filterClienteIdStr = null;
                String filterEstado = null;

                if (query != null) {
                    for (String param : query.split("&")) {
                        String[] pair = param.split("=");
                        if (pair.length > 1) {
                            if ("clienteId".equals(pair[0])) filterClienteIdStr = pair[1];
                            if ("estado".equals(pair[0])) filterEstado = pair[1];
                        }
                    }
                }

                if (rolSolicitante == RolUsuario.ADMINISTRADOR) {
                    if (filterClienteIdStr != null) {
                        int filterClienteId = Integer.parseInt(filterClienteIdStr);
                        pedidosFiltrados = pedidoRepository.findPedidosByClienteId(filterClienteId);
                        if (filterEstado != null) {
                            pedidosFiltrados.removeIf(p -> p.getEstadoActual() == null || !p.getEstadoActual().getClass().getSimpleName().equalsIgnoreCase(filterEstado));
                        }
                    } else if (filterEstado != null) {
                        pedidosFiltrados = pedidoRepository.findPedidosByEstado(filterEstado);
                    } else {
                        pedidosFiltrados = pedidoRepository.findAll();
                    }
                } else if (rolSolicitante == RolUsuario.COMPRADOR) {
                    pedidosFiltrados = pedidoRepository.findPedidosByClienteId(idSolicitante);
                     if (filterEstado != null) { // Comprador can also filter their own orders by state
                        pedidosFiltrados.removeIf(p -> p.getEstadoActual() == null || !p.getEstadoActual().getClass().getSimpleName().equalsIgnoreCase(filterEstado));
                    }
                } else { // VENDEDOR or other non-admin/non-comprador roles
                    statusCode = 403;
                    throw new SecurityException("Acceso denegado a la lista de pedidos para el rol: " + rolSolicitante);
                }
                response = gson.toJson(pedidosFiltrados);
            }
            else {
                statusCode = 404; // Not Found for other paths or methods for now
                response = "{\"error\":\"Ruta no implementada o método no permitido para esta ruta.\"}";
            }

        } catch (JsonSyntaxException e) {
            statusCode = 400; response = "{\"error\":\"JSON malformado: " + e.getMessage() + "\"}";
        } catch (ExcepcionValidacion e) {
            statusCode = 400; response = "{\"error\":\"Error de validación: " + e.getMessage() + "\"}";
        } catch (ExcepcionDuplicado e) {
            statusCode = 409; response = "{\"error\":\"Entidad duplicada: " + e.getMessage() + "\"}";
        } catch (ExcepcionProceso e) { // Catch specific process error for avanzar
            statusCode = 400; // Bad Request or 422 Unprocessable Entity
            response = "{\"error\":\"Error de proceso: " + e.getMessage() + "\"}";
        } catch (SecurityException e) { // Catch specific security error for role checks
            statusCode = 403; // Forbidden
            response = "{\"error\":\"Acceso denegado: " + e.getMessage() + "\"}";
        }
        catch (NumberFormatException e) {
            statusCode = 400; response = "{\"error\":\"ID de pedido o cliente inválido en la URL/parámetros.\"}";
        } catch (Exception e) {
            statusCode = 500; response = "{\"error\":\"Error interno del servidor: " + e.getMessage() + "\"}";
            e.printStackTrace(); 
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
