import { Routes } from '@angular/router';

import { WelcomeComponent } from './pages/welcome/welcome.component';
import { WorkoutsComponent } from './pages/workouts/workouts.component';
import { BookingComponent } from './pages/booking/booking.component';
import { ScheduleComponent } from './pages/schedule/schedule.component';
import { LoginComponent } from './pages/login/login.component';
import { SignupComponent } from './pages/signup/signup.component';

export const routes: Routes = [
  { path: '', redirectTo: '/welcome', pathMatch: 'full' },
  { path: 'welcome', component: WelcomeComponent, title: 'Welcome - FitZone' },
  { path: 'workouts', component: WorkoutsComponent, title: 'Workouts - FitZone' },
  { path: 'booking', component: BookingComponent, title: 'Book a Session - FitZone' },
  { path: 'schedule/:workoutId', component: ScheduleComponent, title: 'Workout Schedule - FitZone' },

  { path: 'login', component: LoginComponent, title: 'Login - FitZone' },
  { path: 'signup', component: SignupComponent, title: 'Sign Up - FitZone' },

];