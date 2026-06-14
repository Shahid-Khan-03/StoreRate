package Backend.controller;

import Backend.dto.AdminDashboardDto;
import Backend.dto.AdminStoreRequest;
import Backend.dto.AdminUserRequest;
import Backend.dto.StoreDto;
import Backend.dto.UserDto;
import Backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDto> dashboard() {
        return ResponseEntity.ok(adminService.dashboard());
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody AdminUserRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @PostMapping("/stores")
    public ResponseEntity<StoreDto> createStore(@Valid @RequestBody AdminStoreRequest request) {
        return ResponseEntity.ok(adminService.createStore(request));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> users(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(adminService.users(search, pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> userDetails(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.userDetails(id));
    }

    @GetMapping("/stores")
    public ResponseEntity<Page<StoreDto>> stores(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(adminService.stores(search, pageable));
    }
}
