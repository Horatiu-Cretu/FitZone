package project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.builder.UserSubscriptionBuilder;
import project.dto.PurchaseSubscriptionRequestDTO;
import project.dto.SubscriptionStatusResponseDTO;
import project.dto.UserSubscriptionViewDTO;
import project.entity.SubscriptionPlan;
import project.entity.UserSubscription;
import project.repository.SubscriptionPlanRepository;
import project.repository.UserSubscriptionRepository;
import project.util.PaymentStatus;
import project.util.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(UserSubscriptionServiceImpl.class);

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentGatewayClient paymentGatewayClient;

    @Autowired
    public UserSubscriptionServiceImpl(UserSubscriptionRepository userSubscriptionRepository,
                                       SubscriptionPlanRepository subscriptionPlanRepository,
                                       PaymentGatewayClient paymentGatewayClient) {
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.paymentGatewayClient = paymentGatewayClient;
    }

    @Override
    @Transactional
    public UserSubscriptionViewDTO purchaseSubscription(PurchaseSubscriptionRequestDTO purchaseRequest, Long userId) {
        logger.info("Attempting to purchase subscription for userId: {} with planId: {}", userId, purchaseRequest.getPlanId());

        SubscriptionPlan plan = subscriptionPlanRepository.findById(purchaseRequest.getPlanId())
                .orElseThrow(() -> {
                    logger.error("Subscription Plan not found with id: {}", purchaseRequest.getPlanId());
                    return new RuntimeException("Subscription Plan not found with id: " + purchaseRequest.getPlanId());
                });

        if (!plan.isActive()) {
            logger.warn("Attempt to purchase inactive planId: {}", purchaseRequest.getPlanId());
            throw new RuntimeException("Cannot purchase an inactive subscription plan.");
        }

        Optional<UserSubscription> existingActiveSubscription = userSubscriptionRepository
                .findActiveSubscriptionForUser(userId, SubscriptionStatus.ACTIVE, LocalDateTime.now());

        if (existingActiveSubscription.isPresent()) {
            logger.warn("User {} already has an active subscription.", userId);
            throw new RuntimeException("User already has an active subscription.");
        }


        PaymentStatus paymentStatus = paymentGatewayClient.processPayment(userId, plan.getPrice(), purchaseRequest.getPaymentConfirmationId());

        if (paymentStatus != PaymentStatus.SUCCESS) {
            logger.error("Payment failed for userId: {} for planId: {}. Status: {}", userId, plan.getId(), paymentStatus);
            throw new RuntimeException("Payment processing failed. Status: " + paymentStatus);
        }

        UserSubscription userSubscription = UserSubscriptionBuilder.toEntity(purchaseRequest, plan, userId);
        UserSubscription savedSubscription = userSubscriptionRepository.save(userSubscription);
        logger.info("Successfully purchased subscriptionId: {} for userId: {} with planId: {}", savedSubscription.getId(), userId, plan.getId());
        return UserSubscriptionBuilder.toViewDTO(savedSubscription);
    }

    @Override
    public UserSubscriptionViewDTO getCurrentUserSubscription(Long userId) {
        Optional<UserSubscription> activeSubscription = userSubscriptionRepository
                .findActiveSubscriptionForUser(userId, SubscriptionStatus.ACTIVE, LocalDateTime.now());

        if (activeSubscription.isPresent()) {
            return UserSubscriptionBuilder.toViewDTO(activeSubscription.get());
        } else {
            return userSubscriptionRepository.findByUserIdOrderByEndDateDesc(userId)
                    .stream()
                    .findFirst()
                    .map(UserSubscriptionBuilder::toViewDTO)
                    .orElse(null);
        }
    }

    @Override
    @Transactional
    public UserSubscriptionViewDTO cancelSubscription(Long userId) {
        UserSubscription activeSubscription = userSubscriptionRepository
                .findActiveSubscriptionForUser(userId, SubscriptionStatus.ACTIVE, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("No active subscription found to cancel for user: " + userId));

        if (activeSubscription.getEndDate().isBefore(LocalDateTime.now().plusDays(1))) {
            throw new RuntimeException("Subscription cannot be cancelled as it's expiring within 24 hours or has already expired.");
        }

        activeSubscription.setStatus(SubscriptionStatus.CANCELLED);

        UserSubscription savedSubscription = userSubscriptionRepository.save(activeSubscription);
        return UserSubscriptionBuilder.toViewDTO(savedSubscription);
    }

    @Override
    public SubscriptionStatusResponseDTO getUserSubscriptionStatus(Long userId) {
        Optional<UserSubscription> currentSubscriptionOpt = userSubscriptionRepository
                .findActiveSubscriptionForUser(userId, SubscriptionStatus.ACTIVE, LocalDateTime.now());

        if (currentSubscriptionOpt.isPresent()) {
            UserSubscription currentSubscription = currentSubscriptionOpt.get();
            return SubscriptionStatusResponseDTO.builder()
                    .userId(userId)
                    .eligible(true)
                    .currentStatus(currentSubscription.getStatus())
                    .subscriptionEndDate(currentSubscription.getEndDate())
                    .message("User has an active subscription.")
                    .build();
        } else {
            Optional<UserSubscription> latestSubscriptionOpt = userSubscriptionRepository.findByUserIdOrderByEndDateDesc(userId)
                    .stream()
                    .findFirst();

            if (latestSubscriptionOpt.isPresent()) {
                UserSubscription latestSubscription = latestSubscriptionOpt.get();
                return SubscriptionStatusResponseDTO.builder()
                        .userId(userId)
                        .eligible(false)
                        .currentStatus(latestSubscription.getStatus())
                        .subscriptionEndDate(latestSubscription.getEndDate())
                        .message("User does not have an active subscription. Last subscription ended/was " + latestSubscription.getStatus() + ".")
                        .build();
            }

            return SubscriptionStatusResponseDTO.builder()
                    .userId(userId)
                    .eligible(false)
                    .currentStatus(null)
                    .subscriptionEndDate(null)
                    .message("User has no subscription history.")
                    .build();
        }
    }
}