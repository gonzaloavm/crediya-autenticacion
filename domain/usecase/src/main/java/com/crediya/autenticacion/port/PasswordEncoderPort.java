package com.crediya.autenticacion.port;

public interface PasswordEncoderPort {
    String encode(String rawPassword);
}