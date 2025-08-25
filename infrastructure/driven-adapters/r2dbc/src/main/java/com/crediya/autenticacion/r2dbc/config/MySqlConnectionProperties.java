package com.crediya.autenticacion.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.r2dbc.mysql")
public record MySqlConnectionProperties(
        String host,
        Integer port,
        String database,
        String username,
        String password
) {}