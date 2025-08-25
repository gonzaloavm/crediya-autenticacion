package com.crediya.autenticacion.api.exceptionhandler;

import com.crediya.autenticacion.api.UsuarioController;
import com.crediya.autenticacion.api.dto.api.ApiResult;
import com.crediya.autenticacion.model.usuario.exceptions.CampoObligatorioException;
import com.crediya.autenticacion.model.usuario.exceptions.SalarioInvalidoException;
import com.crediya.autenticacion.usecase.registrarnuevosolicitante.exceptions.CorreoDuplicadoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice(assignableTypes = UsuarioController.class)
public class UsuarioExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UsuarioExceptionHandler.class);

    @ExceptionHandler(CampoObligatorioException.class)
    public Mono<ResponseEntity<ApiResult<Void>>> handleCampoObligatorio(CampoObligatorioException ex) {
        log.warn("Error de validación: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResult.<Void>builder()
                        .success(false)
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .build()
        ));
    }

    @ExceptionHandler(SalarioInvalidoException.class)
    public Mono<ResponseEntity<ApiResult<Void>>> handleSalarioInvalido(SalarioInvalidoException ex) {
        log.warn("Error de validación: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResult.<Void>builder()
                        .success(false)
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .build()
        ));
    }

    @ExceptionHandler(CorreoDuplicadoException.class)
    public Mono<ResponseEntity<ApiResult<Void>>> handleCorreoDuplicado(CorreoDuplicadoException ex) {
        log.warn("Error de validación: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResult.<Void>builder()
                        .success(false)
                        .code(HttpStatus.CONFLICT.value())
                        .message(ex.getMessage())
                        .build()
        ));
    }

    // Handler para excepciones genéricas no manejadas
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResult<Void>>> handleGenericException(Exception ex) {
        log.error("Ha ocurrido un error inesperado: ", ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResult.<Void>builder()
                        .success(false)
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Ha ocurrido un error inesperado. Por favor, inténtelo de nuevo más tarde.")
                        .build()
        ));
    }
}
