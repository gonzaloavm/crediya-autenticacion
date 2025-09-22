package com.crediya.autenticacion.exception;

import com.crediya.autenticacion.error.ErrorCode;

// Propósito: reglas de negocio complejas que no encajan en validaciones simples
public class BusinessRuleException extends DomainException{
    public BusinessRuleException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
