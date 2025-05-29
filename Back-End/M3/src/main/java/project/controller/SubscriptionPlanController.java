package project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.dto.SubscriptionPlanDTO;
import project.dto.SubscriptionPlanViewDTO;
import project.service.SubscriptionPlanService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubscriptionPlanController extends BaseController {

    private final SubscriptionPlanService subscriptionPlanService;

    @Autowired
    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanViewDTO>> getAllSubscriptionPlans() {
        List<SubscriptionPlanViewDTO> plans = subscriptionPlanService.getAllSubscriptionPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/{planId}")
    public ResponseEntity<SubscriptionPlanViewDTO> getSubscriptionPlanById(@PathVariable Long planId) {
        SubscriptionPlanViewDTO plan = subscriptionPlanService.getSubscriptionPlanById(planId);
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/admin/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionPlanViewDTO> createSubscriptionPlan(@Valid @RequestBody SubscriptionPlanDTO planDTO) {
        SubscriptionPlanViewDTO createdPlan = subscriptionPlanService.createSubscriptionPlan(planDTO);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @PutMapping("/admin/plans/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionPlanViewDTO> updateSubscriptionPlan(@PathVariable Long planId, @Valid @RequestBody SubscriptionPlanDTO planDTO) {
        SubscriptionPlanViewDTO updatedPlan = subscriptionPlanService.updateSubscriptionPlan(planId, planDTO);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/admin/plans/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubscriptionPlan(@PathVariable Long planId) {
        subscriptionPlanService.deleteSubscriptionPlan(planId);
        return ResponseEntity.noContent().build();
    }
}