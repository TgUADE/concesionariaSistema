package com.grupo9.sistemaConcesionaria.model.state;

import com.grupo9.sistemaConcesionaria.model.Pedido;
import com.grupo9.sistemaConcesionaria.model.EstadoPedido;
import com.grupo9.sistemaConcesionaria.model.HistorialEstado;
import com.grupo9.sistemaConcesionaria.exception.PedidoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

/**
 * Clase abstracta base para implementar el patrón State
 * Proporciona funcionalidad común para todos los estados
 */
public abstract class AbstractEstadoPedido implements EstadoPedidoState {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractEstadoPedido.class);
    
    protected String nombreEstado;
    protected String descripcionEstado;
    protected EstadoPedidoState siguienteEstado;

    /**
     * Constructor base
     * @param nombreEstado Nombre del estado
     * @param descripcionEstado Descripción del estado
     */
    public AbstractEstadoPedido(String nombreEstado, String descripcionEstado) {
        this.nombreEstado = nombreEstado;
        this.descripcionEstado = descripcionEstado;
    }

    @Override
    public final void procesar(Pedido pedido) throws PedidoNotFoundException {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo");
        }

        logger.info("Iniciando procesamiento en estado: {} para pedido: {}", 
                   nombreEstado, pedido.getIdPedido());

        try {
            // Validaciones previas
            validarPrecondiciones(pedido);

            // Procesar estado específico
            procesarEstadoEspecifico(pedido);

            // Actualizar el estado del pedido
            actualizarEstadoPedido(pedido);

            // Validar si puede avanzar al siguiente estado
            if (puedeAvanzar(pedido) && siguienteEstado != null) {
                logger.info("Pedido {} puede avanzar al siguiente estado: {}", 
                           pedido.getIdPedido(), siguienteEstado.getNombreEstado());
                // Nota: El avance real al siguiente estado debe ser manejado externamente
                // por el contexto o servicio que maneja la máquina de estados
            }

            logger.info("Procesamiento completado en estado: {} para pedido: {}", 
                       nombreEstado, pedido.getIdPedido());

        } catch (Exception e) {
            logger.error("Error procesando pedido {} en estado {}: {}", 
                        pedido.getIdPedido(), nombreEstado, e.getMessage());
            throw new PedidoNotFoundException("Error en estado " + nombreEstado + ": " + e.getMessage());
        }
    }

    /**
     * Procesamiento específico del estado - debe ser implementado por cada estado concreto
     * @param pedido El pedido a procesar
     * @throws Exception si hay errores específicos del estado
     */
    protected abstract void procesarEstadoEspecifico(Pedido pedido) throws Exception;

    /**
     * Validaciones previas que debe cumplir el pedido antes del procesamiento
     * @param pedido El pedido a validar
     * @throws IllegalArgumentException si no cumple las precondiciones
     */
    protected void validarPrecondiciones(Pedido pedido) {
        if (pedido.getCliente() == null) {
            throw new IllegalArgumentException("El pedido debe tener un cliente asignado");
        }
        if (pedido.getVehiculo() == null) {
            throw new IllegalArgumentException("El pedido debe tener un vehículo asignado");
        }
    }

    /**
     * Actualiza el estado del pedido usando el método cambiarEstado existente
     * @param pedido El pedido a actualizar
     */
    protected void actualizarEstadoPedido(Pedido pedido) {
        EstadoPedido estadoEnum = mapearAEstadoEnum();
        
        // Usar el método cambiarEstado existente que actualiza estado e historial automáticamente
        pedido.cambiarEstado(estadoEnum, nombreEstado);
        
        logger.debug("Estado del pedido {} actualizado a: {}", 
                    pedido.getIdPedido(), estadoEnum.getDescripcion());
    }

    /**
     * Mapea el estado actual a su enum correspondiente
     * @return El enum del estado
     */
    protected abstract EstadoPedido mapearAEstadoEnum();

    /**
     * Establece el siguiente estado en la secuencia
     * @param siguienteEstado El siguiente estado
     */
    public void setSiguienteEstado(EstadoPedidoState siguienteEstado) {
        this.siguienteEstado = siguienteEstado;
    }

    @Override
    public EstadoPedidoState getSiguienteEstado() {
        return siguienteEstado;
    }

    @Override
    public String getNombreEstado() {
        return nombreEstado;
    }

    @Override
    public String getDescripcionEstado() {
        return descripcionEstado;
    }

    @Override
    public boolean esFinal() {
        return siguienteEstado == null;
    }

    @Override
    public boolean puedeAvanzar(Pedido pedido) {
        // Por defecto, siempre puede avanzar si no hay errores
        // Los estados específicos pueden sobrescribir esta lógica
        return true;
    }

    /**
     * Método utilitario para agregar configuraciones adicionales al pedido
     * @param pedido El pedido
     * @param configuracion La configuración a agregar
     */
    protected void agregarConfiguracion(Pedido pedido, String configuracion) {
        pedido.agregarConfiguracion(configuracion);
        logger.debug("Configuración agregada al pedido {}: {}", 
                    pedido.getIdPedido(), configuracion);
    }

    /**
     * Simula un retraso en el procesamiento (para testing/demo)
     * @param millis Milisegundos de retraso
     */
    protected void simularProcesamiento(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Procesamiento interrumpido en estado: {}", nombreEstado);
        }
    }
} 