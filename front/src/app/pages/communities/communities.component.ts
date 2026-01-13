import { Component, OnInit, ChangeDetectorRef, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { AuthService } from '../../services/auth/auth';
import { GroupService, GrupoDTO } from '../../services/group.service';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-communities',
    standalone: true,
    imports: [CommonModule, RouterModule, NavbarComponent, FormsModule],
    templateUrl: './communities.component.html',
    styleUrl: './communities.component.css'
})
export class CommunitiesComponent implements OnInit {

    // Filter removed as per user request
    searchQuery: string = '';

    // Initialized empty to avoid flash of fake content
    communities: any[] = [];
    trending: any[] = [];
    myCommunities: any[] = [];

    // Create Modal
    showCreateModal: boolean = false;
    newGroup: GrupoDTO = { nombre: '', descripcion: '', esPrivado: false, numeroMiembros: 1 };
    isCreating: boolean = false;

    currentUserAccount: string | null = null;
    private isBrowser: boolean;

    constructor(
        private authService: AuthService,
        private groupService: GroupService,
        private router: Router,
        private cdr: ChangeDetectorRef,
        @Inject(PLATFORM_ID) platformId: Object
    ) {
        this.isBrowser = isPlatformBrowser(platformId);
    }

    ngOnInit() {
        this.currentUserAccount = this.authService.getAccountFromToken();

        // Load data in parallel-ish
        this.loadData();
    }

    private triggerChangeDetection() {
        if (this.isBrowser) {
            this.cdr.detectChanges();
        }
    }

    loadData() {
        // 1. Get My Groups first (to know membership)
        if (this.currentUserAccount) {
            this.groupService.getMyGroups(this.currentUserAccount).subscribe({
                next: (myGroupsData) => {
                    this.myCommunities = this.mapToView(myGroupsData);
                    this.triggerChangeDetection();
                    // 2. Then Get All Groups (so we can set isMember correctly)
                    this.loadAllGroups();
                },
                error: (err) => {
                    console.error('Error loading my groups:', err);
                    this.loadAllGroups(); // Load anyway
                }
            });
        } else {
            this.loadAllGroups();
        }

        // 3. Load Trending
        this.groupService.getFeaturedGroups().subscribe({
            next: (data) => {
                this.trending = this.mapToView(data);
                this.triggerChangeDetection();
            }
        });
    }

    loadAllGroups() {
        this.groupService.getAllGroups().subscribe({
            next: (data) => {
                // Map and check membership and ownership
                this.communities = this.mapToView(data).reverse().map(group => ({
                    ...group,
                    isMember: this.isUserMember(group.name),
                    isCreator: this.isUserCreator(group.name)
                }));
                this.triggerChangeDetection();
            },
            error: (err) => console.error('Error loading communities:', err)
        });
    }

    isUserMember(groupName: string): boolean {
        return this.myCommunities.some(my => my.name === groupName);
    }

    onSearch() {
        if (!this.searchQuery.trim()) {
            this.loadAllGroups();
            return;
        }

        this.groupService.searchGroups(this.searchQuery).subscribe({
            next: (data) => {
                this.communities = this.mapToView(data).map(group => ({
                    ...group,
                    isMember: this.isUserMember(group.name)
                }));
            },
            error: (err) => console.error('Error searching groups:', err)
        });
    }

    // Modal Actions
    openCreateModal() {
        this.showCreateModal = true;
    }

    closeCreateModal() {
        this.showCreateModal = false;
        this.newGroup = { nombre: '', descripcion: '', esPrivado: false, numeroMiembros: 1 }; // Reset
    }

    createGroup() {
        if (!this.newGroup.nombre || !this.newGroup.descripcion) return;
        if (!this.currentUserAccount) {
            alert('Debes iniciar sesión para crear un grupo.');
            return;
        }

        this.isCreating = true;
        // First create the group
        this.groupService.createGrupo(this.newGroup).subscribe({
            next: (createdGroup) => {
                // Then automatically join as creator/admin
                this.joinGroupAsCreator(createdGroup.nombre);
            },
            error: (err) => {
                console.error('Error creating group:', err);
                this.isCreating = false;
                alert('Error al crear el grupo. Es posible que el nombre ya exista.');
            }
        });
    }

    joinGroupAsCreator(groupName: string) {
        if (!this.currentUserAccount) return;
        const joinData = {
            nombreGrupo: groupName,
            usuarioMiembro: this.currentUserAccount,
            rol: 'Fundador'
        };

        this.groupService.joinGrupo(joinData).subscribe({
            next: () => {
                this.isCreating = false;
                this.closeCreateModal();
                this.loadData(); // Refresh all
                alert('¡Grupo creado exitosamente!');
            },
            error: (err) => {
                console.error('Error joining as creator:', err);
                this.isCreating = false;
            }
        });
    }

    joinGroup(groupName: string) {
        if (!this.currentUserAccount) {
            alert('Debes iniciar sesión para unirte a un grupo.');
            return;
        }

        const joinData = {
            nombreGrupo: groupName,
            usuarioMiembro: this.currentUserAccount,
            rol: 'Miembro'
        };

        this.groupService.joinGrupo(joinData).subscribe({
            next: () => {
                this.loadData();
                alert(`Te has unido a ${groupName}`);
            },
            error: (err) => {
                console.error('Error joining group:', err);
                alert('Error al unirse al grupo.');
            }
        });
    }

    leaveGroup(groupName: string) {
        if (!confirm(`¿Estás seguro de que quieres salir del grupo "${groupName}"?`)) return;
        if (!this.currentUserAccount) return;

        this.groupService.leaveGroup(groupName, this.currentUserAccount).subscribe({
            next: () => {
                this.loadData();
                alert(`Has salido del grupo "${groupName}"`);
            },
            error: (err) => {
                console.error('Error leaving group:', err);
                alert('Error al salir del grupo.');
            }
        });
    }

    // Edit Modal
    showEditModal: boolean = false;
    editingGroup: GrupoDTO = { nombre: '', descripcion: '', esPrivado: false, numeroMiembros: 0 };
    originalGroupName: string = ''; // Guardar nombre original para la API
    isEditing: boolean = false;

    openEditModal(group: any) {
        // Clone to avoid editing directly
        this.originalGroupName = group.name; // Guardar nombre original
        this.editingGroup = {
            nombre: group.name,
            descripcion: group.description,
            esPrivado: group.esPrivado,
            numeroMiembros: group.members
        };
        this.showEditModal = true;
    }

    closeEditModal() {
        this.showEditModal = false;
        this.originalGroupName = '';
        this.editingGroup = { nombre: '', descripcion: '', esPrivado: false, numeroMiembros: 0 };
    }

    updateGroup() {
        if (!this.editingGroup.nombre || !this.editingGroup.descripcion) return;
        this.isEditing = true;

        // Usar nombre original para la API, enviar nuevo nombre en el body
        this.groupService.updateGrupo(this.originalGroupName, this.editingGroup).subscribe({
            next: () => {
                this.isEditing = false;
                this.closeEditModal();
                this.loadData();
                alert('Grupo actualizado correctamente');
            },
            error: (err) => {
                console.error('Error updating group:', err);
                this.isEditing = false;
                alert('Error al actualizar el grupo.');
            }
        });
    }

    deleteGroup(groupName: string) {
        if (!confirm(`¿Estás seguro de que quieres eliminar el grupo "${groupName}"? Esta acción no se puede deshacer.`)) return;

        this.groupService.deleteGrupo(groupName).subscribe({
            next: () => {
                this.loadData();
                alert(`Grupo "${groupName}" eliminado.`);
            },
            error: (err) => {
                console.error('Error deleting group:', err);
                alert('Error al eliminar el grupo.');
            }
        });
    }

    // Checking roles
    isUserCreator(groupName: string): boolean {
        return this.myCommunities.some(my => my.name === groupName && (my.myRole === 'Creador' || my.myRole === 'Fundador' || my.isCreator));
    }

    toggleGroupMenu(group: any) {
        group.showMenu = !group.showMenu;
    }

    // Participants Modal
    showParticipantsModal: boolean = false;
    participantsList: any[] = [];
    selectedGroupName: string = '';

    openParticipantsModal(group: any) {
        group.showMenu = false;
        this.selectedGroupName = group.name;
        this.groupService.getGroupMembers(group.name).subscribe({
            next: (members) => {
                this.participantsList = members;
                this.showParticipantsModal = true;
                this.triggerChangeDetection();
            },
            error: (err) => console.error('Error loading members:', err)
        });
    }

    closeParticipantsModal() {
        this.showParticipantsModal = false;
        this.participantsList = [];
        this.selectedGroupName = '';
    }

    isGroupCreator(groupName: string): boolean {
        return this.isUserCreator(groupName);
    }

    changeMemberRole(member: any, event: Event) {
        const select = event.target as HTMLSelectElement;
        const newRole = select.value;

        this.groupService.updateMemberRole(this.selectedGroupName, member.usuarioMiembro, newRole).subscribe({
            next: () => {
                member.rol = newRole;
                this.triggerChangeDetection();
            },
            error: (err) => {
                console.error('Error updating role:', err);
                alert('Error al cambiar el rol del miembro.');
                // Revert select to original value
                select.value = member.rol;
            }
        });
    }

    mapToView(dtos: GrupoDTO[]): any[] {
        return dtos.map(dto => ({
            name: dto.nombre,
            esPrivado: dto.esPrivado,
            description: dto.descripcion,
            members: dto.numeroMiembros || 0,
            creador: dto.creadorNombre || 'Desconocido',
            posts: 0,
            isTop: false,
            isMember: false, // Calculated later for general list
            isCreator: dto.myRole === 'Creador' || dto.myRole === 'Fundador', // Captured from "My Groups" endpoint
            myRole: dto.myRole,
            color: this.getColor(dto.esPrivado),
            image: this.getGroupImage(dto.nombre),
            showMenu: false
        }));
    }

    getGroupImage(groupName: string): string | null {
        const imageMap: { [key: string]: string } = {
            'Buscando Trabajo': 'buscando-trabajo.jpg',
            'Tutorías y Ayuda Académica': 'tutorias-y-ayuda.png',
            'Cine y Literatura Clásica': 'cine-y-literatura.jpeg',
            'Desarrolladores SQL y NoSQL': 'desarrolladores-sql.png',
            'Práctica de Idioma Polaco': 'idioma-polaco.png'
        };
        const filename = imageMap[groupName];
        return filename ? `/assets/images/${filename}` : null;
    }

    getColor(esPrivado: boolean): string {
        return esPrivado ? 'bg-gray-600' : 'bg-green-600';
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}
