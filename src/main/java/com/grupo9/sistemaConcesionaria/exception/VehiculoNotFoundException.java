package com.grupo9.sistemaConcesionaria.exception;

/**
 * Excepción lanzada cuando no se encuentra un vehículo solicitado
 */
public class VehiculoNotFoundException extends Exception {

    /**
     * Constructor con mensaje
     * @param message Mensaje de error
     */
    public VehiculoNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje de error
     * @param cause Causa de la excepción
     */
    public VehiculoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 