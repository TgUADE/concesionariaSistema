package com.grupo9.sistemaConcesionaria.exception;

/**
 * Excepción lanzada cuando se intenta registrar un usuario del sistema que ya existe
 */
public class DuplicateUsuarioException extends Exception {

    /**
     * Constructor con mensaje
     * @param message Mensaje de error
     */
    public DuplicateUsuarioException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message Mensaje de error
     * @param cause Causa de la excepción
     */
    public DuplicateUsuarioException(String message, Throwable cause) {
        super(message, cause);
    }
} 