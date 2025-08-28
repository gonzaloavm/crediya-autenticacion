package com.crediya.autenticacion.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("usuario_rol")
public class UsuarioRolData {

    @Id
    private BigInteger id;
    @Column("usuario_id")
    private BigInteger usuarioId;
    @Column("rol_id")
    private BigInteger rolId;
}
