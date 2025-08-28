package com.crediya.autenticacion.r2dbc.relation;

import com.crediya.autenticacion.r2dbc.entity.UsuarioRolData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.math.BigInteger;

public interface UsuarioRolReactiveRepository extends ReactiveCrudRepository<UsuarioRolData, BigInteger> {
    Flux<UsuarioRolData> findByUsuarioId(BigInteger usuarioId);
}