export interface LoginRequest {
    cuenta: string;
    password?: string; // Optional if we use it for other things
    clave?: string; // Match backend if needed, but backend expects 'password' or 'clave'?
    // Backend LoginRequest: private String cuenta; private String password;
}

export interface RegisterRequest {
    cuenta: string;
    password?: string;
    email?: string; // Auto-generated, optional
    ci: string;
    primerNombre: string;
    segundoNombre?: string;
    primerApellido: string;
    segundoApellido?: string;
    sexo: string;
    lugarIso: string;
    rol: string;
    entidadCodigo: string;
    fechaInicio: string;
    fechaFin?: string;
}

export interface AuthResponse {
    token: string;
}
