package project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolledClientViewDTO {
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private LocalDateTime bookingTime;
}