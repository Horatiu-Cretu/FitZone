import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { SubscriptionService, SubscriptionPlanViewDTO, SubscriptionPlanDTO } from '../../../services/subscription.service';
import {Observable} from 'rxjs';

interface PlanFormModel extends SubscriptionPlanDTO {
  id?: string;
  featuresCsv?: string;
}

@Component({
  selector: 'app-subscription-plans',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './subscription-plans.component.html',
  styleUrls: ['./subscription-plans.component.css', '../../../dashboard.css']
})
export class SubscriptionPlansComponent implements OnInit {
  showForm = false;
  editingPlanOriginal: SubscriptionPlanViewDTO | null = null;
  currentPlanForm: PlanFormModel = this.getEmptyPlanForm();

  plans: SubscriptionPlanViewDTO[] = [];
  isLoading = true;
  error: string | null = null;
  formError: string | null = null;

  constructor(private subscriptionService: SubscriptionService) {}

  ngOnInit(): void {
    this.loadPlans();
  }

  loadPlans(): void {
    this.isLoading = true;
    this.error = null;
    this.subscriptionService.getAllSubscriptionPlansForAdmin().subscribe({
      next: (data) => {
        this.plans = data.map(p => ({...p, features: p.description?.split(',').map(f => f.trim()) || [] }));
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching plans for admin', err);
        this.error = 'Failed to load subscription plans.';
        this.isLoading = false;
      }
    });
  }

  openAddPlanModal(): void {
    this.editingPlanOriginal = null;
    this.currentPlanForm = this.getEmptyPlanForm();
    this.showForm = true;
    this.formError = null;
  }

  editPlan(plan: SubscriptionPlanViewDTO): void {
    this.editingPlanOriginal = plan;
    this.currentPlanForm = {
      id: plan.id.toString(),
      name: plan.name,
      price: plan.price,
      durationDays: plan.durationDays,
      description: plan.description,
      featuresCsv: plan.features?.join(', ') || plan.description,
      isActive: plan.isActive,
    };
    this.showForm = true;
    this.formError = null;
  }

  deletePlan(planId: number | string): void {
    if (!confirm(`Are you sure you want to delete plan ID: ${planId}?`)) return;

    this.subscriptionService.deleteAdminSubscriptionPlan(planId).subscribe({
      next: () => {
        alert('Plan deleted (marked as inactive by backend).');
        this.loadPlans(); // Refresh
      },
      error: (err) => {
        console.error('Error deleting plan', err);
        alert(`Failed to delete plan: ${err.error?.error || err.message}`);
      }
    });
  }

  submitPlanForm(form: NgForm): void {
    this.formError = null;
    if (form.invalid) {
      this.formError = "Please fill in all required fields correctly.";
      return;
    }

    const planToSave: SubscriptionPlanDTO = {
      name: this.currentPlanForm.name,
      description: this.currentPlanForm.featuresCsv || this.currentPlanForm.description,
      price: Number(this.currentPlanForm.price),
      durationDays: Number(this.currentPlanForm.durationDays),
      isActive: this.currentPlanForm.isActive === undefined ? true : this.currentPlanForm.isActive,
    };


    let operation: Observable<SubscriptionPlanViewDTO>;

    if (this.editingPlanOriginal && this.currentPlanForm.id) {
      operation = this.subscriptionService.updateAdminSubscriptionPlan(this.currentPlanForm.id, planToSave);
    } else {
      operation = this.subscriptionService.createAdminSubscriptionPlan(planToSave);
    }

    operation.subscribe({
      next: (savedPlan) => {
        alert(`Plan ${savedPlan.name} ${this.editingPlanOriginal ? 'updated' : 'created'} successfully!`);
        this.cancelForm(form);
        this.loadPlans();
      },
      error: (err) => {
        console.error('Error saving plan', err);
        this.formError = `Failed to save plan: ${err.error?.error || err.message}`;
      }
    });
  }

  cancelForm(form?: NgForm): void {
    if (form) {
      form.resetForm(this.getEmptyPlanForm());
    }
    this.showForm = false;
    this.editingPlanOriginal = null;
    this.formError = null;
  }

  getEmptyPlanForm(): PlanFormModel {
    return { name: '', price: 0, durationDays: 30, description: '', featuresCsv: '', isActive: true };
  }
}
