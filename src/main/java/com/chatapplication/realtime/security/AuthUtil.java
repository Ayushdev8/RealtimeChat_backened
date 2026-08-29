package com.chatapplication.realtime.security;


import com.chatapplication.realtime.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class AuthUtil {
    @Value("${jwt.secretkey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccesToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId",user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*60*2))
                .signWith(getSecretKey())
                .compact();


    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))
                .signWith(getSecretKey())
                .compact();
    }
    public String getEmailFromToken(String token){
        Claims claim = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claim.getSubject();

    }
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (ExpiredJwtException e) {
            return false;

        } catch (JwtException e) {
            return false;
        }
    }
}
