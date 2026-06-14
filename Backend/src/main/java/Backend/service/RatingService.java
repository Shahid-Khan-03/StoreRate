package Backend.service;

import Backend.dto.RatingDto;
import Backend.dto.StoreOwnerDashboardDto;
import Backend.entity.Rating;
import Backend.entity.Store;
import Backend.entity.User;
import Backend.exception.ResourceNotFoundException;
import Backend.repository.RatingRepository;
import Backend.repository.StoreRepository;
import Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public List<RatingDto> findRatingsByStoreId(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));
        return ratingRepository.findByStore(store).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public RatingDto getRatingById(Long id) {
        return ratingRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));
    }

    @Transactional
    public RatingDto createOrUpdateRating(RatingDto dto, String currentEmail) {
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + dto.getStoreId()));
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentEmail));
        Rating rating = ratingRepository.findByStoreAndUser(store, user)
                .orElseGet(() -> Rating.builder()
                        .createdAt(LocalDateTime.now())
                        .store(store)
                        .user(user)
                        .build());
        rating.setRating(dto.getRating());
        rating.setComment(dto.getComment());
        return mapToDto(ratingRepository.save(rating));
    }

    @Transactional
    public RatingDto updateRating(Long id, RatingDto dto) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));
        rating.setRating(dto.getRating());
        rating.setComment(dto.getComment());
        return mapToDto(ratingRepository.save(rating));
    }

    @Transactional
    public void deleteRating(Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));
        ratingRepository.delete(rating);
    }

    public StoreOwnerDashboardDto getOwnerDashboard(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + ownerEmail));
        List<RatingDto> ratings = ratingRepository.findByStoreOwner(owner).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        double average = ratings.stream()
                .mapToInt(RatingDto::getRating)
                .average()
                .orElse(0.0);
        Store store = storeRepository.findByOwner(owner).orElse(null);
        return StoreOwnerDashboardDto.builder()
                .storeId(store == null ? null : store.getId())
                .storeName(store == null ? null : store.getName())
                .averageRating(average)
                .totalRatings(ratings.size())
                .build();
    }

    private RatingDto mapToDto(Rating rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .storeId(rating.getStore().getId())
                .storeName(rating.getStore().getName())
                .userId(rating.getUser().getId())
                .userName(rating.getUser().getName())
                .userEmail(rating.getUser().getEmail())
                .createdAt(rating.getCreatedAt() == null ? null : rating.getCreatedAt().toString())
                .build();
    }
}
