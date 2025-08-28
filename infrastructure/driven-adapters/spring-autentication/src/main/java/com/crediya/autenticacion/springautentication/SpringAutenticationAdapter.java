package com.crediya.autenticacion.springautentication;

import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.exceptions.UsuarioNoEncontradoException;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.ports.AutenticationPort;
import com.crediya.autenticacion.usecase.iniciarsesion.dto.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SpringAutenticationAdapter implements AutenticationPort {

    private final ReactiveAuthenticationManager authenticationManager;

    @Override
    public Mono<UsuarioAutenticado> autenticar(String nombreUsuario, String clave) {
        var authToken = new UsernamePasswordAuthenticationToken(nombreUsuario, clave);

        return authenticationManager.authenticate(authToken)
                .map(Authentication::getPrincipal)
                .cast(UserDetails.class)
                .map(userDetails -> {
                    List<String> roles = userDetails.getAuthorities().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList());

                    return new UsuarioAutenticado(userDetails.getUsername(), roles);
                });
    }
}
