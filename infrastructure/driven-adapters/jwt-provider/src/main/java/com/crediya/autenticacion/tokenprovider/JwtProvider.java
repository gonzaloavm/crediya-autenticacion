package com.crediya.autenticacion.tokenprovider;

import com.crediya.autenticacion.ports.JwtProviderPort;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtProvider implements JwtProviderPort {

    private final String secretKey = "esta-es-mi-clave-mucho-mas-extensa-porque-el-jwt-me-rebota-cuando-es-muy-corta-wtf";
    private final long expirationMillis = 3600000; // 1 hora

    public Mono<String> generarToken(String subject, List<String> roles) {
        return Mono.fromCallable(() -> {

            Instant ahora = Instant.now();
            Instant expiracion = ahora.plusMillis(expirationMillis);

            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .subject(subject)
                    .claim("roles", roles)
                    .issuedAt(Date.from(ahora))
                    .expiration(Date.from(expiracion))
                    .signWith(key) // ya no se especifica SignatureAlgorithm
                    .compact();

        });
    }

    @Override
    public Mono<String> getUsernameFromToken(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
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

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
