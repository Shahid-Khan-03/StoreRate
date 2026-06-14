package Backend.service;

import Backend.dto.StoreDto;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    public List<StoreDto> findAllStores(String search, String sortBy, String direction, String currentEmail) {
        User currentUser = currentEmail == null ? null : userRepository.findByEmail(currentEmail).orElse(null);
        Comparator<StoreDto> comparator = switch (sortBy == null ? "name" : sortBy) {
            case "address" -> Comparator.comparing(dto -> nullToEmpty(dto.getAddress()), String.CASE_INSENSITIVE_ORDER);
            case "rating" -> Comparator.comparing(dto -> dto.getAverageRating() == null ? 0.0 : dto.getAverageRating());
            default -> Comparator.comparing(StoreDto::getName, String.CASE_INSENSITIVE_ORDER);
        };
        if ("desc".equalsIgnoreCase(direction) || "rating".equals(sortBy)) {
            comparator = comparator.reversed();
        }
        String needle = search == null ? "" : search.toLowerCase();
        return storeRepository.findAll().stream()
                .map(store -> mapToDto(store, currentUser))
                .filter(store -> needle.isBlank()
                        || store.getName().toLowerCase().contains(needle)
                        || nullToEmpty(store.getAddress()).toLowerCase().contains(needle))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public StoreDto getStoreById(Long id, String currentEmail) {
        User currentUser = currentEmail == null ? null : userRepository.findByEmail(currentEmail).orElse(null);
        return storeRepository.findById(id)
                .map(store -> mapToDto(store, currentUser))
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
    }

    @Transactional
    public StoreDto createStore(StoreDto dto) {
        User owner = dto.getOwnerId() == null ? null : userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + dto.getOwnerId()));
        Store saved = storeRepository.save(Store.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .description(dto.getDescription())
                .owner(owner)
                .build());
        return mapToDto(saved, null);
    }

    @Transactional
    public StoreDto updateStore(Long id, StoreDto dto) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
        store.setName(dto.getName());
        store.setEmail(dto.getEmail());
        store.setAddress(dto.getAddress());
        store.setDescription(dto.getDescription());
        if (dto.getOwnerId() != null) {
            User owner = userRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + dto.getOwnerId()));
            store.setOwner(owner);
        } else {
            store.setOwner(null);
        }
        return mapToDto(storeRepository.save(store), null);
    }

    @Transactional
    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
        storeRepository.delete(store);
    }

    private StoreDto mapToDto(Store store, User currentUser) {
        double average = store.getRatings().stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
        long count = store.getRatings().size();
        Rating userRating = currentUser == null ? null : ratingRepository.findByStoreAndUser(store, currentUser).orElse(null);
        return StoreDto.builder()
                .id(store.getId())
                .name(store.getName())
                .email(store.getEmail())
                .address(store.getAddress())
                .description(store.getDescription())
                .ownerId(store.getOwner() == null ? null : store.getOwner().getId())
                .ownerName(store.getOwner() == null ? null : store.getOwner().getName())
                .averageRating(count > 0 ? average : null)
                .ratingsCount(count)
                .userRating(userRating == null ? null : userRating.getRating())
                .userRatingId(userRating == null ? null : userRating.getId())
                .build();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
