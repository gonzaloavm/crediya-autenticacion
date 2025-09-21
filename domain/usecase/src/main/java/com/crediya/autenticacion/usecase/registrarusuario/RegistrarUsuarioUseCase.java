package com.crediya.autenticacion.usecase.registrarusuario;

import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.rol.exceptions.RolInvalidoException;
import com.crediya.autenticacion.model.rol.ports.RolRepositoryPort;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.exceptions.CampoObligatorioException;
import com.crediya.autenticacion.model.usuario.exceptions.SalarioInvalidoException;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.ports.PasswordEncoderPort;
import com.crediya.autenticacion.ports.UuidProviderPort;
import com.crediya.autenticacion.usecase.registrarusuario.exceptions.CorreoDuplicadoException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RolRepositoryPort rolRepositoryPort;
    private final PasswordEncoderPort passwordEncoder;
    private final UuidProviderPort uuidProviderPort;

    public Mono<Void> registrar(Usuario usuario) {
        return validarCamposObligatorios(usuario)
                .then(validarSalario(usuario))
                .then(validarCorreo(usuario))
                .then(validarRoles(usuario))
                .then(prepararUsuario(usuario))
                .flatMap(usuarioRepositoryPort::guardar)
                .subscribeOn(Schedulers.boundedElastic());
    }

    //region PREPARACION DE USUARIO
    private Mono<Usuario> prepararUsuario(Usuario usuario) {
        List<byte[]> rolIds = Optional.ofNullable(usuario.getRoles())
                .orElse(List.of())
                .stream()
                .map(Rol::getPublicRolId)
                .toList();

        return rolRepositoryPort.buscarPorPublicRolIdIn(rolIds)
                .collectList()
                .map(rolesValidos ->
                        usuario.toBuilder()
                                .clave(codificarClave(usuario.getClave()))
                                .publicUsuarioId(uuidProviderPort.toBytes(uuidProviderPort.generate()))
                                .roles(rolesValidos)
                                .build()
                );
    }

    private String codificarClave(String clave) {
        return passwordEncoder.encode(clave);
    }

    //endregion

    //region VALIDACIONES

    private Mono<Void> validarCamposObligatorios(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            return Mono.error(new CampoObligatorioException("nombre"));
        }
        if (usuario.getApellido() == null || usuario.getApellido().isBlank()) {
            return Mono.error(new CampoObligatorioException("apellido"));
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            return Mono.error(new CampoObligatorioException("email"));
        }
        if (usuario.getClave() == null || usuario.getClave().isBlank()) {
            return Mono.error(new CampoObligatorioException("clave"));
        }

        return Mono.empty();
    }

    private Mono<Void> validarSalario(Usuario usuario) {
        return Mono.justOrEmpty(usuario.getSalarioBase())
                .switchIfEmpty(Mono.error(new CampoObligatorioException("salarioBase")))
                .flatMap(salario -> {
                    if (salario < 0) {
                        return Mono.error(new SalarioInvalidoException("El salario no puede ser negativo."));
                    }
                    if (salario > 15_000_000) {
                        return Mono.error(new SalarioInvalidoException("El salario excede el límite máximo."));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validarCorreo(Usuario usuario) {
        return usuarioRepositoryPort.existePorCorreo(usuario.getEmail())
                .flatMap(existe -> existe
                        ? Mono.error(new CorreoDuplicadoException(usuario.getEmail()))
                        : Mono.empty()
                );
    }

    private Mono<Void> validarRoles(Usuario usuario) {
        return Mono.justOrEmpty(usuario.getRoles())
                .switchIfEmpty(Mono.error(new CampoObligatorioException("roles")))
                .flatMapMany(roles -> {
                    if (roles.isEmpty()) {
                        return Flux.error(new CampoObligatorioException("roles"));
                    }
                    return Flux.fromIterable(roles);
                })
                .flatMap(rol -> rolRepositoryPort.existePorPublicId(rol.getPublicRolId())
                        .filter(existe -> !existe)
                        .map(invalido -> rol)
                )
                .collectList()
                .flatMap(rolesInvalidos -> {
                    if (!rolesInvalidos.isEmpty()) {
                        String ids = rolesInvalidos.stream()
                                .map(rol -> String.valueOf(rol.getRolId()))
                                .collect(Collectors.joining(", "));
                        return Mono.error(new RolInvalidoException("Roles inválidos: " + ids));
                    }
                    return Mono.empty();
                });
    }

    //endregion
}
