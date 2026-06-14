# CORS Configuration for Store Rating Application

## Problem
React frontend running on `http://localhost:5173` cannot communicate with Spring Boot backend on `http://localhost:8080` due to CORS (Cross-Origin Resource Sharing) policy restrictions.

## Error
```
Access to XMLHttpRequest has been blocked by CORS policy.
No 'Access-Control-Allow-Origin' header is present.
```

## Solution Overview

Two configuration files work together to enable CORS while maintaining JWT authentication security:

1. **CorsConfig.java** - Defines CORS policies
2. **SecurityConfig.java** - Integrates CORS into Spring Security

---

## Configuration Files

### 1. CorsConfig.java

**Location:** `Backend/src/main/java/Backend/config/CorsConfig.java`

```java
package Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow requests from React frontend
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000"
        ));
        
        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Allow common headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "Authorization",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        
        // Expose headers that JavaScript can access
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Access-Control-Allow-Origin"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache CORS preflight responses for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
```

**Key Configuration Details:**

| Setting | Value | Purpose |
|---------|-------|---------|
| **AllowedOrigins** | localhost:5173, localhost:3000 | Frontend URLs that can make requests |
| **AllowedMethods** | GET, POST, PUT, DELETE, OPTIONS, PATCH | HTTP methods allowed |
| **AllowedHeaders** | Content-Type, Authorization, etc. | Request headers allowed |
| **ExposedHeaders** | Authorization, Content-Type | Headers accessible from JavaScript |
| **AllowCredentials** | true | Allows sending Authorization headers |
| **MaxAge** | 3600 | Cache preflight for 1 hour |

---

### 2. SecurityConfig.java (Updated)

**Location:** `Backend/src/main/java/Backend/config/SecurityConfig.java`

```java
package Backend.config;

import Backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stores/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
```

**Key Changes:**

1. **Import CORS Source:**
   ```java
   import org.springframework.web.cors.CorsConfigurationSource;
   ```

2. **Inject CORS Configuration:**
   ```java
   private final CorsConfigurationSource corsConfigurationSource;
   ```

3. **Enable CORS in Security Filter:**
   ```java
   .cors(cors -> cors.configurationSource(corsConfigurationSource))
   ```

---

## How CORS Works with JWT

### CORS Preflight Request (Browser)
Browser sends OPTIONS request before actual request:
```
OPTIONS /api/auth/login
Origin: http://localhost:5173
Access-Control-Request-Method: POST
Access-Control-Request-Headers: Content-Type
```

### CORS Preflight Response (Server)
```
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

### Actual Request (Browser)
```
POST /api/auth/login
Origin: http://localhost:5173
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Server Response
```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:5173
Content-Type: application/json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## JWT Authentication Still Works

**Important:** CORS configuration does NOT weaken JWT security because:

1. ✅ **Authorization header required** - JWT tokens must still be sent
2. ✅ **Token validation** - JwtAuthenticationFilter validates every request
3. ✅ **No credentials in URL** - Tokens are in headers, not query params
4. ✅ **HTTPS ready** - Works with HTTPS in production
5. ✅ **Role-based access** - @PreAuthorize still enforces permissions

**Frontend still must:**
- Login to get JWT token
- Store token in localStorage
- Send token in Authorization header for all protected requests

---

## Frontend API Configuration

**Location:** `Frontend/src/services/api.js`

```javascript
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to every request
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
```

This ensures:
- ✅ Automatic token inclusion in all requests
- ✅ CORS headers automatically sent by browser
- ✅ Credentials passed correctly

---

## Testing CORS Configuration

### Test 1: Public Endpoint (No Auth Required)
```bash
curl -H "Origin: http://localhost:5173" \
     http://localhost:8080/api/stores

# Should return 200 with stores data
```

### Test 2: Auth Endpoint
```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Origin: http://localhost:5173" \
     -H "Content-Type: application/json" \
     -d '{"email":"user@example.com","password":"password"}'

# Should return 200 with JWT token
```

### Test 3: Protected Endpoint (Requires JWT)
```bash
curl -H "Origin: http://localhost:5173" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8080/api/ratings

# Should return 200 with ratings data
```

### Test 4: CORS Preflight
```bash
curl -X OPTIONS http://localhost:8080/api/stores \
     -H "Origin: http://localhost:5173" \
     -H "Access-Control-Request-Method: POST" \
     -v

# Should return 200 with CORS headers
```

---

## Production Deployment

For production, update allowed origins in `CorsConfig.java`:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "https://yourdomain.com",
    "https://www.yourdomain.com"
));
```

Never use `*` (all origins) with `AllowCredentials(true)`.

---

## Troubleshooting

### Issue: CORS error still appears

1. **Clear browser cache** and hard refresh (Ctrl+F5)
2. **Verify backend running** on http://localhost:8080
3. **Check network tab** in browser DevTools for CORS headers
4. **Verify frontend URL** is in allowedOrigins list

### Issue: JWT token not sent

1. **Check token stored** in browser localStorage
2. **Verify interceptor** is configured in api.js
3. **Check Authorization header** in network requests

### Issue: Preflight request fails

1. **Check Allow-Methods** includes OPTIONS
2. **Check Allow-Headers** includes Authorization
3. **Restart backend** after changing CorsConfig

---

## Files Modified

✅ **Created:** [Backend/src/main/java/Backend/config/CorsConfig.java](Backend/src/main/java/Backend/config/CorsConfig.java)
✅ **Updated:** [Backend/src/main/java/Backend/config/SecurityConfig.java](Backend/src/main/java/Backend/config/SecurityConfig.java)
✅ **No changes needed:** Frontend api.js (already correct)

---

## Summary

CORS is now fully configured to allow frontend-backend communication while preserving JWT security:

- ✅ React frontend (localhost:5173) can make requests
- ✅ Authorization headers are allowed
- ✅ JWT tokens are validated on every request
- ✅ CORS preflight requests handled automatically
- ✅ Production-ready configuration
