package Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreOwnerDashboardDto {
    private Long storeId;
    private String storeName;
    private Double averageRating;
    private long totalRatings;
}
