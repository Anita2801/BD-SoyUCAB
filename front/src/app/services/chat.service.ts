import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface MessageDTO {
    sender: string;
    senderName?: string;
    content: string;
    time: string;
    isMine: boolean;
}

export interface ChatDTO {
    nombre: string;
    fechaCreacion: string;
    lastMessage: string;
    lastMessageTime: string;
    messages?: MessageDTO[];
}

@Injectable({
    providedIn: 'root'
})
export class ChatService {
    private apiUrl = `${environment.apiUrl}/chats`;

    constructor(private http: HttpClient) { }

    getUserChats(userId: string): Observable<ChatDTO[]> {
        return this.http.get<ChatDTO[]>(`${this.apiUrl}/user/${userId}`);
    }

    getChatMessages(chatName: string, chatDate: string, userId: string): Observable<MessageDTO[]> {
        // Encode chatDate to handle special characters if any, though ISO usually safe in path if careful
        return this.http.get<MessageDTO[]>(`${this.apiUrl}/${chatName}/${chatDate}/messages?userId=${userId}`);
    }

    sendMessage(chatName: string, chatDate: string, senderId: string, content: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/send`, {
            chatName,
            chatDate,
            senderId,
            content
        });
    }

    getChatMembers(chatName: string, chatDate: string): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/${chatName}/${chatDate}/members`);
    }

    // ====================== CRUD METHODS ======================

    createChat(chatName: string, creatorId: string, memberIds: string[] = []): Observable<ChatDTO> {
        return this.http.post<ChatDTO>(this.apiUrl, {
            chatName,
            creatorId,
            memberIds
        });
    }

    deleteChat(chatName: string, chatDate: string, userId: string): Observable<void> {
        const encodedName = encodeURIComponent(chatName);
        const encodedDate = encodeURIComponent(chatDate);
        return this.http.delete<void>(`${this.apiUrl}/${encodedName}/${encodedDate}?userId=${userId}`);
    }

    leaveChat(chatName: string, chatDate: string, userId: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${chatName}/${chatDate}/leave/${userId}`);
    }

    updateMemberRole(chatName: string, chatDate: string, targetUserId: string, newRole: string, requesterId: string): Observable<void> {
        return this.http.put<void>(`${this.apiUrl}/${chatName}/${chatDate}/members/${targetUserId}/role`, {
            role: newRole,
            requesterId
        });
    }

    getUserRole(chatName: string, chatDate: string, userId: string): Observable<{ role: string }> {
        return this.http.get<{ role: string }>(`${this.apiUrl}/${chatName}/${chatDate}/role/${userId}`);
    }

    getFriends(userId: string): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/user/${userId}/friends`);
    }

    addMembers(chatName: string, chatDate: string, memberIds: string[], requesterId: string): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${chatName}/${chatDate}/members`, {
            memberIds,
            requesterId
        });
    }
}
