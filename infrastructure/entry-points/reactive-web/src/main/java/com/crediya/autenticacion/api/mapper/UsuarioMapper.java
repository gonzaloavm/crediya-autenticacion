package com.crediya.autenticacion.api.mapper;

import com.crediya.autenticacion.api.dto.usuario.UsuarioRequest;
import com.crediya.autenticacion.api.dto.usuario.UsuarioResponse;
import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.port.UuidProviderPort;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UsuarioMapper {

    @Autowired
    protected UuidProviderPort uuidProvider;

    //region UsuarioRequest

    @Mapping(target = "usuarioId", ignore = true)
    public abstract Usuario toModel(UsuarioRequest usuarioRequest);

    //endregion

    //region UsuarioResponse

    @Mapping(target = "usuarioId", source = "publicUsuarioId")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "apellido", source = "apellido")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "salarioBase", source = "salarioBase")
    public abstract UsuarioResponse toDto(Usuario usuario);

    //endregion

    protected String map(byte[] publicUsuarioId) {
        return uuidProvider.toString(publicUsuarioId);
    }

    protected List<Rol> map(List<String> roleIds) {
        if (roleIds == null) return List.of();

        return roleIds.stream()
                .map(id -> Rol.builder()
                        .publicRolId(uuidProvider.fromString(id))
                        .build())
                .toList();
    }
}
