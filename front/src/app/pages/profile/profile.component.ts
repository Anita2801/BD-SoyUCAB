import { Component, OnInit, ChangeDetectorRef, NgZone, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth/auth';
import { Router, RouterModule } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { UserService, ProfileDTO, ProfileUpdateDTO } from '../../services/user.service';
import { ContentService } from '../../services/content.service';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, RouterModule, NavbarComponent, FormsModule],
    templateUrl: './profile.component.html',
    styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

    user: ProfileDTO | null = null;
    isLoading = true;
    currentAccount: string | null = null;

    // Menu and Modals
    showMenu = false;
    showEditModal = false;
    showDeleteConfirm = false;
    isSaving = false;

    // Edit form data
    editForm: ProfileUpdateDTO = {};

    // Posts
    posts: any[] = [];

    constructor(
        private authService: AuthService,
        private userService: UserService,
        private contentService: ContentService,
        private router: Router,
        private cdr: ChangeDetectorRef,
        private ngZone: NgZone,
        @Inject(PLATFORM_ID) private platformId: Object
    ) { }

    triggerChangeDetection() {
        if (isPlatformBrowser(this.platformId)) {
            this.cdr.detectChanges();
        }
    }

    ngOnInit() {
        this.currentAccount = this.authService.getAccountFromToken();
        if (!this.currentAccount) {
            this.router.navigate(['/login']);
            return;
        }

        this.loadProfile();
        this.loadPosts();
    }

    loadProfile() {
        if (!this.currentAccount) return;
        this.userService.getProfile(this.currentAccount).subscribe({
            next: (data) => {
                this.ngZone.run(() => {
                    this.user = data;
                    this.isLoading = false;
                    this.triggerChangeDetection();
                });
            },
            error: (err) => {
                console.error('Error loading profile:', err);
                this.isLoading = false;
            }
        });
    }

    loadPosts() {
        if (!this.currentAccount) return;
        this.userService.getUserPosts(this.currentAccount).subscribe({
            next: (data) => {
                this.posts = data;
                this.triggerChangeDetection();
            },
            error: (err) => console.error('Error loading posts:', err)
        });
    }

    // Menu
    toggleMenu() {
        this.showMenu = !this.showMenu;
    }

    closeMenu() {
        this.showMenu = false;
    }

    // Edit Modal
    openEditModal() {
        this.closeMenu();
        if (!this.user) return;

        this.editForm = {
            primerNombre: this.user.primerNombre || '',
            segundoNombre: this.user.segundoNombre || '',
            primerApellido: this.user.primerApellido || '',
            segundoApellido: this.user.segundoApellido || '',
            bio: this.user.bio || '',
            location: this.user.location || '',
            sexo: this.user.sexo || '',
            experience: this.user.experience ? this.user.experience.map(e => ({
                role: e.role,
                company: e.company,
                startDate: e.startDate || '',
                endDate: e.endDate || '',
                description: e.description
            })) : [],
            languages: this.user.languages ? this.user.languages.map(l => ({
                name: l.name,
                level: l.level
            })) : []
        };
        this.showEditModal = true;
    }

    // Dynamic Experience Fields
    addExperience() {
        if (!this.editForm.experience) this.editForm.experience = [];
        this.editForm.experience.push({
            role: '',
            company: '',
            startDate: '',
            endDate: '',
            description: ''
        });
    }

    removeExperience(index: number) {
        if (this.editForm.experience) {
            this.editForm.experience.splice(index, 1);
        }
    }

    // Dynamic Language Fields
    addLanguage() {
        if (!this.editForm.languages) this.editForm.languages = [];
        this.editForm.languages.push({
            name: '',
            level: 50
        });
    }

    removeLanguage(index: number) {
        if (this.editForm.languages) {
            this.editForm.languages.splice(index, 1);
        }
    }

    closeEditModal() {
        this.showEditModal = false;
    }

    saveProfile() {
        if (!this.currentAccount) return;
        this.isSaving = true;
        this.userService.updateProfile(this.currentAccount, this.editForm).subscribe({
            next: (updatedProfile) => {
                this.user = updatedProfile;
                this.isSaving = false;
                this.closeEditModal();
                this.triggerChangeDetection();
            },
            error: (err) => {
                console.error('Error updating profile:', err);
                this.isSaving = false;
                alert('Error al guardar el perfil');
            }
        });
    }

    // Delete
    openDeleteConfirm() {
        this.closeMenu();
        this.showDeleteConfirm = true;
    }

    closeDeleteConfirm() {
        this.showDeleteConfirm = false;
    }

    confirmDelete() {
        if (!this.currentAccount) return;
        this.userService.deleteProfile(this.currentAccount).subscribe({
            next: () => {
                this.authService.logout();
                this.router.navigate(['/login']);
            },
            error: (err) => {
                console.error('Error deleting profile:', err);
                alert('Error al eliminar el perfil');
            }
        });
    }

    getInitials(name: string): string {
        if (!name) return '';
        return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
    }

    // Post Actions
    togglePostMenu(post: any) {
        post.showMenu = !post.showMenu;
        this.cdr.detectChanges();
    }

    // Edit Post Modal
    showEditPostModal = false;
    editingPost: any = null;
    editPostContent = '';

    openEditPostModal(post: any) {
        post.showMenu = false;
        this.editingPost = post;
        this.editPostContent = post.cuerpo;
        this.showEditPostModal = true;
        this.cdr.detectChanges();
    }

    closeEditPostModal() {
        this.showEditPostModal = false;
        this.editingPost = null;
        this.editPostContent = '';
    }

    saveEditedPost() {
        if (!this.editingPost || !this.editPostContent.trim()) return;

        const originalContent = this.editingPost.cuerpo;
        this.editingPost.cuerpo = this.editPostContent; // Optimistic update

        const updatePayload = {
            usuarioCreador: this.currentAccount!,
            fechaHoraCreacion: this.editingPost.fechaHoraCreacion,
            cuerpo: this.editPostContent
        };

        this.contentService.updatePost(updatePayload).subscribe({
            next: () => {
                this.closeEditPostModal();
            },
            error: (err) => {
                console.error('Error updating post', err);
                alert('Error al actualizar la publicación');
                this.editingPost.cuerpo = originalContent; // Revert
            }
        });
    }

    editPost(post: any) {
        this.openEditPostModal(post);
    }

    // Delete Post Modal
    showDeletePostModal = false;
    deletingPost: any = null;

    openDeletePostModal(post: any) {
        post.showMenu = false;
        this.deletingPost = post;
        this.showDeletePostModal = true;
        this.cdr.detectChanges();
    }

    closeDeletePostModal() {
        this.showDeletePostModal = false;
        this.deletingPost = null;
    }

    confirmDeletePost() {
        if (!this.deletingPost) return;

        const post = this.deletingPost;
        const originalPosts = [...this.posts];

        // Optimistic remove
        this.posts = this.posts.filter(p => p.fechaHoraCreacion !== post.fechaHoraCreacion);
        this.closeDeletePostModal();

        this.contentService.deletePost(this.currentAccount!, post.fechaHoraCreacion).subscribe({
            next: () => {
                // Success
            },
            error: (err) => {
                console.error('Error deleting post', err);
                alert('Error al eliminar la publicación');
                this.posts = originalPosts; // Revert
            }
        });
    }

    deletePost(post: any) {
        this.openDeletePostModal(post);
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}
