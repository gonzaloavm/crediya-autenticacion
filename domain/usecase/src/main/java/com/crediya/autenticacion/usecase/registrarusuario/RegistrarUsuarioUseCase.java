package com.crediya.autenticacion.usecase.registrarusuario;

import com.crediya.autenticacion.error.ErrorCode;
import com.crediya.autenticacion.exception.ConflictException;
import com.crediya.autenticacion.exception.DomainNotFoundException;
import com.crediya.autenticacion.exception.DomainValidationException;
import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.rol.ports.RolRepositoryPort;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.port.PasswordEncoderPort;
import com.crediya.autenticacion.port.UuidProviderPort;
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
            return Mono.error(new DomainValidationException(ErrorCode.REQUIRED_FIELD, "El campo obligatorio 'nombre' no puede ser nulo o vacío"));
        }
        if (usuario.getApellido() == null || usuario.getApellido().isBlank()) {
            return Mono.error(new DomainValidationException(ErrorCode.REQUIRED_FIELD, "El campo obligatorio 'apellido' no puede ser nulo o vacío"));
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            return Mono.error(new DomainValidationException(ErrorCode.REQUIRED_FIELD, "El campo obligatorio 'email' no puede ser nulo o vacío"));
        }
        if (usuario.getClave() == null || usuario.getClave().isBlank()) {
            return Mono.error(new DomainValidationException(ErrorCode.REQUIRED_FIELD, "El campo obligatorio 'clave' no puede ser nulo o vacío"));
        }

        return Mono.empty();
    }

    private Mono<Void> validarSalario(Usuario usuario) {
        return Mono.justOrEmpty(usuario.getSalarioBase())
                .switchIfEmpty(Mono.error(new DomainValidationException(ErrorCode.REQUIRED_FIELD, "El campo obligatorio 'salarioBase' no puede ser nulo o vacío")))
                .flatMap(salario -> {
                    if (salario < 0) {
                        return Mono.error(new DomainValidationException(ErrorCode.VALUE_OUT_OF_RANGE, "El salario no puede ser negativo."));
                    }
                    if (salario > 15_000_000) {
                        return Mono.error(new DomainValidationException(ErrorCode.VALUE_OUT_OF_RANGE, "El salario excede el límite máximo de 15,000,000."));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validarCorreo(Usuario usuario) {
        return usuarioRepositoryPort.existePorCorreo(usuario.getEmail())
                .flatMap(existe -> existe
                        ? Mono.error(new ConflictException(ErrorCode.RESOURCE_ALREADY_EXISTS, "El correo electrónico '" + usuario.getEmail() + "' ya se encuentra registrado"))
                        : Mono.empty()
                );
    }

    private Mono<Void> validarRoles(Usuario usuario) {
        return Mono.justOrEmpty(usuario.getRoles())
                .switchIfEmpty(Mono.error(new DomainValidationException(ErrorCode.INVALID_FORMAT, "El campo obligatorio 'roles' no puede ser nulo o vacío")))
                .flatMapMany(roles -> {
                    if (roles.isEmpty()) {
                        return Flux.error(new DomainValidationException(ErrorCode.INVALID_FORMAT, "El campo obligatorio 'roles' no puede ser nulo o vacío"));
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
                                .map(rol -> uuidProviderPort.toString(rol.getPublicRolId()))
                                .collect(Collectors.joining(", "));
                        return Mono.error(new DomainNotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "No se encontraron roles correspondientes a: " + ids));
                    }
                    return Mono.empty();
                });
    }

    //endregion
}
