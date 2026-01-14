import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface GrupoDTO {
    nombre: string;
    descripcion: string;
    esPrivado: boolean;
    numeroMiembros: number;
    myRole?: string;
    creadorNombre?: string;
}

export interface GrupoParticipaDTO {
    nombreGrupo: string;
    usuarioMiembro: string;
    rol: string;
}

@Injectable({
    providedIn: 'root'
})
export class GroupService {
    private apiUrl = `${environment.apiUrl}/grupos`;

    constructor(private http: HttpClient) { }

    getFeaturedGroups(): Observable<GrupoDTO[]> {
        return this.http.get<GrupoDTO[]>(`${this.apiUrl}/featured`);
    }

    getAllGroups(): Observable<GrupoDTO[]> {
        return this.http.get<GrupoDTO[]>(this.apiUrl);
    }

    getMyGroups(cuenta: string): Observable<GrupoDTO[]> {
        return this.http.get<GrupoDTO[]>(`${this.apiUrl}/my-groups/${cuenta}`);
    }

    searchGroups(query: string): Observable<GrupoDTO[]> {
        return this.http.get<GrupoDTO[]>(`${this.apiUrl}/search`, { params: { query } });
    }

    getGroupMembers(groupName: string): Observable<GrupoParticipaDTO[]> {
        return this.http.get<GrupoParticipaDTO[]>(`${this.apiUrl}/${groupName}/miembros`);
    }

    createGrupo(grupo: GrupoDTO): Observable<GrupoDTO> {
        return this.http.post<GrupoDTO>(this.apiUrl, grupo);
    }

    updateGrupo(nombre: string, grupo: GrupoDTO): Observable<GrupoDTO> {
        return this.http.put<GrupoDTO>(`${this.apiUrl}/${nombre}`, grupo);
    }

    deleteGrupo(nombre: string): Observable<void> {
        const encodedNombre = encodeURIComponent(nombre);
        return this.http.delete<void>(`${this.apiUrl}/${encodedNombre}`);
    }

    joinGrupo(data: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/join`, data);
    }

    leaveGroup(groupName: string, usuario: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${groupName}/miembros/${usuario}`);
    }

    updateMemberRole(groupName: string, usuario: string, newRole: string): Observable<void> {
        return this.http.put<void>(`${this.apiUrl}/${groupName}/miembros/${usuario}/role`, { rol: newRole });
    }
}
