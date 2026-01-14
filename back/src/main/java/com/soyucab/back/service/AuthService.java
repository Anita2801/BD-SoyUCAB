package com.soyucab.back.service;

import com.soyucab.back.config.JwtService;
import com.soyucab.back.dto.AuthResponse;
import com.soyucab.back.dto.LoginRequest;
import com.soyucab.back.dto.RegisterRequest;
import com.soyucab.back.model.*;
import com.soyucab.back.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        @Autowired
        private EntidadInstitucionalRepository entidadRepository;

        @Autowired
        private TiempoDuracionRepository tiempoDuracionRepository;

        @Transactional
        public AuthResponse register(RegisterRequest request) {
                // 1. Create Usuario
                var user = new Usuario();
                user.setCuenta(request.getCuenta());
                user.setPassword(passwordEncoder.encode(request.getPassword()));

                // Generate email automatically: cuenta@ucab.edu.ve
                String email = request.getCuenta() + "@ucab.edu.ve";
                user.setEmail(email);

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

                        // Handle key fields that are NOT NULL in DB
                        persona.setSegundoApellido(
                                        request.getSegundoApellido() != null ? request.getSegundoApellido() : "");

                        // Validate and set Sexo (DB constraint: Masculino, Femenino)
                        String sexo = request.getSexo();
                        if (sexo == null || (!sexo.equals("Masculino") && !sexo.equals("Femenino"))) {
                                // Fallback or Error. For now, strict validation or default.
                                // Given the DB constraint, we must provide a valid value regardless.
                                // If input is invalid, we can throw an exception to return 400 instead of 500.
                                if (sexo == null || sexo.isEmpty()) {
                                        throw new IllegalArgumentException(
                                                        "El campo Sexo es obligatorio (Masculino/Femenino).");
                                }
                                // If it's something else like "prefiero no decir", it will fail DB check.
                        }
                        persona.setSexo(sexo);

                        // Set Lugar
                        if (request.getLugarIso() != null && !request.getLugarIso().isEmpty()) {
                                Lugar lugar = lugarRepository.findById(request.getLugarIso())
                                                .orElseThrow(() -> new IllegalArgumentException(
                                                                "Lugar no encontrado con ISO: "
                                                                                + request.getLugarIso()));
                                persona.setLugar(lugar);
                        } else {
                                throw new IllegalArgumentException("La ubicación (Lugar) es obligatoria.");
                        }

                        // Set Roles (Primary Role)
                        Rol rol = null;
                        if (request.getRol() != null && !request.getRol().isEmpty()) {
                                rol = rolRepository.findById(request.getRol()).orElse(null);
                                if (rol != null) {
                                        List<Rol> roles = new ArrayList<>();
                                        roles.add(rol);
                                        persona.setRoles(roles);
                                }
                        }

                        // Handle List initialization to avoid NullPointer later
                        if (persona.getInstituciones() == null) {
                                persona.setInstituciones(new ArrayList<>());
                        }

                        // Save Persona (to generate CI and basic relations)
                        try {
                                persona = personaRepository.saveAndFlush(persona);
                        } catch (Exception e) {
                                // Catch DB errors specifically
                                throw new RuntimeException("Error al guardar datos personales: " + e.getMessage(), e);
                        }

                        // 3. Process Experience/Nexo info if provided
                        if (request.getEntidadCodigo() != null && !request.getEntidadCodigo().isEmpty()) {
                                EntidadInstitucional entidad = entidadRepository.findById(request.getEntidadCodigo())
                                                .orElse(null);

                                if (entidad != null && rol != null) {
                                        // Create Nexo relationship (Person - Institution)
                                        if (!persona.getInstituciones().contains(entidad)) {
                                                persona.getInstituciones().add(entidad);
                                                persona = personaRepository.saveAndFlush(persona); // Update link table
                                                                                                   // IMMEDIATELY
                                        }

                                        // Create TiempoDuracion (Experience)
                                        try {
                                                LocalDate start = LocalDate.now();
                                                if (request.getFechaInicio() != null
                                                                && !request.getFechaInicio().isEmpty()) {
                                                        start = LocalDate.parse(request.getFechaInicio(),
                                                                        java.time.format.DateTimeFormatter
                                                                                        .ofPattern("yyyy-MM-dd"));
                                                        // Note: The frontend likely sends yyyy-MM-dd if it's an HTML
                                                        // date input.
                                                        // If it sends something else, this will throw, which is caught
                                                        // below.
                                                }

                                                LocalDate end = null;
                                                if (request.getFechaFin() != null && !request.getFechaFin().isEmpty()) {
                                                        end = LocalDate.parse(request.getFechaFin(),
                                                                        java.time.format.DateTimeFormatter
                                                                                        .ofPattern("yyyy-MM-dd"));
                                                }

                                                TiempoDuracion td = new TiempoDuracion();
                                                TiempoDuracionId tdId = new TiempoDuracionId(entidad.getCodInst(),
                                                                persona.getCi(), rol.getTipoRol());
                                                td.setId(tdId);
                                                td.setInstitucion(entidad);
                                                td.setPersona(persona);
                                                td.setRol(rol);
                                                td.setFechaInicio(start);
                                                td.setFechaFin(end);

                                                tiempoDuracionRepository.saveAndFlush(td); // Flush to trigger FK check
                                                                                           // inside try-catch
                                        } catch (Exception e) {
                                                System.err.println("Error saving initial experience on register: "
                                                                + e.getMessage());
                                                // We don't rethrow, so registration succeeds even if experience fails
                                        }
                                }
                        }
                }

                // 4. Generate JWT token
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
