package com.crediya.autenticacion.model.rol.ports;

import com.crediya.autenticacion.model.rol.Rol;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface RolRepositoryPort {
    Mono<Boolean> existePorId(BigInteger id);
    Mono<Rol> buscarPorId(BigInteger id);
}
