package com.grupo9.sistemaConcesionaria.exception;

/**
 * Excepción lanzada cuando no se encuentra un cliente solicitado
 */
public class ClienteNotFoundException extends Exception {

    /**
     * Constructor con mensaje
     * @param message Mensaje de error
     */
    public ClienteNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje de error
     * @param cause Causa de la excepción
     */
    public ClienteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 