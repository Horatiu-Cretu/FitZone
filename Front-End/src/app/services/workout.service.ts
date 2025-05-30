import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface TrainingSessionViewDTO {
  id: number;
  name: string;
  description: string;
  startTime: string;
  endTime: string;
  capacity: number;
  currentParticipants: number;
  trainerId: number;
  imageUrl?: string;
  trainer?: string;
  time?: string;
}

export interface TrainingSessionDTO {
  name: string;
  description: string;
  startTime: string;
  endTime: string;
  capacity: number;
}

export interface EnrolledClientViewDTO {
  clientId: number;
  clientName?: string;
  clientEmail?: string;
  bookingTime: string;
  name?: string;
  email?: string;
  bookingDate?: string;
  status?: string;
}


@Injectable({
  providedIn: 'root'
})
export class WorkoutService {
  private apiUrl = `${environment.apiUrl}/m2-proxy/training-sessions`;

  constructor(private http: HttpClient) { }

  getAllWorkouts(): Observable<TrainingSessionViewDTO[]> {
    return this.http.get<TrainingSessionViewDTO[]>(this.apiUrl);
  }

  getWorkoutById(sessionId: number | string): Observable<TrainingSessionViewDTO> {
    return this.http.get<TrainingSessionViewDTO>(`${this.apiUrl}/${sessionId}`);
  }

  // For Trainers
  getTrainerSessions(): Observable<TrainingSessionViewDTO[]> {
    return this.http.get<TrainingSessionViewDTO[]>(`${this.apiUrl}/trainer/my-sessions`);
  }

  getEnrolledClients(sessionId: number | string): Observable<EnrolledClientViewDTO[]> {

    return this.http.get<EnrolledClientViewDTO[]>(`${this.apiUrl}/${sessionId}/enrolled-clients`);
  }

  createTrainingSession(sessionData: TrainingSessionDTO): Observable<TrainingSessionViewDTO> {
    return this.http.post<TrainingSessionViewDTO>(this.apiUrl, sessionData);
  }


}
