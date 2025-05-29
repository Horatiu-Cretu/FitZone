package project.service;

import project.dto.PurchaseSubscriptionRequestDTO;
import project.dto.SubscriptionStatusResponseDTO;
import project.dto.UserSubscriptionViewDTO;

public interface UserSubscriptionService {
    UserSubscriptionViewDTO purchaseSubscription(PurchaseSubscriptionRequestDTO purchaseRequest, Long userId);
    UserSubscriptionViewDTO getCurrentUserSubscription(Long userId);
    UserSubscriptionViewDTO cancelSubscription(Long userId);
    SubscriptionStatusResponseDTO getUserSubscriptionStatus(Long userId);
}