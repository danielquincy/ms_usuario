package com.microservicio.utils;

import com.microservicio.model.dto.RegistroRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generarToken(RegistroRequest usuario) {

        //Fecha Actual
        Date now = new Date();

        //Fecha de Expiracpion
        Date expiryDate = new Date(now.getTime() + expiration);

        //Llave como algoritmo de encriptación con BASE64
        Key key =  Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts
                .builder()
                .setSubject(usuario.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }
}
