import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { WelcomeComponent } from './pages/welcome/welcome.component';
import { WorkoutsComponent } from './pages/workouts/workouts.component';
import { BookingComponent } from './pages/booking/booking.component';

const routes: Routes = [
  { path: '', redirectTo: '/welcome', pathMatch: 'full' },
  { path: 'welcome', component: WelcomeComponent, title: 'Welcome - GymName' },
  { path: 'workouts', component: WorkoutsComponent, title: 'Workouts - GymName' },
  { path: 'booking', component: BookingComponent, title: 'Book a Session - GymName' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
