package project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanViewDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationDays;
    @JsonProperty("isActive")
    private boolean isActive;
}