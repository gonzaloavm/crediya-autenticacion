package com.crediya.autenticacion.r2dbc;

import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import com.crediya.autenticacion.r2dbc.entity.UsuarioEntity;
import com.crediya.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public class UsuarioReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Usuario,
        UsuarioEntity,
        BigInteger,
        UsuarioReactiveRepository
> implements UsuarioRepository {
    public UsuarioReactiveRepositoryAdapter(UsuarioReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.mapBuilder(d, Usuario.class));
    }

    @Override
    public Mono<Void> guardar(Usuario usuario) {
        return super.save(usuario).then();
    }

    @Override
    public Mono<Boolean> existePorCorreo(String email) {
        return repository.findByEmail(email).hasElement();
    }
}
