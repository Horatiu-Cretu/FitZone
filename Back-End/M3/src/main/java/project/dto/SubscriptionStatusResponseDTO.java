package project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.util.SubscriptionStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionStatusResponseDTO {
    private Long userId;
    private boolean eligible;
    private SubscriptionStatus currentStatus;
    private LocalDateTime subscriptionEndDate;
    private String message;
}