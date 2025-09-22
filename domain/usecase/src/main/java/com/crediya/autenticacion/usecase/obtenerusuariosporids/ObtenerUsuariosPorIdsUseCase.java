package com.crediya.autenticacion.usecase.obtenerusuariosporids;

import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.ports.UsuarioRepositoryPort;
import com.crediya.autenticacion.port.UuidProviderPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class ObtenerUsuariosPorIdsUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UuidProviderPort uuidProviderPort;

    public Flux<Usuario> buscar(List<String> externalIds) {
        List<byte[]> publicIds = externalIds.stream()
                .map(uuidProviderPort::fromString)
                .toList();

        return usuarioRepositoryPort.buscarPorPublicUsuarioIds(publicIds);
    }

}
