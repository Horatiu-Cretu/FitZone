package project.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.dto.BookingRequestDTO;
import project.dto.BookingViewDTO;
import project.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController extends BaseController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingViewDTO> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO, HttpServletRequest request) {
        Long clientId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        if (clientId == null || !"USER".equals(userRole)) {
            logger.warn("Unauthorized attempt to create booking. User ID: {}, Role: {}", clientId, userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Client ID {} (Role: {}) attempting to create booking.", clientId, userRole);
        BookingViewDTO createdBooking = bookingService.createBooking(bookingRequestDTO, clientId);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingViewDTO>> getMyBookings(HttpServletRequest request) {
        Long clientId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        if (clientId == null || !"USER".equals(userRole)) {
            logger.warn("Unauthorized attempt to get own bookings. User ID: {}, Role: {}", clientId, userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Fetching bookings for client ID: {} (Role: {})", clientId, userRole);
        List<BookingViewDTO> bookings = bookingService.getBookingsByClientId(clientId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<List<BookingViewDTO>> getBookingsForSession(@PathVariable Long sessionId, HttpServletRequest request) {
        Long trainerId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        if (trainerId == null || !"TRAINER".equals(userRole)) {
            logger.warn("Unauthorized attempt to get bookings for session {}. User ID: {}, Role: {}", sessionId, trainerId, userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Trainer ID {} (Role: {}) attempting to view bookings for session ID {}.", trainerId, userRole, sessionId);
        List<BookingViewDTO> bookings = bookingService.getBookingsByTrainingSessionId(sessionId, trainerId);
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId, HttpServletRequest request) {
        Long clientId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        if (clientId == null || !"USER".equals(userRole)) {
            logger.warn("Unauthorized attempt to cancel booking {}. User ID: {}, Role: {}", bookingId, clientId, userRole);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Client ID {} (Role: {}) attempting to cancel booking ID {}.", clientId, userRole, bookingId);
        bookingService.cancelBooking(bookingId, clientId);
        return ResponseEntity.noContent().build();
    }
}