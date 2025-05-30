import { Routes } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { AdminOverviewComponent } from './admin-overview/admin-overview.component';
import { SubscriptionPlansComponent } from './subscription-plans/subscription-plans.component';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminDashboardComponent,
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: AdminOverviewComponent, title: 'Admin Overview - FitZone' },
      { path: 'subscription-plans', component: SubscriptionPlansComponent, title: 'Manage Subscriptions - FitZone Admin' },
    ]
  }
];
