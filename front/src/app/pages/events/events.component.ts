import { Component, OnInit, ChangeDetectorRef, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { AuthService } from '../../services/auth/auth';

import { EventService, EventoDTO } from '../../services/event.service';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-events',
    standalone: true,
    imports: [CommonModule, RouterModule, NavbarComponent, FormsModule],
    templateUrl: './events.component.html',
    styleUrl: './events.component.css'
})
export class EventsComponent implements OnInit {

    events: any[] = []; // Will hold mapped events
    searchQuery: string = ''; // Search query model
    private isBrowser: boolean;

    constructor(
        private authService: AuthService,
        private eventService: EventService,
        private router: Router,
        private cdr: ChangeDetectorRef,
        @Inject(PLATFORM_ID) platformId: Object
    ) {
        this.isBrowser = isPlatformBrowser(platformId);
    }

    private triggerChangeDetection() {
        if (this.isBrowser) {
            this.cdr.detectChanges();
        }
    }

    ngOnInit() {
        this.loadEvents();
    }

    loadEvents() {
        this.eventService.getAllEvents().subscribe({
            next: (data) => {
                this.events = this.mapToView(data);
                this.triggerChangeDetection();
            },
            error: (err) => console.error('Error loading events:', err)
        });
    }

    mapToView(dtos: EventoDTO[]): any[] {
        console.log('Events received:', dtos);
        const currentUser = this.currentUserAccount;
        return dtos.map(dto => {
            const imagePath = this.getEventImage(dto.nombre);
            const isMember = dto.attendeesList?.includes(currentUser || '') ?? false;
            // Always set a fallback gradient color
            const color = this.getFallbackColor(dto.nombre);
            console.log(`Mapping event: "${dto.nombre}" -> Image: ${imagePath}, isMember: ${isMember}`);
            return {
                id: dto.nombre,
                title: dto.nombre,
                description: dto.descripcion,
                date: dto.fecha,
                rawDate: dto.fecha,
                rawOrganizer: dto.usuarioOrganizador,
                time: dto.time,
                location: dto.location,
                attendees: dto.attendees,
                category: dto.category,
                color: color,
                image: imagePath,
                isMember: isMember,
                attendeesList: dto.attendeesList || [],
                showMenu: false
            };
        });
    }


    onSearch() {
        if (!this.searchQuery.trim()) {
            this.loadEvents();
            return;
        }

        this.eventService.getAllEvents().subscribe({
            next: (data) => {
                const allEvents = this.mapToView(data);
                this.events = allEvents.filter(e =>
                    e.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                    e.description.toLowerCase().includes(this.searchQuery.toLowerCase())
                );
            }
        });
    }

    getBadgeColor(category: string): string {
        switch (category) {
            case 'Académico': return 'bg-cyan-500 text-white';
            case 'Deportivo': return 'bg-green-600 text-white';
            case 'Cultural': return 'bg-yellow-500 text-white';
            case 'Social': return 'bg-purple-500 text-white';
            default: return 'bg-gray-500 text-white';
        }
    }

    getColorByCategory(category: string): string {
        switch (category) {
            case 'Académico': return 'bg-blue-900';
            case 'Deportivo': return 'bg-green-600';
            case 'Cultural': return 'bg-yellow-600';
            case 'Social': return 'bg-purple-700';
            default: return 'bg-gray-500';
        }
    }

    // Get a varied color for events without images
    getFallbackColor(eventName: string): string {
        const colors = [
            'bg-gradient-to-br from-teal-500 to-emerald-600',
            'bg-gradient-to-br from-blue-500 to-indigo-600',
            'bg-gradient-to-br from-amber-500 to-orange-600',
            'bg-gradient-to-br from-rose-500 to-pink-600',
            'bg-gradient-to-br from-purple-500 to-violet-600',
            'bg-gradient-to-br from-cyan-500 to-blue-600'
        ];
        // Use name hash to pick consistent color
        let hash = 0;
        for (let i = 0; i < eventName.length; i++) {
            hash = eventName.charCodeAt(i) + ((hash << 5) - hash);
        }
        return colors[Math.abs(hash) % colors.length];
    }

    getEventImage(eventName: string): string | null {
        // Exact match map
        const imageMap: { [key: string]: string } = {
            'Feria del Emprendimiento Polar': 'feria-polar.png',
            'Taller de Innovacion Cervecera': 'taller-cervecera.png',
            'Jornada de Becas Erasmus': 'jornada-becas.jpeg',
            'Noche de Cine Frances': 'noche-cine.jpg',
            'Foro de Banca Digital': 'banca-digital.png',
            'Dia de la Sostenibilidad': 'sostenibilidad.jpeg',
            'Reto Snacks Innovadores': 'reto-snacks.jpeg'
        };

        // Check exact match first
        let filename = imageMap[eventName];
        if (filename) return `/assets/images/${filename}`;

        // Try trimmed
        const trimmedName = eventName.trim();
        filename = imageMap[trimmedName];
        if (filename) return `/assets/images/${filename}`;

        // Partial/keyword matching as fallback
        const nameLower = eventName.toLowerCase();
        if (nameLower.includes('polar') || nameLower.includes('emprendimiento')) return '/assets/images/feria-polar.png';
        if (nameLower.includes('cervecera') || nameLower.includes('innovacion')) return '/assets/images/taller-cervecera.png';
        if (nameLower.includes('becas') || nameLower.includes('erasmus')) return '/assets/images/jornada-becas.jpeg';
        if (nameLower.includes('cine') || nameLower.includes('frances')) return '/assets/images/noche-cine.jpg';
        if (nameLower.includes('banca') || nameLower.includes('digital')) return '/assets/images/banca-digital.png';
        if (nameLower.includes('sostenibilidad')) return '/assets/images/sostenibilidad.jpeg';
        if (nameLower.includes('snacks') || nameLower.includes('reto')) return '/assets/images/reto-snacks.jpeg';

        return null;
    }

    // Models for Create/Edit
    showCreateModal = false;
    showEditModal = false;
    showParticipantsModal = false;
    newEvent: any = { nombre: '', descripcion: '', fecha: '', time: '09:00', location: 'Campus', category: 'General' };
    editingEvent: any = null;
    viewingEvent: any = null;
    participants: string[] = [];
    isSubmitting = false;

    // Helper to get current user (assuming AuthService stores it or we check localStorage/Service)
    get currentUserAccount(): string | null {
        return this.authService.getAccountFromToken();
    }

    // Modal Actions
    openCreateModal() {
        if (!this.authService.isAuthenticated) {
            alert('Debes iniciar sesión');
            return;
        }
        this.showCreateModal = true;
    }

    closeCreateModal() {
        this.showCreateModal = false;
        this.newEvent = { nombre: '', descripcion: '', fecha: '', time: '09:00', location: 'Campus', category: 'General' };
    }

    openEditModal(event: any) {
        if (!this.canEdit(event)) return;
        // Copy event data to editingEvent
        this.editingEvent = { ...event };
        this.showEditModal = true;
    }

    closeEditModal() {
        this.showEditModal = false;
        this.editingEvent = null;
    }

    // CRUD
    createEvent() {
        if (!this.currentUserAccount) return;
        this.isSubmitting = true;
        const dto: EventoDTO = {
            nombre: this.newEvent.nombre,
            descripcion: this.newEvent.descripcion,
            fecha: this.newEvent.fecha,
            usuarioOrganizador: this.currentUserAccount,
            attendees: 0,
            time: this.newEvent.time,
            location: this.newEvent.location,
            category: this.newEvent.category
        };

        this.eventService.createEvento(dto).subscribe({
            next: (created) => {
                this.isSubmitting = false;
                this.closeCreateModal();
                this.loadEvents();
            },
            error: (err) => {
                console.error('Error creating event', err);
                this.isSubmitting = false;
                alert('Error al crear evento');
            }
        });
    }

    updateEvent() {
        if (!this.editingEvent) return;
        this.isSubmitting = true;

        const dto: EventoDTO = {
            nombre: this.editingEvent.title,
            fecha: this.editingEvent.date, // This might be new date
            usuarioOrganizador: this.editingEvent.rawOrganizer,
            descripcion: this.editingEvent.description,
            attendees: this.editingEvent.attendees,
            time: this.editingEvent.time,
            location: this.editingEvent.location,
            category: this.editingEvent.category
        };

        // Pass original keys to identify the record
        const oldName = this.editingEvent.title; // Name edit not supported yet in UI but good practice
        const oldDate = this.editingEvent.rawDate;
        const oldOrg = this.editingEvent.rawOrganizer;

        this.eventService.updateEvento(dto, oldName, oldDate, oldOrg).subscribe({
            next: () => {
                this.isSubmitting = false;
                this.closeEditModal();
                this.loadEvents();
            },
            error: (err) => {
                console.error('Error updating event', err);
                this.isSubmitting = false;
            }
        });
    }

    deleteEvent(event: any) {
        if (!confirm('¿Estás seguro de eliminar este evento?')) return;

        // We need original fields. Assuming mapToView preserves them or we can infer.
        // Wait, mapToView used name as ID. We need date and organizer.
        // We should enhance mapToView to include these raw fields.
        if (!event.rawDate || !event.rawOrganizer) {
            console.error('Missing raw data for delete');
            return;
        }

        this.eventService.deleteEvento(event.title, event.rawDate, event.rawOrganizer).subscribe({
            next: () => {
                this.loadEvents();
            },
            error: (err) => alert('Error al eliminar evento')
        });
    }

    toggleAttendance(event: any) {
        if (!this.currentUserAccount) return;
        // Need to construct DTO for ID
        if (!event.rawDate || !event.rawOrganizer) return;

        const dto: EventoDTO = {
            nombre: event.title,
            fecha: event.rawDate,
            usuarioOrganizador: event.rawOrganizer,
            descripcion: event.description,
            attendees: 0, time: '', location: '', category: ''
        };

        this.eventService.toggleAttendance(dto, this.currentUserAccount).subscribe({
            next: (added) => {
                if (added) {
                    event.attendees++;
                    event.isMember = true;
                } else {
                    event.attendees--;
                    event.isMember = false;
                }
                this.triggerChangeDetection();
            },
            error: (err) => console.error(err)
        });
    }

    openParticipantsModal(event: any) {
        this.viewingEvent = event;
        this.showParticipantsModal = true;
        // Load participants from backend
        this.eventService.getEventParticipants(event.title, event.rawDate, event.rawOrganizer).subscribe({
            next: (data) => {
                this.participants = data;
                this.triggerChangeDetection();
            },
            error: (err) => console.error('Error loading participants:', err)
        });
    }

    closeParticipantsModal() {
        this.showParticipantsModal = false;
        this.viewingEvent = null;
        this.participants = [];
    }

    // Permissions
    canEdit(event: any): boolean {
        return this.currentUserAccount === event.rawOrganizer;
    }

    toggleEventMenu(event: any) {
        event.showMenu = !event.showMenu;
        this.triggerChangeDetection();
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}
