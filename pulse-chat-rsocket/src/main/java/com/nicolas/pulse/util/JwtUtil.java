package com.nicolas.pulse.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.BadCredentialsException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    private static final String TYP = "typ";
    private static final String REFRESH = "REFRESH";
    private static final String ACCESS = "ACCESS";

    public static String generateHs256Key() {
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public static SecretKey generateSecretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private static String generateToken(SecretKey secretKey, String uuid, String accountId, Long expirationTimeMills, String type, Map<String, ?> map) {
        Date now = new Date();
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .id(uuid)
                .subject(accountId)
                .issuedAt(now)
                .notBefore(now)
                .issuer("pulse-chat")
                .expiration(new Date(now.getTime() + expirationTimeMills))
                .signWith(secretKey, Jwts.SIG.HS256)
                .claims(Map.of(TYP, type))
                .claims(map)
                .compact();
    }

    public static String generateRefreshToken(SecretKey secretKey, String uuid, String accountId, Long expirationTimeMills) {
        return generateToken(secretKey, uuid, accountId, expirationTimeMills, REFRESH, Map.of());
    }

    public static String generateAccessToken(SecretKey secretKey, String uuid, String accountId, Long expirationTimeMills, Map<String, ?> map) {
        return generateToken(secretKey, uuid, accountId, expirationTimeMills, ACCESS, map);
    }

    public static Claims validateAccessToken(SecretKey secretKey, String token) {
        return validateToken(secretKey, token, ACCESS);
    }

    public static Claims validateRefreshToken(SecretKey secretKey, String token) {
        return validateToken(secretKey, token, REFRESH);
    }

    private static Claims validateToken(SecretKey secretKey, String token, String type) {
        Claims payload = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        if (payload.get(TYP, String.class).equals(type)) {
            throw new BadCredentialsException("Token type '%S' is not allowed here.".formatted(type));
        }
        return payload;
    }
}
