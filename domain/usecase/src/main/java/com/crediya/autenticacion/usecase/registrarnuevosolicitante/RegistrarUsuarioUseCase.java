package com.crediya.autenticacion.usecase.registrarnuevosolicitante;

import com.crediya.autenticacion.model.rol.exceptions.RolInvalidoException;
import com.crediya.autenticacion.model.rol.ports.RolRepositoryPort;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.exceptions.CampoObligatorioException;
import com.crediya.autenticacion.model.usuario.exceptions.SalarioInvalidoException;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.ports.PasswordEncoderPort;
import com.crediya.autenticacion.usecase.registrarnuevosolicitante.exceptions.CorreoDuplicadoException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final RolRepositoryPort rolRepositoryPort;
    private final PasswordEncoderPort passwordEncoder;

    public Mono<Void> registrar(Usuario usuario) {
        return Mono.fromCallable(() ->
                        usuario.toBuilder()
                                .clave(passwordEncoder.encode(usuario.getClave()))
                                .build()
                )
                .flatMap(usuarioHasheado ->
                        validarCamposObligatorios(usuarioHasheado)
                                .then(validarSalario(usuarioHasheado))
                                .then(validarCorreo(usuarioHasheado))
                                .then(validarRoles(usuarioHasheado))
                                .then(usuarioRepositoryPort.guardar(usuarioHasheado))
                )
                .subscribeOn(Schedulers.boundedElastic());
    }


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
                .flatMap(rol -> rolRepositoryPort.existePorId(rol.getId())
                        .filter(existe -> !existe)
                        .map(invalido -> rol)
                )
                .collectList()
                .flatMap(rolesInvalidos -> {
                    if (!rolesInvalidos.isEmpty()) {
                        String ids = rolesInvalidos.stream()
                                .map(rol -> String.valueOf(rol.getId()))
                                .collect(Collectors.joining(", "));
                        return Mono.error(new RolInvalidoException("Roles inválidos: " + ids));
                    }
                    return Mono.empty();
                });
    }


}
