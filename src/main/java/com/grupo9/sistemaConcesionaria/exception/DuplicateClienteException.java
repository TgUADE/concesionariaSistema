package com.grupo9.sistemaConcesionaria.exception;

/**
 * Excepción lanzada cuando se intenta registrar un cliente que ya existe
 */
public class DuplicateClienteException extends Exception {

    /**
     * Constructor con mensaje
     * @param message Mensaje de error
     */
    public DuplicateClienteException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje de error
     * @param cause Causa de la excepción
     */
    public DuplicateClienteException(String message, Throwable cause) {
        super(message, cause);
    }
} 