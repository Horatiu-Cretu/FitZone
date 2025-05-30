import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BookingService, BookingViewDTO } from '../../../services/booking.service';

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './my-bookings.component.html',
  styleUrls: ['./my-bookings.component.css', '../../../dashboard.css']
})
export class MyBookingsComponent implements OnInit {
  bookings: BookingViewDTO[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(private bookingService: BookingService) { }

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.isLoading = true;
    this.error = null;
    this.bookingService.getClientBookings().subscribe({
      next: (data) => {
        this.bookings = data.map(b => ({
          ...b,
          workoutName: b.trainingSessionName,
          date: new Date(b.bookingTime).toLocaleDateString(),
          time: new Date(b.bookingTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          trainer: 'N/A (Fetch if needed)'
        }));
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching bookings', err);
        this.error = 'Failed to load your bookings.';
        this.isLoading = false;
      }
    });
  }

  cancelBooking(bookingId: number): void {
    if (!confirm('Are you sure you want to cancel this booking?')) {
      return;
    }
    this.bookingService.cancelBooking(bookingId).subscribe({
      next: () => {
        alert('Booking cancelled successfully.');
        this.loadBookings();
      },
      error: (err) => {
        console.error('Error cancelling booking', err);
        alert(`Failed to cancel booking: ${err.error?.error || err.message}`);
      }
    });
  }
}
