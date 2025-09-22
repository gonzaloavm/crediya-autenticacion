package com.crediya.autenticacion.usecase.iniciarsesion;

import com.crediya.autenticacion.error.ErrorCode;
import com.crediya.autenticacion.exception.AuthenticationException;
import com.crediya.autenticacion.port.AutenticationPort;
import com.crediya.autenticacion.port.JwtProviderPort;
import com.crediya.autenticacion.dto.AuthRequest;
import com.crediya.autenticacion.dto.JwtResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class IniciarSesionUseCase {

    private final JwtProviderPort jwtTokenProvider;
    private final AutenticationPort autenticationPort;
    private static final Logger logger = Logger.getLogger(IniciarSesionUseCase.class.getName());

    public Mono<JwtResponse> autenticar(AuthRequest request) {
        return autenticationPort.autenticar(request.nombreUsuario(), request.clave())
                .doOnNext(autenticado -> {
                    logger.fine("Usuario autenticado: " + autenticado.email());
                })
                .flatMap(autenticado ->
                        jwtTokenProvider.generarToken(autenticado)
                                .map(JwtResponse::new)
                )
                .onErrorResume(throwable ->
                        Mono.error(new AuthenticationException(
                                ErrorCode.INVALID_CREDENTIALS,
                                "Falló la autenticación para el correo '" + request.nombreUsuario() + "'. Verifica tus credenciales.")
                        )
                );
    }

}
