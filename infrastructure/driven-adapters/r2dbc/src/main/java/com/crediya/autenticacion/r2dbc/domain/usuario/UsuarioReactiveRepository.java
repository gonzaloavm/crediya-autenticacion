package com.crediya.autenticacion.r2dbc.domain.usuario;

import com.crediya.autenticacion.r2dbc.entity.UsuarioData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioData, BigInteger>, ReactiveQueryByExampleExecutor<UsuarioData> {

    Mono<UsuarioData> findByEmail(String email);

    Flux<UsuarioData> findByPublicUsuarioIdIn(List<byte[]> externalIds);
}
