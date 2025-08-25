package com.crediya.autenticacion.usecase.registrarnuevosolicitante;

import com.crediya.autenticacion.model.usuario.Usuario;
import com.crediya.autenticacion.model.usuario.exceptions.CampoObligatorioException;
import com.crediya.autenticacion.model.usuario.exceptions.SalarioInvalidoException;
import com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import com.crediya.autenticacion.usecase.registrarnuevosolicitante.exceptions.CorreoDuplicadoException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private final UsuarioRepository repository;

    public Mono<Void> registrar(Usuario usuario) {
        return validarCamposObligatorios(usuario)
                .then(validarSalario(usuario))
                .then(validarCorreo(usuario))
                .then(repository.guardar(usuario));
    }

    private Mono<Void> validarCamposObligatorios(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            return Mono.error(new CampoObligatorioException("nombre"));
        }
        if (usuario.getApellido() == null || usuario.getApellido().isBlank()) {
            return Mono.error(new CampoObligatorioException("apellido"));
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            return Mono.error(new CampoObligatorioException("email"));
        }
        if (usuario.getSalarioBase() == null) {
            return Mono.error(new CampoObligatorioException("salarioBase"));
        }

        return Mono.empty();
    }

    private Mono<Void> validarSalario(Usuario usuario) {
        if (usuario.getSalarioBase() < 0) {
            return Mono.error(new SalarioInvalidoException("El salario no puede ser negativo."));
        }
        if (usuario.getSalarioBase() > 15_000_000) {
            return Mono.error(new SalarioInvalidoException("El salario excede el límite máximo de $15.000.000."));
        }
        return Mono.empty();
    }

    private Mono<Void> validarCorreo(Usuario usuario) {
        return repository.existePorCorreo(usuario.getEmail())
                .flatMap(existe -> existe
                        ? Mono.error(new CorreoDuplicadoException(usuario.getEmail()))
                        : Mono.empty()
                );
    }

}
