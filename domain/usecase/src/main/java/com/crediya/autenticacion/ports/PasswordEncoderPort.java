package com.crediya.autenticacion.ports;

public interface PasswordEncoderPort {
    String encode(String rawPassword);
}