package com.nicolas.pulse.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;
import java.util.List;

public class JwtUtil {
    private final long accessTokenMs = 1000L * 60 * 15; // 15 min
    private final Key key = Keys.hmacShaKeyFor("replace-with-512-bit-secret-key-xxxxxxxxxxxxxxxxxxxxxxxxxxxx".getBytes());

    public String generateToken(String subject, List<String> roles) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessTokenMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public static Mono<Claims> validateToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            Claims claims = jws.getBody();
            return Mono.just(claims);
        } catch (JwtException e) {
            return Mono.empty();
        }
    }

}
