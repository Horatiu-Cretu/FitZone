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
@RequestMapping("/m3-proxy")
public class M3ForwardingController extends BaseController {

    private final RestTemplate restTemplate;

    @Value("${m3.service.url}")
    private String m3ServiceUrl;

    @Autowired
    public M3ForwardingController(RestTemplate restTemplate) {
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

    @PostMapping("/subscriptions/purchase")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> purchaseSubscription(@RequestBody String body, HttpServletRequest request) {
        String url = m3ServiceUrl + "/api/subscriptions/purchase";
        HttpEntity<String> entity = new HttpEntity<>(body, createHeaders(request));
        logger.info("Forwarding POST request to M3: {}", url);
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @GetMapping("/subscription-plans")
    @PreAuthorize("hasAnyRole('USER', 'TRAINER', 'ADMIN')")
    public ResponseEntity<String> getAllSubscriptionPlans(HttpServletRequest request) {
        String url = m3ServiceUrl + "/api/plans";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(request));
        logger.info("Forwarding GET request to M3 for subscription plans: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }
}