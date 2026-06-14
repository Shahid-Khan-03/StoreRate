package Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private List<String> messages;
    private String path;
}
