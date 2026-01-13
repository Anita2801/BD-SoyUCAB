import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { UserService, NotificationDTO } from '../../services/user.service';
import { AuthService } from '../../services/auth/auth';

@Component({
    selector: 'app-notifications',
    standalone: true,
    imports: [CommonModule, RouterModule, NavbarComponent],
    templateUrl: './notifications.component.html'
})
export class NotificationsComponent implements OnInit {

    notifications: NotificationDTO[] = [];
    isLoading = true;

    constructor(
        private userService: UserService,
        private authService: AuthService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        // Get current user account
        // Hack: hardcoded for now or get from auth
        const account = 'qdmancha.22';

        this.userService.getNotifications(account).subscribe({
            next: (data) => {
                this.notifications = data;
                this.isLoading = false;
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error('Error loading notifications:', err);
                this.isLoading = false;
            }
        });
    }
}
