package com.crediya.autenticacion.model.rol;
import lombok.*;

import java.math.BigInteger;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Rol {
    private BigInteger id;
    private String nombre;
    private String descripcion;
}
