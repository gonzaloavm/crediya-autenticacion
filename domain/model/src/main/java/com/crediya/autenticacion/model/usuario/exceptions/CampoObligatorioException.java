package com.crediya.autenticacion.model.usuario.exceptions;

public class CampoObligatorioException extends RuntimeException {
    public CampoObligatorioException(String campo) {
        super("El campo obligatorio '" + campo + "' no puede ser nulo o vacío");
    }
}
