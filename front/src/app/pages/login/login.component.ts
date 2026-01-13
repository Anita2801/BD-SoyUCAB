import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth'; // Import class 'AuthService' from file 'auth'
import { LoginRequest } from '../../models/auth.models';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
    credentials: LoginRequest = { cuenta: '', password: '' };
    errorMessage: string = '';
    showPassword = false;

    togglePassword() {
        this.showPassword = !this.showPassword;
    }

    constructor(private authService: AuthService, private router: Router) { }

    ngOnInit() {
        // Auto-clear session when visiting login page to prevent stale 403 errors
        // Only run in browser, not during SSR
        if (typeof window !== 'undefined') {
            this.authService.logout();
        }
    }

    onSubmit() {
        const payload = {
            cuenta: this.credentials.cuenta.trim(),
            password: (this.credentials.password || '').trim()
        };
        this.authService.login(payload).subscribe({
            next: (res) => {
                console.log('Login successful', res);
                this.router.navigate(['/dashboard']); // Retrieve to dashboard
            },
            error: (err) => {
                console.error('Login failed', err);
                this.errorMessage = `Error (${err.status}): Credenciales inválidas o problema de conexión.`;
            }
        });
    }
}
