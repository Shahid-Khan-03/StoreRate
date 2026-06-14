package Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    private long totalUsers;
    private long totalStores;
    private long totalRatings;
}
