package com.crediya.autenticacion.tokenprovider;

import com.crediya.autenticacion.error.ErrorCode;
import com.crediya.autenticacion.exception.AuthenticationException;
import com.crediya.autenticacion.port.JwtProviderPort;
import com.crediya.autenticacion.dto.JwtClaims;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtProvider implements JwtProviderPort {

    private final String secretKey = "esta-es-mi-clave-mucho-mas-extensa-porque-el-jwt-me-rebota-cuando-es-muy-corta-wtf";
    private final long expirationMillis = 3600000; // 1 hora

    public Mono<String> generarToken(JwtClaims usuarioAutenticado) {
        return Mono.fromCallable(() -> {

            Instant ahora = Instant.now();
            Instant expiracion = ahora.plusMillis(expirationMillis);

            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .subject(usuarioAutenticado.sub())
                    .claim("email", usuarioAutenticado.email())
                    .claim("roles", usuarioAutenticado.roles())
                    .claim("documentoIdentidad", usuarioAutenticado.documentoIdentidad())
                    .issuedAt(Date.from(ahora))
                    .expiration(Date.from(expiracion))
                    .signWith(key) // ya no se especifica SignatureAlgorithm
                    .compact();
        });
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token);
                return true;
            } catch (JwtException | IllegalArgumentException e) {
                return false;
            }
        });
    }

    @Override
    public Mono<List<String>> getRolesFromToken(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Object roles = claims.get("roles");
            if (roles instanceof List<?>) {
                return ((List<?>) roles).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
    }

    @Override
    public Mono<JwtClaims> getClaimsFromToken(String token) {
        return Mono.fromCallable(() -> {
                    Claims claims = Jwts.parser()
                            .verifyWith(getSigningKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    List<String> roles = extractRolesSafely(claims);

                    return new JwtClaims(
                            claims.getSubject(),
                            claims.get("email", String.class),
                            claims.get("documentoIdentidad", String.class),
                            roles,
                            token
                    );
                })
                .subscribeOn(Schedulers.parallel())
                .onErrorMap(e -> new AuthenticationException(
                        ErrorCode.INVALID_CREDENTIALS,
                        "Falló la autenticación. Token inválido o expirado."
                ));
    }

    private static List<String> extractRolesSafely(Claims claims) {
        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?>) {
            return ((List<?>) rolesObject).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        // Si el claim viene como String con CSV
        if (rolesObject instanceof String csv) {
            return Arrays.stream(csv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        // Fallback: no hay roles o formato inesperado
        return Collections.emptyList();
    }


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
