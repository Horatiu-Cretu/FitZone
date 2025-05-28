package project.controller.forwarding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import project.controller.BaseController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/m2-proxy")
public class M2ForwardingController extends BaseController {

    private final RestTemplate restTemplate;

    @Value("${m2.service.url}")
    private String m2ServiceUrl;

    @Autowired
    public M2ForwardingController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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

    @GetMapping("/training-sessions")
    @PreAuthorize("hasAnyRole('USER', 'TRAINER', 'ADMIN')")
    public ResponseEntity<String> getAllTrainingSessions(HttpServletRequest request) {
        String url = m2ServiceUrl + "/api/training-sessions";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(request));
        logger.info("Forwarding GET request to M2: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    @PostMapping("/training-sessions")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<String> createTrainingSession(@RequestBody String body, HttpServletRequest request) {
        String url = m2ServiceUrl + "/api/training-sessions";
        HttpEntity<String> entity = new HttpEntity<>(body, createHeaders(request));
        logger.info("Forwarding POST request to M2: {}", url);
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @GetMapping("/training-sessions/{sessionId}/enrolled-clients")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<String> getEnrolledClients(@PathVariable String sessionId, HttpServletRequest request) {
        String url = m2ServiceUrl + "/api/training-sessions/" + sessionId + "/enrolled-clients";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(request));
        logger.info("Forwarding GET request to M2: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> createBooking(@RequestBody String body, HttpServletRequest request) {
        String url = m2ServiceUrl + "/api/bookings";
        HttpEntity<String> entity = new HttpEntity<>(body, createHeaders(request));
        logger.info("Forwarding POST request to M2 (booking): {}", url);
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}