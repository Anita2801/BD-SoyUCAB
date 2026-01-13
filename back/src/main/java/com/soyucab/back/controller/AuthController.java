package com.soyucab.back.controller;

import com.soyucab.back.dto.AuthResponse;
import com.soyucab.back.dto.LoginRequest;
import com.soyucab.back.dto.RegisterRequest;
import com.soyucab.back.model.Lugar;
import com.soyucab.back.model.Rol;
import com.soyucab.back.model.EntidadInstitucional;
import com.soyucab.back.repository.LugarRepository;
import com.soyucab.back.repository.RolRepository;
import com.soyucab.back.repository.EntidadInstitucionalRepository;
import com.soyucab.back.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @Autowired
    private LugarRepository lugarRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EntidadInstitucionalRepository entidadRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/lugares")
    public List<Lugar> getAllLugares() {
        return lugarRepository.findAll();
    }

    @GetMapping("/roles")
    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    @GetMapping("/entidades")
    public List<EntidadInstitucional> getAllEntidades() {
        return entidadRepository.findAll();
    }
}
