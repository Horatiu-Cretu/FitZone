import { Routes } from '@angular/router';
import { ClientDashboardComponent } from './client-dashboard/client-dashboard.component';
import { ClientOverviewComponent } from './client-overview/client-overview.component';
import { MyBookingsComponent } from './my-bookings/my-bookings.component';
import { BuyMembershipComponent } from './buy-membership/buy-membership.component';
import { MySubscriptionsComponent } from './my-subscriptions/my-subscriptions.component';
import { WorkoutsComponent } from '../workouts/workouts.component';
import { ScheduleComponent } from '../schedule/schedule.component';


export const CLIENT_ROUTES: Routes = [
  {
    path: '',
    component: ClientDashboardComponent,
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: ClientOverviewComponent, title: 'My Dashboard - FitZone' },
      { path: 'my-bookings', component: MyBookingsComponent, title: 'My Bookings - FitZone' },
      { path: 'buy-membership', component: BuyMembershipComponent, title: 'Buy Membership - FitZone' },
      { path: 'my-subscriptions', component: MySubscriptionsComponent, title: 'My Subscriptions - FitZone'},
      { path: 'workouts', component: WorkoutsComponent, title: 'Workouts - FitZone' },
      { path: 'schedule/:workoutId', component: ScheduleComponent, title: 'Workout Schedule - FitZone' }
    ]
  }
];
