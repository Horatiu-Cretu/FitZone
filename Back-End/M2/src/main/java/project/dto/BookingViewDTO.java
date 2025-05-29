package project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.entity.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingViewDTO {
    private Long id;
    private Long clientId;
    private Long trainingSessionId;
    private String trainingSessionName;
    private LocalDateTime bookingTime;
    private BookingStatus status;
}