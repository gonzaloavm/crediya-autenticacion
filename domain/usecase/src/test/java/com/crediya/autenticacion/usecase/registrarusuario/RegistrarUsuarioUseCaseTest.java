package com.crediya.autenticacion.usecase.registrarusuario;

import com.crediya.autenticacion.exception.ConflictException;
import com.crediya.autenticacion.exception.DomainValidationException;
import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.rol.ports.RolRepositoryPort;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.port.PasswordEncoderPort;
import com.crediya.autenticacion.port.UuidProviderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrarUsuarioUseCaseTest {

    private UsuarioRepositoryPort usuarioRepositoryPort;
    private RolRepositoryPort rolRepositoryPort;
    private PasswordEncoderPort passwordEncoder;
    private UuidProviderPort uuidProviderPort;
    private RegistrarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        usuarioRepositoryPort = Mockito.mock(UsuarioRepositoryPort.class);
        rolRepositoryPort = Mockito.mock(RolRepositoryPort.class);
        passwordEncoder = Mockito.mock(PasswordEncoderPort.class);
        uuidProviderPort = Mockito.mock(UuidProviderPort.class);
        useCase = new RegistrarUsuarioUseCase(usuarioRepositoryPort, rolRepositoryPort, passwordEncoder, uuidProviderPort);
    }

    private Usuario buildUsuarioValido() {

        UUID rolUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID usuarioUuid = UUID.fromString("01996aaa-9f11-7ddd-b090-8e55b0238f80");

        byte[] rolId = uuidProviderPort.toBytes(rolUuid);
        byte[] usuarioId = uuidProviderPort.toBytes(usuarioUuid);

        Rol rol = Rol.builder().publicRolId(rolId).nombre("USER").build();
        return Usuario.builder()
                .publicUsuarioId(usuarioId)
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

        UUID rolUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        byte[] rolId = new byte[16];
        when(uuidProviderPort.toBytes(rolUuid)).thenReturn(rolId);

        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");
        when(usuarioRepositoryPort.existePorCorreo(usuario.getEmail())).thenReturn(Mono.just(false));
        when(rolRepositoryPort.existePorPublicId(rolId).thenReturn(Mono.just(true)));
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
                .expectErrorMatches(err -> err instanceof DomainValidationException &&
                        err.getMessage().contains("nombre"))
                .verify();
    }

    @Test
    void fallaPorSalarioNegativo() {
        Usuario usuario = buildUsuarioValido().toBuilder().salarioBase(-100.0).build();

        // mocks mínimos para que llegue a la validación de salario
        when(usuarioRepositoryPort.existePorCorreo(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.registrar(usuario))
                .expectError(DomainValidationException.class)
                .verify();
    }

    @Test
    void fallaPorCorreoDuplicado() {
        Usuario usuario = buildUsuarioValido();

        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");
        when(usuarioRepositoryPort.existePorCorreo(usuario.getEmail())).thenReturn(Mono.just(true));

        // Mock adicional necesario para evitar NPE si Mockito devuelve null
        when(usuarioRepositoryPort.guardar(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.registrar(usuario))
                .expectError(ConflictException.class)
                .verify();

        // Validación adicional (opcional) para asegurarte que no se llamó a guardar()
        verify(usuarioRepositoryPort, never()).guardar(any());
    }

    @Test
    void fallaPorRolInvalido() {

        UUID rolUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        byte[] rolId = new byte[16];

        when(uuidProviderPort.toBytes(rolUuid)).thenReturn(rolId);

        Rol rolInvalido = Rol.builder().publicRolId(rolId).nombre("INVALIDO").build();
        Usuario usuario = buildUsuarioValido().toBuilder().roles(List.of(rolInvalido)).build();

        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");
        when(usuarioRepositoryPort.existePorCorreo(usuario.getEmail())).thenReturn(Mono.just(false));
        when(rolRepositoryPort.existePorPublicId(rolId)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.registrar(usuario))
                .expectError(DomainValidationException.class)
                .verify();
    }
}