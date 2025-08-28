package com.crediya.autenticacion.springautentication;

import com.crediya.autenticacion.model.rol.ports.RolRepositoryPort;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomReactiveUserDetailsService.class);

    private final UsuarioRepositoryPort usuarioRepository;
    private final RolRepositoryPort rolRepositoryPort;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return usuarioRepository.buscarPorCorreo(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Usuario no encontrado: " + username)))
                .flatMap(this::mapToUserDetails);
    }

    private Mono<UserDetails> mapToUserDetails(Usuario usuario) {
        log.info("Usuario a mapear {}", usuario);

        return Flux.fromIterable(usuario.getRoles())
                .flatMap(rol -> rolRepositoryPort.buscarPorId(rol.getId())) // buscamos nombre real del rol
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collectList()
                .map(authorities -> {
                    log.info("Authorities cargadas: {}", authorities);
                    return new User(usuario.getEmail(), usuario.getClave(), authorities);
                });
    }
}