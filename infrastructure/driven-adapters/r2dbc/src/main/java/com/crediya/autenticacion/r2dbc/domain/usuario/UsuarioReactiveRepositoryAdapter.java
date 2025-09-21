package com.crediya.autenticacion.r2dbc.domain.usuario;

import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.r2dbc.entity.UsuarioData;
import com.crediya.autenticacion.r2dbc.helper.ReactiveAdapterOperations;
import com.crediya.autenticacion.r2dbc.entity.UsuarioRolData;
import com.crediya.autenticacion.r2dbc.relation.UsuarioRolReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    // Repositorio tecnico (Tabla intermedia)
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
                                    savedUsuario.getUsuarioId(),
                                    rol.getRolId()
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
        log.debug("Iniciando búsqueda de usuario por correo: {}", correo);

        return repository.findByEmail(correo)
                .doOnNext(data -> log.debug("Usuario encontrado en repositorio: {}", data))
                .map(super::toEntity)
                .doOnNext(entity -> log.debug("Transformado a entidad de dominio: {}", entity))
                .flatMap(usuario ->
                        usuarioRolReactiveRepository.findByUsuarioId(usuario.getUsuarioId())
                                .doOnNext(rolData -> log.debug("Rol asociado encontrado: {}", rolData))
                                .map(usuarioRolData -> Rol.builder()
                                        .rolId(usuarioRolData.getRolId())
                                        .build()
                                )
                                .collectList()
                                .doOnNext(roles -> log.debug("Roles recolectados: {}", roles))
                                .map(roles -> {
                                    Usuario enriquecido = usuario.toBuilder().roles(roles).build();
                                    log.debug("Usuario enriquecido con roles: {}", enriquecido);
                                    return enriquecido;
                                })
                );
    }

    @Override
    public Flux<Usuario> buscarPorPublicUsuarioIds(List<byte[]> publicUsuarioIds) {
        return repository.findByPublicUsuarioIdIn(publicUsuarioIds)
                .map(this::toEntity);
    }
}
