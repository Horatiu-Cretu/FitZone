import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms'; // Import FormsModule and NgForm

interface Session {
  id: string;
  name: string;
  date: string;
  time: string;
  capacity: number;
  room: string;
}

@Component({
  selector: 'app-manage-sessions',
  standalone: true,
  imports: [CommonModule, FormsModule], // Add FormsModule
  templateUrl: './manage-sessions.component.html',
  styleUrls: ['./manage-sessions.component.css']
})
export class ManageSessionsComponent {
  sessions: Session[] = [
    { id: 's001', name: 'Morning Yoga', date: '2025-06-15', time: '08:00', capacity: 15, room: 'Studio B' },
    { id: 's002', name: 'HIIT Blast', date: '2025-06-16', time: '17:30', capacity: 20, room: 'Main Hall' }
  ];

  showAddForm = false;
  newSession: Partial<Session> = { name: '', date: '', time: '', capacity: 10, room: '' };
  editingSession: Session | null = null;

  constructor() { }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    this.editingSession = null;
    this.newSession = { name: '', date: '', time: '', capacity: 10, room: '' };
  }

  submitSessionForm(form: NgForm): void {
    if (form.invalid) {
      return;
    }
    if (this.editingSession) {
      const index = this.sessions.findIndex(s => s.id === this.editingSession!.id);
      if (index > -1) {
        this.sessions[index] = { ...this.editingSession, ...this.newSession } as Session;
      }
    } else {
      const newId = 's' + (Math.random().toString(36).substring(2, 7));
      this.sessions.push({ id: newId, ...this.newSession } as Session);
    }
    this.toggleAddForm();
    form.resetForm();
  }

  editSession(session: Session): void {
    this.editingSession = session;
    this.newSession = { ...session };
    this.showAddForm = true;
  }

  deleteSession(sessionId: string): void {
    this.sessions = this.sessions.filter(s => s.id !== sessionId);
  }
}
