package com.soyucab.back.service;

import com.soyucab.back.dto.ProfileDTO;
import com.soyucab.back.model.*;
import com.soyucab.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private DescripcionRepository descripcionRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private SeRelacionaRepository seRelacionaRepository;

    @Autowired
    private TiempoDuracionRepository tiempoDuracionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrganizacionAsociadaRepository organizacionAsociadaRepository;

    public java.util.List<Persona> findAll() {
        return personaRepository.findAll();
    }

    public java.util.Optional<Persona> findById(String id) {
        return personaRepository.findById(id);
    }

    public Persona save(Persona persona) {
        return personaRepository.save(persona);
    }

    public void deleteById(String id) {
        personaRepository.deleteById(id);
    }

    public java.util.Optional<Persona> findByUsuario(String cuenta) {
        return personaRepository.findByUsuario_Cuenta(cuenta);
    }

    public java.util.List<Persona> getSuggestions(String cuenta) {
        // Naive implementation or use repository method
        return personaRepository.findTop3ByUsuario_CuentaNot(cuenta);
    }

    public ProfileDTO updateProfile(String cuenta, com.soyucab.back.dto.ProfileUpdateDTO updateDTO) {
        Persona persona = personaRepository.findByUsuario_Cuenta(cuenta)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + cuenta));

        // Update name fields
        if (updateDTO.getPrimerNombre() != null)
            persona.setPrimerNombre(updateDTO.getPrimerNombre());
        if (updateDTO.getSegundoNombre() != null)
            persona.setSegundoNombre(updateDTO.getSegundoNombre());
        if (updateDTO.getPrimerApellido() != null)
            persona.setPrimerApellido(updateDTO.getPrimerApellido());
        if (updateDTO.getSegundoApellido() != null)
            persona.setSegundoApellido(updateDTO.getSegundoApellido());

        if (updateDTO.getSexo() != null)
            persona.setSexo(updateDTO.getSexo());

        personaRepository.save(persona);

        // Update bio in Descripcion if provided
        if (updateDTO.getBio() != null) {
            var descriptions = descripcionRepository.findByUsuario_Cuenta(cuenta);
            if (!descriptions.isEmpty()) {
                var desc = descriptions.get(0);
                desc.getId().setDescripcion(updateDTO.getBio());
                descripcionRepository.save(desc);
            }
        }

        return getProfile(cuenta);
    }

    public void deleteByUsuario(String cuenta) {
        var personaOpt = personaRepository.findByUsuario_Cuenta(cuenta);
        if (personaOpt.isPresent()) {
            personaRepository.delete(personaOpt.get());
        }
        // Also delete the Usuario
        usuarioRepository.deleteById(cuenta);
    }

    public ProfileDTO getProfile(String cuenta) {
        // Try to find Persona first
        var personaOpt = personaRepository.findByUsuario_Cuenta(cuenta);

        if (personaOpt.isPresent()) {
            return mapPersonaToProfile(personaOpt.get(), cuenta);
        }

        // Check for Organization
        var orgOpt = organizacionAsociadaRepository.findByUsuario_Cuenta(cuenta);
        if (orgOpt.isPresent()) {
            return mapOrganizacionToProfile(orgOpt.get(), cuenta);
        }

        throw new RuntimeException("Usuario no encontrado (ni Persona ni Organización): " + cuenta);
    }

    private ProfileDTO mapPersonaToProfile(Persona persona, String cuenta) {
        ProfileDTO dto = new ProfileDTO();

        // 1. Basic Info - Full Name Construction
        StringBuilder fullName = new StringBuilder();
        if (persona.getPrimerNombre() != null)
            fullName.append(persona.getPrimerNombre());
        if (persona.getSegundoNombre() != null && !persona.getSegundoNombre().trim().isEmpty()) {
            fullName.append(" ").append(persona.getSegundoNombre());
        }
        if (persona.getPrimerApellido() != null)
            fullName.append(" ").append(persona.getPrimerApellido());
        if (persona.getSegundoApellido() != null && !persona.getSegundoApellido().trim().isEmpty()) {
            fullName.append(" ").append(persona.getSegundoApellido());
        }
        dto.setName(fullName.toString().trim());

        dto.setPrimerNombre(persona.getPrimerNombre());
        dto.setSegundoNombre(persona.getSegundoNombre());
        dto.setPrimerApellido(persona.getPrimerApellido());
        dto.setSegundoApellido(persona.getSegundoApellido());

        dto.setLocation(persona.getLugar() != null ? persona.getLugar().getNombre() : "Ubicación desconocida");
        dto.setSexo(persona.getSexo());

        System.out.println("DEBUG: Profile for " + cuenta);
        System.out.println("DEBUG: 1st Name: " + persona.getPrimerNombre());
        System.out.println("DEBUG: 2nd Name: " + persona.getSegundoNombre());
        System.out.println("DEBUG: 1st Surname: " + persona.getPrimerApellido());
        System.out.println("DEBUG: 2nd Surname: " + persona.getSegundoApellido());
        System.out.println("DEBUG: Generated Full Name: " + fullName.toString());

        // Email (Account)

        // Email (Account)
        dto.setEmail(cuenta + "@ucab.edu.ve");
        // Phone kept in DTO but not displayed per request
        dto.setPhone("+58 412-0000000");

        // Roles
        if (persona.getRoles() != null && !persona.getRoles().isEmpty()) {
            dto.setRole(persona.getRoles().get(0).getTipoRol());
        } else {
            dto.setRole("Miembro");
        }

        // Semester
        dto.setSemester("Semestre Activo");

        // 2. Bio
        var descriptions = descripcionRepository.findByUsuario_Cuenta(cuenta);
        if (!descriptions.isEmpty()) {
            dto.setBio(descriptions.get(0).getId().getDescripcion());
        } else {
            dto.setBio("Sin descripción.");
        }

        // 3. Stats
        ProfileDTO.StatsDTO stats = new ProfileDTO.StatsDTO();
        stats.setPosts((int) publicacionRepository.countByUsuario_Cuenta(cuenta));
        stats.setConnections((int) seRelacionaRepository
                .countByReceptor_CuentaAndEstadoOrSolicitante_CuentaAndEstado(cuenta, "Aceptada", cuenta, "Aceptada"));
        // Views removed
        dto.setStats(stats);

        // 4. Languages
        if (persona.getIdiomas() != null) {
            dto.setLanguages(persona.getIdiomas().stream().map(habla -> {
                ProfileDTO.LanguageDTO lang = new ProfileDTO.LanguageDTO();
                lang.setName(habla.getIdioma().getIdioma());
                lang.setLevel(mapNivelToPercentage(habla.getNivelFluidez()));
                return lang;
            }).collect(Collectors.toList()));
        } else {
            dto.setLanguages(new ArrayList<>());
        }

        // 5. Experience
        var experiences = tiempoDuracionRepository.findByPersona_Usuario_Cuenta(cuenta);
        dto.setExperience(experiences.stream().map(td -> {
            ProfileDTO.ExperienceDTO exp = new ProfileDTO.ExperienceDTO();
            exp.setRole(td.getRol().getTipoRol());
            exp.setCompany(td.getInstitucion().getNombre());
            String start = td.getFechaInicio().format(DateTimeFormatter.ofPattern("MMM yyyy"));
            String end = td.getFechaFin() != null ? td.getFechaFin().format(DateTimeFormatter.ofPattern("MMM yyyy"))
                    : "Presente";
            exp.setPeriod(start + " - " + end);
            exp.setDescription("Miembro de " + td.getInstitucion().getNombre());
            return exp;
        }).collect(Collectors.toList()));

        // 6. Contacts
        var connections = seRelacionaRepository.findBySolicitante_Cuenta(cuenta);
        dto.setContacts(connections.stream().limit(3).map(rel -> {
            return mapContact(rel.getReceptor());
        }).collect(Collectors.toList()));

        return dto;
    }

    private ProfileDTO mapOrganizacionToProfile(OrganizacionAsociada org, String cuenta) {
        ProfileDTO dto = new ProfileDTO();
        dto.setName(org.getNombre());
        dto.setLocation(org.getLugar() != null ? org.getLugar().getNombre() : "Ubicación desconocida");
        dto.setEmail(cuenta + "@ucab.edu.ve");
        dto.setPhone("N/A");
        dto.setRole("Organización");
        dto.setSemester("Aliado Institucional"); // Placeholder

        // Bio
        var descriptions = descripcionRepository.findByUsuario_Cuenta(cuenta);
        if (!descriptions.isEmpty()) {
            dto.setBio(descriptions.get(0).getId().getDescripcion());
        } else {
            dto.setBio("Organización asociada a la UCAB.");
        }

        // Stats
        ProfileDTO.StatsDTO stats = new ProfileDTO.StatsDTO();
        stats.setPosts((int) publicacionRepository.countByUsuario_Cuenta(cuenta));
        stats.setConnections((int) seRelacionaRepository
                .countByReceptor_CuentaAndEstadoOrSolicitante_CuentaAndEstado(cuenta, "Aceptada", cuenta, "Aceptada"));
        dto.setStats(stats);

        // Languages - Organizations might not have languages in this schema
        dto.setLanguages(new ArrayList<>());

        // Experience - Not applicable or empty
        dto.setExperience(new ArrayList<>());

        // Contacts
        var connections = seRelacionaRepository.findBySolicitante_Cuenta(cuenta);
        dto.setContacts(connections.stream().limit(3).map(rel -> {
            return mapContact(rel.getReceptor());
        }).collect(Collectors.toList()));

        return dto;
    }

    private ProfileDTO.ContactDTO mapContact(Usuario user) {
        ProfileDTO.ContactDTO c = new ProfileDTO.ContactDTO();
        // We know Usuario is OneToOne with Persona usually, but Usuario entity doesn't
        // have direct reference to Persona defined in parsed file?
        // Actually, Persona has OneToOne Usuario. Usuario does NOT have OneToOne
        // Persona field shown in log Step 3013.
        // So from Usuario we can't easily get Persona details (Name) without lookup.
        // For efficiency, we might just put Account Name or basic info.
        // OR better: repository lookup. But mapping inside stream is bad for N+1.
        // Fallback: Use account as name or try to find Persona.
        c.setName(user.getCuenta());
        c.setRole("Miembro");
        c.setInitials(user.getCuenta().substring(0, 2).toUpperCase());
        c.setColor("bg-blue-500");
        return c;
    }

    private int mapNivelToPercentage(String nivel) {
        if (nivel == null)
            return 0;
        switch (nivel.toLowerCase()) {
            case "básico":
                return 30;
            case "intermedio":
                return 60;
            case "avanzado":
                return 90;
            case "nativo":
                return 100;
            default:
                return 50;
        }
    }
}
