package com.soyucab.back.controller;

import com.soyucab.back.model.Usuario;
import com.soyucab.back.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/reset-password/{cuenta}")
    public String resetPassword(@PathVariable String cuenta) {
        Usuario usuario = usuarioRepository.findById(cuenta).orElse(null);
        if (usuario != null) {
            usuario.setPassword("12345"); // Plain text as per config
            usuarioRepository.save(usuario);
            return "Password reset to 12345 for user: " + cuenta;
        }
        return "User not found";
    }

    @GetMapping("/check/{cuenta}")
    public String checkPassword(@PathVariable String cuenta) {
        Usuario usuario = usuarioRepository.findById(cuenta).orElse(null);
        return usuario != null ? "Password is: " + usuario.getPassword() : "User not found";
    }
}
