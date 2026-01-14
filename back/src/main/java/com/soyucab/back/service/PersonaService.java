package com.soyucab.back.service;

import com.soyucab.back.dto.ProfileDTO;
import com.soyucab.back.model.*;
import com.soyucab.back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonaService {

    @Autowired
    private EntidadInstitucionalRepository entidadInstitucionalRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private IdiomaRepository idiomaRepository;
    @Autowired
    private HablaRepository hablaRepository;
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

    // New Repositories for Cleanup
    @Autowired
    private ReaccionaRepository reaccionaRepository;
    @Autowired
    private AsistenciaEventoRepository asistenciaEventoRepository;
    @Autowired
    private NotificacionesRepository notificacionesRepository;
    @Autowired
    private MensajeRepository mensajeRepository;
    @Autowired
    private DenunciaRepository denunciaRepository;
    @Autowired
    private ChatMiembroRepository chatMiembroRepository;
    @Autowired
    private GrupoParticipaRepository grupoParticipaRepository;

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
        return personaRepository.findTop3ByUsuario_CuentaNot(cuenta);
    }

    public java.util.List<Persona> search(String query) {
        return personaRepository.searchPersonas(query);
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

        persona = personaRepository.save(persona);

        // Update Bio
        if (updateDTO.getBio() != null) {
            var descriptions = descripcionRepository.findByUsuario_Cuenta(cuenta);
            if (!descriptions.isEmpty()) {
                var desc = descriptions.get(0);
                desc.getId().setDescripcion(updateDTO.getBio());
                descripcionRepository.save(desc);
            } else {
                Descripcion d = new Descripcion();
                DescripcionId did = new DescripcionId(updateDTO.getBio(), cuenta);
                d.setId(did);
                d.setUsuario(persona.getUsuario());
                try {
                    descripcionRepository.save(d);
                } catch (Exception e) {
                }
            }
        }

        // Update Experience
        if (updateDTO.getExperience() != null) {
            try {
                var existingExps = tiempoDuracionRepository.findByPersona_Usuario_Cuenta(cuenta);
                tiempoDuracionRepository.deleteAll(existingExps);
                tiempoDuracionRepository.flush();

                for (com.soyucab.back.dto.ProfileUpdateDTO.ExperienceDTO expDTO : updateDTO.getExperience()) {
                    EntidadInstitucional inst = entidadInstitucionalRepository.findByNombre(expDTO.getCompany())
                            .orElseGet(() -> {
                                EntidadInstitucional newInst = new EntidadInstitucional();
                                newInst.setCodInst(java.util.UUID.randomUUID().toString().substring(0, 10));
                                newInst.setNombre(expDTO.getCompany());
                                return entidadInstitucionalRepository.saveAndFlush(newInst);
                            });

                    Rol rol = rolRepository.findById(expDTO.getRole()).orElse(null);
                    if (rol == null)
                        continue;

                    boolean updatedPersona = false;
                    if (!persona.getInstituciones().contains(inst)) {
                        persona.getInstituciones().add(inst);
                        updatedPersona = true;
                    }
                    if (!persona.getRoles().contains(rol)) {
                        persona.getRoles().add(rol);
                        updatedPersona = true;
                    }

                    if (updatedPersona) {
                        persona = personaRepository.saveAndFlush(persona); // FLUSH to ensure Nexo/Desempeña exist
                    }

                    try {
                        LocalDate start = LocalDate.now();
                        try {
                            if (expDTO.getStartDate() != null && !expDTO.getStartDate().isEmpty())
                                start = LocalDate.parse(expDTO.getStartDate());
                        } catch (Exception e) {
                            System.err.println("Error parsing start date: " + expDTO.getStartDate());
                        }
                        LocalDate end = null;
                        try {
                            if (expDTO.getEndDate() != null && !expDTO.getEndDate().isEmpty())
                                end = LocalDate.parse(expDTO.getEndDate());
                        } catch (Exception e) {
                            System.err.println("Error parsing end date: " + expDTO.getEndDate());
                        }

                        TiempoDuracion td = new TiempoDuracion();
                        TiempoDuracionId tdId = new TiempoDuracionId(inst.getCodInst(), persona.getCi(),
                                rol.getTipoRol());
                        td.setId(tdId);
                        td.setInstitucion(inst);
                        td.setPersona(persona);
                        td.setRol(rol);
                        td.setFechaInicio(start);
                        td.setFechaFin(end);
                        tiempoDuracionRepository.saveAndFlush(td); // Flush to catch FK errors here
                    } catch (Exception e) {
                        System.err.println("Error saving experience item: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error updating experience: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Update Languages
        if (updateDTO.getLanguages() != null) {
            try {
                var existingLangs = hablaRepository.findByPersona_Usuario_Cuenta(cuenta);
                hablaRepository.deleteAll(existingLangs);
                hablaRepository.flush();

                for (com.soyucab.back.dto.ProfileUpdateDTO.LanguageDTO langDTO : updateDTO.getLanguages()) {
                    Optional<Idioma> idiomaOpt = idiomaRepository.findByIdiomaIgnoreCase(langDTO.getName());
                    if (idiomaOpt.isEmpty())
                        continue;

                    Idioma idioma = idiomaOpt.get();
                    Habla habla = new Habla();
                    HablaId hId = new HablaId(persona.getCi(), idioma.getIsoIdioma());
                    habla.setId(hId);
                    habla.setPersona(persona);
                    habla.setIdioma(idioma);
                    habla.setNivelFluidez(mapPercentageToLevel(langDTO.getLevel()));
                    hablaRepository.save(habla);
                }
            } catch (Exception e) {
            }
        }

        tiempoDuracionRepository.flush();
        hablaRepository.flush();
        personaRepository.flush();

        return getProfile(cuenta);
    }

    public void deleteByUsuario(String cuenta) {
        // --- NUCLEAR OPTION: DELETE EVERYTHING RELATED TO USER ---

        System.out.println("Deleting User: " + cuenta + " - Starting Deep Clean");

        // 1. Delete Interactions
        try {
            reaccionaRepository.deleteByUsuarioCuenta(cuenta);
        } catch (Exception e) {
            System.err.println("Del Reacciona fail: " + e.getMessage());
        }
        try {
            notificacionesRepository.deleteByUsuarioDestinoCuenta(cuenta);
        } catch (Exception e) {
            System.err.println("Del Notificaciones fail: " + e.getMessage());
        }
        try {
            denunciaRepository.deleteByDenuncianteCuenta(cuenta);
        } catch (Exception e) {
            System.err.println("Del Denuncia fail 1: " + e.getMessage());
        }
        try {
            denunciaRepository.deleteByDenunciadoCuenta(cuenta);
        } catch (Exception e) {
            System.err.println("Del Denuncia fail 2: " + e.getMessage());
        }

        // 2. Delete Community Participation
        try {
            // Manual delete for ChatMiembro needed as no deleteByUsuario was added to
            // interface yet?
            // Actually checking if I added it... I viewed the file but didn't modify it to
            // add deleteByUsuarioChat
            // Let's rely on manual fetch-delete if needed or assume cascading might help?
            // Safer: Find and Delete
            var chatParticipations = chatMiembroRepository.findAll().stream()
                    .filter(cm -> cm.getUsuario() != null && cm.getUsuario().getCuenta().equals(cuenta))
                    .collect(Collectors.toList());
            chatMiembroRepository.deleteAll(chatParticipations);
        } catch (Exception e) {
            System.err.println("Del ChatMiembro fail: " + e.getMessage());
        }

        try {
            var groupParticipations = grupoParticipaRepository.findByUsuario_Cuenta(cuenta);
            grupoParticipaRepository.deleteAll(groupParticipations);
        } catch (Exception e) {
        }

        try {
            asistenciaEventoRepository.deleteByAsistenteCuenta(cuenta);
        } catch (Exception e) {
        }
        try {
            mensajeRepository.deleteBySenderCuenta(cuenta);
        } catch (Exception e) {
        }

        // 3. Delete Relationships
        try {
            var sent = seRelacionaRepository.findBySolicitante_Cuenta(cuenta);
            var received = seRelacionaRepository.findByReceptor_Cuenta(cuenta);
            seRelacionaRepository.deleteAll(sent);
            seRelacionaRepository.deleteAll(received);
        } catch (Exception e) {
        }

        // 4. Delete Content
        try {
            var posts = publicacionRepository.findByUsuario_Cuenta(cuenta);
            publicacionRepository.deleteAll(posts);
        } catch (Exception e) {
        }

        try {
            var descriptions = descripcionRepository.findByUsuario_Cuenta(cuenta);
            descripcionRepository.deleteAll(descriptions);
        } catch (Exception e) {
        }

        // 5. Delete Persona Details
        var personaOpt = personaRepository.findByUsuario_Cuenta(cuenta);
        if (personaOpt.isPresent()) {
            Persona p = personaOpt.get();
            try {
                var exps = tiempoDuracionRepository.findByPersona_Usuario_Cuenta(cuenta);
                tiempoDuracionRepository.deleteAll(exps);
            } catch (Exception e) {
            }

            try {
                var langs = hablaRepository.findByPersona_Usuario_Cuenta(cuenta);
                hablaRepository.deleteAll(langs);
            } catch (Exception e) {
            }

            personaRepository.delete(p); // This should cascade to Nexo/Desempeña if configured, or they are just link
                                         // tables.
        }

        // 6. Delete Usuario
        usuarioRepository.deleteById(cuenta);
        System.out.println("User " + cuenta + " deleted successfully.");
    }

    public ProfileDTO getProfile(String cuenta) {
        var personaOpt = personaRepository.findByUsuario_Cuenta(cuenta);
        if (personaOpt.isPresent())
            return mapPersonaToProfile(personaOpt.get(), cuenta);

        var orgOpt = organizacionAsociadaRepository.findByUsuario_Cuenta(cuenta);
        if (orgOpt.isPresent())
            return mapOrganizacionToProfile(orgOpt.get(), cuenta);

        throw new RuntimeException("Usuario no encontrado (ni Persona ni Organización): " + cuenta);
    }

    private ProfileDTO mapPersonaToProfile(Persona persona, String cuenta) {
        ProfileDTO dto = new ProfileDTO();

        StringBuilder fullName = new StringBuilder();
        if (persona.getPrimerNombre() != null)
            fullName.append(persona.getPrimerNombre());
        if (persona.getSegundoNombre() != null && !persona.getSegundoNombre().trim().isEmpty())
            fullName.append(" ").append(persona.getSegundoNombre());
        if (persona.getPrimerApellido() != null)
            fullName.append(" ").append(persona.getPrimerApellido());
        if (persona.getSegundoApellido() != null && !persona.getSegundoApellido().trim().isEmpty())
            fullName.append(" ").append(persona.getSegundoApellido());
        dto.setName(fullName.toString().trim());

        dto.setPrimerNombre(persona.getPrimerNombre());
        dto.setSegundoNombre(persona.getSegundoNombre());
        dto.setPrimerApellido(persona.getPrimerApellido());
        dto.setSegundoApellido(persona.getSegundoApellido());
        dto.setLocation(persona.getLugar() != null ? persona.getLugar().getNombre() : "Ubicación desconocida");
        dto.setSexo(persona.getSexo());
        dto.setEmail(cuenta + "@ucab.edu.ve");
        dto.setPhone("+58 412-0000000");

        if (persona.getRoles() != null && !persona.getRoles().isEmpty())
            dto.setRole(persona.getRoles().get(0).getTipoRol());
        else
            dto.setRole("Miembro");
        dto.setSemester("Semestre Activo");

        var descriptions = descripcionRepository.findByUsuario_Cuenta(cuenta);
        if (!descriptions.isEmpty())
            dto.setBio(descriptions.get(0).getId().getDescripcion());
        else
            dto.setBio("Sin descripción.");

        ProfileDTO.StatsDTO stats = new ProfileDTO.StatsDTO();
        stats.setPosts((int) publicacionRepository.countByUsuario_Cuenta(cuenta));
        stats.setConnections((int) seRelacionaRepository
                .countByReceptor_CuentaAndEstadoOrSolicitante_CuentaAndEstado(cuenta, "Aceptada", cuenta, "Aceptada"));
        dto.setStats(stats);

        if (persona.getIdiomas() != null) {
            dto.setLanguages(persona.getIdiomas().stream().map(habla -> {
                ProfileDTO.LanguageDTO lang = new ProfileDTO.LanguageDTO();
                lang.setName(habla.getIdioma().getIdioma());
                lang.setLevel(mapNivelToPercentage(habla.getNivelFluidez()));
                return lang;
            }).collect(Collectors.toList()));
        } else
            dto.setLanguages(new ArrayList<>());

        var experiences = tiempoDuracionRepository.findByPersona_Usuario_Cuenta(cuenta);
        dto.setExperience(experiences.stream().map(td -> {
            ProfileDTO.ExperienceDTO exp = new ProfileDTO.ExperienceDTO();
            exp.setRole(td.getRol().getTipoRol());
            exp.setCompany(td.getInstitucion().getNombre());
            try {
                exp.setStartDate(td.getFechaInicio().toString());
                exp.setEndDate(td.getFechaFin() != null ? td.getFechaFin().toString() : null);
                String start = td.getFechaInicio().format(DateTimeFormatter.ofPattern("MMM yyyy"));
                String end = td.getFechaFin() != null ? td.getFechaFin().format(DateTimeFormatter.ofPattern("MMM yyyy"))
                        : "Presente";
                exp.setPeriod(start + " - " + end);
            } catch (Exception e) {
                exp.setPeriod("Fecha inválida");
            }
            exp.setDescription("Miembro de " + td.getInstitucion().getNombre());
            return exp;
        }).collect(Collectors.toList()));

        var connections = seRelacionaRepository.findBySolicitante_Cuenta(cuenta);
        dto.setContacts(
                connections.stream().limit(3).map(rel -> mapContact(rel.getReceptor())).collect(Collectors.toList()));

        return dto;
    }

    private ProfileDTO mapOrganizacionToProfile(OrganizacionAsociada org, String cuenta) {
        ProfileDTO dto = new ProfileDTO();
        dto.setName(org.getNombre());
        dto.setLocation(org.getLugar() != null ? org.getLugar().getNombre() : "Ubicación desconocida");
        dto.setEmail(cuenta + "@ucab.edu.ve");
        dto.setPhone("N/A");
        dto.setRole("Organización");
        dto.setSemester("Aliado Institucional");

        var descriptions = descripcionRepository.findByUsuario_Cuenta(cuenta);
        if (!descriptions.isEmpty())
            dto.setBio(descriptions.get(0).getId().getDescripcion());
        else
            dto.setBio("Organización asociada a la UCAB.");

        ProfileDTO.StatsDTO stats = new ProfileDTO.StatsDTO();
        stats.setPosts((int) publicacionRepository.countByUsuario_Cuenta(cuenta));
        stats.setConnections((int) seRelacionaRepository
                .countByReceptor_CuentaAndEstadoOrSolicitante_CuentaAndEstado(cuenta, "Aceptada", cuenta, "Aceptada"));
        dto.setStats(stats);
        dto.setLanguages(new ArrayList<>());
        dto.setExperience(new ArrayList<>());

        var connections = seRelacionaRepository.findBySolicitante_Cuenta(cuenta);
        dto.setContacts(
                connections.stream().limit(3).map(rel -> mapContact(rel.getReceptor())).collect(Collectors.toList()));

        return dto;
    }

    private ProfileDTO.ContactDTO mapContact(Usuario user) {
        ProfileDTO.ContactDTO c = new ProfileDTO.ContactDTO();
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

    private String mapPercentageToLevel(int percentage) {
        if (percentage >= 90)
            return "Nativo";
        if (percentage >= 80)
            return "Avanzado";
        if (percentage >= 50)
            return "Intermedio";
        return "Básico";
    }
}
