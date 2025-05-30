import { Routes } from '@angular/router';
import { TrainerDashboardComponent } from './trainer-dashboard/trainer-dashboard.component';
import { TrainerOverviewComponent } from './trainer-overview/trainer-overview.component';
import { MyTrainingSessionsComponent } from './my-training-sessions/my-training-sessions.component';
import { SessionDetailsComponent } from './session-details/session-details.component';
import { ManageSessionsComponent } from './manage-sessions/manage-sessions.component';

export const TRAINER_ROUTES: Routes = [
  {
    path: '',
    component: TrainerDashboardComponent,
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: TrainerOverviewComponent, title: 'Trainer Dashboard - FitZone' },
      { path: 'my-sessions', component: MyTrainingSessionsComponent, title: 'My Sessions - FitZone Trainer' },
      { path: 'session-details/:sessionId', component: SessionDetailsComponent, title: 'Session Details - FitZone Trainer' },
      { path: 'manage-sessions', component: ManageSessionsComponent, title: 'Manage Sessions - FitZone Trainer' }
    ]
  }
];
