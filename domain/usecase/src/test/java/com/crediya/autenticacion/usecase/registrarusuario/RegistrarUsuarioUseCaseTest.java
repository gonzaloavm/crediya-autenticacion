package com.crediya.autenticacion.usecase.registrarusuario;

import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.rol.exceptions.RolInvalidoException;
import com.crediya.autenticacion.model.rol.ports.RolRepositoryPort;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.exceptions.CampoObligatorioException;
import com.crediya.autenticacion.model.usuario.exceptions.SalarioInvalidoException;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.ports.PasswordEncoderPort;
import com.crediya.autenticacion.usecase.registrarusuario.exceptions.CorreoDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrarUsuarioUseCaseTest {

    private UsuarioRepositoryPort usuarioRepositoryPort;
    private RolRepositoryPort rolRepositoryPort;
    private PasswordEncoderPort passwordEncoder;
    private RegistrarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        usuarioRepositoryPort = Mockito.mock(UsuarioRepositoryPort.class);
        rolRepositoryPort = Mockito.mock(RolRepositoryPort.class);
        passwordEncoder = Mockito.mock(PasswordEncoderPort.class);
        useCase = new RegistrarUsuarioUseCase(usuarioRepositoryPort, rolRepositoryPort, passwordEncoder);
    }

    private Usuario buildUsuarioValido() {
        Rol rol = Rol.builder().id(BigInteger.ONE).nombre("USER").build();
        return Usuario.builder()
                .id(BigInteger.ONE)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .clave("1234")
                .salarioBase(2000.0)
                .roles(List.of(rol))
                .build();
    }

    @Test
    void registrarUsuarioExitosamente() {
        Usuario usuario = buildUsuarioValido();

        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");
        when(usuarioRepositoryPort.existePorCorreo(usuario.getEmail())).thenReturn(Mono.just(false));
        when(rolRepositoryPort.existePorId(BigInteger.ONE)).thenReturn(Mono.just(true));
        when(usuarioRepositoryPort.guardar(any(Usuario.class))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.registrar(usuario))
                .verifyComplete();

        verify(usuarioRepositoryPort).guardar(any(Usuario.class));
    }

    @Test
    void fallaPorNombreObligatorio() {
        Usuario usuario = buildUsuarioValido().toBuilder().nombre(null).build();

        // mocks mínimos para que llegue a la validación de campos
        when(usuarioRepositoryPort.existePorCorreo(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.registrar(usuario))
                .expectErrorMatches(err -> err instanceof CampoObligatorioException &&
                        err.getMessage().contains("nombre"))
                .verify();
    }

    @Test
    void fallaPorSalarioNegativo() {
        Usuario usuario = buildUsuarioValido().toBuilder().salarioBase(-100.0).build();

        // mocks mínimos para que llegue a la validación de salario
        when(usuarioRepositoryPort.existePorCorreo(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.registrar(usuario))
                .expectError(SalarioInvalidoException.class)
                .verify();
    }

    @Test
    void fallaPorCorreoDuplicado() {
        Usuario usuario = buildUsuarioValido();

        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");
        when(usuarioRepositoryPort.existePorCorreo(usuario.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.registrar(usuario))
                .expectError(CorreoDuplicadoException.class)
                .verify();
    }

    @Test
    void fallaPorRolInvalido() {
        Rol rolInvalido = Rol.builder().id(BigInteger.TEN).nombre("INVALIDO").build();
        Usuario usuario = buildUsuarioValido().toBuilder().roles(List.of(rolInvalido)).build();

        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");
        when(usuarioRepositoryPort.existePorCorreo(usuario.getEmail())).thenReturn(Mono.just(false));
        when(rolRepositoryPort.existePorId(BigInteger.TEN)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.registrar(usuario))
                .expectError(RolInvalidoException.class)
                .verify();
    }
}