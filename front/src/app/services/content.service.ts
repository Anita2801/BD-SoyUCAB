import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ContenidoDTO {
    usuarioCreador: string;
    fechaHoraCreacion: string;
    cuerpo: string;
    meGusta: number;
    noMeGusta: number;
    // Optional for UI logic
    liked?: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class ContentService {
    private apiUrl = `${environment.apiUrl}/contenido`;

    constructor(private http: HttpClient) { }

    getFeed(): Observable<ContenidoDTO[]> {
        return this.http.get<ContenidoDTO[]>(`${this.apiUrl}`);
    }

    createPost(content: string, usuario: string): Observable<ContenidoDTO> {
        const payload = {
            usuarioCreador: usuario,
            cuerpo: content
        };
        return this.http.post<ContenidoDTO>(`${this.apiUrl}`, payload);
    }

    updatePost(post: any): Observable<ContenidoDTO> {
        return this.http.put<ContenidoDTO>(`${this.apiUrl}`, post);
    }

    deletePost(usuario: string, fecha: string): Observable<void> {
        const encodedUsuario = encodeURIComponent(usuario);
        const encodedFecha = encodeURIComponent(fecha);
        return this.http.delete<void>(`${this.apiUrl}?usuario=${encodedUsuario}&fecha=${encodedFecha}`);
    }

    toggleReaction(userId: string, contentOwner: string, contentDate: string, reactionType: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/react?userId=${userId}&contentOwner=${contentOwner}&contentDate=${contentDate}&reactionType=${reactionType}`, {});
    }
}
