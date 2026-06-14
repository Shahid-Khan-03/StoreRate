package Backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminStoreRequest {

    @NotBlank
    @Size(max = 60)
    private String name;

    @Email
    private String email;

    @Size(max = 400)
    private String address;

    private String description;

    private Long ownerId;
}
