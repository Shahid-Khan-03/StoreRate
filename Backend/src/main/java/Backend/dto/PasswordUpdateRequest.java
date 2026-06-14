package Backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateRequest {
    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[^A-Za-z0-9]).*$", message = "Password must include at least one uppercase letter and one special character")
    private String newPassword;
}
