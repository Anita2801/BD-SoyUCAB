import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { CommunitiesComponent } from './pages/communities/communities.component';
import { EventsComponent } from './pages/events/events.component';
import { authGuard } from './services/auth/auth.guard';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
    { path: 'communities', component: CommunitiesComponent, canActivate: [authGuard] },
    { path: 'events', component: EventsComponent, canActivate: [authGuard] },
    { path: 'chat', loadComponent: () => import('./pages/chat/chat.component').then(m => m.ChatComponent), canActivate: [authGuard] },
    { path: '', redirectTo: '/login', pathMatch: 'full' }
];
