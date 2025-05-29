package project.builder;

import project.dto.TrainingSessionDTO;
import project.dto.TrainingSessionViewDTO;
import project.entity.TrainingSession;

public class TrainingSessionBuilder {

    public static TrainingSession toEntity(TrainingSessionDTO dto) {
        return TrainingSession.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .capacity(dto.getCapacity())
                .trainerId(dto.getTrainerId())
                .currentParticipants(0)
                .build();
    }

    public static TrainingSessionViewDTO toViewDTO(TrainingSession entity) {
        return TrainingSessionViewDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .capacity(entity.getCapacity())
                .currentParticipants(entity.getCurrentParticipants())
                .trainerId(entity.getTrainerId())
                .build();
    }
}