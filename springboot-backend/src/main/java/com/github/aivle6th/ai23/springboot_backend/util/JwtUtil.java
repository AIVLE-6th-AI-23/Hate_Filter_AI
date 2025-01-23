// package com.github.aivle6th.ai23.springboot_backend.util;

// import io.jsonwebtoken.*;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import jakarta.annotation.PostConstruct;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;

// import java.nio.charset.StandardCharsets;
// import java.security.Key;
// import java.util.Date;

// @Component
// public class JwtUtil {
//     @Value("${jwt.secret}")
//     private String secret;

//     private Key key;

//     @PostConstruct
//     public void init() {
//         byte[] keyBytes = Decoders.BASE64.decode(secret);
//         this.key = Keys.hmacShaKeyFor(keyBytes);
//     }

//     public String generateToken(String employeeId) {
//         return Jwts.builder()
//                 .setSubject(employeeId)
//                 .setIssuedAt(new Date())
//                 .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30분
//                 .signWith(key)
//                 .compact();
//     }

//     public String validateAndGetEmployeeId(String token) {
//         Claims claims = Jwts.parserBuilder()
//                 .setSigningKey(key)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//         return claims.getSubject();
//     }
// }


