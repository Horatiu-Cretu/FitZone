import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { WorkoutService, TrainingSessionViewDTO } from '../../../services/workout.service';

interface MySessionViewModel extends TrainingSessionViewDTO {
  displayDate: string;
  displayTime: string;
  className?: string;
}

@Component({
  selector: 'app-my-training-sessions',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe],
  templateUrl: './my-training-sessions.component.html',
  styleUrls: ['./my-training-sessions.component.css', '../../../dashboard.css']
})
export class MyTrainingSessionsComponent implements OnInit {
  sessions: MySessionViewModel[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(private workoutService: WorkoutService) { }

  ngOnInit(): void {
    this.loadSessions();
  }

  loadSessions(): void {
    this.isLoading = true;
    this.error = null;
    this.workoutService.getTrainerSessions().subscribe({
      next: (data) => {
        this.sessions = data.map(session => ({
          ...session,
          className: session.name,
          displayDate: new Date(session.startTime).toLocaleDateString(),
          displayTime: new Date(session.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        }));
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching trainer sessions', err);
        this.error = 'Failed to load your sessions.';
        this.isLoading = false;
      }
    });
  }
}
