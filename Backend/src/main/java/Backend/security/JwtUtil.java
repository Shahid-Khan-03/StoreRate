package Backend.security;

import Backend.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generateToken(Long userId, String name, String email, Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        String roleWithPrefix = "ROLE_" + role.name(); // e.g. "ROLE_ADMIN"

        return Jwts.builder()
                .setSubject(email)
                .claim("id", userId)
                .claim("name", name)
                .claim("roles", roleWithPrefix)  // plural — matches AuthContext.parseToken()
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public Set<GrantedAuthority> extractAuthorities(String token) {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();

        
        String roles = body.get("roles", String.class);

        if (roles != null && !roles.isBlank()) {
            return Set.of(new SimpleGrantedAuthority(roles));
        }

        
        String role = body.get("role", String.class);

        if (role != null && !role.isBlank()) {
            
            return Set.of(new SimpleGrantedAuthority("ROLE_" + role));
        }

        
        return Collections.emptySet();
    }
}
