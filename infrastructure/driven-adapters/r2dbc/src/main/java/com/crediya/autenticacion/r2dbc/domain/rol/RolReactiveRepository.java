package com.crediya.autenticacion.r2dbc.domain.rol;

import com.crediya.autenticacion.r2dbc.entity.RolData;
import com.crediya.autenticacion.r2dbc.entity.UsuarioData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolData, BigInteger>, ReactiveQueryByExampleExecutor<RolData> {
}
