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
    @GetMapping("/subscription-plans/{planId}")
    @PreAuthorize("hasAnyRole('USER', 'TRAINER', 'ADMIN')")
    public ResponseEntity<String> getSubscriptionPlanById(@PathVariable Long planId, HttpServletRequest request) {
        String url = m3ServiceUrl + "/api/plans/" + planId;
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(request));
        logger.info("Forwarding GET request to M3 for planId {}: {}", planId, url);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    @PostMapping("/admin/subscription-plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createSubscriptionPlan(@RequestBody String body, HttpServletRequest request) {
        String url = m3ServiceUrl + "/api/admin/plans";
        HttpEntity<String> entity = new HttpEntity<>(body, createHeaders(request));
        logger.info("Forwarding POST request to M3 for creating plan: {}", url);
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @PutMapping("/admin/subscription-plans/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateSubscriptionPlan(@PathVariable Long planId, @RequestBody String body, HttpServletRequest request) {
        String url = m3ServiceUrl + "/api/admin/plans/" + planId;
        HttpEntity<String> entity = new HttpEntity<>(body, createHeaders(request));
        logger.info("Forwarding PUT request to M3 for planId {}: {}", planId, url);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    @DeleteMapping("/admin/subscription-plans/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSubscriptionPlan(@PathVariable Long planId, HttpServletRequest request) {
        String url = m3ServiceUrl + "/api/admin/plans/" + planId;
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(request));
        logger.info("Forwarding DELETE request to M3 for planId {}: {}", planId, url);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }

    @GetMapping("/subscriptions/my-subscription")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getMySubscription(HttpServletRequest request) {
        String url = m3ServiceUrl + "/api/subscriptions/my-subscription";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(request));
        logger.info("Forwarding GET request to M3 for current user subscription: {}", url);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    @PostMapping("/subscriptions/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> cancelSubscription(HttpServletRequest request) {
        String url = m3ServiceUrl + "/api/subscriptions/cancel";
        HttpEntity<String> entity = new HttpEntity<>("", createHeaders(request));
        logger.info("Forwarding POST request to M3 for cancelling subscription: {}", url);
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @GetMapping("/internal/subscriptions/user/{userId}/status")
    public ResponseEntity<String> getUserSubscriptionStatus(@PathVariable Long userId, HttpServletRequest request) {
        String url = m3ServiceUrl + "/api/internal/subscriptions/user/" + userId + "/status";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(request));
        logger.info("Forwarding GET request to M3 for user {} subscription status: {}", userId, url);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

}