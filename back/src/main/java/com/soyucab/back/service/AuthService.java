package com.soyucab.back.service;

import com.soyucab.back.config.JwtService;
import com.soyucab.back.dto.AuthResponse;
import com.soyucab.back.dto.LoginRequest;
import com.soyucab.back.dto.RegisterRequest;
import com.soyucab.back.model.Usuario;
import com.soyucab.back.model.Persona;
import com.soyucab.back.model.Lugar;
import com.soyucab.back.model.Rol;
import com.soyucab.back.repository.UsuarioRepository;
import com.soyucab.back.repository.PersonaRepository;
import com.soyucab.back.repository.LugarRepository;
import com.soyucab.back.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UsuarioRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        @Autowired
        private PersonaRepository personaRepository;

        @Autowired
        private LugarRepository lugarRepository;

        @Autowired
        private RolRepository rolRepository;

        @Transactional
        public AuthResponse register(RegisterRequest request) {
                // 1. Create Usuario
                var user = new Usuario();
                user.setCuenta(request.getCuenta());
                user.setPassword(passwordEncoder.encode(request.getPassword()));

                // Generate email automatically: cuenta@ucab.edu.ve
                String email = request.getCuenta() + "@ucab.edu.ve";
                user.setEmail(email);

                // Set nombre and apellido in Usuario table as well (for compatibility)
                user.setNombre(request.getPrimerNombre());
                user.setApellido(request.getPrimerApellido());

                repository.save(user);

                // 2. Create Persona if CI is provided
                if (request.getCi() != null && !request.getCi().isEmpty()) {
                        Persona persona = new Persona();
                        persona.setCi(request.getCi());
                        persona.setUsuario(user);
                        persona.setPrimerNombre(request.getPrimerNombre());
                        persona.setSegundoNombre(request.getSegundoNombre());
                        persona.setPrimerApellido(request.getPrimerApellido());
                        persona.setSegundoApellido(request.getSegundoApellido());
                        persona.setSexo(request.getSexo() != null ? request.getSexo() : "No especificado");

                        // Set Lugar
                        if (request.getLugarIso() != null && !request.getLugarIso().isEmpty()) {
                                Lugar lugar = lugarRepository.findById(request.getLugarIso()).orElse(null);
                                persona.setLugar(lugar);
                        }

                        // Set Roles
                        if (request.getRol() != null && !request.getRol().isEmpty()) {
                                Rol rol = rolRepository.findById(request.getRol()).orElse(null);
                                if (rol != null) {
                                        List<Rol> roles = new ArrayList<>();
                                        roles.add(rol);
                                        persona.setRoles(roles);
                                }
                        }

                        personaRepository.save(persona);
                }

                // 3. Generate JWT token
                var userDetails = new User(user.getCuenta(), user.getPassword(), Collections.emptyList());
                var jwtToken = jwtService.generateToken(userDetails);

                return AuthResponse.builder()
                                .token(jwtToken)
                                .build();
        }

        public AuthResponse authenticate(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getCuenta(),
                                                request.getPassword()));
                var user = repository.findById(request.getCuenta())
                                .orElseThrow();

                var userDetails = new User(user.getCuenta(), user.getPassword() != null ? user.getPassword() : "",
                                Collections.emptyList());

                var jwtToken = jwtService.generateToken(userDetails);
                return AuthResponse.builder()
                                .token(jwtToken)
                                .build();
        }
}
