import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

interface Workout {
  id: number;
  name: string;
  trainer: string;
  time: string;
  description: string;
  imageUrl: string;
}

@Component({
  selector: 'app-workouts',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './workouts.component.html',
  styleUrls: ['./workouts.component.css']
})
export class WorkoutsComponent {

  workouts: Workout[] = [
    {
      id: 1,
      name: 'Full Body Blast',
      trainer: 'Alex Johnson',
      time: 'Mon, Wed 6:00 PM',
      description: 'High-intensity interval training targeting all major muscle groups.',
      imageUrl: 'assets/workout-images/workout-blast.jpg'
    },
    {
      id: 2,
      name: 'Yoga Flow',
      trainer: 'Maria Garcia',
      time: 'Tue, Thu 9:00 AM',
      description: 'Improve flexibility, balance, and mindfulness with this Vinyasa flow.',
      imageUrl: 'assets/workout-images/yoga.jpg'
    },
    {
      id: 3,
      name: 'Strength & Conditioning',
      trainer: 'David Lee',
      time: 'Fri 7:00 AM, Sat 10:00 AM',
      description: 'Build muscle and increase power using compound lifts and conditioning drills.',
      imageUrl: 'assets/workout-images/Strenght-Conditioning.jpg'
    },
    {
      id: 4,
      name: 'Spin Cycle Challenge',
      trainer: 'Sarah Chen',
      time: 'Mon, Wed 7:00 AM',
      description: 'Indoor cycling class focused on endurance and high-energy sprints.',
      imageUrl: 'assets/workout-images/cycle.jpg'
    },
    {
      id: 5,
      name: 'Boxing Fundamentals',
      trainer: 'Coach Carter',
      time: 'Tue, Thu 5:00 PM',
      description: 'Learn the basics of boxing: stance, punches, and footwork in a high-energy session.',
      imageUrl: 'assets/workout-images/box.jpg'
    },
    {
      id: 6,
      name: 'Mat Pilates',
      trainer: 'Isabelle Moreau',
      time: 'Mon, Wed 10:00 AM',
      description: 'Strengthen your core, improve posture, and increase flexibility with controlled mat exercises.',
      imageUrl: 'assets/workout-images/pilates.jpg'
    }
  ];

  constructor() {}
}
