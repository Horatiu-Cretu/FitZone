import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

interface Day {
  date: Date;
  dayOfMonth: number;
  isCurrentMonth: boolean;
  isToday: boolean;
}

interface HourSlot {
  hour: number;
  displayTime: string;
  status: 'booked' | 'available';
}

const MOCK_BOOKINGS: { [key: string]: { [date: string]: number[] } } = {
  '1': { '2025-05-06': [18], '2025-05-08': [18] },
  '2': { '2025-05-07': [9], '2025-05-09': [9] },
};


@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit {
  workoutId: string | null = null;
  workoutName: string = 'Workout';

  currentDate: Date = new Date();
  today: Date = new Date();

  calendarDays: Day[] = [];
  weekdays: string[] = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  selectedDate: Date | null = null;
  hourlySchedule: HourSlot[] = [];

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.currentDate = new Date(2025, 4, 5);

    this.workoutId = this.route.snapshot.paramMap.get('workoutId');
    this.workoutName = `Workout ID: ${this.workoutId || 'Unknown'}`;
    this.generateCalendar();
    this.today.setHours(0, 0, 0, 0);
  }

  generateCalendar(): void {
    this.calendarDays = [];
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();

    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);

    const firstDayWeekday = firstDayOfMonth.getDay();
    const totalDays = lastDayOfMonth.getDate();

    const daysInPrevMonth = new Date(year, month, 0).getDate();
    for (let i = firstDayWeekday - 1; i >= 0; i--) {
      const date = new Date(year, month - 1, daysInPrevMonth - i);
      this.calendarDays.push({ date: date, dayOfMonth: date.getDate(), isCurrentMonth: false, isToday: false });
    }

    for (let i = 1; i <= totalDays; i++) {
      const date = new Date(year, month, i);
      const isToday = date.getTime() === this.today.getTime();
      this.calendarDays.push({ date: date, dayOfMonth: i, isCurrentMonth: true, isToday: isToday });
    }

    const lastDayWeekday = lastDayOfMonth.getDay();
    let daysToAdd = 6 - lastDayWeekday;

    const totalCellsFilled = this.calendarDays.length + daysToAdd;
    if (totalCellsFilled < 42) {
      daysToAdd += (42 - totalCellsFilled);
    }

    for (let i = 1; i <= daysToAdd; i++) {
      const date = new Date(year, month + 1, i);
      this.calendarDays.push({ date: date, dayOfMonth: i, isCurrentMonth: false, isToday: false });
    }
  }

  selectDay(day: Day): void {
    if (!day.isCurrentMonth) return;
    this.selectedDate = day.date;
    console.log('Selected date:', this.selectedDate);
    this.generateTimetable(this.selectedDate);
  }

  generateTimetable(date: Date): void {
    this.hourlySchedule = [];
    for (let hour = 8; hour <= 19; hour++) {
      const displayHour = hour % 12 === 0 ? 12 : hour % 12;
      const ampm = hour < 12 || hour === 24 ? 'AM' : 'PM';
      const displayTime = `${displayHour}:00 ${ampm}`;
      this.hourlySchedule.push({
        hour: hour,
        displayTime: displayTime,
        status: this.getBookingStatus(this.workoutId, date, hour)
      });
    }
  }

  getBookingStatus(workoutId: string | null, date: Date, hour: number): 'booked' | 'available' {
    if (!workoutId) return 'available';
    const dateString = date.toISOString().split('T')[0];
    const bookingsForWorkout = MOCK_BOOKINGS[workoutId];
    if (bookingsForWorkout && bookingsForWorkout[dateString]) {
      return bookingsForWorkout[dateString].includes(hour) ? 'booked' : 'available';
    }
    return 'available';
  }

  goToPreviousMonth(): void {
    this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() - 1, 1);
    this.selectedDate = null;
    this.hourlySchedule = [];
    this.generateCalendar();
  }

  goToNextMonth(): void {
    this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 1);
    this.selectedDate = null;
    this.hourlySchedule = [];
    this.generateCalendar();
  }

  isDaySelected(day: Day): boolean {
    return !!this.selectedDate && day.date.getTime() === this.selectedDate.getTime();
  }

  bookTimeSlot(slot: HourSlot): void {
    if (!this.selectedDate || !this.workoutId) return;

    const dateString = this.selectedDate.toISOString().split('T')[0];

    console.log(`Attempting to book Workout ID: ${this.workoutId} on ${dateString} at ${slot.displayTime} (${slot.hour}:00)`);

    if (!MOCK_BOOKINGS[this.workoutId]) {
      MOCK_BOOKINGS[this.workoutId] = {};
    }
    if (!MOCK_BOOKINGS[this.workoutId][dateString]) {
      MOCK_BOOKINGS[this.workoutId][dateString] = [];
    }
    if (!MOCK_BOOKINGS[this.workoutId][dateString].includes(slot.hour)) {
      MOCK_BOOKINGS[this.workoutId][dateString].push(slot.hour);
      console.log('Mock booking successful!');
      this.generateTimetable(this.selectedDate);
    } else {
      console.log('Slot already booked in mock data.');
    }

  }
}
