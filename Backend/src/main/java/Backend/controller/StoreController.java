package Backend.controller;

import Backend.dto.StoreDto;
import Backend.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<List<StoreDto>> getAllStores(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Authentication authentication) {
        String email = authentication == null ? null : authentication.getName();
        return ResponseEntity.ok(storeService.findAllStores(search, sortBy, direction, email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id, Authentication authentication) {
        String email = authentication == null ? null : authentication.getName();
        return ResponseEntity.ok(storeService.getStoreById(id, email));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoreDto> createStore(@Valid @RequestBody StoreDto storeDto) {
        return ResponseEntity.ok(storeService.createStore(storeDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoreDto> updateStore(@PathVariable Long id, @Valid @RequestBody StoreDto storeDto) {
        return ResponseEntity.ok(storeService.updateStore(id, storeDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }
}
