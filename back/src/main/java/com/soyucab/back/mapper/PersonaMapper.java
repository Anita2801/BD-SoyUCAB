package com.soyucab.back.mapper;

import com.soyucab.back.dto.PersonaDTO;
import com.soyucab.back.model.Persona;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {
    public PersonaDTO toDTO(Persona persona) {
        if (persona == null)
            return null;
        PersonaDTO dto = new PersonaDTO();
        dto.setCi(persona.getCi());
        if (persona.getUsuario() != null) {
            dto.setUsuarioCuenta(persona.getUsuario().getCuenta());
        }
        dto.setPrimerNombre(persona.getPrimerNombre());
        dto.setSegundoNombre(persona.getSegundoNombre());
        dto.setPrimerApellido(persona.getPrimerApellido());
        dto.setSegundoApellido(persona.getSegundoApellido());
        dto.setSexo(persona.getSexo());
        if (persona.getLugar() != null) {
            dto.setLugarNombre(persona.getLugar().getNombre());
            dto.setLugarIso(persona.getLugar().getIsoLugar());
        }
        return dto;
    }
}
