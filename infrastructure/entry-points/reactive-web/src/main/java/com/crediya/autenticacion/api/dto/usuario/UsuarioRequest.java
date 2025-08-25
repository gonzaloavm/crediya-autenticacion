package com.crediya.autenticacion.api.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UsuarioRequest(

    @Schema(description = "Nombres del usuario", example = "Juan Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Los nombres no pueden ser nulos o vacíos")
    String nombre,

    @Schema(description = "Apellidos del usuario", example = "Pérez Gómez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Los apellidos no pueden ser nulos o vacíos")
    String apellido,

    @Schema(description = "Fecha de nacimiento del usuario", example = "1990-05-15")
    LocalDate fechaNacimiento,

    @Schema(description = "Dirección de residencia del usuario", example = "Av. Principal 123")
    String direccion,

    @Schema(description = "Número de teléfono del usuario", example = "987654321")
    String telefono,

    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El correo electrónico no puede ser nulo o vacío")
    @Email(message = "El formato del correo electrónico es inválido")
    String email,

    @Schema(description = "Salario base del usuario", example = "2500000.50", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0", maximum = "15000000")
    @NotNull(message = "El salario base no puede ser nulo")
    @Min(value = 0, message = "El salario base no puede ser negativo")
    @Max(value = 15_000_000, message = "El salario base excede el valor máximo permitido")
    Double salarioBase,

    @Schema(description = "Documento de identidad del usuario", example = "123456789")
    String documentoIdentidad
) {}