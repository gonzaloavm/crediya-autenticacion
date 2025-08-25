package com.crediya.autenticacion.api.mapper;

import com.crediya.autenticacion.api.dto.usuario.UsuarioRequest;
import com.crediya.autenticacion.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "idRol", ignore = true)
    Usuario toModel(UsuarioRequest usuarioRequest);
}
