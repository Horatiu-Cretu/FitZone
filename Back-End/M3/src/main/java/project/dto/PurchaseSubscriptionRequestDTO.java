package project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseSubscriptionRequestDTO {

    @NotNull(message = "Subscription Plan ID cannot be null.")
    private Long planId;

    private String paymentConfirmationId;
}