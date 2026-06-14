package Backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUserRequest {

    @NotBlank
    @Size(min = 20, max = 60, message = "Name must be between 20 and 60 characters")
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 16, message = "Password must be 8-16 characters")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).+$",
        message = "Password must contain at least one uppercase letter and one special character"
    )
    private String password;

    @Size(max = 400)
    private String address;

    @NotBlank
    private String role;
}
