package Backend.controller;

import Backend.dto.RatingDto;
import Backend.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<RatingDto>> getRatingsByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(ratingService.findRatingsByStoreId(storeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDto> getRatingById(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getRatingById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RatingDto> createRating(@Valid @RequestBody RatingDto ratingDto, Authentication authentication) {
        return ResponseEntity.ok(ratingService.createOrUpdateRating(ratingDto, authentication.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<RatingDto> updateRating(@PathVariable Long id, @Valid @RequestBody RatingDto ratingDto) {
        return ResponseEntity.ok(ratingService.updateRating(id, ratingDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
