package com.crediya.autenticacion.api.mapper;

import com.crediya.autenticacion.api.dto.usuario.UsuarioRequest;
import com.crediya.autenticacion.api.dto.usuario.UsuarioResponse;
import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    //region UsuarioRequest

    @Mapping(target = "id", ignore = true)
    Usuario toModel(UsuarioRequest usuarioRequest);

    //endregion

    //region UsuarioResponse

    @Mapping(target = "usuarioExternalId", source = "usuarioExternalId")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "apellido", source = "apellido")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "salarioBase", source = "salarioBase")
    UsuarioResponse toDto(Usuario usuario);

    //endregion

    default Rol toRol(Long id) {
        if (id == null) {
            return null;
        }
        return Rol.builder().id(BigInteger.valueOf(id)).build();
    }
}
