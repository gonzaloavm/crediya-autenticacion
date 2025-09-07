package com.crediya.autenticacion.usecase.registrarusuario.exceptions;

public class CorreoDuplicadoException extends RuntimeException {
    public CorreoDuplicadoException(String correo) {
        super("El correo electrónico '" + correo + "' ya se encuentra registrado");
    }
}
