package Backend.controller;

import Backend.dto.AdminDashboardDto;
import Backend.dto.StoreOwnerDashboardDto;
import Backend.repository.RatingRepository;
import Backend.repository.StoreRepository;
import Backend.repository.UserRepository;
import Backend.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final RatingRepository ratingRepository;
    private final RatingService ratingService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardDto> adminDashboard() {
        return ResponseEntity.ok(new AdminDashboardDto(
                userRepository.count(),
                storeRepository.count(),
                ratingRepository.count()
        ));
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<StoreOwnerDashboardDto> ownerDashboard(Authentication authentication) {
        return ResponseEntity.ok(ratingService.getOwnerDashboard(authentication.getName()));
    }
}
