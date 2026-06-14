package Backend.service;

import Backend.dto.OwnerRatingDto;
import Backend.dto.PasswordUpdateRequest;
import Backend.dto.StoreOwnerDashboardDto;
import Backend.entity.Store;
import Backend.entity.User;
import Backend.repository.RatingRepository;
import Backend.repository.StoreRepository;
import Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final RatingRepository ratingRepository;
    private final PasswordEncoder passwordEncoder;

 
    public StoreOwnerDashboardDto dashboard(String email) {
        User owner = findByEmail(email);
        Store store = storeRepository.findByOwner(owner).orElse(null);

        if (store == null) {
            return StoreOwnerDashboardDto.builder()
                    .storeName(null)
                    .averageRating(0.0)
                    .totalRatings(0L)
                    .build();
        }

        Double avg = ratingRepository.findAverageRatingByStore(store);
        long count = ratingRepository.countByStore(store);

        return StoreOwnerDashboardDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .averageRating(avg != null ? avg : 0.0)
                .totalRatings(count)
                .build();
    }

   
    public List<OwnerRatingDto> ratings(String email) {
        User owner = findByEmail(email);
        Store store = storeRepository.findByOwner(owner).orElse(null);

        if (store == null) {
            return Collections.emptyList();
        }

        return ratingRepository.findByStoreOrderByUpdatedAtDesc(store)
                .stream()
                .map(r -> OwnerRatingDto.builder()
                        .id(r.getId())
                        .userName(r.getUser().getName())
                        .userEmail(r.getUser().getEmail())
                        .rating(r.getRating())
                        .ratingDate(r.getUpdatedAt() != null ? r.getUpdatedAt() : r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

   
    @Transactional
    public void changePassword(String email, PasswordUpdateRequest request) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
