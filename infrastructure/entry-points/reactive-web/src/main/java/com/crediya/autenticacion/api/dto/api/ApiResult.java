package com.crediya.autenticacion.api.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(name = "ApiResult", description = "Plantilla de respuesta genérica para las APIs")
public class ApiResult<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;
}
