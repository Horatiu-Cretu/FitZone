package project.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import project.util.PaymentStatus;

import java.math.BigDecimal;

@Component
public class PaymentGatewayClient {

    private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayClient.class);

    public PaymentStatus processPayment(Long userId, BigDecimal amount, String paymentConfirmationId) {
        logger.info("Attempting to process payment for userId: {}, amount: {}, confirmationId: {}", userId, amount, paymentConfirmationId);

        if (paymentConfirmationId == null || paymentConfirmationId.trim().isEmpty()) {
            logger.warn("Payment processing skipped for userId: {}. No payment confirmation ID provided. Simulating SUCCESS for now.", userId);
            return PaymentStatus.SUCCESS;
        }


        if ("FAIL_PAYMENT".equalsIgnoreCase(paymentConfirmationId)) {
            logger.warn("Simulating FAILED payment for userId: {} due to confirmationId: {}", userId, paymentConfirmationId);
            return PaymentStatus.FAILED;
        }

        if ("PENDING_PAYMENT".equalsIgnoreCase(paymentConfirmationId)) {
            logger.info("Simulating PENDING payment for userId: {} due to confirmationId: {}", userId, paymentConfirmationId);
            return PaymentStatus.PENDING;
        }

        logger.info("Payment processed successfully for userId: {}, amount: {}. Confirmation ID: {}", userId, amount, paymentConfirmationId);
        return PaymentStatus.SUCCESS;
    }
}