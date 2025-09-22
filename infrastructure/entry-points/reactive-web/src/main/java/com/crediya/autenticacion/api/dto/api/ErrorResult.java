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
@Schema(name = "ErrorResult", description = "Plantilla de respuesta de errores de las APIs")
public class ErrorResult {
    private Integer status;
    private String errorCode;
    private String title;
    private String detail;
}
