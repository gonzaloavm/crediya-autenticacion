package com.crediya.autenticacion.model.rol.ports;

import com.crediya.autenticacion.model.rol.Rol;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface RolRepositoryPort {
    Mono<Boolean> existePorPublicId(byte[] publicRolId);
    Mono<Rol> buscarPorId(BigInteger id);
    Flux<Rol> buscarPorPublicRolIdIn(List<byte[]> ids);
}
