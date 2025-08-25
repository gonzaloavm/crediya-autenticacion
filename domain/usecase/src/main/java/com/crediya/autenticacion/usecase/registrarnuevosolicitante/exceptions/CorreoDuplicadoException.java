package com.crediya.autenticacion.usecase.registrarnuevosolicitante.exceptions;

public class CorreoDuplicadoException extends RuntimeException {
    public CorreoDuplicadoException(String correo) {
        super("El correo electrónico '" + correo + "' ya se encuentra registrado");
    }
}
