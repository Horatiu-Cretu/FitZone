package project.builder;

import project.dto.SubscriptionPlanDTO;
import project.dto.SubscriptionPlanViewDTO;
import project.entity.SubscriptionPlan;

import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionPlanBuilder {

    public static SubscriptionPlan toEntity(SubscriptionPlanDTO dto) {
        if (dto == null) {
            return null;
        }
        return SubscriptionPlan.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .durationDays(dto.getDurationDays())
                .isActive(dto.getIsActive() == null ? true : dto.getIsActive())
                .build();
    }

    public static SubscriptionPlanViewDTO toViewDTO(SubscriptionPlan entity) {
        if (entity == null) {
            return null;
        }
        return SubscriptionPlanViewDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .durationDays(entity.getDurationDays())
                .isActive(entity.isActive())
                .build();
    }

    public static List<SubscriptionPlanViewDTO> toViewDTOList(List<SubscriptionPlan> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(SubscriptionPlanBuilder::toViewDTO)
                .collect(Collectors.toList());
}
}