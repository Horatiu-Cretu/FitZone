import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();
    console.log(`[AuthInterceptor] Intercepting request to: ${request.url}`);

    if (token) {
      console.log(`[AuthInterceptor] Token found, attaching to headers for URL: ${request.url}.`);
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    } else {
      console.log(`[AuthInterceptor] No token found for URL: ${request.url}.`);
    }
    return next.handle(request);
  }
}
