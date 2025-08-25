package com.crediya.autenticacion.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;

@Configuration
public class MySqlConnectionPool {

    public static final int INITIAL_SIZE = 10;
    public static final int MAX_SIZE = 20;
    public static final int MAX_IDLE_TIME = 30;

    @Bean
    public ConnectionFactory connectionFactory(MySqlConnectionProperties properties) {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, properties.host())
                .option(PORT, properties.port())
                .option(USER, properties.username())
                .option(PASSWORD, properties.password())
                .option(DATABASE, properties.database())
                .build();

        ConnectionFactory connectionFactory = ConnectionFactories.get(options);

        ConnectionPoolConfiguration config = ConnectionPoolConfiguration.builder(connectionFactory)
                .initialSize(INITIAL_SIZE)
                .maxSize(MAX_SIZE)
                .maxIdleTime(Duration.ofSeconds(MAX_IDLE_TIME))
                .build();

        return new ConnectionPool(config);
    }

    // Opcional
    // @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory factory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(factory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        return initializer;
    }

}
