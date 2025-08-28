package com.crediya.autenticacion.security.filter;

import com.crediya.autenticacion.ports.JwtProviderPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtProviderPort jwtProviderPort;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.justOrEmpty(getJwtFromRequest(exchange.getRequest()))
                .flatMap(token -> jwtProviderPort.validateToken(token)
                        .flatMap(isValid -> {
                            if (Boolean.TRUE.equals(isValid)) {
                                return jwtProviderPort.getRolesFromToken(token)
                                        .flatMap(roles -> {
                                            List<SimpleGrantedAuthority> authorities = roles.stream()
                                                    .map(SimpleGrantedAuthority::new)
                                                    .collect(Collectors.toList());

                                            log.info("Authorities extraídos del token: {}", authorities);

                                            var authentication = new UsernamePasswordAuthenticationToken(
                                                    null,
                                                    null,
                                                    authorities
                                            );

                                            return chain.filter(exchange)
                                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                                        });
                            }
                            return chain.filter(exchange);
                        })
                        .onErrorResume(e -> chain.filter(exchange))
                )
                .switchIfEmpty(chain.filter(exchange));
    }


    private String getJwtFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
