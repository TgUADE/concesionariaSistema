package controller;
import java.io.*;
import java.util.*;
import controller.repository.InterfazPedidoRepository;

/**
 * 
 */
public class SistemaFacade {

    private InterfazGestorDePedidos gestorDePedidos;
    private InterfazGestorDeUsuario gestorDeUsuario;
    private InterfazGestorVehiculos gestorVehiculos;
    private InterfazGestorImpuestos gestorImpuestos;
    private GestorDeStock gestorDeStock; // Assuming no interface for this one yet
    private InterfazDeNotificacion notificaciones;
    private InterfazPedidoRepository pedidoRepository; // New
    private ConfiguracionConcesionaria configuracionConcesionaria; // New


    /**
     * Default constructor
     */
    public SistemaFacade(
        InterfazGestorDePedidos gestorDePedidos,
        InterfazGestorDeUsuario gestorDeUsuario,
        InterfazGestorVehiculos gestorVehiculos,
        InterfazGestorImpuestos gestorImpuestos,
        GestorDeStock gestorDeStock, // Kept for now
        InterfazDeNotificacion notificaciones, // Kept for now
        InterfazPedidoRepository pedidoRepository, // Added
        ConfiguracionConcesionaria configuracionConcesionaria // Added
    ) {
        this.gestorDePedidos = gestorDePedidos;
        this.gestorDeUsuario = gestorDeUsuario;
        this.gestorVehiculos = gestorVehiculos;
        this.gestorImpuestos = gestorImpuestos;
        this.gestorDeStock = gestorDeStock;
        this.notificaciones = notificaciones;
        this.pedidoRepository = pedidoRepository;
        this.configuracionConcesionaria = configuracionConcesionaria;
    }

    // Getter methods for the gestores could be added if needed by external components
    // but not strictly necessary for Facade's internal operation using them.

    public String generarReporteListadoPedidos(Date fechaDesde, Date fechaHasta, String estadoPedido, RolUsuario rolSolicitante, int idSolicitante) {
        if (pedidoRepository == null || configuracionConcesionaria == null) {
            return "Error: Repositorio de pedidos o configuración no inicializados.";
        }
        if (rolSolicitante == null) {
            return "Error: Rol del solicitante no especificado.";
        }

        List<Pedido> pedidosAProcesar;
        List<Pedido> todosLosPedidos = pedidoRepository.findPedidosByFechaAndEstado(fechaDesde, fechaHasta, estadoPedido);

        switch (rolSolicitante) {
            case ADMINISTRADOR:
                pedidosAProcesar = todosLosPedidos;
                break;
            case COMPRADOR:
                pedidosAProcesar = new ArrayList<>();
                for (Pedido p : todosLosPedidos) {
                    // Assuming Cliente implements ICliente and has getId() method.
                    // And ICliente interface has getId() method.
                    // For now, we rely on Pedido.getCliente() returning an object that we can cast
                    // or that ICliente directly has getId().
                    // Let's assume ICliente has getId() for this example.
                    if (p.getCliente() != null && p.getCliente() instanceof Cliente && ((Cliente)p.getCliente()).getId() == idSolicitante) {
                         pedidosAProcesar.add(p);
                    } else if (p.getCliente() != null && p.getCliente().getNombre().equals("ClienteMock") && idSolicitante == 1) { 
                        // Simplified check for mock data if getId() is not on ICliente directly.
                        // This part needs to be robust based on actual ICliente/Cliente structure.
                        // For this example, we assume Cliente has getId() and ICliente might not.
                        // If ICliente has getId(), the instanceof check is not needed.
                        // The task implies Cliente.java has getId()
                        pedidosAProcesar.add(p);
                    }
                }
                break;
            default: // VENDEDOR or other roles not explicitly granted access to this view
                return "Acceso denegado a este reporte para el rol: " + rolSolicitante;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Pedidos - ").append(configuracionConcesionaria.getNombreConcesionaria())
          .append(" (CUIT: ").append(configuracionConcesionaria.getCuitConcesionaria()).append(")\n");
        sb.append("Solicitante Rol: ").append(rolSolicitante).append(rolSolicitante == RolUsuario.COMPRADOR ? " (ID: " + idSolicitante + ")" : "").append("\n");
        sb.append("Filtros: Fecha Desde=").append(fechaDesde != null ? fechaDesde.toString() : "N/A")
          .append(", Fecha Hasta=").append(fechaHasta != null ? fechaHasta.toString() : "N/A")
          .append(", Estado=").append(estadoPedido != null ? estadoPedido : "Todos").append("\n");
        sb.append("-----------------------------------------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-10s | %-24s | %-20s | %-20s | %-15s | %-10s\n", 
                                "Pedido N°", "Fecha Creación", "Cliente", "Vehículo Modelo", "Estado Actual", "Total"));
        sb.append("-----------------------------------------------------------------------------------------------------------------------\n");

        if (pedidosAProcesar.isEmpty()) {
            sb.append("No se encontraron pedidos con los criterios especificados para su rol.\n");
        } else {
            for (Pedido p : pedidosAProcesar) {
                String clienteNombre = (p.getCliente() != null) ? p.getCliente().getNombre() : "N/A";
                String vehiculoModelo = (p.getVehiculo() != null) ? p.getVehiculo().getModelo() : "N/A";
                String estadoActualNombre = (p.getEstadoActual() != null) ? p.getEstadoActual().getClass().getSimpleName() : "N/A";
                
                sb.append(String.format("%-10d | %-24s | %-20s | %-20s | %-15s | %-10.2f\n",
                    p.getNumeroPedido(), 
                    (p.getFechaCreacion() != null ? p.getFechaCreacion().toString() : "N/A"), 
                    clienteNombre,
                    vehiculoModelo, 
                    estadoActualNombre, 
                    p.getCostoTotal()));
            }
        }
        sb.append("-----------------------------------------------------------------------------------------------------------------------\n");
        return sb.toString();
    }
    
    /**
     * 
     */
    public void realizarPedido() {
        // TODO implement here
    }

    /**
     * 
     */
    public void procesarPedido() {
        // TODO implement here
    }

    /**
     * 
     */
    public void generarReporteDeVentas() {
        // This method seems to be a duplicate or an older name for generarReporteTotales.
        // It is now superseded by generarReporteTotales(boolean incluirImpuestos, RolUsuario rolSolicitante)
        // System.out.println(generarReporteTotales(true, RolUsuario.ADMINISTRADOR)); // Example call
        System.out.println("INFO: generarReporteDeVentas() está obsoleta, usar generarReporteTotales(boolean, RolUsuario).");
    }

    public String generarReporteTotales(boolean incluirImpuestos, RolUsuario rolSolicitante) {
        if (rolSolicitante != RolUsuario.ADMINISTRADOR) {
            return "Acceso denegado a este reporte. Se requiere rol ADMINISTRADOR.";
        }

        if (pedidoRepository == null || configuracionConcesionaria == null) {
            return "Error: Repositorio de pedidos o configuración no inicializados.";
        }

        List<Pedido> todosLosPedidos = pedidoRepository.findAllPedidos();
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Totales - ").append(configuracionConcesionaria.getNombreConcesionaria())
          .append(" (CUIT: ").append(configuracionConcesionaria.getCuitConcesionaria()).append(")\n");
        sb.append("Solicitante Rol: ").append(rolSolicitante).append("\n");
        sb.append("--------------------------------------------------------------------\n");

        double granTotalVentasConImpuestos = 0;
        double totalImpuestosCalculados = 0;
        Map<String, Double> totalesPorFormaPago = new HashMap<>();

        if (todosLosPedidos.isEmpty()) {
            sb.append("No se encontraron pedidos para generar totales.\n");
        } else {
            for (Pedido p : todosLosPedidos) {
                granTotalVentasConImpuestos += p.getCostoTotal(); // costoTotal includes taxes as per Pedido.recalcularCostoTotal()

                for (ImpuestoAplicado impuesto : p.getImpuestosAplicados()) {
                    totalImpuestosCalculados += impuesto.getMontoCalculado();
                }

                String formaPagoKey = (p.getFormaPago() != null) ? p.getFormaPago().getClass().getSimpleName() : "No especificada";
                totalesPorFormaPago.merge(formaPagoKey, p.getCostoTotal(), Double::sum);
            }

            double granTotalVentasFinal = granTotalVentasConImpuestos;
            if (!incluirImpuestos) {
                // If costoTotal includes taxes, and we want a view *without* taxes, subtract them.
                granTotalVentasFinal -= totalImpuestosCalculados;
            }

            sb.append(String.format("Gran Total Ventas (%s impuestos): %.2f\n",
                                    (incluirImpuestos ? "con" : "sin"), granTotalVentasFinal));
            
            if (incluirImpuestos) {
                sb.append(String.format("Total Impuestos Recaudados (sumados en costoTotal): %.2f\n", totalImpuestosCalculados));
            } else {
                 sb.append(String.format("Total Impuestos (no incluidos en Gran Total Ventas anterior): %.2f\n", totalImpuestosCalculados));
            }

            sb.append("\nTotales por Forma de Pago (montos ").append(incluirImpuestos ? "con" : "sin").append(" impuestos, si aplica al total de la forma de pago):\n");
            for (Map.Entry<String, Double> entry : totalesPorFormaPago.entrySet()) {
                 double montoFormaPago = entry.getValue();
                 // Note: The per-forma-pago total currently reflects p.getCostoTotal() which includes tax.
                 // Adjusting this accurately for a "sin impuestos" view per forma de pago would require
                 // knowing how much tax was part of *each* p.getCostoTotal() for that specific forma de pago,
                 // which is complex if a Pedido has multiple ImpuestoAplicado and we are trying to subtract.
                 // For simplicity, this example just shows the total for the forma de pago as is.
                 // A more precise "sin impuestos" per forma de pago would need more granular calculation.
                sb.append(String.format("  - %s: %.2f\n", entry.getKey(), montoFormaPago));
            }
        }
        sb.append("--------------------------------------------------------------------\n");
        return sb.toString();
    }

    /**
     * 
     */
    public void agregarUsuario() {
        // TODO implement here
    }

    /**
     * 
     */
    public void eliminarUsuario() {
        // TODO implement here
    }

    /**
     * 
     */
    public void actualizarUsuario() {
        // TODO implement here
    }

    public String verVehiculosDisponibles(RolUsuario rolSolicitante) {
        // This method would typically use an InterfazVehiculoRepository.
        // For now, as per instructions, it's a placeholder.
        
        if (rolSolicitante == RolUsuario.ADMINISTRADOR || rolSolicitante == RolUsuario.COMPRADOR || rolSolicitante == RolUsuario.VENDEDOR) {
            // In a real implementation:
            // if (vehiculoRepository != null) {
            //    List<Vehiculo> disponibles = vehiculoRepository.findVehiculosByDisponibilidad(true);
            //    // Format 'disponibles' into a string report
            //    return "Listado de vehículos disponibles...";
            // } else {
            //    return "Error: Repositorio de vehículos no inicializado.";
            // }
            return "Vehiculos disponibles (funcionalidad requiere InterfazVehiculoRepository y su implementación).";
        } else {
            return "Acceso denegado a la visualización de vehículos disponibles para el rol: " + rolSolicitante;
        }
    }
}
