package com.soyucab.back.service;

import com.soyucab.back.model.Usuario;
import com.soyucab.back.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(String cuenta) {
        return usuarioRepository.findById(cuenta);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(String cuenta) {
        usuarioRepository.deleteById(cuenta);
    }
}
