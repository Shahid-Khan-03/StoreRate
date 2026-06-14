package Backend.controller;

import Backend.dto.AuthRequest;
import Backend.dto.AuthResponse;
import Backend.dto.RegisterRequest;
import Backend.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        authenticationService.register(request);
        AuthResponse response = authenticationService.authenticate(new AuthRequest() {{
            setEmail(request.getEmail());
            setPassword(request.getPassword());
        }});
        return ResponseEntity.ok(response);
    }
}
