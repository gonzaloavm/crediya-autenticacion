package com.crediya.autenticacion.api;

import com.crediya.autenticacion.api.dto.api.ApiResult;
import com.crediya.autenticacion.api.dto.usuario.UsuarioRequest;
import com.crediya.autenticacion.api.mapper.UsuarioMapper;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.transactional.TransactionalRegistrarUsuario;
import com.crediya.autenticacion.usecase.registrarusuario.RegistrarUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final TransactionalRegistrarUsuario transactionalRegistrarUsuario;
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final UsuarioMapper usuarioMapper;

    @PostMapping
    @Operation(summary = "Registrar un nuevo usuario", description = "Registra un usuario con sus datos personales y verifica la unicidad del correo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. campos obligatorios vacíos o salario fuera de rango)"),
            @ApiResponse(responseCode = "409", description = "El correo electrónico ya está registrado por otro usuario")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ASESOR')")
    public Mono<ResponseEntity<ApiResult<Void>>> registrarUsuario(@RequestBody UsuarioRequest usuarioRequest) {
        log.info("Iniciando registro de usuario: {}", usuarioRequest.email());

        Usuario usuario = usuarioMapper.toModel(usuarioRequest);

        return transactionalRegistrarUsuario.registrar(usuario)
                .doOnSuccess(v -> log.info("Usuario registrado exitosamente: {}", usuarioRequest.email()))
                .thenReturn(ResponseEntity.status(201).body(
                        ApiResult.<Void>builder()
                                .success(true)
                                .code(201)
                                .message("Usuario registrado con éxito")
                                .build()
                ));
    }

    @GetMapping
    @Operation(summary = "Recuperar un usuario", description = "Recuperar un usuario por identificador externo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario recuperado"),
            @ApiResponse(responseCode = "400", description = "No se encontró el usuario"),
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ASESOR')")
    public Mono<ResponseEntity<ApiResult<Void>>> recuperarUsuario(@RequestBody UsuarioRequest usuarioRequest) {
        log.info("Iniciando registro de usuario: {}", usuarioRequest.email());

        Usuario usuario = usuarioMapper.toModel(usuarioRequest);

        return registrarUsuarioUseCase.registrar(usuario)
                .doOnSuccess(v -> log.info("Usuario recuperado exitosamente: {}", usuarioRequest.email()))
                .thenReturn(ResponseEntity.status(201).body(
                        ApiResult.<Void>builder()
                                .success(true)
                                .code(201)
                                .message("Usuario registrado con éxito")
                                .build()
                ));
    }
}
