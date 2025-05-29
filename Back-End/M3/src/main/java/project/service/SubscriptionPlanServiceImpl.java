package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.builder.SubscriptionPlanBuilder;
import project.dto.SubscriptionPlanDTO;
import project.dto.SubscriptionPlanViewDTO;
import project.entity.SubscriptionPlan;
import project.repository.SubscriptionPlanRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    public SubscriptionPlanServiceImpl(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Override
    @Transactional
    public SubscriptionPlanViewDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO) {
        SubscriptionPlan plan = SubscriptionPlanBuilder.toEntity(planDTO);
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        return SubscriptionPlanBuilder.toViewDTO(savedPlan);
    }

    @Override
    public SubscriptionPlanViewDTO getSubscriptionPlanById(Long planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Subscription Plan not found with id: " + planId));
        return SubscriptionPlanBuilder.toViewDTO(plan);
    }

    @Override
    public List<SubscriptionPlanViewDTO> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findAll().stream()
                .map(SubscriptionPlanBuilder::toViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionPlanViewDTO> getAllActiveSubscriptionPlans() {
        return subscriptionPlanRepository.findByIsActiveTrue().stream()
                .map(SubscriptionPlanBuilder::toViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubscriptionPlanViewDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO planDTO) {
        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Subscription Plan not found with id: " + planId));

        existingPlan.setName(planDTO.getName());
        existingPlan.setDescription(planDTO.getDescription());
        existingPlan.setPrice(planDTO.getPrice());
        existingPlan.setDurationDays(planDTO.getDurationDays());
        if (planDTO.getIsActive() != null) {
            existingPlan.setActive(planDTO.getIsActive());
        }

        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(existingPlan);
        return SubscriptionPlanBuilder.toViewDTO(updatedPlan);
    }

    @Override
    @Transactional
    public void deleteSubscriptionPlan(Long planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Subscription Plan not found with id: " + planId));
        plan.setActive(false);
        subscriptionPlanRepository.save(plan);
    }
}