import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BookingService, BookingViewDTO } from '../../../services/booking.service';
import { SubscriptionService, UserSubscriptionViewDTO } from '../../../services/subscription.service';
import { AuthService } from '../../../services/auth.service';
import { forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';

interface NextBookingViewModel {
  id: number;
  trainingSessionName: string;
  bookingTime: string;
  displayTime?: string;
  displayDate?: string;
}

interface CurrentSubscriptionViewModel {
  id: number;
  planName: string;
  endDate: string;
  status: 'ACTIVE' | 'EXPIRED' | 'CANCELLED' | 'PENDING_PAYMENT';
  displayEndDate?: string;
}


@Component({
  selector: 'app-client-overview',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './client-overview.component.html',
  styleUrls: ['../../../dashboard.css', './client-overview.component.css']
})
export class ClientOverviewComponent implements OnInit {
  nextBooking: NextBookingViewModel | null = null;
  currentSubscription: CurrentSubscriptionViewModel | null = null;
  isLoading = true;
  error: string | null = null;
  userName: string = 'Client';

  constructor(
    private bookingService: BookingService,
    private subscriptionService: SubscriptionService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadOverviewData();
    this.userName = this.authService.getUserEmailFromToken() || 'Client';
  }

  loadOverviewData(): void {
    this.isLoading = true;
    this.error = null;

    forkJoin({
      bookings: this.bookingService.getClientBookings(),
      subscription: this.subscriptionService.getMySubscription()
    }).pipe(
      map(({ bookings, subscription }) => {
        const upcomingBookings = bookings
          .filter(b => b.status === 'CONFIRMED' && new Date(b.bookingTime) > new Date())
          .sort((a, b) => new Date(a.bookingTime).getTime() - new Date(b.bookingTime).getTime());

        const nextBookingData = upcomingBookings.length > 0 ? upcomingBookings[0] : null;

        return {
          nextBookingData: nextBookingData,
          currentSubscriptionData: subscription && subscription.status === 'ACTIVE' ? subscription : null
        };
      })
    ).subscribe({
      next: (data) => {
        if (data.nextBookingData) {
          this.nextBooking = {
            id: data.nextBookingData.id,
            trainingSessionName: data.nextBookingData.trainingSessionName,
            bookingTime: data.nextBookingData.bookingTime,
            displayTime: new Date(data.nextBookingData.bookingTime).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'}),
            displayDate: new Date(data.nextBookingData.bookingTime).toLocaleDateString(undefined, { dateStyle: 'full' })
          };
        } else {
          this.nextBooking = null;
        }

        if (data.currentSubscriptionData) {
          this.currentSubscription = {
            id: data.currentSubscriptionData.id,
            planName: data.currentSubscriptionData.planName,
            endDate: data.currentSubscriptionData.endDate,
            status: data.currentSubscriptionData.status,
            displayEndDate: new Date(data.currentSubscriptionData.endDate).toLocaleDateString(undefined, {dateStyle: 'full'})
          };
        } else {
          this.currentSubscription = null;
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading client overview data', err);
        if (err.status === 404 || err.error?.message?.toLowerCase().includes('not found')) {
          this.currentSubscription = null;
        } else {
          this.error = 'Failed to load dashboard data.';
        }
        this.isLoading = false;
      }
    });
  }
}
