package com.crediya.autenticacion.model.rol.exceptions;

public class RolInvalidoException extends RuntimeException {
    public RolInvalidoException(String campo) {
        super("Rol inválido: no se encontró ningún rol correspondiente al ID ingresado [" + campo + "]");
    }
}