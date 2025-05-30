import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface BookingRequestDTO {
  trainingSessionId: number | string;
}

export interface BookingViewDTO {
  id: number;
  clientId: number;
  trainingSessionId: number;
  trainingSessionName: string;
  bookingTime: string;
  status: 'CONFIRMED' | 'CANCELLED';
  date?: string;
  time?: string;
  trainer?: string;
  workoutName?: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private apiUrl = `${environment.apiUrl}/m2-proxy/bookings`;

  constructor(private http: HttpClient) { }

  createBooking(bookingData: BookingRequestDTO): Observable<BookingViewDTO> {
    return this.http.post<BookingViewDTO>(this.apiUrl, bookingData);
  }

  getClientBookings(): Observable<BookingViewDTO[]> {
    return this.http.get<BookingViewDTO[]>(`${this.apiUrl}/my-bookings`);
  }

  cancelBooking(bookingId: number | string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${bookingId}`);
  }
}
