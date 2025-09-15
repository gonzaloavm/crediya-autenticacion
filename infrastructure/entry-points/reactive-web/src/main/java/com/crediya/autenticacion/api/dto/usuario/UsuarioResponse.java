package com.crediya.autenticacion.api.dto.usuario;

public record UsuarioResponse (
    String usuarioExternalId,
    String nombre,
    String apellido,
    String email,
    Double salarioBase
){}
