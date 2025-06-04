package com.grupo9.sistemaConcesionaria.model.state;

import com.grupo9.sistemaConcesionaria.model.Pedido;
import com.grupo9.sistemaConcesionaria.exception.PedidoNotFoundException;

/**
 * Patrón State - Interfaz base para los estados del proceso de pedidos
 * Define las operaciones que cada estado debe implementar
 */
public interface EstadoPedidoState {

    /**
     * Procesa el pedido en el estado actual
     * @param pedido El pedido a procesar
     * @throws PedidoNotFoundException si hay errores en el procesamiento
     */
    void procesar(Pedido pedido) throws PedidoNotFoundException;

    /**
     * Valida si el pedido puede pasar al siguiente estado
     * @param pedido El pedido a validar
     * @return true si puede continuar al siguiente estado
     */
    boolean puedeAvanzar(Pedido pedido);

    /**
     * Obtiene el siguiente estado en el flujo
     * @return El siguiente estado o null si es el último
     */
    EstadoPedidoState getSiguienteEstado();

    /**
     * Obtiene el nombre del estado actual
     * @return Nombre del estado
     */
    String getNombreEstado();

    /**
     * Obtiene una descripción del estado actual
     * @return Descripción del estado
     */
    String getDescripcionEstado();

    /**
     * Verifica si es un estado final
     * @return true si es un estado final
     */
    boolean esFinal();
} 