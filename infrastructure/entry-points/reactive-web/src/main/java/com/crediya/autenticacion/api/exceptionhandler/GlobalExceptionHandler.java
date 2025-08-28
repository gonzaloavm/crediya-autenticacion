package com.crediya.autenticacion.api.exceptionhandler;

import com.crediya.autenticacion.api.UsuarioController;
import com.crediya.autenticacion.api.dto.api.ApiResult;
import com.crediya.autenticacion.model.rol.exceptions.RolInvalidoException;
import com.crediya.autenticacion.model.usuario.exceptions.CampoObligatorioException;
import com.crediya.autenticacion.model.usuario.exceptions.SalarioInvalidoException;
import com.crediya.autenticacion.usecase.registrarnuevosolicitante.exceptions.CorreoDuplicadoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Mono<ResponseEntity<ApiResult<Void>>> handleRolInvalido(HttpMessageNotReadableException ex) {

        // Obtener la causa raíz
        Throwable rootCause = getRootCause(ex);

        log.warn("Error de deserialización de JSON: {}", rootCause.getMessage());

        String mensajeUsuario = "JSON inválido o tipos de datos incorrectos. "
                + "Verifique que los campos enviados coincidan con el formato esperado.";

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResult.<Void>builder()
                        .success(false)
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(mensajeUsuario)
                        .build()
        ));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ApiResult<Void>>> handleInvalidJson(ServerWebInputException ex) {

        // Obtener la causa raíz
        Throwable rootCause = getRootCause(ex);

        log.warn("Error al leer el body de la petición HTTP: {}", rootCause.getMessage());

        String mensajeUsuario = "JSON inválido o tipos de datos incorrectos. "
                + "Verifique que los campos enviados coincidan con el formato esperado. ";

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResult.<Void>builder()
                        .success(false)
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(mensajeUsuario)
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

    @ExceptionHandler(AuthorizationDeniedException.class)
    public Mono<ResponseEntity<ApiResult<Void>>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());

        String mensajeUsuario = "No tienes permisos para realizar esta acción.";

        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResult.<Void>builder()
                        .success(false)
                        .code(HttpStatus.FORBIDDEN.value())
                        .message(mensajeUsuario)
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
