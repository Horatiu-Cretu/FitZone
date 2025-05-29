package project.controller.forwarding;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import project.controller.BaseController;

import java.util.Map;

@RestController
@RequestMapping("/m2-proxy")
public class M2ForwardingController extends BaseController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${m2.service.url}")
    private String m2ServiceUrl;

    @Autowired
    public M2ForwardingController(RestTemplate restTemplate, ObjectMapper objectMapper) { // Injectează ObjectMapper
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private HttpHeaders createHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            headers.set("Authorization", authHeader);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private ResponseEntity<String> forwardRequest(String path, HttpMethod method, HttpServletRequest request, String body) {
        String url = m2ServiceUrl + path;
        HttpEntity<String> entity = new HttpEntity<>(body, createHeaders(request));
        logger.info("Forwarding {} request to M2: {} with body: {}", method, url, body != null ? "present" : "absent");
        try {
            return restTemplate.exchange(url, method, entity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Error forwarding request to M2: {} - {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @GetMapping("/training-sessions")
    public ResponseEntity<String> getAllTrainingSessions(HttpServletRequest request) {
        return forwardRequest("/api/training-sessions", HttpMethod.GET, request, null);
    }

    @GetMapping("/training-sessions/{sessionId}")
    public ResponseEntity<String> getTrainingSessionById(@PathVariable String sessionId, HttpServletRequest request) {
        return forwardRequest("/api/training-sessions/" + sessionId, HttpMethod.GET, request, null);
    }

    @PostMapping("/training-sessions")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<String> createTrainingSession(@RequestBody String body, HttpServletRequest request) {
        Long trainerIdFromToken = (Long) request.getAttribute("userId");
        if (trainerIdFromToken == null) {
            logger.warn("M1: Trainer ID not found in token for createTrainingSession proxy.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\":\"Trainer ID missing from token\"}");
        }

        String modifiedBody;
        try {
            Map<String, Object> bodyMap = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            bodyMap.put("trainerId", trainerIdFromToken);
            modifiedBody = objectMapper.writeValueAsString(bodyMap);
            logger.info("M1: Forwarding createTrainingSession for trainerId {}. Original body from client: {}, Modified body to M2: {}", trainerIdFromToken, body, modifiedBody);
        } catch (Exception e) {
            logger.error("M1: Error processing JSON body for createTrainingSession: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Invalid request body format\"}");
        }
        return forwardRequest("/api/training-sessions", HttpMethod.POST, request, modifiedBody);
    }

    @GetMapping("/training-sessions/trainer/my-sessions")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<String> getTrainerSessions(HttpServletRequest request) {
        return forwardRequest("/api/training-sessions/trainer", HttpMethod.GET, request, null);
    }

    @GetMapping("/training-sessions/{sessionId}/enrolled-clients")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<String> getEnrolledClients(@PathVariable String sessionId, HttpServletRequest request) {
        return forwardRequest("/api/training-sessions/" + sessionId + "/enrolled-clients", HttpMethod.GET, request, null);
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> createBooking(@RequestBody String body, HttpServletRequest request) {
        Long clientIdFromToken = (Long) request.getAttribute("userId");
        if (clientIdFromToken == null) {
            logger.warn("M1: Client ID not found in token for createBooking proxy.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\":\"Client ID missing from token\"}");
        }
        return forwardRequest("/api/bookings", HttpMethod.POST, request, body);
    }

    @GetMapping("/bookings/my-bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getMyBookings(HttpServletRequest request) {
        return forwardRequest("/api/bookings/my-bookings", HttpMethod.GET, request, null);
    }

    @GetMapping("/bookings/session/{sessionId}")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<String> getBookingsForSession(@PathVariable String sessionId, HttpServletRequest request) {
        return forwardRequest("/api/bookings/session/" + sessionId, HttpMethod.GET, request, null);
    }

    @DeleteMapping("/bookings/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> cancelBooking(@PathVariable String bookingId, HttpServletRequest request) {
        return forwardRequest("/api/bookings/" + bookingId, HttpMethod.DELETE, request, null);
    }
}