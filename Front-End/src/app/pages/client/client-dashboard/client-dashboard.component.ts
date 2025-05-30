import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './client-dashboard.component.html',
  styleUrls: ['../../../dashboard.css', './client-dashboard.component.css']
})
export class ClientDashboardComponent implements OnInit {
  userName: string = 'Client';
  userRole: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.userRole = this.authService.getCurrentUserRole();
    this.userName = this.authService.getUserEmailFromToken() || 'Client';

    if (this.userRole !== 'USER') {
      console.warn("Incorrect role accessing client dashboard. Current role:", this.userRole);

    }
  }

  logout(): void {
    this.authService.logout();
  }
}
