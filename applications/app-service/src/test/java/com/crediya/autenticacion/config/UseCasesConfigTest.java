package com.crediya.autenticacion.config;

import com.crediya.autenticacion.model.rol.ports.RolRepositoryPort;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.ports.AutenticationPort;
import com.crediya.autenticacion.ports.JwtProviderPort;
import com.crediya.autenticacion.ports.PasswordEncoderPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }

        @Bean
        public JwtProviderPort jwtProviderPort() { return Mockito.mock(JwtProviderPort.class); }

        @Bean
        public AutenticationPort autenticationPort() { return Mockito.mock(AutenticationPort.class); }

        @Bean
        public UsuarioRepositoryPort usuarioRepositoryPort() { return Mockito.mock(UsuarioRepositoryPort.class); }

        @Bean
        public RolRepositoryPort rolRepositoryPort() { return Mockito.mock(RolRepositoryPort.class); }

        @Bean
        public PasswordEncoderPort passwordEncoderPort() { return Mockito.mock(PasswordEncoderPort.class); }
    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}