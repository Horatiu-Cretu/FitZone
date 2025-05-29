package project.builder;

import project.dto.BookingRequestDTO;
import project.dto.BookingViewDTO;
import project.entity.Booking;
import project.entity.TrainingSession;
import project.entity.BookingStatus;

import java.time.LocalDateTime;

public class BookingBuilder {

    public static Booking toEntity(BookingRequestDTO dto, TrainingSession trainingSession) {
        return Booking.builder()
                .clientId(dto.getClientId())
                .trainingSession(trainingSession)
                .bookingTime(LocalDateTime.now())
                .status(BookingStatus.CONFIRMED)
                .build();
    }

    public static BookingViewDTO toViewDTO(Booking entity) {
        return BookingViewDTO.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .trainingSessionId(entity.getTrainingSession().getId())
                .trainingSessionName(entity.getTrainingSession().getName())
                .bookingTime(entity.getBookingTime())
                .status(entity.getStatus())
                .build();
    }
}