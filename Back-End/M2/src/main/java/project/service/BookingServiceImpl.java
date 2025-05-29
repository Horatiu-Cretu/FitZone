package project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import project.builder.BookingBuilder;
import project.dto.BookingRequestDTO;
import project.dto.BookingViewDTO;
import project.dto.SubscriptionStatusDTO;
import project.entity.Booking;
import project.entity.BookingStatus;
import project.entity.TrainingSession;
import project.repository.BookingRepository;
import project.repository.TrainingSessionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final RestTemplate restTemplate;

    @Value("${m3.service.url}")
    private String m3ServiceUrl;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              TrainingSessionRepository trainingSessionRepository,
                              RestTemplate restTemplate) {
        this.bookingRepository = bookingRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.restTemplate = restTemplate;
    }

    private boolean checkUserSubscription(Long clientId) {
        String M3_SUBSCRIPTION_CHECK_URL = m3ServiceUrl + "/api/internal/subscriptions/user/" + clientId + "/status";
        try {
            logger.debug("Checking subscription for client ID: {} at URL: {}", clientId, M3_SUBSCRIPTION_CHECK_URL);
            ResponseEntity<SubscriptionStatusDTO> response = restTemplate.getForEntity(M3_SUBSCRIPTION_CHECK_URL, SubscriptionStatusDTO.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Subscription status for client ID {}: eligible = {}", clientId, response.getBody().isEligible());
                return response.getBody().isEligible();
            }
            logger.warn("Failed to get valid subscription status for client ID {}. Status code: {}", clientId, response.getStatusCode());
            return false;
        } catch (HttpClientErrorException e) {
            logger.error("HTTP error checking subscription for client ID {}: {} - {}", clientId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return false;
        } catch (Exception e) {
            logger.error("Error checking subscription for client ID {}: {}", clientId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public BookingViewDTO createBooking(BookingRequestDTO bookingRequestDTO, Long clientIdFromToken) {
        if (!clientIdFromToken.equals(bookingRequestDTO.getClientId())) {
            logger.warn("Client ID {} in DTO does not match authenticated client ID {}.", bookingRequestDTO.getClientId(), clientIdFromToken);
            throw new SecurityException("Client ID in request does not match authenticated user.");
        }

        if (!checkUserSubscription(bookingRequestDTO.getClientId())) {
            logger.warn("Client ID: {} does not have an active subscription or is not eligible to book.", bookingRequestDTO.getClientId());
            throw new RuntimeException("Client does not have an active subscription or is not eligible to book.");
        }

        TrainingSession trainingSession = trainingSessionRepository.findById(bookingRequestDTO.getTrainingSessionId())
                .orElseThrow(() -> {
                    logger.warn("Training session with ID: {} not found for booking.", bookingRequestDTO.getTrainingSessionId());
                    return new RuntimeException("Training session not found.");
                });

        if (trainingSession.getCurrentParticipants() >= trainingSession.getCapacity()) {
            logger.warn("Training session ID: {} is full. Capacity: {}, Participants: {}",
                    trainingSession.getId(), trainingSession.getCapacity(), trainingSession.getCurrentParticipants());
            throw new RuntimeException("Training session is full.");
        }

        if (trainingSession.getStartTime().isBefore(LocalDateTime.now())) {
            logger.warn("Attempt to book past training session ID: {}. Start time: {}", trainingSession.getId(), trainingSession.getStartTime());
            throw new RuntimeException("Cannot book a session that has already started or passed.");
        }

        if (bookingRepository.existsByClientIdAndTrainingSessionId(bookingRequestDTO.getClientId(), trainingSession.getId())) {
            logger.warn("Client ID: {} already booked for session ID: {}", bookingRequestDTO.getClientId(), trainingSession.getId());
            throw new RuntimeException("Client already booked for this session.");
        }

        int updatedRows = trainingSessionRepository.incrementParticipants(trainingSession.getId());
        if (updatedRows == 0) {
            logger.error("Failed to increment participants for session ID: {}. Session might be full or an issue occurred.", trainingSession.getId());
            throw new RuntimeException("Failed to book session. Please try again.");
        }

        Booking booking = BookingBuilder.toEntity(bookingRequestDTO, trainingSession);
        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking created with ID: {} for client ID: {} and session ID: {}",
                savedBooking.getId(), savedBooking.getClientId(), savedBooking.getTrainingSession().getId());

        return BookingBuilder.toViewDTO(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingViewDTO> getBookingsByClientId(Long clientId) {
        List<Booking> bookings = bookingRepository.findByClientIdWithTrainingSession(clientId);
        logger.debug("Fetched {} bookings for client ID: {}", bookings.size(), clientId);
        return bookings.stream()
                .map(BookingBuilder::toViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingViewDTO> getBookingsByTrainingSessionId(Long trainingSessionId, Long requestingTrainerId) {
        TrainingSession session = trainingSessionRepository.findById(trainingSessionId)
                .orElseThrow(() -> {
                    logger.warn("Training session with ID: {} not found for fetching bookings.", trainingSessionId);
                    return new RuntimeException("Training session not found.");
                });

        if (!session.getTrainerId().equals(requestingTrainerId)) {
            logger.warn("Trainer ID {} attempted to access bookings for session {} owned by trainer ID {}.",
                    requestingTrainerId, trainingSessionId, session.getTrainerId());
            throw new SecurityException("You are not authorized to view bookings for this session.");
        }

        List<Booking> bookings = bookingRepository.findByTrainingSessionIdWithTrainingSession(trainingSessionId);
        logger.debug("Fetched {} bookings for session ID: {}", bookings.size(), trainingSessionId);
        return bookings.stream()
                .map(BookingBuilder::toViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, Long clientIdFromToken) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.warn("Booking with ID: {} not found for cancellation.", bookingId);
                    return new RuntimeException("Booking not found.");
                });

        if (!booking.getClientId().equals(clientIdFromToken)) {
            logger.warn("Client ID {} attempted to cancel booking {} owned by client ID {}.",
                    clientIdFromToken, bookingId, booking.getClientId());
            throw new SecurityException("You are not authorized to cancel this booking.");
        }

        TrainingSession trainingSession = booking.getTrainingSession();
        if (trainingSession == null) {
            trainingSession = trainingSessionRepository.findById(booking.getTrainingSession().getId()).orElseThrow(() -> new RuntimeException("Training Session not found for booking"));
        }

        if (trainingSession.getStartTime().isBefore(LocalDateTime.now().plusHours(2))) {
            logger.warn("Attempt to cancel booking ID: {} too close to session start time. Session start: {}",
                    bookingId, trainingSession.getStartTime());
            throw new RuntimeException("Cannot cancel booking less than 2 hours before the session starts.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            logger.info("Booking ID: {} is already cancelled.", bookingId);
            throw new RuntimeException("Booking is already cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        trainingSessionRepository.decrementParticipants(trainingSession.getId());
        logger.info("Booking with ID: {} cancelled successfully for client ID: {}", bookingId, clientIdFromToken);
    }
}