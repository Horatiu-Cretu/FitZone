import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule, TitleCasePipe, DatePipe } from '@angular/common';
import { WorkoutService, TrainingSessionViewDTO } from '../../services/workout.service';
import { BookingService, BookingRequestDTO } from '../../services/booking.service';
import { AuthService } from '../../services/auth.service';

interface Day {
  date: Date;
  dayOfMonth: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  hasAvailability?: boolean;
}

interface HourSlot {
  hour: number;
  displayTime: string;
  status: 'booked' | 'available' | 'past' | 'not-scheduled';
  isBookable: boolean;
  specificSessionId?: number;
}

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule, RouterModule, TitleCasePipe, DatePipe], // Added DatePipe
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit {
  workoutIdParam: string | null = null;
  workoutDetails: TrainingSessionViewDTO | null = null;

  currentDate: Date = new Date();
  today: Date = new Date();

  calendarDays: Day[] = [];
  weekdays: string[] = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  selectedDate: Date | null = null;
  hourlySchedule: HourSlot[] = [];
  isLoadingWorkoutDetails = true;
  isLoadingSchedule = false;
  scheduleError: string | null = null;
  bookingMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    public router: Router, // Made public for template access
    private workoutService: WorkoutService,
    private bookingService: BookingService,
    public authService: AuthService
  ) {
    this.today.setHours(0, 0, 0, 0);
  }

  ngOnInit(): void {
    this.workoutIdParam = this.route.snapshot.paramMap.get('workoutId');
    if (this.workoutIdParam) {
      this.loadWorkoutDetails(this.workoutIdParam);
    } else {
      this.scheduleError = "Workout ID not provided.";
      this.isLoadingWorkoutDetails = false;
    }
    this.generateCalendar();
  }

  loadWorkoutDetails(id: string): void {
    this.isLoadingWorkoutDetails = true;
    this.workoutService.getWorkoutById(id).subscribe({
      next: (data) => {
        this.workoutDetails = data;
        this.isLoadingWorkoutDetails = false;
      },
      error: (err) => {
        console.error(`Error fetching workout details for ID ${id}`, err);
        this.scheduleError = "Could not load workout details.";
        this.isLoadingWorkoutDetails = false;
      }
    });
  }

  generateCalendar(): void {
    this.calendarDays = [];
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();
    const todayDate = new Date(); todayDate.setHours(0,0,0,0);

    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);
    const firstDayWeekday = firstDayOfMonth.getDay();
    const totalDaysInMonth = lastDayOfMonth.getDate();

    const daysInPrevMonth = new Date(year, month, 0).getDate();
    for (let i = firstDayWeekday - 1; i >= 0; i--) {
      const date = new Date(year, month - 1, daysInPrevMonth - i);
      this.calendarDays.push({ date, dayOfMonth: date.getDate(), isCurrentMonth: false, isToday: false });
    }

    for (let i = 1; i <= totalDaysInMonth; i++) {
      const date = new Date(year, month, i);
      const isTodayFlag = date.getTime() === todayDate.getTime();
      this.calendarDays.push({ date, dayOfMonth: i, isCurrentMonth: true, isToday: isTodayFlag });
    }

    const cellsToFill = 42;
    const daysFilled = this.calendarDays.length;
    for (let i = 1; i <= cellsToFill - daysFilled; i++) {
      const date = new Date(year, month + 1, i);
      this.calendarDays.push({ date, dayOfMonth: i, isCurrentMonth: false, isToday: false });
    }
  }

  selectDay(day: Day): void {
    this.bookingMessage = null;
    if (!day.isCurrentMonth || !this.workoutDetails) return;

    this.selectedDate = day.date;

    this.generateTimetableForSelectedWorkout(this.workoutDetails, day.date);
  }

  generateTimetableForSelectedWorkout(workoutBaseDetails: TrainingSessionViewDTO, date: Date): void {
    this.isLoadingSchedule = true;
    this.hourlySchedule = [];
    this.scheduleError = null;



    if (!workoutBaseDetails || !workoutBaseDetails.startTime) {
      this.scheduleError = "Workout details are incomplete to generate a timetable.";
      this.isLoadingSchedule = false;
      return;
    }

    const sessionStartDateTime = new Date(workoutBaseDetails.startTime);
    const selectedDayStart = new Date(date); selectedDayStart.setHours(0,0,0,0);
    const selectedDayEnd = new Date(date); selectedDayEnd.setHours(23,59,59,999);

    if (sessionStartDateTime >= selectedDayStart && sessionStartDateTime <= selectedDayEnd) {
      const hour = sessionStartDateTime.getHours();
      const displayTime = sessionStartDateTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      let status: 'available' | 'booked' | 'past' | 'not-scheduled' = 'available';
      let isBookable = true;

      if (sessionStartDateTime < this.today && sessionStartDateTime.toDateString() !== this.today.toDateString()) { // Check if strictly in the past
        status = 'past';
        isBookable = false;
      } else if (workoutBaseDetails.currentParticipants >= workoutBaseDetails.capacity) {
        status = 'booked';
        isBookable = false;
      }

      this.hourlySchedule.push({
        hour: hour,
        displayTime: displayTime,
        status: status,
        isBookable: isBookable,
        specificSessionId: workoutBaseDetails.id
      });
    } else {
      this.scheduleError = `${workoutBaseDetails.name} is not scheduled for this specific date based on its current details.`;
    }

    if (this.hourlySchedule.length === 0 && !this.scheduleError) {
      this.scheduleError = `No scheduled slots found for ${workoutBaseDetails.name} on this day.`;
    }
    this.isLoadingSchedule = false;
  }

  bookTimeSlot(slot: HourSlot): void {
    if (!this.selectedDate || !slot.specificSessionId || !slot.isBookable) {
      this.bookingMessage = "Cannot book this slot.";
      return;
    }

    if (!this.authService.getToken()) {
      this.bookingMessage = "You need to be logged in to book a session.";
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    const bookingData: BookingRequestDTO = {
      trainingSessionId: slot.specificSessionId
    };

    this.isLoadingSchedule = true;
    this.bookingMessage = null;

    this.bookingService.createBooking(bookingData).subscribe({
      next: (response) => {
        this.bookingMessage = `Successfully booked ${this.workoutDetails?.name} at ${slot.displayTime} on ${this.selectedDate?.toLocaleDateString()}! Booking ID: ${response.id}`;
        if(this.workoutDetails && this.selectedDate) {
          this.workoutDetails.currentParticipants++; // Optimistic update
          this.generateTimetableForSelectedWorkout(this.workoutDetails, this.selectedDate);
        }
        this.isLoadingSchedule = false;
      },
      error: (err) => {
        console.error('Booking failed', err);
        this.bookingMessage = `Booking failed: ${err.error?.error || err.message || 'Please try again.'}`;
        this.isLoadingSchedule = false;
      }
    });
  }

  goToPreviousMonth(): void {
    this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() - 1, 1);
    this.selectedDate = null;
    this.hourlySchedule = [];
    this.bookingMessage = null;
    this.scheduleError = null;
    this.generateCalendar();
  }

  goToNextMonth(): void {
    this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 1);
    this.selectedDate = null;
    this.hourlySchedule = [];
    this.bookingMessage = null;
    this.scheduleError = null;
    this.generateCalendar();
  }

  isDaySelected(day: Day): boolean {
    return !!this.selectedDate && day.date.getTime() === this.selectedDate.getTime() && day.isCurrentMonth;
  }
}
