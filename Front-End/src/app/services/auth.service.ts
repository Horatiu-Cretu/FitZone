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
    console.log('[AuthService] Constructor: IsBrowser:', this.isBrowser);

    const initialTokenExists = this.hasToken();
    const initialRole = this._getRoleFromTokenInternal();
    console.log('[AuthService] Constructor: Initial token exists?', initialTokenExists);
    console.log('[AuthService] Constructor: Initial role from token:', initialRole);

    this.loggedIn = new BehaviorSubject<boolean>(initialTokenExists);
    this.isLoggedIn$ = this.loggedIn.asObservable();
    this._currentUserRole = new BehaviorSubject<string | null>(initialRole);
    this.currentUserRole$ = this._currentUserRole.asObservable();
  }

  private hasToken(): boolean {
    if (this.isBrowser) {
      const tokenExists = !!localStorage.getItem(this.tokenKey);
      return tokenExists;
    }
    return false;
  }

  private _getRoleFromTokenInternal(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const role = payload.role;
        return role;
      } catch (e) {
        console.error('[AuthService] _getRoleFromTokenInternal: Error parsing token for role:', e);
        return null;
      }
    }
    return null;
  }

  public getCurrentUserRole(): string | null {
    const role = this._currentUserRole.getValue();
    return role;
  }


  login(credentials: LoginRequestDTO): Observable<LoginResponseDTO> {
    console.log('[AuthService] login(): Attempting login for email:', credentials.email);
    return this.http.post<LoginResponseDTO>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        console.log('[AuthService] login(): Login successful, received token:', response.token ? 'Token received' : 'No token in response');
        this.setToken(response.token);
      })
    );
  }

  signup(userData: UserDTO): Observable<UserViewDTO> {
    console.log('[AuthService] signup(): Attempting signup for email:', userData.email);
    return this.http.post<UserViewDTO>(`${this.apiUrl}/signup`, userData);
  }

  logout(): void {
    console.log('[AuthService] logout(): Clearing token and navigating to login.');
    this.clearToken();
    this.router.navigate(['/login']);
  }

  setToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem(this.tokenKey, token);
      console.log('[AuthService] setToken(): Token set in localStorage. Updating BehaviorSubjects.');
      this.loggedIn.next(true);
      const role = this._getRoleFromTokenInternal();
      console.log('[AuthService] setToken(): Role after setting token:', role);
      this._currentUserRole.next(role);
    } else {
      console.log('[AuthService] setToken(): Not in browser, token not set in localStorage.');
    }
  }

  getToken(): string | null {
    if (this.isBrowser) {
      const token = localStorage.getItem(this.tokenKey);
      return token;
    }
    return null;
  }

  clearToken(): void {
    if (this.isBrowser) {
      console.log('[AuthService] clearToken(): Removing token from localStorage.');
      localStorage.removeItem(this.tokenKey);
    }
    console.log('[AuthService] clearToken(): Updating BehaviorSubjects to logged out state.');
    this.loggedIn.next(false);
    this._currentUserRole.next(null);
  }

  getUserIdFromToken(): number | null {
    const token = this.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const userId = payload.sub ? Number(payload.sub) : null;
        return userId;
      } catch (e) {
        console.error('[AuthService] getUserIdFromToken(): Error parsing userId from token:', e);
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
        const email = payload.email || null;
        return email;
      } catch (e) {
        console.error('[AuthService] getUserEmailFromToken(): Error parsing email from token:', e);
        return null;
      }
    }
    return null;
  }
}
