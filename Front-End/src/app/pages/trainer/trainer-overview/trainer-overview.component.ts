import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { WorkoutService, TrainingSessionViewDTO } from '../../../services/workout.service'; // Import service

@Component({
  selector: 'app-trainer-overview',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe],
  templateUrl: './trainer-overview.component.html',
  styleUrls: ['./trainer-overview.component.css', '../../../dashboard.css']
})
export class TrainerOverviewComponent implements OnInit {
  upcomingSessions: TrainingSessionViewDTO[] = [];
  totalParticipantsToday = 0;
  isLoading = true;
  error: string | null = null;
  trainerName: string = "Trainer";

  constructor(private workoutService: WorkoutService) {}

  ngOnInit(): void {
    this.loadUpcomingSessions();
  }

  loadUpcomingSessions(): void {
    this.isLoading = true;
    this.error = null;
    this.workoutService.getTrainerSessions().subscribe({
      next: (sessions) => {
        const now = new Date();
        this.upcomingSessions = sessions
          .filter(s => new Date(s.startTime) >= now)
          .sort((a,b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime())
          .map(s => ({
            ...s,
            className: s.name,

          }));

        const todayStr = now.toISOString().split('T')[0];
        this.totalParticipantsToday = sessions
          .filter(s => s.startTime.startsWith(todayStr))
          .reduce((sum, current) => sum + current.currentParticipants, 0);

        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching upcoming sessions for trainer', err);
        this.error = "Failed to load upcoming sessions.";
        this.isLoading = false;
      }
    });
  }
}
