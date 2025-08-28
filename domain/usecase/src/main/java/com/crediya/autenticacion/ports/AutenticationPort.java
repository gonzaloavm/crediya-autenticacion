package com.crediya.autenticacion.ports;

import com.crediya.autenticacion.usecase.iniciarsesion.dto.UsuarioAutenticado;
import reactor.core.publisher.Mono;

public interface AutenticationPort {
    Mono<UsuarioAutenticado> autenticar(String nombreUsuario, String clave);
}
