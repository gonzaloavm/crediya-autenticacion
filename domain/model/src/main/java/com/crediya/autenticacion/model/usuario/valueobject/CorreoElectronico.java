package com.crediya.autenticacion.model.usuario.valueobject;

public final class CorreoElectronico {

    private final String value;

    CorreoElectronico(String value) {
        if (!value.contains("@")) {
            throw new IllegalArgumentException("Correo inválido");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }
}
