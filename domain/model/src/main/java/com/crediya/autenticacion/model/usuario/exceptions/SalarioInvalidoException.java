package com.crediya.autenticacion.model.usuario.exceptions;

public class SalarioInvalidoException extends RuntimeException {
    public SalarioInvalidoException(String detalle) {
        super("Salario inválido: " + detalle);
    }
}