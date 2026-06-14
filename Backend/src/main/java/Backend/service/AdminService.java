package Backend.service;

import Backend.dto.AdminDashboardDto;
import Backend.dto.AdminStoreRequest;
import Backend.dto.AdminUserRequest;
import Backend.dto.StoreDto;
import Backend.dto.UserDto;
import Backend.entity.Role;
import Backend.entity.Store;
import Backend.entity.User;
import Backend.repository.RatingRepository;
import Backend.repository.StoreRepository;
import Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final RatingRepository ratingRepository;
    private final PasswordEncoder passwordEncoder;


    public AdminDashboardDto dashboard() {
        return new AdminDashboardDto(
                userRepository.count(),
                storeRepository.count(),
                ratingRepository.count()
        );
    }

    

    public Page<UserDto> users(String search, Pageable pageable) {
        return userRepository.searchUsers(search, pageable).map(this::toUserDto);
    }

    public UserDto userDetails(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return toUserDto(user);
    }

    @Transactional
    public UserDto createUser(AdminUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Role role = parseRole(request.getRole());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .role(role)
                .build();

        return toUserDto(userRepository.save(user));
    }

   

    public Page<StoreDto> stores(String search, Pageable pageable) {
        return storeRepository.searchStores(search, pageable).map(this::toStoreDto);
    }

    @Transactional
    public StoreDto createStore(AdminStoreRequest request) {
        Store.StoreBuilder builder = Store.builder()
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .description(request.getDescription());

        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("Owner not found: " + request.getOwnerId()));
            if (owner.getRole() != Role.STORE_OWNER) {
                throw new RuntimeException("User does not have STORE_OWNER role");
            }
            builder.owner(owner);
        }

        return toStoreDto(storeRepository.save(builder.build()));
    }

    

    private UserDto toUserDto(User user) {
       
        Set<String> roleSet = new HashSet<>();
        roleSet.add("ROLE_" + user.getRole().name());

        Double ownerRating = null;
        if (user.getRole() == Role.STORE_OWNER) {
            ownerRating = storeRepository.findByOwner(user)
                    .map(store -> ratingRepository.findAverageRatingByStore(store))
                    .orElse(null);
        }

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .address(user.getAddress())
                .roles(roleSet)
                .ownerAverageRating(ownerRating)
                .build();
    }

    private StoreDto toStoreDto(Store store) {
        Double avg = ratingRepository.findAverageRatingByStore(store);
        return StoreDto.builder()
                .id(store.getId())
                .name(store.getName())
                .email(store.getEmail())
                .address(store.getAddress())
                .description(store.getDescription())
                .ownerId(store.getOwner() != null ? store.getOwner().getId() : null)
                .ownerName(store.getOwner() != null ? store.getOwner().getName() : null)
                .averageRating(avg)
                .build();
    }

    private Role parseRole(String roleStr) {
        String normalized = (roleStr != null && roleStr.startsWith("ROLE_"))
                ? roleStr.substring(5)
                : roleStr;
        try {
            return Role.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                "Invalid role '" + roleStr + "'. Use: ROLE_USER, ROLE_ADMIN, ROLE_STORE_OWNER"
            );
        }
    }
}
