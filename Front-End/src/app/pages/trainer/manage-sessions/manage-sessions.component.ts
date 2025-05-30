import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { WorkoutService, TrainingSessionViewDTO, TrainingSessionDTO } from '../../../services/workout.service';

interface SessionFormModel {
  id?: string;
  name: string;
  date: string;
  time: string;
  capacity: number;
  room: string;
  description: string;
}

@Component({
  selector: 'app-manage-sessions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-sessions.component.html',
  styleUrls: ['./manage-sessions.component.css', '../../../dashboard.css']
})
export class ManageSessionsComponent implements OnInit {
  sessions: TrainingSessionViewDTO[] = [];
  isLoading = true;
  error: string | null = null;

  showAddForm = false;
  newSession: SessionFormModel = this.getEmptyFormModel();
  editingSession: TrainingSessionViewDTO | null = null;

  constructor(private workoutService: WorkoutService) { }

  ngOnInit(): void {
    this.loadTrainerSessions();
  }

  loadTrainerSessions(): void {
    this.isLoading = true;
    this.error = null;
    this.workoutService.getTrainerSessions().subscribe({
      next: (data) => {
        this.sessions = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching trainer sessions', err);
        this.error = 'Failed to load your sessions for management.';
        this.isLoading = false;
      }
    });
  }

  getEmptyFormModel(): SessionFormModel {
    return { name: '', date: '', time: '', capacity: 10, room: 'Studio A', description: '' };
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    this.editingSession = null;
    this.newSession = this.getEmptyFormModel();
  }

  submitSessionForm(form: NgForm): void {
    if (form.invalid) {
      return;
    }

    const startTimeISO = `${this.newSession.date}T${this.newSession.time}:00`;
    const startDate = new Date(startTimeISO);
    const endDate = new Date(startDate.getTime() + 60 * 60 * 1000);
    const endTimeISO = endDate.toISOString().substring(0, 16);

    const sessionData: TrainingSessionDTO = {
      name: this.newSession.name,
      description: this.newSession.description,
      startTime: startTimeISO,
      endTime: endTimeISO,
      capacity: Number(this.newSession.capacity),
    };

    if (this.editingSession) {
      console.warn('Update functionality for trainer sessions is not yet implemented on backend via M1/M2 proxy for trainers.');
      alert('Update functionality not implemented yet.');
      this.toggleAddForm();
      form.resetForm(this.getEmptyFormModel());
    } else {
      this.workoutService.createTrainingSession(sessionData).subscribe({
        next: (createdSession) => {
          this.sessions.push(createdSession);
          this.toggleAddForm();
          form.resetForm(this.getEmptyFormModel());
          alert('Session added successfully!');
          this.loadTrainerSessions();
        },
        error: (err) => {
          console.error('Error creating session', err);
          alert(`Error adding session: ${err.error?.error || err.message}`);
        }
      });
    }
  }

  editSession(session: TrainingSessionViewDTO): void {
    this.editingSession = session;
    const startDate = new Date(session.startTime);
    this.newSession = {
      id: session.id.toString(),
      name: session.name,
      date: startDate.toISOString().split('T')[0],
      time: startDate.toTimeString().split(' ')[0].substring(0,5),
      capacity: session.capacity,
      room: 'N/A',
      description: session.description
    };
    this.showAddForm = true;
    alert('Editing is not fully implemented with backend update yet.');
  }

  deleteSession(sessionId: number | string): void {
    console.warn('Delete functionality for trainer sessions is not yet implemented on backend via M1/M2 proxy for trainers.');
    if (confirm('Are you sure you want to delete this session? This action is not connected to backend yet.')) {
      this.sessions = this.sessions.filter(s => s.id.toString() !== sessionId.toString());
    }
  }
}
