package com.easj.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(jwtSecret.trim());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = extractToken(request);
        System.out.println("Token recibido en JwtFilter: " + token); // Verificar token recibido
        
        if (token != null && validarToken(token)) {
            UserDetails userDetails = getUserDetailsFromToken(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            System.out.println("❌ Token inválido o no presente");
        }

        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean validarToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .setAllowedClockSkewSeconds(300) // Permite hasta 5 minutos de diferencia
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("Hora actual del servidor de validación: " + System.currentTimeMillis());
            System.out.println("Fecha de expiración del token: " + claims.getExpiration().getTime());
            System.out.println("Token válido. Expira en: " + claims.getExpiration());

            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println(" El token ha expirado.");
        } catch (UnsupportedJwtException e) {
            System.out.println(" El token tiene un formato no soportado.");
        } catch (MalformedJwtException e) {
            System.out.println(" El token está mal formado.");
        } catch (SecurityException e) { 
            System.out.println(" Firma del token inválida o seguridad comprometida.");
        } catch (IllegalArgumentException e) {
            System.out.println(" Token vacío o nulo.");
        } catch (Exception e) {
            System.out.println(" Error desconocido al validar el token: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private UserDetails getUserDetailsFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        
        return new User(username, "", Collections.emptyList()); // Devuelve el usuario autenticado
    }
}
