package com.crediya.autenticacion.model.usuario.ports;

import com.crediya.autenticacion.model.usuario.Usuario;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface UsuarioRepositoryPort {
    Mono<Void> guardar(Usuario usuario);
    Mono<Boolean> existePorCorreo(String correo);
    Mono<Usuario> buscarPorCorreo(String correo);
    Mono<Void> asignarRol(BigInteger usuarioId, BigInteger rolId);
}
