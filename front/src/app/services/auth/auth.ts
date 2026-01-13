import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegisterRequest, AuthResponse } from '../../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl.replace('/api', '/auth')}`; // Quick fix to point to /auth or use environment.authUrl
  private tokenKey = 'auth_token';
  private currentUserSubject = new BehaviorSubject<string | null>(null);

  constructor(private http: HttpClient) {
    if (typeof localStorage !== 'undefined') {
      const token = localStorage.getItem(this.tokenKey);
      this.currentUserSubject.next(token);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.token) {
          this.setToken(response.token);
        }
      })
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data).pipe(
      tap(response => {
        if (response.token) {
          this.setToken(response.token);
        }
      })
    );
  }

  logout(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(this.tokenKey);
    }
    this.currentUserSubject.next(null);
  }

  private setToken(token: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(this.tokenKey, token);
    }
    this.currentUserSubject.next(token);
  }

  get token(): string | null {
    return this.currentUserSubject.value;
  }

  get isAuthenticated(): boolean {
    return !!this.token; // Simple check, ideally verify expiration
  }

  getAccountFromToken(): string | null {
    const token = this.token;
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub; // Claims: sub is the username
    } catch (e) {
      console.error('Error decoding token', e);
      return null;
    }
  }
}
