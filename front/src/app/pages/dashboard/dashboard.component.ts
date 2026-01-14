import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth/auth';
import { UserService } from '../../services/user.service';
import { ContentService } from '../../services/content.service';
import { EventService } from '../../services/event.service';
import { GroupService } from '../../services/group.service';
import { Router, RouterModule } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, RouterModule, NavbarComponent, FormsModule],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

    currentUser: any = {
        name: 'Cargando...',
        role: 'Estudiante',
        semester: '...',
        bio: 'Miembro de la comunidad UCAB',
        stats: { connections: 0 }
    };

    currentAccount: string | null = null;

    posts: any[] = [];

    upcomingEvents: any[] = [];

    suggestions: any[] = [];

    featuredGroups: any[] = [];

    newPostContent = '';

    constructor(
        private authService: AuthService,
        private userService: UserService,
        private contentService: ContentService,
        private eventService: EventService,
        private groupService: GroupService,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        // 1. Get Current User Info
        const account = this.getAccountFromToken();
        this.currentAccount = account;
        if (!account) {
            this.router.navigate(['/login']);
            return;
        }

        if (account) {
            this.userService.getProfile(account).subscribe({
                next: (profile) => {
                    this.currentUser = {
                        name: profile.name,
                        role: profile.role,
                        semester: profile.semester || 'Semestre no definido',
                        bio: profile.bio || 'Sin descripción',
                        initials: (profile.name.split(' ').map(n => n[0]).join('').substring(0, 2)).toUpperCase(),
                        stats: profile.stats
                    };
                    this.cdr.detectChanges();
                },
                error: (err) => console.error('Error fetching profile:', err)
            });

            // Load Suggestions
            this.userService.getSuggestions(account).subscribe({
                next: (users) => {
                    this.suggestions = users.map(u => ({
                        name: `${u.primerNombre} ${u.primerApellido}`,
                        role: 'Miembro de la comunidad',
                        mutual: Math.floor(Math.random() * 20),
                        initials: (u.primerNombre.charAt(0) + u.primerApellido.charAt(0)).toUpperCase(),
                        color: 'bg-green-700'
                    }));
                    this.cdr.detectChanges();
                },
                error: (err) => console.error('Error loading suggestions:', err)
            });
        }

        // 2. Load Feed
        this.loadFeed();

        // 3. Load Events
        this.eventService.getUpcomingEvents().subscribe({
            next: (events) => {
                this.upcomingEvents = events.map(e => ({
                    day: new Date(e.fecha).getDate().toString(),
                    month: new Date(e.fecha).toLocaleString('es-ES', { month: 'short' }),
                    title: e.nombre,
                    attendees: e.attendees
                }));
                this.cdr.detectChanges();
            },
            error: (err) => console.error('Error loading events:', err)
        });

        // 4. Load Groups
        this.groupService.getFeaturedGroups().subscribe({
            next: (groups) => {
                this.featuredGroups = groups.map(g => ({
                    name: g.nombre,
                    members: g.numeroMiembros || 10,
                    color: 'bg-teal-600'
                }));
                this.cdr.detectChanges();
            },
            error: (err) => console.error('Error loading groups:', err)
        });
    }

    getAccountFromToken(): string | null {
        return this.authService.getAccountFromToken();
    }

    loadFeed() {
        this.contentService.getFeed().subscribe({
            next: (data) => {
                // Sort by date descending (Newest first)
                this.posts = data.sort((a: any, b: any) => {
                    return new Date(b.fechaHoraCreacion).getTime() - new Date(a.fechaHoraCreacion).getTime();
                }).map((item: any) => ({
                    id: Math.random(),
                    author: {
                        name: item.authorName || item.usuarioCreador,
                        account: item.usuarioCreador, // Store account for ownership check
                        role: 'Miembro',
                        initials: (item.authorName || item.usuarioCreador).substring(0, 2).toUpperCase(),
                        color: 'bg-blue-600'
                    },
                    time: new Date(item.fechaHoraCreacion).toLocaleDateString(),
                    fechaHoraCreacion: item.fechaHoraCreacion, // Keep for editing
                    content: item.cuerpo,
                    image: false,
                    stats: {
                        likes: item.meGusta || 0,
                        dislikes: item.noMeGusta || 0,
                        comments: 0,
                        shares: 0
                    },
                    liked: false,
                    disliked: false,
                    showComments: false,
                    showMenu: false, // For menu toggle
                    newCommentText: '', // Initialize for input binding
                    commentsList: []
                }));
                this.cdr.detectChanges();
            },
            error: (err) => console.error('Error loading feed:', err)
        });
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }

    createPost() {
        if (!this.newPostContent.trim()) return;

        const account = this.getAccountFromToken() || 'anon';

        this.contentService.createPost(this.newPostContent, account).subscribe({
            next: (newItem) => {
                // Add to list immediately
                this.posts.unshift({
                    id: Math.random(),
                    author: {
                        name: this.currentUser.name,
                        account: account, // CRITICAL FIX: Add account so menu logic works (isOwner check)
                        role: this.currentUser.role,
                        initials: this.currentUser.initials,
                        color: 'bg-teal-600'
                    },
                    usuarioCreador: account, // Also needed for delete logic
                    fechaHoraCreacion: newItem.fechaHoraCreacion, // Real date from backend
                    time: 'Ahora mismo',
                    content: this.newPostContent,
                    stats: { likes: 0, dislikes: 0, comments: 0, shares: 0 },
                    liked: false,
                    disliked: false,
                    showMenu: false
                });
                this.newPostContent = '';
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error('Error creating post:', err);
                alert('No se pudo publicar. Intente de nuevo.');
            }
        });
    }

    toggleLike(post: any) {
        if (!this.currentAccount) return;

        // Optimistic update
        const wasDisliked = post.disliked;
        if (wasDisliked) {
            post.disliked = false;
            post.stats.dislikes--;
        }
        post.liked = !post.liked;
        if (post.liked) post.stats.likes++;
        else post.stats.likes--;

        // Call backend
        this.contentService.toggleReaction(
            this.currentAccount,
            post.author.account,
            post.fechaHoraCreacion,
            'Me Gusta'
        ).subscribe({
            next: () => {
                // Success - already updated optimistically
            },
            error: (err) => {
                console.error('Error toggling like:', err);
                // Revert on error
                post.liked = !post.liked;
                if (post.liked) post.stats.likes++;
                else post.stats.likes--;
            }
        });
    }

    toggleDislike(post: any) {
        if (!this.currentAccount) return;

        // Optimistic update
        const wasLiked = post.liked;
        if (wasLiked) {
            post.liked = false;
            post.stats.likes--;
        }
        post.disliked = !post.disliked;
        if (post.disliked) post.stats.dislikes++;
        else post.stats.dislikes--;

        // Call backend
        this.contentService.toggleReaction(
            this.currentAccount,
            post.author.account,
            post.fechaHoraCreacion,
            'No Me Gusta'
        ).subscribe({
            next: () => {
                // Success - already updated optimistically
            },
            error: (err) => {
                console.error('Error toggling dislike:', err);
                // Revert on error
                post.disliked = !post.disliked;
                if (post.disliked) post.stats.dislikes++;
                else post.stats.dislikes--;
            }
        });
    }

    toggleComments(post: any) {
        post.showComments = !post.showComments;
    }

    addComment(post: any) {
        if (!post.newCommentText || !post.newCommentText.trim()) return;

        const newComment = {
            user: this.currentUser.name,
            initials: this.currentUser.initials,
            color: 'bg-teal-600', // Default color for current user
            text: post.newCommentText,
            time: 'Ahora'
        };

        post.commentsList.push(newComment);
        post.stats.comments++;
        post.newCommentText = ''; // Clear input
    }

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
        this.editPostContent = post.content;
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

        const originalContent = this.editingPost.content;
        this.editingPost.content = this.editPostContent; // Optimistic update

        const updatePayload = {
            usuarioCreador: this.editingPost.author?.account || this.currentAccount,
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
                this.editingPost.content = originalContent; // Revert
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
        this.posts = this.posts.filter(p => p !== post);
        this.closeDeletePostModal(); // Close modal immediately

        this.contentService.deletePost(post.author.account || this.currentUser.account || post.usuarioCreador, post.fechaHoraCreacion).subscribe({
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
}

