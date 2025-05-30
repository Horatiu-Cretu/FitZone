import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { SubscriptionService, SubscriptionPlanViewDTO, SubscriptionPlanDTO } from '../../../services/subscription.service';
import {Observable} from 'rxjs';

interface PlanFormComponentModel {
  id?: string;
  name: string;
  price: number;
  durationDays: number;
  description: string;
  isActive: boolean;
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
  currentPlanForm: PlanFormComponentModel = this.getEmptyPlanForm();

  plans: SubscriptionPlanViewDTO[] = [];
  isLoading = true;
  error: string | null = null;
  formError: string | null = null;

  constructor(private subscriptionService: SubscriptionService) {}

  ngOnInit(): void {
    console.log('[SubscriptionPlansComponent] ngOnInit: Component initialized, calling loadPlans().');
    this.loadPlans();
  }

  loadPlans(): void {
    console.log('[SubscriptionPlansComponent] loadPlans(): Starting to load plans.');
    this.isLoading = true;
    this.error = null;
    this.subscriptionService.getAllSubscriptionPlansForAdmin().subscribe({
      next: (data: any[]) => {
        console.log('[SubscriptionPlansComponent] loadPlans(): Successfully received data:', data);
        this.plans = data.map((p: any) => {
          const mappedPlan: SubscriptionPlanViewDTO = {
            id: p.id,
            name: p.name,
            description: p.description,
            price: p.price,
            durationDays: p.durationDays,
            isActive: p.isActive, // Keep the fix from before
            features: p.description?.split(',').map((f: string) => f.trim()) || [],
          };
          return mappedPlan;
        });
        this.isLoading = false;
      },
      error: (err) => {
        console.error('[SubscriptionPlansComponent] loadPlans(): Error fetching plans for admin. Full error object:', err);
        console.error(`[SubscriptionPlansComponent] loadPlans(): Status: ${err.status}, StatusText: ${err.statusText}, URL: ${err.url}`);
        if (err.error) {
          console.error('[SubscriptionPlansComponent] loadPlans(): Error body:', err.error);
        }
        this.error = `Failed to load subscription plans. Status: ${err.status} - ${err.statusText || 'Unknown Error'}. Check console for details.`;
        this.isLoading = false;
      }
    });
  }

  getEmptyPlanForm(): PlanFormComponentModel {
    return { name: '', price: 0, durationDays: 30, description: '', featuresCsv: '', isActive: true };
  }

  openAddPlanModal(): void {
    console.log('[SubscriptionPlansComponent] openAddPlanModal(): Opening form to add new plan.');
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
    if (!confirm(`Are you sure you want to delete plan ID: ${planId}? This will mark it as inactive.`)) return;

    this.subscriptionService.deleteAdminSubscriptionPlan(planId).subscribe({
      next: () => {
        alert('Plan marked as inactive successfully.');
        this.loadPlans();
      },
      error: (err) => {
        console.error('Error deleting plan', err);
        alert(`Failed to delete plan: ${err.error?.error || err.message}`);
      }
    });
  }

  submitPlanForm(form: NgForm): void {
    console.log('[SubscriptionPlansComponent] submitPlanForm(): Attempting to submit plan form. Editing:', !!this.editingPlanOriginal, 'Data:', this.currentPlanForm);
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
      isActive: this.currentPlanForm.isActive,
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
    this.currentPlanForm = this.getEmptyPlanForm();
    this.showForm = false;
    this.editingPlanOriginal = null;
    this.formError = null;
  }

  get formTitle(): string {
    if (this.editingPlanOriginal) {
      return 'Edit Plan: ' + this.editingPlanOriginal.name;
    }
    return 'Add New Plan';
  }
}
