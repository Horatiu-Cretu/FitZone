package project.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.dto.EnrolledClientViewDTO;
import project.dto.TrainingSessionDTO;
import project.dto.TrainingSessionViewDTO;
import project.service.TrainingSessionService;

import java.util.List;

@RestController
@RequestMapping("/api/training-sessions")
public class TrainingSessionController extends BaseController {

    private final TrainingSessionService trainingSessionService;

    @Autowired
    public TrainingSessionController(TrainingSessionService trainingSessionService) {
        this.trainingSessionService = trainingSessionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<TrainingSessionViewDTO> createTrainingSession(@RequestBody TrainingSessionDTO trainingSessionDTO, HttpServletRequest request) {
        Long trainerId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        if (trainerId == null || !"TRAINER".equals(userRole)) {
            logger.warn("Unauthorized attempt to create training session. User ID: {}, Role: {}", trainerId, userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Trainer ID {} (Role: {}) attempting to create training session.", trainerId, userRole);
        TrainingSessionViewDTO createdSession = trainingSessionService.createTrainingSession(trainingSessionDTO, trainerId);
        return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TrainingSessionViewDTO>> getAllAvailableTrainingSessions() {
        List<TrainingSessionViewDTO> sessions = trainingSessionService.getAllAvailableTrainingSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<TrainingSessionViewDTO> getTrainingSessionById(@PathVariable Long sessionId) {
        TrainingSessionViewDTO session = trainingSessionService.getTrainingSessionById(sessionId);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/trainer")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<TrainingSessionViewDTO>> getMyTrainingSessions(HttpServletRequest request) {
        Long trainerId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        if (trainerId == null || !"TRAINER".equals(userRole)) {
            logger.warn("Unauthorized attempt to get trainer sessions. User ID: {}, Role: {}", trainerId, userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Fetching sessions for trainer ID: {} (Role: {})", trainerId, userRole);
        List<TrainingSessionViewDTO> sessions = trainingSessionService.getTrainingSessionsByTrainer(trainerId);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/{sessionId}/enrolled-clients")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<EnrolledClientViewDTO>> getEnrolledClientsForSession(@PathVariable Long sessionId, HttpServletRequest request) {
        Long trainerId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        if (trainerId == null || !"TRAINER".equals(userRole)) {
            logger.warn("Unauthorized attempt to get enrolled clients for session {}. User ID: {}, Role: {}", sessionId, trainerId, userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Trainer ID {} (Role: {}) attempting to view enrolled clients for session ID {}.", trainerId, userRole, sessionId);
        List<EnrolledClientViewDTO> enrolledClients = trainingSessionService.getEnrolledClientsForSession(sessionId, trainerId);
        return ResponseEntity.ok(enrolledClients);
    }
}