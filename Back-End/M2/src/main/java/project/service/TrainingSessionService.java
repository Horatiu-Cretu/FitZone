package project.service;

import project.dto.TrainingSessionDTO;
import project.dto.TrainingSessionViewDTO;
import project.dto.EnrolledClientViewDTO;

import java.util.List;

public interface TrainingSessionService {
    TrainingSessionViewDTO createTrainingSession(TrainingSessionDTO trainingSessionDTO, Long trainerId);
    List<TrainingSessionViewDTO> getAllAvailableTrainingSessions();
    TrainingSessionViewDTO getTrainingSessionById(Long sessionId);
    List<TrainingSessionViewDTO> getTrainingSessionsByTrainer(Long trainerId);
    List<EnrolledClientViewDTO> getEnrolledClientsForSession(Long sessionId, Long requestingTrainerId);
}