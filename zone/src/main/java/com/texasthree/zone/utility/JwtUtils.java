package com.texasthree.zone.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static Key SIGN_KEY = Keys.hmacShaKeyFor("8465fd6e360b4c48b6afa9aa71aae906210c0cd9ef7044a38c4a4f0468aa3210".getBytes());

    public static String create(String id, String issuer, long ttlMillis, Map<String, Object> other) {
        long nowMillis = System.currentTimeMillis();
        var builder = Jwts.builder()
                .setId(id)
                .setIssuedAt(new Date(nowMillis))
                .setIssuer(issuer)
                .signWith(SIGN_KEY);
        if (other != null) {
            builder.addClaims(other);
        }

        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            builder.setExpiration(new Date(expMillis));
        }
        return builder.compact();
    }

    public static Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGN_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
}
