package com.crediya.autenticacion.api.exceptionhandler;

import com.crediya.autenticacion.api.LoginController;
import com.crediya.autenticacion.api.UsuarioController;
import com.crediya.autenticacion.api.dto.api.ApiResult;
import com.crediya.autenticacion.model.usuario.exceptions.CampoObligatorioException;
import com.crediya.autenticacion.usecase.iniciarsesion.exceptions.AutenticacionFallidaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice(assignableTypes = LoginController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoginExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginExceptionHandler.class);

    @ExceptionHandler(AutenticacionFallidaException.class)
    public Mono<ResponseEntity<ApiResult<Void>>> handleCampoObligatorio(AutenticacionFallidaException ex) {
        log.warn("Error de validación: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResult.<Void>builder()
                        .success(false)
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .build()
        ));
    }
}
