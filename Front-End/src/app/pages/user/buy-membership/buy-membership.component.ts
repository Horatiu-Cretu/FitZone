import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface MembershipPlan {
  id: string;
  name: string;
  price: string;
  features: string[];
  highlight?: boolean;
}

@Component({
  selector: 'app-buy-membership',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './buy-membership.component.html',
  styleUrls: ['./buy-membership.component.css']
})
export class BuyMembershipComponent {
  plans: MembershipPlan[] = [
    {
      id: 'student',
      name: 'Student Pass',
      price: '$29/month',
      features: ['Access to all basic equipment', 'Group fitness classes (limited)', 'Valid Student ID required']
    },
    {
      id: 'gold',
      name: 'Gold Membership',
      price: '$49/month',
      features: ['Access to all equipment', 'Unlimited group fitness classes', 'Personalized workout plan', 'Locker access'],
      highlight: true
    },
    {
      id: 'diamond',
      name: 'Diamond Membership',
      price: '$79/month',
      features: ['All Gold benefits', 'Access to premium facilities (sauna, pool)', 'Personal training session (1/month)', 'Guest passes (2/month)']
    }
  ];

  constructor() { }

  selectPlan(planId: string): void {
    console.log('Selected plan:', planId);
  }
}

