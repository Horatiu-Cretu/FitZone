import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { WorkoutService, TrainingSessionViewDTO } from '../../services/workout.service';

@Component({
  selector: 'app-workouts',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './workouts.component.html',
  styleUrls: ['./workouts.component.css']
})
export class WorkoutsComponent implements OnInit {

  workouts: TrainingSessionViewDTO[] = [];
  isLoading = true;
  error: string | null = null;

  private workoutImageMap: { [key: string]: string } = {
    'Full Body Blast': 'assets/workout-images/workout-blast.jpg',
    'Yoga Flow': 'assets/workout-images/yoga.jpg',
    'Strength & Conditioning': 'assets/workout-images/Strenght-Conditioning.jpg',
    'Spin Cycle Challenge': 'assets/workout-images/cycle.jpg',
    'Boxing Fundamentals': 'assets/workout-images/box.jpg',
    'Mat Pilates': 'assets/workout-images/pilates.jpg'
  };
  private mockDetailsEnhancer(session: TrainingSessionViewDTO): TrainingSessionViewDTO {
    const enhancedSession = { ...session };
    enhancedSession.trainer = `Trainer ID: ${session.trainerId}`;
    enhancedSession.time = new Date(session.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' - ' + new Date(session.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    enhancedSession.imageUrl = this.workoutImageMap[session.name] || 'https://placehold.co/300x200/cccccc/FFFFFF?text=Gym+Class';
    return enhancedSession;
  }


  constructor(private workoutService: WorkoutService) {}

  ngOnInit(): void {
    this.loadWorkouts();
  }

  loadWorkouts(): void {
    this.isLoading = true;
    this.error = null;
    this.workoutService.getAllWorkouts().subscribe({
      next: (data) => {
        this.workouts = data.map(session => this.mockDetailsEnhancer(session));
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching workouts', err);
        this.error = 'Failed to load workouts. Please try again later.';
        this.isLoading = false;
      }
    });
  }
}
