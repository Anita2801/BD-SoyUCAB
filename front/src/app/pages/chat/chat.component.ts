import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked, ChangeDetectorRef, Inject, PLATFORM_ID, NgZone, ApplicationRef } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { ChatService, ChatDTO, MessageDTO } from '../../services/chat.service';
import { AuthService } from '../../services/auth/auth';

@Component({
    selector: 'app-chat',
    standalone: true,
    imports: [CommonModule, FormsModule, NavbarComponent],
    templateUrl: './chat.component.html',
    styles: [`
    .chat-height { height: calc(100vh - 80px); }
    .msg-content { max-width: 70%; }
  `]
})
export class ChatComponent implements OnInit, AfterViewChecked {
    @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

    chats: any[] = [];
    messages: any[] = [];
    selectedChat: any = null;
    newMessage: string = '';
    currentUser: any = null;
    showMembers: boolean = false;
    members: any[] = [];
    isLoading = false;
    private isBrowser: boolean;

    // CRUD properties
    showCreateModal = false;
    showOptionsMenu = false;
    newChatName = '';
    userRole: string = '';

    // Add Members
    showAddMembersModal = false;
    availableFriends: any[] = [];
    selectedFriends: Set<string> = new Set();
    isCreating = false;

    constructor(
        private chatService: ChatService,
        private authService: AuthService,
        private cdr: ChangeDetectorRef,
        private zone: NgZone,
        private appRef: ApplicationRef,
        @Inject(PLATFORM_ID) platformId: Object
    ) {
        this.isBrowser = isPlatformBrowser(platformId);
    }

    private triggerChangeDetection() {
        if (this.isBrowser) {
            this.cdr.markForCheck();
            this.cdr.detectChanges();
            this.appRef.tick();
        }
    }

    ngOnInit() {
        const userId = this.authService.getAccountFromToken();
        if (userId) {
            this.currentUser = userId;
            this.loadChats();
        }
    }

    ngAfterViewChecked() {
        this.scrollToBottom();
    }

    scrollToBottom(): void {
        if (this.scrollContainer) {
            this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
        }
    }

    loadChats() {
        this.isLoading = true;
        const userId = this.currentUser;
        if (userId) {
            this.chatService.getUserChats(userId).subscribe({
                next: (chats) => {
                    this.chats = chats;
                    this.isLoading = false;
                    if (this.chats.length > 0) {
                        this.selectChat(this.chats[0]);
                    }
                    this.triggerChangeDetection();
                },
                error: (err) => {
                    console.error('Error loading chats:', err);
                    this.isLoading = false;
                }
            });
        }
    }

    selectChat(chat: any) {
        this.selectedChat = chat;
        this.showOptionsMenu = false;
        this.messages = []; // Clear messages while loading
        this.userRole = ''; // Reset role while loading

        // Load user role first to ensure proper permissions display
        this.chatService.getUserRole(chat.nombre, chat.fechaCreacion, this.currentUser).subscribe({
            next: (res) => {
                this.zone.run(() => {
                    this.userRole = res.role || '';
                    this.triggerChangeDetection();
                });
            },
            error: () => {
                this.userRole = '';
                this.triggerChangeDetection();
            }
        });

        // Load messages
        this.chatService.getChatMessages(chat.nombre, chat.fechaCreacion, this.currentUser).subscribe({
            next: (msgs) => {
                this.zone.run(() => {
                    this.messages = msgs ? [...msgs] : [];
                    this.triggerChangeDetection();
                    setTimeout(() => this.scrollToBottom(), 50);
                });
            },
            error: (err) => {
                console.error('Error loading messages:', err);
                this.messages = [];
                this.triggerChangeDetection();
            }
        });
        this.showMembers = false;
    }

    toggleMembers() {
        this.showMembers = !this.showMembers;
        if (this.showMembers && this.selectedChat) {
            this.loadMembers();
        }
    }

    loadMembers() {
        if (!this.selectedChat) return;
        this.chatService.getChatMembers(this.selectedChat.nombre, this.selectedChat.fechaCreacion)
            .subscribe({
                next: (members) => {
                    this.members = members || [];
                    this.triggerChangeDetection();
                },
                error: (err) => console.error('Error loading members:', err)
            });
    }

    loadMessages(chat: ChatDTO) {
        this.chatService.getChatMessages(chat.nombre, chat.fechaCreacion, this.currentUser).subscribe({
            next: (msgs) => {
                this.messages = [...msgs];
                this.triggerChangeDetection();
                setTimeout(() => this.scrollToBottom(), 0);
            },
            error: (err) => console.error('Error loading messages', err)
        });
    }

    sendMessage() {
        if (!this.newMessage.trim() || !this.selectedChat) return;

        const content = this.newMessage;
        this.newMessage = '';

        this.chatService.sendMessage(
            this.selectedChat.nombre,
            this.selectedChat.fechaCreacion,
            this.currentUser,
            content
        ).subscribe({
            next: () => {
                if (this.selectedChat) {
                    this.loadMessages(this.selectedChat);
                }
            },
            error: (err) => {
                console.error('Error sending message', err);
                this.newMessage = content;
            }
        });
    }

    // ====================== CRUD METHODS ======================

    toggleOptionsMenu() {
        this.showOptionsMenu = !this.showOptionsMenu;
    }

    openCreateModal() {
        this.showCreateModal = true;
        this.newChatName = '';
        this.isCreating = false;
    }

    closeCreateModal() {
        this.showCreateModal = false;
    }

    createChat() {
        if (!this.newChatName.trim()) return;
        if (this.isCreating) return;

        this.isCreating = true;
        const chatName = this.newChatName.trim();

        this.chatService.createChat(chatName, this.currentUser, []).subscribe({
            next: (newChat) => {
                this.zone.run(() => {
                    this.isCreating = false;
                    this.showCreateModal = false;
                    this.newChatName = '';

                    // Reload chats and select the new one
                    this.chatService.getUserChats(this.currentUser).subscribe({
                        next: (chats) => {
                            this.chats = chats;
                            // Find and select the newly created chat
                            const created = chats.find(c => c.nombre === chatName);
                            if (created) {
                                this.selectChat(created);
                            } else if (chats.length > 0) {
                                this.selectChat(chats[0]);
                            }
                            this.triggerChangeDetection();
                        },
                        error: () => this.triggerChangeDetection()
                    });
                });
            },
            error: (err) => {
                this.zone.run(() => {
                    this.isCreating = false;
                    console.error('Error creating chat:', err);
                    alert('Error al crear el chat: ' + (err.error?.message || err.message || 'Error desconocido'));
                    this.triggerChangeDetection();
                });
            }
        });
    }

    // Delete Chat Modal
    showDeleteChatModal = false;

    openDeleteChatModal() {
        if (!this.selectedChat) return;
        this.showOptionsMenu = false;
        this.showDeleteChatModal = true;
        this.triggerChangeDetection();
    }

    closeDeleteChatModal() {
        this.showDeleteChatModal = false;
    }

    confirmDeleteChat() {
        if (!this.selectedChat) return;

        const chatToDelete = this.selectedChat;
        this.closeDeleteChatModal(); // Close modal immediately

        this.chatService.deleteChat(chatToDelete.nombre, chatToDelete.fechaCreacion, this.currentUser).subscribe({
            next: () => {
                this.zone.run(() => {
                    this.selectedChat = null;
                    this.messages = [];
                    this.userRole = '';
                    this.loadChats();
                    this.triggerChangeDetection();
                });
            },
            error: (err) => {
                this.zone.run(() => {
                    console.error('Error deleting chat:', err);
                    alert('Error: ' + (err.error?.message || 'Solo el creador puede eliminar el chat'));
                    this.triggerChangeDetection();
                });
            }
        });
    }

    deleteChat() {
        this.openDeleteChatModal();
    }

    leaveChat() {
        if (!this.selectedChat) return;
        if (!confirm('¿Estás seguro de que deseas abandonar este chat?')) return;

        this.chatService.leaveChat(this.selectedChat.nombre, this.selectedChat.fechaCreacion, this.currentUser).subscribe({
            next: () => {
                this.selectedChat = null;
                this.loadChats();
            },
            error: (err) => {
                console.error('Error leaving chat:', err);
                alert('Error al abandonar el chat');
            }
        });
    }

    changeMemberRole(member: any, event: any) {
        const newRole = event.target.value;
        if (!this.selectedChat) return;

        this.chatService.updateMemberRole(
            this.selectedChat.nombre,
            this.selectedChat.fechaCreacion,
            member.username,
            newRole,
            this.currentUser
        ).subscribe({
            next: () => {
                member.role = newRole;
                this.triggerChangeDetection();
            },
            error: (err) => {
                console.error('Error updating role:', err);
                alert('Error: Solo el creador puede cambiar roles');
                event.target.value = member.role; // revert
            }
        });
    }

    isCreator(): boolean {
        return this.userRole === 'Creador';
    }

    canAddMembers(): boolean {
        return this.userRole === 'Creador' || this.userRole === 'Administrador';
    }

    openAddMembersModal() {
        if (!this.selectedChat) return;
        this.showAddMembersModal = true;
        this.selectedFriends.clear();
        this.availableFriends = [];

        this.chatService.getFriends(this.currentUser).subscribe(friends => {
            // Filter out existing members
            const currentMemberIds = new Set(this.members.map(m => m.username));
            this.availableFriends = friends.filter(f => !currentMemberIds.has(f.username));
            this.triggerChangeDetection();
        });
    }

    closeAddMembersModal() {
        this.showAddMembersModal = false;
    }

    toggleFriendSelection(username: string) {
        if (this.selectedFriends.has(username)) {
            this.selectedFriends.delete(username);
        } else {
            this.selectedFriends.add(username);
        }
    }

    addSelectedMembers() {
        if (this.selectedFriends.size === 0 || !this.selectedChat) return;

        const memberIds = Array.from(this.selectedFriends);
        this.chatService.addMembers(
            this.selectedChat.nombre,
            this.selectedChat.fechaCreacion,
            memberIds,
            this.currentUser
        ).subscribe({
            next: () => {
                this.closeAddMembersModal();
                this.loadMembers(); // Refresh members
                alert('Miembros agregados exitosamente');
            },
            error: (err) => {
                console.error('Error adding members', err);
                alert('Error al agregar miembros');
            }
        });
    }
}
