package com.crediya.autenticacion.springautentication;

import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.rol.ports.RolRepositoryPort;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.port.AutenticationPort;
import com.crediya.autenticacion.dto.JwtClaims;
import com.crediya.autenticacion.port.UuidProviderPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomAuthAdapter implements AutenticationPort {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthAdapter.class);

    private final UsuarioRepositoryPort usuarioRepository;
    private final RolRepositoryPort rolRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final UuidProviderPort uuidProviderPort;

    public Mono<JwtClaims> autenticar(String correo, String clave) {
        return usuarioRepository.buscarPorCorreo(correo)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado: " + correo)))
                .flatMap(usuario -> validarCredenciales(usuario, clave));
    }

    private Mono<JwtClaims> validarCredenciales(Usuario usuario, String clave) {
        if (!passwordEncoder.matches(clave, usuario.getClave())) {
            return Mono.error(new RuntimeException("Credenciales inválidas"));
        }

        log.debug("Usuario autenticado {}", usuario.getEmail());

        return Flux.fromIterable(usuario.getRoles())
                .flatMap(rol -> rolRepositoryPort.buscarPorId(rol.getRolId()))
                .map(Rol::getNombre)
                .collectList()
                .map(roles -> {
                    log.debug("Roles cargados: {}", roles);
                    return new JwtClaims(
                            uuidProviderPort.toString(usuario.getPublicUsuarioId()),
                            usuario.getEmail(),
                            usuario.getDocumentoIdentidad(),
                            roles,
                            null);
                });
    }
}
