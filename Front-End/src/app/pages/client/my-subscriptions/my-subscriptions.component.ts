import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SubscriptionService, UserSubscriptionViewDTO } from '../../../services/subscription.service';

@Component({
  selector: 'app-my-subscriptions',
  standalone: true,
  imports: [CommonModule, RouterModule, CurrencyPipe, DatePipe],
  templateUrl: './my-subscriptions.component.html',
  styleUrls: ['./my-subscriptions.component.css', '../../../dashboard.css']
})
export class MySubscriptionsComponent implements OnInit {
  activeSubscription: UserSubscriptionViewDTO | null = null;
  latestSubscription: UserSubscriptionViewDTO | null = null;

  isLoading = true;
  error: string | null = null;
  cancelMessage: string | null = null;

  constructor(private subscriptionService: SubscriptionService) {}

  ngOnInit(): void {
    this.loadSubscription();
  }

  loadSubscription(): void {
    this.isLoading = true;
    this.error = null;
    this.cancelMessage = null;
    this.subscriptionService.getMySubscription().subscribe({
      next: (data) => {
        if (data) {
          this.latestSubscription = data;
          if (data.status === 'ACTIVE') {
            this.activeSubscription = data;
          } else {
            this.activeSubscription = null;
          }
        } else {
          this.latestSubscription = null;
          this.activeSubscription = null;
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching subscription', err);
        if (err.status === 404 || err.error?.message?.toLowerCase().includes('not found')) {
          this.error = 'No subscription found.';
          this.latestSubscription = null;
          this.activeSubscription = null;
        } else {
          this.error = 'Failed to load your subscription details.';
        }
        this.isLoading = false;
      }
    });
  }

  getCanCancel(sub: UserSubscriptionViewDTO | null): boolean {
    if (!sub) return false;
    return sub.status === 'ACTIVE' && new Date(sub.endDate) > new Date(Date.now() + 24 * 60 * 60 * 1000);
  }


  requestCancelSubscription(subId: number): void {
    if (!confirm('Are you sure you want to cancel this subscription?')) {
      return;
    }
    this.cancelMessage = null;
    this.subscriptionService.cancelMySubscription().subscribe({
      next: (response) => {
        this.cancelMessage = `Subscription ${response.planName} is now ${response.status}.`;
        alert(this.cancelMessage);
        this.loadSubscription();
      },
      error: (err) => {
        console.error('Error cancelling subscription', err);
        this.cancelMessage = `Failed to cancel subscription: ${err.error?.error || err.message}`;
        alert(this.cancelMessage);
      }
    });
  }
}
