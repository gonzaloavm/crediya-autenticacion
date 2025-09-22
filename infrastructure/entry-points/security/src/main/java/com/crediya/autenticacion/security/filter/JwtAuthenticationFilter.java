package com.crediya.autenticacion.security.filter;

import com.crediya.autenticacion.dto.JwtClaims;
import com.crediya.autenticacion.port.JwtProviderPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtProviderPort jwtProviderPort;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        String path    = exchange.getRequest().getPath().value();
        String token   = getJwtFromRequest(exchange.getRequest());

        // Logueo inicial
        log.debug("JwtAuthenticationFilter – Path: [{}], Method: [{}], Token presente: {}",
                path,
                exchange.getRequest().getMethod(),
                StringUtils.hasText(token));

        // Todo el flujo queda dentro de Mono.defer para evaluar lazy
        return Mono.defer(() -> {
            // Si no hay token, fallback sin contexto, sin más operadores reactivos
            if (!StringUtils.hasText(token)) {
                log.warn("Token no encontrado para path: {}", path);
                return chain.filter(exchange);
            }

            // Token presente, validamos y extraemos claims
            Mono<JwtClaims> claimsMono = jwtProviderPort.validateToken(token)
                    .filter(valid -> valid)                           // sólo si es verdadero
                    .flatMap(valid -> jwtProviderPort.getClaimsFromToken(token));

            // Cacheamos claimsMono para no disparar múltiples subscripciones
            claimsMono = claimsMono.cache();

            // Branching explícito sobre la existencia de claims
            Mono<JwtClaims> finalClaimsMono = claimsMono;
            return claimsMono.hasElement()
                    .flatMap(hasClaims -> {
                        if (hasClaims) {
                            // Hay claims válidos: aplicamos filtro con SecurityContext
                            return finalClaimsMono
                                    .flatMap(claims -> {
                                        List<SimpleGrantedAuthority> authorities = claims.roles().stream()
                                                .map(SimpleGrantedAuthority::new)
                                                .toList();

                                        log.debug("Authorities extraídos del token: {}", authorities);
                                        log.debug("JWT Claims: {}", claims);

                                        UsernamePasswordAuthenticationToken auth =
                                                new UsernamePasswordAuthenticationToken(claims, null, authorities);

                                        log.debug("Autenticación exitosa para path: {}, user: {}",
                                                path, auth.getName());

                                        return chain.filter(exchange)
                                                .contextWrite(
                                                        ReactiveSecurityContextHolder.withAuthentication(auth)
                                                );
                                    });
                        } else {
                            // 4b) Token inválido o validación fallida
                            log.warn("Token inválido para path: {}", path);
                            return chain.filter(exchange);
                        }
                    })
                    // 5) Capturamos cualquier excepción en validación o parsing
                    .onErrorResume(ex -> {
                        log.warn("Error procesando JWT para path {}: {}", path, ex.toString());
                        return chain.filter(exchange);
                    });
        });
    }

    private String getJwtFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

