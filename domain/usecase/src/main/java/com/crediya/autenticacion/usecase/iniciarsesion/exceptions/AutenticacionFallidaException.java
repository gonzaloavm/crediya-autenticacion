package com.crediya.autenticacion.usecase.iniciarsesion.exceptions;

public class AutenticacionFallidaException extends RuntimeException {
    public AutenticacionFallidaException(String correo) {
        super("Falló la autenticación para el correo '" + correo + "'. Verifica tus credenciales.");
    }
}