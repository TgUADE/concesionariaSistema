package com.grupo9.sistemaConcesionaria.exception;

/**
 * Excepción lanzada cuando no se encuentra un usuario del sistema solicitado
 */
public class UsuarioNotFoundException extends Exception {

    /**
     * Constructor con mensaje
     * @param message Mensaje de error
     */
    public UsuarioNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje de error
     * @param cause Causa de la excepción
     */
    public UsuarioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 