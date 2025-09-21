package com.crediya.autenticacion.r2dbc.domain.rol;

import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.rol.ports.RolRepositoryPort;
import com.crediya.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import com.crediya.autenticacion.r2dbc.entity.RolData;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol,
        RolData,
        BigInteger,
        RolReactiveRepository
        > implements RolRepositoryPort {

    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Rol.class));
    }

    @Override
    public Mono<Boolean> existePorPublicId(byte[] publicRolId) {
        return repository.findByPublicRolId(publicRolId).hasElement();
    }

    @Override
    public Mono<Rol> buscarPorId(BigInteger id) {
        return super.findById(id);
    }

    @Override
    public Flux<Rol> buscarPorPublicRolIdIn(List<byte[]> ids) {
        return repository.findByPublicRolIdIn(ids)
                .map(this::toEntity);
    }
}
