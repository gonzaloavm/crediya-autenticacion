package com.crediya.autenticacion.api.exceptionhandler;

import com.crediya.autenticacion.api.dto.api.ApiResult;
import com.crediya.autenticacion.api.dto.api.ErrorResult;
import com.crediya.autenticacion.exception.DomainException;
import com.crediya.autenticacion.exception.UseCaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ErrorResult>> handleDomainException(DomainException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResult.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errorCode(ex.getErrorCode().getCode())
                        .title(ex.getErrorCode().getTitle())
                        .detail(ex.getMessage())
                        .build()
        ));
    }

    @ExceptionHandler(UseCaseException.class)
    public Mono<ResponseEntity<ErrorResult>> handleUsecaseException(UseCaseException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResult.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errorCode(ex.getErrorCode().getCode())
                        .title(ex.getErrorCode().getTitle())
                        .detail(ex.getMessage())
                        .build()
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Mono<ResponseEntity<ErrorResult>> handleRolInvalido(HttpMessageNotReadableException ex) {

        // Obtener la causa raíz
        Throwable rootCause = getRootCause(ex);

        log.warn("Error de deserialización de JSON: {}", rootCause.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResult.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errorCode(String.valueOf((HttpStatus.BAD_REQUEST.value())))
                        .title("SERVER_ERROR")
                        .detail("JSON inválido o tipos de datos incorrectos. Verifique que los campos enviados coincidan con el formato esperado.")
                        .build()
        ));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorResult>> handleInvalidJson(ServerWebInputException ex) {

        // Obtener la causa raíz
        Throwable rootCause = getRootCause(ex);

        log.warn("Error al leer el body de la petición HTTP: {}", rootCause.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResult.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errorCode(String.valueOf((HttpStatus.BAD_REQUEST.value())))
                        .title("SERVER_ERROR")
                        .detail("JSON inválido o tipos de datos incorrectos. Verifique que los campos enviados coincidan con el formato esperado. ")
                        .build()
        ));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public Mono<ResponseEntity<ErrorResult>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResult.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .errorCode(String.valueOf((HttpStatus.FORBIDDEN.value())))
                        .title("SERVER_ERROR")
                        .detail("No tienes permisos para realizar esta acción.")
                        .build()
        ));
    }

    // Handler para excepciones genéricas no manejadas
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResult>> handleGenericException(Exception ex) {
        log.error("Ha ocurrido un error inesperado: ", ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResult.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .errorCode(String.valueOf((HttpStatus.INTERNAL_SERVER_ERROR.value())))
                        .title("SERVER_ERROR")
                        .detail("Ha ocurrido un error inesperado. Por favor, inténtelo de nuevo más tarde.")
                        .build()
        ));
    }


    // Recuperar causa raiz de la excepcion
    private Throwable getRootCause(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }
}
