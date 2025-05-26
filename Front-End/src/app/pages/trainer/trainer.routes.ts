import { Routes } from '@angular/router';
import { MyTrainingSessionsComponent } from './my-training-sessions/my-training-sessions.component';
import { SessionDetailsComponent } from './session-details/session-details.component';
import { ManageSessionsComponent } from './manage-sessions/manage-sessions.component';

export const TRAINER_ROUTES: Routes = [
  {
    path: 'my-training-sessions',
    component: MyTrainingSessionsComponent,
    title: 'My Sessions - FitZone Trainer'
  },
  {
    path: 'session-details/:sessionId',
    component: SessionDetailsComponent,
    title: 'Session Details - FitZone Trainer'
  },
  {
    path: 'manage-sessions',
    component: ManageSessionsComponent,
    title: 'Manage Sessions - FitZone Trainer'
  },

  {
    path: '',
    redirectTo: 'my-training-sessions',
    pathMatch: 'full'
  }
];
