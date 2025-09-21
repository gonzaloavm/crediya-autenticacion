package com.crediya.autenticacion.r2dbc.domain.rol;

import com.crediya.autenticacion.r2dbc.entity.RolData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolData, BigInteger>, ReactiveQueryByExampleExecutor<RolData> {
    Mono<RolData> findByPublicRolId(byte[] publicRolId);
    Flux<RolData> findByPublicRolIdIn(List<byte[]> ids);
}
