import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { BookingService, BookingRequestDTO, BookingViewDTO } from '../../services/booking.service';
import { WorkoutService, TrainingSessionViewDTO } from '../../services/workout.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { OnInit } from '@angular/core';

interface BookingFormModel {
  selectedWorkoutId: string | null;

}

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.css']
})
export class BookingComponent implements OnInit {
  public submissionMessage: string | null = null;
  public bookingError: string | null = null;
  public model: BookingFormModel = { selectedWorkoutId: null };
  public availableWorkouts: TrainingSessionViewDTO[] = [];

  constructor(
    private bookingService: BookingService,
    private workoutService: WorkoutService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAvailableWorkouts();
  }

  loadAvailableWorkouts(): void {
    this.workoutService.getAllWorkouts().subscribe({
      next: (workouts) => {
        this.availableWorkouts = workouts.filter(w => new Date(w.startTime) > new Date() && w.currentParticipants < w.capacity);
      },
      error: (err) => {
        console.error("Failed to load workouts for booking form", err);
        this.bookingError = "Could not load available workouts.";
      }
    });
  }

  public submitBooking(form: NgForm): void {
    this.submissionMessage = null;
    this.bookingError = null;

    if (!this.authService.getToken()) {
      this.bookingError = "You must be logged in to make a booking.";
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/booking' } });
      return;
    }

    if (form.invalid || !this.model.selectedWorkoutId) {
      this.bookingError = "Please select a workout.";
      return;
    }

    const bookingData: BookingRequestDTO = {
      trainingSessionId: this.model.selectedWorkoutId
    };

    this.bookingService.createBooking(bookingData).subscribe({
      next: (response: BookingViewDTO) => {
        this.submissionMessage = `Booking successful for ${response.trainingSessionName}! Booking ID: ${response.id}. Check "My Bookings".`;
        form.resetForm({ selectedWorkoutId: null });
        this.model = { selectedWorkoutId: null };
      },
      error: (err) => {
        console.error('Booking submission failed', err);
        this.bookingError = `Booking failed: ${err.error?.error || err.message || 'Please try again.'}`;
      }
    });
  }
}
