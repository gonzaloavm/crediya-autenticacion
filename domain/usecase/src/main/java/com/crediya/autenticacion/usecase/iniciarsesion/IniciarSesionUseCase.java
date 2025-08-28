package com.crediya.autenticacion.usecase.iniciarsesion;

import com.crediya.autenticacion.ports.AutenticationPort;
import com.crediya.autenticacion.ports.JwtProviderPort;
import com.crediya.autenticacion.usecase.iniciarsesion.dto.AuthRequest;
import com.crediya.autenticacion.usecase.iniciarsesion.dto.JwtResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class IniciarSesionUseCase {

    private final JwtProviderPort jwtTokenProvider;
    private final AutenticationPort autenticationPort;
    private static final Logger log = Logger.getLogger(IniciarSesionUseCase.class.getName());

    public Mono<JwtResponse> autenticar(AuthRequest request) {
        return autenticationPort.autenticar(request.nombreUsuario(), request.clave())
                .doOnNext(autenticado -> {
                    log.info("Usuario autenticado: " + autenticado);
                })
                .flatMap(autenticado ->
                        jwtTokenProvider.generarToken(autenticado.email(), autenticado.roles())
                                .map(JwtResponse::new)
                )
                .onErrorResume(throwable -> {
                    log.warning("Error en autenticación para usuario "
                            + request.nombreUsuario()
                            + ": "
                            + throwable.getMessage());
                    return Mono.error(new RuntimeException("Autenticación fallida", throwable));
                });
    }

}
