package com.crediya.autenticacion.model.usuario.gateways;

import com.crediya.autenticacion.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Void> guardar(Usuario usuario);
    Mono<Boolean> existePorCorreo(String correo);
}
