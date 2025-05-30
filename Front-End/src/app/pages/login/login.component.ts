import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, LoginRequestDTO } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginData: LoginRequestDTO = {
    email: '',
    password: ''
  };
  selectedRoleForUI: string = 'USER';
  loginError: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  onLogin(): void {
    this.loginError = null;
    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        console.log('Login successful', response);
        const role = this.authService.getCurrentUserRole();
        if (role === 'ADMIN') {
          this.router.navigate(['/admin/overview']);
        } else if (role === 'TRAINER') {
          this.router.navigate(['/trainer/overview']);
        } else if (role === 'USER') {
          this.router.navigate(['/client/overview']);
        } else {
          this.router.navigate(['/welcome']);
        }
      },
      error: (err) => {
        console.error('Login failed', err);
        this.loginError = err.error?.error || 'Invalid email or password.';
      }
    });
  }
}
