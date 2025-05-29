package project.service;

import project.dto.BookingRequestDTO;
import project.dto.BookingViewDTO;

import java.util.List;

public interface BookingService {
    BookingViewDTO createBooking(BookingRequestDTO bookingRequestDTO, Long clientIdFromToken);
    List<BookingViewDTO> getBookingsByClientId(Long clientId);
    List<BookingViewDTO> getBookingsByTrainingSessionId(Long trainingSessionId, Long requestingTrainerId);
    void cancelBooking(Long bookingId, Long clientIdFromToken);
}