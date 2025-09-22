package com.crediya.autenticacion.port;

import com.crediya.autenticacion.dto.JwtClaims;
import reactor.core.publisher.Mono;

import java.util.List;

public interface JwtProviderPort {
    Mono<String> generarToken(JwtClaims usuarioAutenticado);
    Mono<Boolean> validateToken(String token);
    Mono<List<String>> getRolesFromToken(String token);
    Mono<JwtClaims> getClaimsFromToken(String token);
}
