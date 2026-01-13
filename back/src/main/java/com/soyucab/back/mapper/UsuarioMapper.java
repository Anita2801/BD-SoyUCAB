package com.soyucab.back.mapper;

import com.soyucab.back.dto.UsuarioDTO;
import com.soyucab.back.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null)
            return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setCuenta(usuario.getCuenta());
        return dto;
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null)
            return null;
        Usuario usuario = new Usuario();
        usuario.setCuenta(dto.getCuenta());
        return usuario;
    }
}
