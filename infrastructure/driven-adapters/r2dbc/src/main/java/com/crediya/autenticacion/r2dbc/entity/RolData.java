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
@Table("roles")
public class RolData {

    @Id
    @Column("rol_id")
    private BigInteger rolId;
    @Column("public_rol_id")
    private byte[] publicRolId;
    @Column
    private String nombre;
    @Column
    private String descripcion;
}
