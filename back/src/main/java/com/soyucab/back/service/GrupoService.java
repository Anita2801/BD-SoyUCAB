package com.soyucab.back.service;

import com.soyucab.back.model.Grupo;
import com.soyucab.back.model.GrupoParticipa;
import com.soyucab.back.repository.GrupoRepository;
import com.soyucab.back.repository.GrupoParticipaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private GrupoParticipaRepository grupoParticipaRepository;

    public Grupo save(Grupo grupo) {
        return grupoRepository.save(grupo);
    }

    public List<Grupo> findAll() {
        return grupoRepository.findAll();
    }

    public List<Grupo> getFeaturedGroups() {
        return grupoRepository.findAll().stream().limit(3).collect(java.util.stream.Collectors.toList());
    }

    public Optional<Grupo> findByName(String name) {
        return grupoRepository.findById(name);
    }

    public GrupoParticipa addMember(GrupoParticipa membership) {
        return grupoParticipaRepository.save(membership);
    }

    public List<GrupoParticipa> getMembers(String groupName) {
        return grupoParticipaRepository.findByGrupo_Nombre(groupName);
    }

    public List<Grupo> searchGroups(String query) {
        return grupoRepository.findByNombreContainingIgnoreCase(query);
    }

    public List<GrupoParticipa> getUserGroups(String account) {
        return grupoParticipaRepository.findByUsuario_Cuenta(account);
    }

    @org.springframework.transaction.annotation.Transactional
    public Grupo update(String nombreOriginal, Grupo grupoDetails) {
        return grupoRepository.findById(nombreOriginal).map(grupo -> {
            String nuevoNombre = grupoDetails.getNombre();

            // Si el nombre cambió, necesitamos crear un nuevo grupo y migrar miembros
            if (nuevoNombre != null && !nuevoNombre.equals(nombreOriginal)) {
                // 1. Crear el nuevo grupo
                Grupo nuevoGrupo = new Grupo();
                nuevoGrupo.setNombre(nuevoNombre);
                nuevoGrupo.setDescripcion(grupoDetails.getDescripcion());
                nuevoGrupo.setTipo(grupoDetails.getTipo());
                grupoRepository.save(nuevoGrupo);

                // 2. Migrar miembros al nuevo grupo
                List<GrupoParticipa> miembros = grupoParticipaRepository.findByGrupo_Nombre(nombreOriginal);
                for (GrupoParticipa miembro : miembros) {
                    GrupoParticipa nuevoMiembro = new GrupoParticipa();
                    nuevoMiembro.setId(
                            new com.soyucab.back.model.GrupoParticipaId(nuevoNombre, miembro.getUsuario().getCuenta()));
                    nuevoMiembro.setGrupo(nuevoGrupo);
                    nuevoMiembro.setUsuario(miembro.getUsuario());
                    nuevoMiembro.setRol(miembro.getRol());
                    grupoParticipaRepository.save(nuevoMiembro);
                }

                // 3. Eliminar membresías y grupo antiguo
                grupoParticipaRepository.deleteByGrupo_Nombre(nombreOriginal);
                grupoRepository.deleteById(nombreOriginal);

                return nuevoGrupo;
            } else {
                // Solo actualizar descripción y tipo
                grupo.setDescripcion(grupoDetails.getDescripcion());
                grupo.setTipo(grupoDetails.getTipo());
                return grupoRepository.save(grupo);
            }
        }).orElseThrow(() -> new RuntimeException("Grupo not found with name " + nombreOriginal));
    }

    @org.springframework.transaction.annotation.Transactional
    public void delete(String nombre) {
        if (grupoRepository.existsById(nombre)) {
            grupoParticipaRepository.deleteByGrupo_Nombre(nombre);
            grupoRepository.deleteById(nombre);
        } else {
            throw new RuntimeException("Grupo not found with name " + nombre);
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void removeMember(String groupName, String usuario) {
        com.soyucab.back.model.GrupoParticipaId id = new com.soyucab.back.model.GrupoParticipaId(groupName, usuario);
        grupoParticipaRepository.deleteById(id);
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateMemberRole(String groupName, String usuario, String newRole) {
        com.soyucab.back.model.GrupoParticipaId id = new com.soyucab.back.model.GrupoParticipaId(groupName, usuario);
        grupoParticipaRepository.findById(id).ifPresent(member -> {
            member.setRol(newRole);
            grupoParticipaRepository.save(member);
        });
    }
}
