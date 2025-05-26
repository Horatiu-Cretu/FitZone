import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface TrainingSession {
  id: string;
  className: string;
  date: string;
  time: string;
  participants: number;
  capacity: number;
}

@Component({
  selector: 'app-my-training-sessions',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './my-training-sessions.component.html',
  styleUrls: ['./my-training-sessions.component.css']
})
export class MyTrainingSessionsComponent {
  sessions: TrainingSession[] = [
    { id: 'ts001', className: 'Full Body Blast', date: '2025-06-10', time: '6:00 PM', participants: 15, capacity: 20 },
    { id: 'ts002', className: 'Strength & Conditioning', date: '2025-06-11', time: '7:00 AM', participants: 10, capacity: 15 },
    { id: 'ts003', className: 'Boxing Fundamentals', date: '2025-06-12', time: '5:00 PM', participants: 12, capacity: 12 }
  ];
  constructor() { }
}
