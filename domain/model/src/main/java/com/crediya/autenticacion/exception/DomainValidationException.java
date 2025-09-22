package com.crediya.autenticacion.exception;

import com.crediya.autenticacion.error.ErrorCode;

// Propósito: violaciones de reglas de negocio genéricas (campo obligatorio, formato, rangos)
public class DomainValidationException extends DomainException{
    public DomainValidationException(ErrorCode errorCode, String message) { super(errorCode, message); }
}
