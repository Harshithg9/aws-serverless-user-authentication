package com.harshit.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

public class JWTUtil {

    private static final SecretManagerService service =
            new SecretManagerService();

    private static final String SECRET_KEY =
            service.getSecret("jwt-secret");

    static {
        System.out.println("JWT Secret Length = " + SECRET_KEY.length());
        System.out.println("JWT Secret = " + SECRET_KEY);
    }

    public static String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes()),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getUsername(String token) {

        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}