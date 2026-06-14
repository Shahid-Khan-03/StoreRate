package Backend.config;

import Backend.entity.Role;
import Backend.entity.User;
import Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedDefaultUsers() {
        return args -> {
            boolean adminExists = userRepository.findAll().stream()
                    .anyMatch(user -> user.getRole() == Role.ADMIN);
            if (!adminExists) {
                userRepository.save(User.builder()
                        .name("Default System Administrator")
                        .email("admin@example.com")
                        .address("Default admin account")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build());
            }

            boolean ownerExists = userRepository.findAll().stream()
                    .anyMatch(user -> user.getRole() == Role.STORE_OWNER);
            if (!ownerExists) {
                userRepository.save(User.builder()
                        .name("Default Store Owner Account")
                        .email("owner@example.com")
                        .address("Default store owner account")
                        .password(passwordEncoder.encode("owner123"))
                        .role(Role.STORE_OWNER)
                        .build());
            }
        };
    }
}
