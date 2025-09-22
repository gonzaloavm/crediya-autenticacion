package com.crediya.autenticacion.exception;

import com.crediya.autenticacion.error.ErrorCode;

// Propósito: cuando una entidad no existe durante la ejecución de lógica de dominio
public class DomainNotFoundException extends DomainException{
    public DomainNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
