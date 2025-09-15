package com.crediya.autenticacion.r2dbc.domain.rol;

import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.rol.ports.RolRepositoryPort;
import com.crediya.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import com.crediya.autenticacion.r2dbc.entity.RolData;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

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
    public Mono<Boolean> existePorId(BigInteger id) {
        return repository.findById(id).hasElement();
    }

    @Override
    public Mono<Rol> buscarPorId(BigInteger id) {
        return super.findById(id);
    }
}
