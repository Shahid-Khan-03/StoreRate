package Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerRatingDto {
    private Long id;
    private String userName;
    private String userEmail;
    private Integer rating;
   
    private LocalDateTime ratingDate;
}
