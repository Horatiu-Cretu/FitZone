package project.service;

import project.dto.SubscriptionPlanDTO;
import project.dto.SubscriptionPlanViewDTO;

import java.util.List;

public interface SubscriptionPlanService {
    SubscriptionPlanViewDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO);
    SubscriptionPlanViewDTO getSubscriptionPlanById(Long planId);
    List<SubscriptionPlanViewDTO> getAllSubscriptionPlans();
    List<SubscriptionPlanViewDTO> getAllActiveSubscriptionPlans();
    SubscriptionPlanViewDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO planDTO);
    void deleteSubscriptionPlan(Long planId);
}