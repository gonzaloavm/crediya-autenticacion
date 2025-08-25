package com.crediya.autenticacion.r2dbc.entity;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("usuario")
public class UsuarioEntity {

    @Id
    private BigInteger id;
    @Column
    private String nombre;
    @Column
    private String apellido;
    @Column
    private String email;
    @Column("documento_identidad")
    private String documentoIdentidad;
    @Column
    private String telefono;
    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column
    private String direccion;
    @Column("id_rol")
    private Long idRol;
    @Column("salario_base")
    private Double salarioBase;
}
