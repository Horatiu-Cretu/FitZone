package project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanDTO {

    @NotBlank(message = "Plan name cannot be blank.")
    private String name;

    private String description;

    @NotNull(message = "Price cannot be null.")
    @Positive(message = "Price must be positive.")
    private BigDecimal price;

    @NotNull(message = "Duration in days cannot be null.")
    @Positive(message = "Duration must be a positive number of days.")
    private Integer durationDays;

    private Boolean isActive;
}