package com.crediya.autenticacion.exception;

import com.crediya.autenticacion.error.ErrorCode;

public class ConflictException extends DomainException{
    protected ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
