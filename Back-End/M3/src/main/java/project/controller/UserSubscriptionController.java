package project.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.dto.PurchaseSubscriptionRequestDTO;
import project.dto.SubscriptionStatusResponseDTO;
import project.dto.UserSubscriptionViewDTO;
import project.service.UserSubscriptionService;

@RestController
@RequestMapping("/api")
public class UserSubscriptionController extends BaseController {

    private final UserSubscriptionService userSubscriptionService;

    @Autowired
    public UserSubscriptionController(UserSubscriptionService userSubscriptionService) {
        this.userSubscriptionService = userSubscriptionService;
    }

    @PostMapping("/subscriptions/purchase")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserSubscriptionViewDTO> purchaseSubscription(@Valid @RequestBody PurchaseSubscriptionRequestDTO purchaseRequest, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserSubscriptionViewDTO purchasedSubscription = userSubscriptionService.purchaseSubscription(purchaseRequest, userId);
        return new ResponseEntity<>(purchasedSubscription, HttpStatus.CREATED);
    }

    @GetMapping("/subscriptions/my-subscription")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserSubscriptionViewDTO> getMySubscription(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserSubscriptionViewDTO subscription = userSubscriptionService.getCurrentUserSubscription(userId);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/subscriptions/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserSubscriptionViewDTO> cancelSubscription(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserSubscriptionViewDTO cancelledSubscription = userSubscriptionService.cancelSubscription(userId);
        return ResponseEntity.ok(cancelledSubscription);
    }

    @GetMapping("/internal/subscriptions/user/{userId}/status")
    public ResponseEntity<SubscriptionStatusResponseDTO> getUserSubscriptionStatus(@PathVariable Long userId) {
        SubscriptionStatusResponseDTO statusResponse = userSubscriptionService.getUserSubscriptionStatus(userId);
        return ResponseEntity.ok(statusResponse);
    }
}