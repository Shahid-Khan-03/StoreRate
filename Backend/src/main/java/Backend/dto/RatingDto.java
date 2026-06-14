package Backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    private Long id;

    @Min(1)
    @Max(5)
    private int rating;

    private String comment;
    private Long storeId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String storeName;
    private String createdAt;
}
