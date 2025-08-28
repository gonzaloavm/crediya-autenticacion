package com.crediya.autenticacion.usecase.iniciarsesion.dto;

import java.util.List;

public record UsuarioAutenticado(String email, List<String> roles) {}
