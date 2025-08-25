package com.crediya.autenticacion.transactional;

import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.usecase.registrarnuevosolicitante.RegistrarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TransactionalRegistrarUsuario {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final TransactionalOperator transactionalOperator;

    public Mono<Void> registrar(Usuario usuario) {
        Mono<Void> registroChain = registrarUsuarioUseCase.registrar(usuario);
        return transactionalOperator.transactional(registroChain);
    }
}