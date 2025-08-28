package com.crediya.autenticacion.r2dbc.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.annotation.Id;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Table("usuarios")
public class UsuarioData {

    @Id
    @Column
    private BigInteger id;
    @Column
    private String nombre;
    @Column
    private String apellido;
    @Column
    private String email;
    @Column
    private String clave;
    @Column("documento_identidad")
    private String documentoIdentidad;
    @Column
    private String telefono;
    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;
    @Column
    private String direccion;
    @Column("salario_base")
    private Double salarioBase;
}
