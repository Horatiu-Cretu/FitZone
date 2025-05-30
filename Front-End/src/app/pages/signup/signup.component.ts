import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, UserDTO } from '../../services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  signupData: UserDTO = {
    fullName: '',
    email: '',
    password: '',
    role: 'USER'
  };
  signupError: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  onSignup(): void {
    this.signupError = null;
    this.authService.signup(this.signupData).subscribe({
      next: (response) => {
        console.log('Signup successful', response);
        alert('Signup successful! Please login.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Signup failed', err);
        this.signupError = err.error?.error || 'Signup failed. Please try again.';
      }
    });
  }
}
