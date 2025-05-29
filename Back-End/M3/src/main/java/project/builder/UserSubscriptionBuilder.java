package project.builder;

import project.dto.PurchaseSubscriptionRequestDTO;
import project.dto.UserSubscriptionViewDTO;
import project.entity.SubscriptionPlan;
import project.entity.UserSubscription;
import project.util.SubscriptionStatus;

import java.time.LocalDateTime;

public class UserSubscriptionBuilder {

    public static UserSubscription toEntity(PurchaseSubscriptionRequestDTO dto, SubscriptionPlan plan, Long userId) {
        if (dto == null || plan == null || userId == null) {
            return null;
        }
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(plan.getDurationDays());

        return UserSubscription.builder()
                .userId(userId)
                .subscriptionPlan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .status(SubscriptionStatus.ACTIVE)
                .paymentId(dto.getPaymentConfirmationId())
                .build();
    }

    public static UserSubscriptionViewDTO toViewDTO(UserSubscription entity) {
        if (entity == null) {
            return null;
        }
        return UserSubscriptionViewDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .planName(entity.getSubscriptionPlan().getName())
                .planDescription(entity.getSubscriptionPlan().getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .pricePaid(entity.getSubscriptionPlan().getPrice())
                .build();
    }
}