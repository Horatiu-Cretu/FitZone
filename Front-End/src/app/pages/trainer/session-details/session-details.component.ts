import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { WorkoutService, TrainingSessionViewDTO, EnrolledClientViewDTO } from '../../../services/workout.service';
import { forkJoin } from 'rxjs';

interface SessionDetailsView extends TrainingSessionViewDTO {
  clients: EnrolledClientViewDTO[];
  displayDate?: string;
  displayTime?: string;
  room?: string;
}

@Component({
  selector: 'app-session-details',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe],
  templateUrl: './session-details.component.html',
  styleUrls: ['./session-details.component.css', '../../../dashboard.css']
})
export class SessionDetailsComponent implements OnInit {
  session: SessionDetailsView | null = null;
  loadingError: string | null = null;
  isLoading = true;
  sessionIdFromRoute: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private workoutService: WorkoutService
  ) {}

  ngOnInit(): void {
    this.sessionIdFromRoute = this.route.snapshot.paramMap.get('sessionId');
    if (this.sessionIdFromRoute) {
      this.loadDetails(this.sessionIdFromRoute);
    } else {
      this.loadingError = "Session ID not provided in route.";
      this.isLoading = false;
    }
  }

  loadDetails(idStr: string): void {
    this.isLoading = true;
    this.loadingError = null;

    forkJoin({
      sessionDetails: this.workoutService.getWorkoutById(idStr),
      enrolledClients: this.workoutService.getEnrolledClients(idStr)
    }).subscribe({
      next: ({ sessionDetails, enrolledClients }) => {
        const numericId = sessionDetails.id;
        const roomData = (numericId === 1) ? 'Studio A' : (numericId === 2 ? 'Main Hall' : 'Studio B'); // Example mock room

        this.session = {
          ...sessionDetails,
          clients: enrolledClients.map(client => ({
            ...client,
            name: client.clientName || `Client ID: ${client.clientId}`,
            email: client.clientEmail || 'N/A',
            bookingDate: client.bookingTime,
            status: 'Confirmed'
          })),
          displayDate: new Date(sessionDetails.startTime).toLocaleDateString(undefined, { dateStyle: 'full' }),
          displayTime: `${new Date(sessionDetails.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} - ${new Date(sessionDetails.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`,
          room: roomData,
        };
        this.isLoading = false;
      },
      error: (err) => {
        console.error(`Error fetching details for session ${idStr}`, err);
        this.loadingError = `Failed to load session details: ${err.error?.error || err.message}`;
        this.isLoading = false;
      }
    });
  }
}
