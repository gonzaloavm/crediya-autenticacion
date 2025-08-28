package com.crediya.autenticacion.model.usuario;

import com.crediya.autenticacion.model.rol.Rol;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Usuario {

    private BigInteger id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String email;
    private String clave;
    private String documentoIdentidad;
    private Double salarioBase;
    private List<Rol> roles;

}
