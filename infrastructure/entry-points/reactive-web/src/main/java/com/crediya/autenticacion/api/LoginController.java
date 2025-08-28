package com.crediya.autenticacion.api;

import com.crediya.autenticacion.api.dto.api.ApiResult;
import com.crediya.autenticacion.api.dto.usuario.UsuarioRequest;
import com.crediya.autenticacion.api.mapper.UsuarioMapper;
import com.crediya.autenticacion.transactional.TransactionalRegistrarUsuario;
import com.crediya.autenticacion.usecase.iniciarsesion.IniciarSesionUseCase;
import com.crediya.autenticacion.usecase.iniciarsesion.dto.AuthRequest;
import com.crediya.autenticacion.usecase.iniciarsesion.dto.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    // Se inyecta el caso de uso para iniciar sesión
    private final IniciarSesionUseCase iniciarSesionUseCase;

    @PostMapping
    @Operation(summary = "Generar Token", description = "Generar un Token JWT para autenticacion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa, token JWT generado"),
            @ApiResponse(responseCode = "401", description = "Credenciales de autenticación inválidas")
    })
    public Mono<ResponseEntity<ApiResult<JwtResponse>>> iniciarSesion(@RequestBody AuthRequest authRequest) {
        log.info("Iniciando intento de login para el usuario: {}", authRequest.nombreUsuario());

        return iniciarSesionUseCase.autenticar(authRequest)
                .map(jwtResponse -> {
                    log.info("Autenticación exitosa. Token generado para: {}", authRequest.nombreUsuario());
                    // Devuelve una respuesta con el token JWT en el cuerpo
                    return ResponseEntity.ok(
                            ApiResult.<JwtResponse>builder()
                                    .success(true)
                                    .code(200)
                                    .message("Autenticación exitosa")
                                    .data(jwtResponse)
                                    .build()
                    );
                })
                .onErrorResume(RuntimeException.class, e -> {
                    log.error("Error durante la autenticación: {}", e.getMessage());
                    // Maneja el error de autenticación y devuelve una respuesta 401
                    return Mono.just(ResponseEntity.status(401).body(
                            ApiResult.<JwtResponse>builder()
                                    .success(false)
                                    .code(401)
                                    .message("Credenciales inválidas")
                                    .build()
                    ));
                });
    }
}
