package project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.builder.TrainingSessionBuilder;
import project.dto.EnrolledClientViewDTO;
import project.dto.TrainingSessionDTO;
import project.dto.TrainingSessionViewDTO;
import project.entity.Booking;
import project.entity.TrainingSession;
import project.repository.BookingRepository;
import project.repository.TrainingSessionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingSessionServiceImpl implements TrainingSessionService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingSessionServiceImpl.class);

    private final TrainingSessionRepository trainingSessionRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public TrainingSessionServiceImpl(TrainingSessionRepository trainingSessionRepository,
                                      BookingRepository bookingRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public TrainingSessionViewDTO createTrainingSession(TrainingSessionDTO trainingSessionDTO, Long trainerIdFromToken) {
        if (trainingSessionDTO.getTrainerId() == null) {
            logger.warn("Trainer ID is missing in the DTO for createTrainingSession.");
            throw new IllegalArgumentException("Trainer ID must be provided in the request payload.");
        }
        if (!trainerIdFromToken.equals(trainingSessionDTO.getTrainerId())) {
            logger.warn("Trainer ID {} in DTO does not match authenticated trainer ID {}.", trainingSessionDTO.getTrainerId(), trainerIdFromToken);
            throw new IllegalArgumentException("Trainer ID in request does not match authenticated trainer.");
        }
        TrainingSession trainingSession = TrainingSessionBuilder.toEntity(trainingSessionDTO);
        TrainingSession savedSession = trainingSessionRepository.save(trainingSession);
        logger.info("Training session created with ID: {} by trainer ID: {}", savedSession.getId(), trainerIdFromToken);
        return TrainingSessionBuilder.toViewDTO(savedSession);
    }

    @Override
    public List<TrainingSessionViewDTO> getAllAvailableTrainingSessions() {
        List<TrainingSession> sessions = trainingSessionRepository.findAllByStartTimeAfter(LocalDateTime.now());
        logger.debug("Fetched {} available training sessions.", sessions.size());
        return sessions.stream()
                .map(TrainingSessionBuilder::toViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TrainingSessionViewDTO getTrainingSessionById(Long sessionId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    logger.warn("Training session with ID: {} not found.", sessionId);
                    return new RuntimeException("Training session not found with ID: " + sessionId);
                });
        return TrainingSessionBuilder.toViewDTO(session);
    }

    @Override
    public List<TrainingSessionViewDTO> getTrainingSessionsByTrainer(Long trainerId) {
        List<TrainingSession> sessions = trainingSessionRepository.findByTrainerId(trainerId);
        logger.debug("Fetched {} training sessions for trainer ID: {}.", sessions.size(), trainerId);
        return sessions.stream()
                .map(TrainingSessionBuilder::toViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrolledClientViewDTO> getEnrolledClientsForSession(Long sessionId, Long requestingTrainerId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    logger.warn("Training session with ID: {} not found for fetching enrolled clients.", sessionId);
                    return new RuntimeException("Training session not found with ID: " + sessionId);
                });

        if (!session.getTrainerId().equals(requestingTrainerId)) {
            logger.warn("Trainer ID {} attempted to access session {} owned by trainer ID {}.", requestingTrainerId, sessionId, session.getTrainerId());
            throw new SecurityException("You are not authorized to view enrolled clients for this session.");
        }

        List<Booking> bookings = bookingRepository.findByTrainingSessionId(sessionId);
        logger.debug("Fetched {} bookings for session ID: {}.", bookings.size(), sessionId);

        return bookings.stream()
                .map(booking -> EnrolledClientViewDTO.builder()
                        .clientId(booking.getClientId())
                        .bookingTime(booking.getBookingTime())
                        .build())
                .collect(Collectors.toList());
    }
}