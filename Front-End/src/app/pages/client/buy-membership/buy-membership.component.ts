import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SubscriptionService, SubscriptionPlanViewDTO, PurchaseSubscriptionRequestDTO } from '../../../services/subscription.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-buy-membership',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './buy-membership.component.html',
  styleUrls: ['./buy-membership.component.css']
})
export class BuyMembershipComponent implements OnInit {
  plans: SubscriptionPlanViewDTO[] = [];
  isLoading = true;
  error: string | null = null;
  purchaseMessage: string | null = null;


  constructor(private subscriptionService: SubscriptionService, private router: Router) { }

  ngOnInit(): void {
    this.loadPlans();
  }

  loadPlans(): void {
    this.isLoading = true;
    this.error = null;
    this.subscriptionService.getAllSubscriptionPlansForClient().subscribe({
      next: (data) => {
        this.plans = data.map(plan => ({
          ...plan,
          features: plan.description ? plan.description.split(',').map(f => f.trim()) : ['Basic gym access.'],
          highlight: plan.name.toLowerCase().includes('gold')
        }));
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching membership plans', err);
        this.error = 'Failed to load membership plans.';
        this.isLoading = false;
      }
    });
  }

  selectPlan(planId: number | string): void {
    if (!confirm(`Are you sure you want to purchase this plan (ID: ${planId})? Payment will be simulated.`)) {
      return;
    }
    this.purchaseMessage = null;
    const purchaseData: PurchaseSubscriptionRequestDTO = {
      planId: planId,
      paymentConfirmationId: 'SIMULATED_PAYMENT_SUCCESS_' + Date.now()
    };

    this.subscriptionService.purchaseSubscription(purchaseData).subscribe({
      next: (response) => {
        this.purchaseMessage = `Successfully purchased ${response.planName}! Your subscription is now ${response.status}.`;
        alert(this.purchaseMessage);
        this.router.navigate(['/client/my-subscriptions']);
      },
      error: (err) => {
        console.error('Error purchasing subscription', err);
        this.purchaseMessage = `Purchase failed: ${err.error?.error || err.message}`;
        alert(this.purchaseMessage);
      }
    });
  }
}
