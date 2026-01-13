import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface EventoDTO {
    nombre: string;
    fecha: string;
    usuarioOrganizador: string;
    descripcion: string;
    attendees: number;
    time: string;
    location: string;
    category: string;
    attendeesList?: string[]; // List of participant usernames
}

@Injectable({
    providedIn: 'root'
})
export class EventService {
    private apiUrl = `${environment.apiUrl}/eventos`;

    constructor(private http: HttpClient) { }

    getAllEvents(): Observable<EventoDTO[]> {
        return this.http.get<EventoDTO[]>(this.apiUrl);
    }

    getUpcomingEvents(): Observable<EventoDTO[]> {
        return this.http.get<EventoDTO[]>(`${this.apiUrl}/upcoming`);
    }

    createEvento(event: EventoDTO): Observable<EventoDTO> {
        return this.http.post<EventoDTO>(this.apiUrl, event);
    }

    updateEvento(event: EventoDTO, oldName: string, oldDate: string, oldOrg: string): Observable<EventoDTO> {
        return this.http.put<EventoDTO>(`${this.apiUrl}?oldName=${oldName}&oldDate=${oldDate}&oldOrg=${oldOrg}`, event);
    }

    deleteEvento(nombre: string, fecha: string, organizador: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}?nombre=${nombre}&fecha=${fecha}&organizador=${organizador}`);
    }

    toggleAttendance(event: EventoDTO, userId: string): Observable<boolean> {
        return this.http.post<boolean>(`${this.apiUrl}/attend?userId=${userId}`, event);
    }

    getEventParticipants(nombre: string, fecha: string, organizador: string): Observable<string[]> {
        return this.http.get<string[]>(`${this.apiUrl}/participants?nombre=${nombre}&fecha=${fecha}&organizador=${organizador}`);
    }
}
