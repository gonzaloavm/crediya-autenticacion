package com.crediya.autenticacion.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    REQUIRED_FIELD("1001", "REQUIRED_FIELD", "El campo es obligatorio y no fue proporcionado."),
    INVALID_FORMAT("1002", "INVALID_FORMAT", "El formato del campo no es válido."),
    VALUE_OUT_OF_RANGE("1003", "VALUE_OUT_OF_RANGE", "El valor está fuera del rango permitido."),

    INVALID_CREDENTIALS("2001", "INVALID_CREDENTIALS", "Las credenciales de autenticación son incorrectas."),
    EXPIRED_TOKEN("2002", "EXPIRED_TOKEN", "El token de sesión ha expirado."),

    RESOURCE_NOT_FOUND("3001", "RESOURCE_NOT_FOUND", "El recurso solicitado no fue encontrado."),
    RESOURCE_ALREADY_EXISTS("3002", "RESOURCE_ALREADY_EXISTS", "Ya existe un recurso con los mismos identificadores."),

    SERVICE_ERROR("5001", "SERVICE_ERROR", "Ha ocurrido un error inesperado. ");

    private final String code;
    private final String title;
    private final String defaultMesage;
}
