import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

export interface LoginRequestDTO {
  email: string;
  password: string;
}

export interface LoginResponseDTO {
  token: string;
}

export interface UserDTO {
  fullName: string;
  email: string;
  password: string;
  role?: 'USER' | 'TRAINER' | 'ADMIN';
}

export interface UserViewDTO {
  id: number;
  fullName: string;
  email: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private tokenKey = 'authToken';
  private isBrowser: boolean;

  private loggedIn: BehaviorSubject<boolean>;
  isLoggedIn$: Observable<boolean>;

  private _currentUserRole: BehaviorSubject<string | null>;
  currentUserRole$: Observable<string | null>;


  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);

    this.loggedIn = new BehaviorSubject<boolean>(this.hasToken());
    this.isLoggedIn$ = this.loggedIn.asObservable();
    this._currentUserRole = new BehaviorSubject<string | null>(this._getRoleFromTokenInternal());
    this.currentUserRole$ = this._currentUserRole.asObservable();
  }

  private hasToken(): boolean {
    if (this.isBrowser) {
      return !!localStorage.getItem(this.tokenKey);
    }
    return false;
  }

  private _getRoleFromTokenInternal(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.role;
      } catch (e) {
        console.error('Error parsing token for role:', e);
        return null;
      }
    }
    return null;
  }

  public getCurrentUserRole(): string | null {
    return this._currentUserRole.getValue();
  }


  login(credentials: LoginRequestDTO): Observable<LoginResponseDTO> {
    return this.http.post<LoginResponseDTO>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.setToken(response.token);
      })
    );
  }

  signup(userData: UserDTO): Observable<UserViewDTO> {
    return this.http.post<UserViewDTO>(`${this.apiUrl}/signup`, userData);
  }

  logout(): void {
    this.clearToken();
    this.router.navigate(['/login']);
  }

  setToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem(this.tokenKey, token);
      this.loggedIn.next(true);
      this._currentUserRole.next(this._getRoleFromTokenInternal());
    }
  }

  getToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem(this.tokenKey);
    }
    return null;
  }

  clearToken(): void {
    if (this.isBrowser) {
      localStorage.removeItem(this.tokenKey);
    }
    this.loggedIn.next(false);
    this._currentUserRole.next(null);
  }

  getUserIdFromToken(): number | null {
    const token = this.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.sub ? Number(payload.sub) : null;
      } catch (e) {
        console.error('Error parsing userId from token:', e);
        return null;
      }
    }
    return null;
  }

  getUserEmailFromToken(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.email || null;
      } catch (e) {
        console.error('Error parsing email from token:', e);
        return null;
      }
    }
    return null;
  }
}
