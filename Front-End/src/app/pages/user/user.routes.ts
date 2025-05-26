import { Routes } from '@angular/router';
import { MyBookingsComponent } from './my-bookings/my-bookings.component';
import { BuyMembershipComponent } from './buy-membership/buy-membership.component';

export const USER_ROUTES: Routes = [
  {
    path: 'my-bookings',
    component: MyBookingsComponent,
    title: 'My Bookings - FitZone'
  },
  {
    path: 'buy-membership',
    component: BuyMembershipComponent,
    title: 'Buy Membership - FitZone'
  },
  {
    path: '',
    redirectTo: 'my-bookings',
    pathMatch: 'full'
  }
];

