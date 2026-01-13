import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth';
import { RegisterRequest } from '../../models/auth.models';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './register.component.html',
    styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
    data: RegisterRequest = {
        cuenta: '',
        password: '',
        ci: '',
        primerNombre: '',
        segundoNombre: '',
        primerApellido: '',
        segundoApellido: '',
        sexo: '',
        lugarIso: '',
        rol: '',
        entidadCodigo: '',
        fechaInicio: '',
        fechaFin: ''
    };
    errorMessage: string = '';

    lugares: any[] = [];
    roles: any[] = [];
    entidades: any[] = [];

    constructor(
        private authService: AuthService,
        private router: Router,
        private http: HttpClient
    ) { }

    ngOnInit() {
        this.loadLugares();
        this.loadRoles();
        this.loadEntidades();
    }

    loadLugares() {
        this.http.get<any[]>(`${environment.apiUrl.replace('/api', '')}/auth/lugares`).subscribe({
            next: (data) => this.lugares = data,
            error: (err) => console.error('Error loading lugares:', err)
        });
    }

    loadRoles() {
        this.http.get<any[]>(`${environment.apiUrl.replace('/api', '')}/auth/roles`).subscribe({
            next: (data) => this.roles = data,
            error: (err) => console.error('Error loading roles:', err)
        });
    }

    loadEntidades() {
        this.http.get<any[]>(`${environment.apiUrl.replace('/api', '')}/auth/entidades`).subscribe({
            next: (data) => this.entidades = data,
            error: (err) => console.error('Error loading entidades:', err)
        });
    }

    onSubmit() {
        this.authService.register(this.data).subscribe({
            next: (res) => {
                console.log('Register successful', res);
                this.router.navigate(['/dashboard']);
            },
            error: (err) => {
                console.error('Register failed', err);
                this.errorMessage = 'Error al registrar. Verifique los datos e intente de nuevo.';
            }
        });
    }
}
