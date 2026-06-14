package Backend.service;

import Backend.dto.PasswordUpdateRequest;
import Backend.dto.RegisterRequest;
import Backend.dto.UserDto;
import Backend.entity.Role;
import Backend.entity.User;
import Backend.exception.ResourceAlreadyExistsException;
import Backend.exception.ResourceNotFoundException;
import Backend.repository.RatingRepository;
import Backend.repository.StoreRepository;
import Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final RatingRepository ratingRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("User with email already exists: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    public List<UserDto> findAll(String search, String sortBy, String direction) {
        
        Comparator<UserDto> comparator = switch (sortBy == null ? "name" : sortBy) {
            case "email"   -> Comparator.comparing(UserDto::getEmail, String.CASE_INSENSITIVE_ORDER);
            case "address" -> Comparator.comparing(
                    dto -> dto.getAddress() == null ? "" : dto.getAddress(),
                    String.CASE_INSENSITIVE_ORDER);
            case "role"    -> Comparator.comparing(
                    dto -> dto.getRole() == null ? "" : dto.getRole(),
                    String.CASE_INSENSITIVE_ORDER);
            default        -> Comparator.comparing(UserDto::getName, String.CASE_INSENSITIVE_ORDER);
        };

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        String needle = search == null ? "" : search.toLowerCase();

        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .filter(u -> needle.isBlank()
                        || u.getName().toLowerCase().contains(needle)
                        || u.getEmail().toLowerCase().contains(needle)
                        || (u.getAddress() != null && u.getAddress().toLowerCase().contains(needle))
                        || (u.getRole() != null && u.getRole().toLowerCase().contains(needle)))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("User with email already exists: " + dto.getEmail());
        }

        
        String rawRole = dto.getRole();
        if (rawRole == null || rawRole.isBlank()) rawRole = "USER";
        
        if (rawRole.startsWith("ROLE_")) rawRole = rawRole.substring(5);

        Role role;
        try {
            role = Role.valueOf(rawRole);
        } catch (IllegalArgumentException e) {
            role = Role.USER;
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .password(passwordEncoder.encode(dto.getPassword())) // JsonIgnore on response only
                .role(role)
                .build();

        return mapToDto(userRepository.save(user));
    }

    public UserDto getUser(Long id) {
        return mapToDto(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id)));
    }

    @Transactional
    public void updatePassword(String email, PasswordUpdateRequest request) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    
    private UserDto mapToDto(User user) {
        Double ownerAverage = null;
        if (user.getRole() == Role.STORE_OWNER) {
           
            ownerAverage = storeRepository.findByOwner(user)
                    .map(store -> ratingRepository.findAverageRatingByStore(store))
                    .orElse(null);
        }

      
        String roleName = user.getRole().name();                    
        Set<String> roleSet = Set.of("ROLE_" + roleName);           

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .address(user.getAddress())
                .role(roleName)        
                .roles(roleSet)        
                .ownerAverageRating(ownerAverage)
                .build();
    }
}
