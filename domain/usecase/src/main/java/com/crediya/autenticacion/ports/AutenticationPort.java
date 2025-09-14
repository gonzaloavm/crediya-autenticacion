package com.crediya.autenticacion.ports;

import com.crediya.autenticacion.dto.JwtClaims;
import reactor.core.publisher.Mono;

public interface AutenticationPort {
    Mono<JwtClaims> autenticar(String nombreUsuario, String clave);
}
