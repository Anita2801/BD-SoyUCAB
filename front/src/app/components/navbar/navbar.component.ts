import { Component, ChangeDetectorRef, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth';
import { UserService, ProfileDTO } from '../../services/user.service';
import { ChatService, ChatDTO } from '../../services/chat.service';

@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './navbar.component.html',
    styles: []
})
export class NavbarComponent {

    user: ProfileDTO | null = null;
    showNotifications = false;
    showMessages = false;
    showUserMenu = false;

    notifications: any[] = [];

    messages = [
        {
            name: 'María Torres',
            content: '¿Pudiste revisar el documento que te e...',
            time: 'Hace 5 min',
            initials: 'MT',
            color: 'bg-teal-700',
            unread: true
        },
        {
            name: 'Carlos Vega',
            content: 'Gracias por aceptar mi solicitud!',
            time: 'Hace 1 hora',
            initials: 'CV',
            color: 'bg-teal-800',
            unread: true
        },
        {
            name: 'Prof. Ana López',
            content: 'La clase de mañana será virtual',
            time: 'Hace 3 horas',
            initials: 'AL',
            color: 'bg-teal-800',
            unread: true
        },
        {
            name: 'Roberto Pérez',
            content: 'Nos vemos en el evento de hoy 🔥',
            time: 'Hace 5 horas',
            initials: 'RP',
            color: 'bg-teal-700',
            unread: false
        },
        {
            name: 'Laura Martínez',
            content: 'Excelente presentación!',
            time: 'Hace 1 día',
            initials: 'LM',
            color: 'bg-teal-700',
            unread: false
        }
    ];

    private isBrowser: boolean;

    constructor(
        private authService: AuthService,
        private router: Router,
        private userService: UserService,
        private chatService: ChatService,
        private cdr: ChangeDetectorRef,
        @Inject(PLATFORM_ID) platformId: Object
    ) {
        this.isBrowser = isPlatformBrowser(platformId);
        // Fetch minimal profile for navbar
        const currentUser = this.authService.getAccountFromToken();
        if (!currentUser) return;
        this.userService.getProfile(currentUser).subscribe({
            next: (data) => {
                this.user = data;
                this.loadData(currentUser);
                this.triggerChangeDetection();
            },
            error: (err) => console.error('Navbar user fetch error:', err)
        });
    }

    private triggerChangeDetection() {
        if (this.isBrowser) {
            this.cdr.detectChanges();
        }
    }

    loadData(userId: string) {
        // Load Chats
        this.chatService.getUserChats(userId).subscribe({
            next: (chats) => {
                this.messages = chats.map(chat => ({
                    name: chat.nombre,
                    content: chat.lastMessage,
                    time: new Date(chat.lastMessageTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                    initials: chat.nombre.substring(0, 2).toUpperCase(),
                    color: 'bg-teal-700',
                    unread: true
                }));
                this.triggerChangeDetection();
            },
            error: (err) => console.error('Error loading chats:', err)
        });

        // Load Notifications
        this.userService.getNotifications(userId).subscribe({
            next: (data) => {
                this.notifications = data.map(n => ({
                    name: n.actorName,
                    action: n.action, // "le gustó tu publicación"
                    time: n.time,
                    initials: n.actorAvatar,
                    color: n.action.includes('no') ? 'bg-red-500' : 'bg-teal-500', // Red for dislike, Teal for like
                    unread: !n.read
                }));
                this.triggerChangeDetection();
            },
            error: (err) => console.error('Error loading notifications:', err)
        });
    }

    getInitials(name: string): string {
        if (!name) return '';
        return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
    }

    toggleNotifications() {
        this.showNotifications = !this.showNotifications;
        if (this.showNotifications) {
            this.showMessages = false;
            this.showUserMenu = false;
        }
    }

    toggleMessages() {
        this.showMessages = !this.showMessages;
        if (this.showMessages) {
            this.showNotifications = false;
            this.showUserMenu = false;
        }
    }

    toggleUserMenu() {
        this.showUserMenu = !this.showUserMenu;
        if (this.showUserMenu) {
            this.showNotifications = false;
            this.showMessages = false;
        }
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}
