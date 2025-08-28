package com.crediya.autenticacion.r2dbc.domain.usuario;

import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.r2dbc.entity.UsuarioData;
import com.crediya.autenticacion.r2dbc.domain.helper.ReactiveAdapterOperations;
import com.crediya.autenticacion.r2dbc.entity.UsuarioRolData;
import com.crediya.autenticacion.r2dbc.relation.UsuarioRolReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Usuario,
        UsuarioData,
        BigInteger,
        UsuarioReactiveRepository
> implements UsuarioRepositoryPort {

    private final UsuarioRolReactiveRepository usuarioRolReactiveRepository;
    private static final Logger log = LoggerFactory.getLogger(UsuarioReactiveRepositoryAdapter.class);

    public UsuarioReactiveRepositoryAdapter(
            UsuarioReactiveRepository repository,
            ObjectMapper mapper,
            UsuarioRolReactiveRepository usuarioRolRepository) {
        super(repository, mapper, d -> mapper.map(d, Usuario.class));
        this.usuarioRolReactiveRepository = usuarioRolRepository;
    }

    @Override
    public Mono<Void> guardar(Usuario usuario) {
        return super.save(usuario)
                .flatMap(savedUsuario -> {

                    List<UsuarioRolData> relaciones = Optional.ofNullable(usuario.getRoles())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(rol -> new UsuarioRolData(
                                    null, // ID autogenerado
                                    savedUsuario.getId(),
                                    rol.getId()
                            ))
                            .toList();

                    return usuarioRolReactiveRepository.saveAll(relaciones).then();
                });
    }

    @Override
    public Mono<Boolean> existePorCorreo(String correo) {
        return repository.findByEmail(correo).hasElement();
    }

    @Override
    public Mono<Usuario> buscarPorCorreo(String correo) {
        return repository.findByEmail(correo)
                .map(super::toEntity)
                .flatMap(usuario ->
                        usuarioRolReactiveRepository.findByUsuarioId(usuario.getId()) // <-- método en tu repo técnico
                                .map(usuarioRolData -> Rol.builder()
                                        .id(usuarioRolData.getRolId())
                                        .build()
                                )
                                .collectList()
                                .map(roles -> usuario.toBuilder().roles(roles).build())
                );
    }


    @Override
    public Mono<Void> asignarRol(BigInteger usuarioId, BigInteger rolId) {
        UsuarioRolData entity = new UsuarioRolData(null, usuarioId, rolId);
        return usuarioRolReactiveRepository.save(entity).then();
    }
}
