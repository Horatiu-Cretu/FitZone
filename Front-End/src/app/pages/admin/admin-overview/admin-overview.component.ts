import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SubscriptionService, SubscriptionPlanViewDTO } from '../../../services/subscription.service';

@Component({
  selector: 'app-admin-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-overview.component.html',
  styleUrls: ['./admin-overview.component.css', '../../../dashboard.css']
})
export class AdminOverviewComponent implements OnInit {
  stats = {
    totalUsers: 0,
    activeSubscriptions: 0,
    totalTrainers: 0,
    popularClass: 'N/A',
    totalSubscriptionPlans: 0
  };
  isLoading = true;
  error: string | null = null;
  adminName: string = "Admin";

  constructor(private subscriptionService: SubscriptionService) {}

  ngOnInit(): void {
    this.loadAdminStats();
  }

  loadAdminStats(): void {
    this.isLoading = true;
    this.error = null;
    this.subscriptionService.getAllSubscriptionPlansForAdmin().subscribe({
      next: (plans) => {
        this.stats.totalSubscriptionPlans = plans.length;
        this.stats.totalUsers = 125;
        this.stats.activeSubscriptions = plans.filter(p => p.isActive).length * 10; // Mock calculation
        this.stats.totalTrainers = 10;
        this.stats.popularClass = 'Full Body Blast';

        this.isLoading = false;
      },
      error: (err) => {
        console.error("Error loading admin stats", err);
        this.error = "Failed to load some admin statistics.";
        this.isLoading = false;
      }
    });
  }
}
