package com.crediya.autenticacion.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String correo) {
        super("Falló la autenticación para el correo '" + correo + "'. Verifica tus credenciales.");
    }
}