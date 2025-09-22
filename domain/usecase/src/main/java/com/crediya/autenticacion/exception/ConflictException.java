package com.crediya.autenticacion.exception;

import com.crediya.autenticacion.error.ErrorCode;

// Propósito: la acción choca con el estado actual (recurso duplicado, versión incorrecta).
public class ConflictException extends UseCaseException{
    public ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
