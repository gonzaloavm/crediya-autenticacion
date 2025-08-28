package com.crediya.autenticacion.api.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public record UsuarioRequest(

    @Schema(description = "Nombres del usuario", example = "Gonzalo Aarón", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Los nombres no pueden ser nulos o vacíos")
    String nombre,

    @Schema(description = "Apellidos del usuario", example = "Veramendi Montedoro", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Los apellidos no pueden ser nulos o vacíos")
    String apellido,

    @Schema(description = "Fecha de nacimiento del usuario", example = "1998-04-12")
    LocalDate fechaNacimiento,

    @Schema(description = "Dirección de residencia del usuario", example = "Av. Perú")
    String direccion,

    @Schema(description = "Número de teléfono del usuario", example = "999999999")
    String telefono,

    @Schema(description = "Correo electrónico del usuario", example = "gon.vermont@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El correo electrónico no puede ser nulo o vacío")
    @Email(message = "El formato del correo electrónico es inválido")
    String email,

    @Schema(description = "Clave del usuario", example = "**********", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La clave no puede ser nula o vacía")
    String clave,

    @Schema(description = "Salario base del usuario", example = "2500000.50", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0", maximum = "15000000")
    @NotNull(message = "El salario base no puede ser nulo")
    @Min(value = 0, message = "El salario base no puede ser negativo")
    @Max(value = 15_000_000, message = "El salario base excede el valor máximo permitido")
    Double salarioBase,

    @Schema(description = "Documento de identidad del usuario", example = "123456789")
    String documentoIdentidad,

    @Schema(description = "Lista de IDs de los roles", example = "[1, 2]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "El usuario debe tener al menos un rol asignado")
    List<BigInteger> roles
) {}