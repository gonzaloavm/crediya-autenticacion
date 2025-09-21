package com.crediya.autenticacion.usecase.obtenerusuariosporids;

import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class ObtenerUsuariosPorIdsUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public Flux<Usuario> buscar(List<String> externalIds){
        return usuarioRepositoryPort.buscarPorPublicUsuarioIds(externalIds);
    }

}
