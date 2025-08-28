package com.crediya.autenticacion.model.usuario.exceptions;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String detalle) {
        super("No se encontró el usuario en base a las credenciales registradas");
    }
}
