package com.crediya.autenticacion.exception;

import com.crediya.autenticacion.error.ErrorCode;

// Propósito: validaciones específicas de casos de uso (más allá del dominio).
public class ApplicationValidationException extends UseCaseException{
    public ApplicationValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
