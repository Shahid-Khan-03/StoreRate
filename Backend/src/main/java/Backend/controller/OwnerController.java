package Backend.controller;

import Backend.dto.OwnerRatingDto;
import Backend.dto.PasswordUpdateRequest;
import Backend.dto.StoreOwnerDashboardDto;
import Backend.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STORE_OWNER')")
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping("/dashboard")
    public ResponseEntity<StoreOwnerDashboardDto> dashboard(Authentication authentication) {
        return ResponseEntity.ok(ownerService.dashboard(authentication.getName()));
    }

    @GetMapping("/ratings")
    public ResponseEntity<List<OwnerRatingDto>> ratings(Authentication authentication) {
        return ResponseEntity.ok(ownerService.ratings(authentication.getName()));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody PasswordUpdateRequest request,
            Authentication authentication) {
        ownerService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
