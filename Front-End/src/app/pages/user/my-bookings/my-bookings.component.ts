import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Booking {
  id: string;
  workoutName: string;
  date: string;
  time: string;
  trainer: string;
  status: string;
}

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-bookings.component.html',
  styleUrls: ['./my-bookings.component.css']
})
export class MyBookingsComponent {
  bookings: Booking[] = [
    { id: 'b001', workoutName: 'Full Body Blast', date: '2025-06-10', time: '6:00 PM', trainer: 'Alex Johnson', status: 'Confirmed' },
    { id: 'b002', workoutName: 'Yoga Flow', date: '2025-06-12', time: '9:00 AM', trainer: 'Maria Garcia', status: 'Confirmed' },
    { id: 'b003', workoutName: 'Spin Cycle Challenge', date: '2025-06-15', time: '7:00 AM', trainer: 'Sarah Chen', status: 'Cancelled' }
  ];

  constructor() { }
}

