package com.crediya.autenticacion.ports;

import reactor.core.publisher.Mono;

import java.util.List;

public interface JwtProviderPort {
    Mono<String> generarToken(String subject, List<String> roles);
    Mono<String> getUsernameFromToken(String token);
    Mono<Boolean> validateToken(String token);
    Mono<List<String>> getRolesFromToken(String token);
}
