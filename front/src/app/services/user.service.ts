import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface PersonaDTO {
    ci: string;
    usuarioCuenta: string;
    primerNombre: string;
    segundoNombre?: string;
    primerApellido: string;
    segundoApellido: string;
    sexo: string;
    lugarNombre: string;
    lugarIso?: string;
}

export interface ProfileDTO {
    name: string;
    primerNombre?: string;
    segundoNombre?: string;
    primerApellido?: string;
    segundoApellido?: string;
    role: string;
    semester: string;
    location: string;
    email: string;
    phone: string;
    bio: string;
    stats: {
        connections: number;
        posts: number;
        views: number;
    };
    languages: { name: string; level: number; }[];
    experience: { role: string; company: string; period: string; startDate: string; endDate: string; description: string; }[];
    contacts: { name: string; role: string; initials: string; color: string; }[];
    sexo?: string;
}

export interface NotificationDTO {
    id: string;
    actorName: string;
    actorAvatar: string;
    action: string;
    contentPreview: string;
    time: string;
    type: string;
    read: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrl = `${environment.apiUrl}/personas`;

    constructor(private http: HttpClient) { }

    getPersonaByCuenta(cuenta: string): Observable<PersonaDTO> {
        return this.http.get<PersonaDTO>(`${this.apiUrl}/usuario/${cuenta}`);
    }

    getSuggestions(cuenta: string): Observable<PersonaDTO[]> {
        return this.http.get<PersonaDTO[]>(`${this.apiUrl}/suggestions/${cuenta}`);
    }

    searchPersonas(query: string): Observable<PersonaDTO[]> {
        return this.http.get<PersonaDTO[]>(`${this.apiUrl}/search?query=${query}`);
    }

    getProfile(cuenta: string): Observable<ProfileDTO> {
        return this.http.get<ProfileDTO>(`${this.apiUrl}/profile/${cuenta}`);
    }

    getNotifications(cuenta: string): Observable<NotificationDTO[]> {
        return this.http.get<NotificationDTO[]>(`${environment.apiUrl}/notificaciones/reacciones/${cuenta}`);
    }

    updateProfile(cuenta: string, data: ProfileUpdateDTO): Observable<ProfileDTO> {
        return this.http.put<ProfileDTO>(`${this.apiUrl}/profile/${cuenta}`, data);
    }

    deleteProfile(cuenta: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/usuario/${cuenta}`);
    }

    getUserPosts(cuenta: string): Observable<any[]> {
        return this.http.get<any[]>(`${environment.apiUrl}/contenido/usuario/${cuenta}`);
    }

    followUser(solicitante: string, receptor: string): Observable<any> {
        return this.http.post(`${environment.apiUrl}/social/relacion`, {
            usuarioSolicitante: solicitante,
            usuarioReceptor: receptor,
            tipoRelacion: 'Seguimiento',
            estado: 'Aceptada', // Assuming following is immediate
            fechaRelacion: new Date().toISOString().split('T')[0]
        });
    }

    getFollowing(cuenta: string): Observable<SeRelacionaDTO[]> {
        return this.http.get<SeRelacionaDTO[]>(`${environment.apiUrl}/social/${cuenta}/following`);
    }
}

export interface SeRelacionaDTO {
    usuarioReceptor: string;
    usuarioSolicitante: string;
    estado: string;
    tipoRelacion: string;
}

export interface ProfileUpdateDTO {
    primerNombre?: string;
    segundoNombre?: string;
    primerApellido?: string;
    segundoApellido?: string;
    bio?: string;
    phone?: string;
    location?: string;
    sexo?: string;
    experience?: {
        role: string;
        company: string;
        startDate: string;
        endDate?: string;
        description: string;
    }[];
    languages?: {
        name: string;
        level: number;
    }[];
}
