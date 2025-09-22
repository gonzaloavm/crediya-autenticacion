package com.crediya.autenticacion.exception;

import com.crediya.autenticacion.error.ErrorCode;

// Propósito: solicitado por el caso de uso pero no encontrado (reemplaza a UsuarioNoEncontradoException)
public class ResourceNotFoundException extends UseCaseException{
    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
