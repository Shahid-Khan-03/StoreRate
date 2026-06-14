package Backend.service;

import Backend.dto.AuthRequest;
import Backend.dto.AuthResponse;
import Backend.dto.RegisterRequest;
import Backend.entity.Role;
import Backend.entity.User;
import Backend.repository.UserRepository;
import Backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

   
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

  
    public AuthResponse authenticate(AuthRequest request) {
       
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getEmail()));

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        
        return new AuthResponse(
    token,
    user.getId(),
    user.getName(),
    user.getEmail(),
    user.getRole().name()
);
    }
}
