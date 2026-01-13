import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth'; // or './auth.service' depending on rename
// Actually it is 'auth.ts' but class is AuthService. 

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const token = authService.token;

    // Skip adding token for auth endpoints
    if (token && !req.url.includes('/auth')) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    return next(req);
};
