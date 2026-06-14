package Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String description;
    private Long ownerId;
    private String ownerName;
    private Double averageRating;
    private long ratingsCount;
    private Integer userRating;
    private Long userRatingId;
}
