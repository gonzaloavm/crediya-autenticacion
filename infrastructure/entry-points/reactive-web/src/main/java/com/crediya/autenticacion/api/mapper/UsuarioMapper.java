package com.crediya.autenticacion.api.mapper;

import com.crediya.autenticacion.api.dto.usuario.UsuarioRequest;
import com.crediya.autenticacion.model.rol.Rol;
import com.crediya.autenticacion.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    Usuario toModel(UsuarioRequest usuarioRequest);

    default Rol toRol(Long id) {
        if (id == null) {
            return null;
        }
        return Rol.builder().id(BigInteger.valueOf(id)).build();
    }
}
