--Create Table 

--Lugar 
Create table Lugar 
(ISO_Lugar varchar(10) PRIMARY KEY,
Nombre_L varchar (20) NOT NULL,
Tipo_L varchar (15) NOT NULL,
ISO_Superior varchar(10), 
CONSTRAINT FK_ISO_Superior FOREIGN KEY (ISO_Superior) REFERENCES Lugar (ISO_Lugar),
CONSTRAINT C_Tipo_Lugar CHECK (Tipo_L in ('País', 'Estado', 'Ciudad'))); 

--Usuario
Create table Usuario 
(Cuenta varchar (15) PRIMARY KEY); 

--Idioma 
Create table Idioma 
(ISO_Idioma varchar (3) Primary KEY,
Idioma varchar (12) NOT NULL);

--Persona 
Create table Persona 
(CI varchar (15) PRIMARY KEY,
Persona_Usuario varchar (15) NOT NULL UNIQUE,
Primer_Nombre varchar (15) NOT NULL,
Segundo_Nombre varchar (15),
Primer_Apellido varchar (15) NOT NULL, 
Segundo_Apellido varchar (15) NOT NULL, 
Sexo varchar (10) NOT NULL,
Ubicacion_Persona varchar (10) NOT NULL, 
CONSTRAINT FK_Persona_Cuenta FOREIGN KEY (Persona_Usuario) REFERENCES Usuario (Cuenta),
CONSTRAINT C_Sexo CHECK (sexo in ('Masculino', 'Femenino')),
CONSTRAINT FK_ubi_Persona FOREIGN KEY (Ubicacion_Persona) REFERENCES Lugar (ISO_Lugar)); 

--Organización 
Create table Organizacion_Asociada 
(RIF varchar (15) PRIMARY KEY,
OA_Usuario varchar (15) NOT NULL UNIQUE,
Nombre_O varchar (30) NOT NULL, 
Ubicacion_OA varchar (10) NOT NULL, 
CONSTRAINT FK_OA_Cuenta FOREIGN KEY (OA_Usuario) REFERENCES Usuario (Cuenta),
CONSTRAINT FK_Ubi_OA FOREIGN KEY (Ubicacion_OA) REFERENCES Lugar (ISO_Lugar));

--Entidad Institucional
Create table Entidad_Institucional
(Cod_Inst varchar (10) PRIMARY KEY,
Nombre_Ent_Inst varchar (50) NOT NULL,
Cod_Inst_Sup varchar (10),
CONSTRAINT FK_Cod_Inst FOREIGN KEY (Cod_Inst_Sup) REFERENCES Entidad_Institucional (Cod_Inst));

--Rol
Create table Rol 
(Tipo_Rol varchar (20) PRIMARY KEY, 
CONSTRAINT C_Tipo_Rol check (Tipo_Rol in ('Estudiante', 'Profesor', 'Empleado', 'Egresado')));

--Habilidad 
Create table Habilidad 
(Habilidad varchar (30) PRIMARY KEY); 


--Contenido 
Create table Contenido
(Usuario_Creador varchar (15) NOT NULL,
FechaHora_Creacion TIMESTAMP (6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
Cuerpo_Contenido varchar (300), 
Nro_Me_Gusta INTEGER DEFAULT 0,
Nro_No_Me_Gusta INTEGER DEFAULT 0,
PRIMARY KEY (Usuario_Creador, FechaHora_Creacion),
CONSTRAINT FK_Usuario_Creador FOREIGN KEY (Usuario_Creador) REFERENCES Usuario (Cuenta));

--Reacciona 
Create table Reacciona 
(Usuario_Reacciona varchar (15) NOT NULL,
Usuario_Contenido varchar (15) NOT NULL,
FechaHora_Contenido TIMESTAMP (6) NOT NULL,
Reaccion varchar (15) NOT NULL,
PRIMARY KEY (Usuario_Reacciona, Usuario_Contenido, FechaHora_Contenido),
CONSTRAINT FK_U_Reacciona FOREIGN KEY (Usuario_Reacciona) REFERENCES Usuario (Cuenta),
CONSTRAINT FK_U_Contenido FOREIGN KEY (Usuario_Contenido, FechaHora_Contenido) REFERENCES Contenido (Usuario_Creador, FechaHora_Creacion),
CONSTRAINT Tipo_Reaccion CHECK (Reaccion in ('Me Gusta', 'No Me Gusta')));

--Evento
Create table Evento 
(Nombre_Evento varchar (30) NOT NULL,
Fecha_Evento DATE NOT NULL,
Usuario_Evento varchar (15) NOT NULL,
Desc_Evento varchar (200),
PRIMARY KEY (Nombre_Evento, Fecha_Evento, Usuario_Evento),
CONSTRAINT FK_U_Evento FOREIGN KEY (Usuario_Evento) REFERENCES Usuario (Cuenta));

--CHAT
Create Table Chat 
(Nombre_Chat VARCHAR (30) NOT NULL,
Fecha_Creacion_Chat TIMESTAMP (6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (Nombre_Chat, Fecha_Creacion_Chat));

--Chat_Miembro
Create table Chat_Miembro
(Chat_Participa varchar (30) NOT NULL,
Fecha_Chat TIMESTAMP(6) NOT NULL,
Usuario_Chat varchar (15) NOT NULL,
Rol_Chat varchar (15) NOT NULL, 
PRIMARY KEY (Chat_Participa, Fecha_Chat, Usuario_Chat),
CONSTRAINT FK_Chat_Participa FOREIGN KEY (Chat_Participa, Fecha_Chat) REFERENCES Chat (Nombre_Chat, Fecha_Creacion_Chat),
CONSTRAINT FK_U_CHAT FOREIGN KEY (Usuario_Chat) REFERENCES Usuario (Cuenta),
CONSTRAINT Tipo_Rol_Chat check (Rol_Chat in ('Miembro', 'Administrador', 'Creador')));

--Habla
CREATE TABLE Habla 
(CI_Persona VARCHAR(15) NOT NULL,
ISO_Idioma VARCHAR(3) NOT NULL,
Nivel_Fluidez VARCHAR(20),
PRIMARY KEY (CI_Persona, ISO_Idioma),
CONSTRAINT FK_Habla_Persona FOREIGN KEY (CI_Persona) REFERENCES Persona (CI),
CONSTRAINT FK_Habla_Idioma FOREIGN KEY (ISO_Idioma) REFERENCES Idioma (ISO_Idioma),
CONSTRAINT C_Nivel_Fluidez CHECK (Nivel_Fluidez IN ('Básico', 'Intermedio', 'Avanzado', 'Nativo')));

--Se_Relaciona 
Create table Se_Relaciona
(Usuario_Receptor VARCHAR(15) NOT NULL,
Usuario_Solicitante VARCHAR(15) NOT NULL, 
Estado VARCHAR(15) NOT NULL DEFAULT 'Pendiente',
Tipo_Relacion VARCHAR(15) NOT NULL,
Fecha_Relacion DATE NOT NULL,
PRIMARY KEY (Usuario_Receptor, Usuario_Solicitante),
CONSTRAINT FK_U_Receptor FOREIGN KEY (Usuario_Receptor) REFERENCES Usuario (Cuenta),
CONSTRAINT FK_U_Solicitante FOREIGN KEY (Usuario_Solicitante) REFERENCES Usuario (Cuenta),
CONSTRAINT C_Tipo_Relacion CHECK (Tipo_Relacion in ('Seguimiento', 'Amistad')),
CONSTRAINT CK_Estado_Relacion CHECK (Estado IN ('Pendiente', 'Aceptada', 'Rechazada')),
CONSTRAINT CK_No_Autorelacion CHECK (Usuario_Receptor <> Usuario_Solicitante));

--Grupo
Create table Grupo 
(Nombre_Grupo VARCHAR (30) PRIMARY KEY,
Desc_Grupo varchar (300),
Tipo_Grupo varchar (12),
CONSTRAINT ck_tipo_grupo CHECK (Tipo_Grupo IN ('Público', 'Privado')));

--Grupo_Participa 
CREATE TABLE Grupo_Participa 
(Nombre_Grupo VARCHAR(30) NOT NULL,
Usuario_Miembro VARCHAR(15) NOT NULL,    
Rol_Miembro VARCHAR(15) NOT NULL,
PRIMARY KEY (Nombre_Grupo, Usuario_Miembro),
CONSTRAINT FK_Grupo_Relacion FOREIGN KEY (Nombre_Grupo) REFERENCES Grupo (Nombre_Grupo),
CONSTRAINT FK_Miembro_Relacion FOREIGN KEY (Usuario_Miembro) REFERENCES Usuario (Cuenta),
CONSTRAINT CK_Rol_Miembro CHECK (Rol_Miembro IN ('Miembro', 'Moderador', 'Fundador')));

--Publicación 
Create Table Publicacion 
(Titulo_Pub varchar (50) NOT NULL,
Autor varchar (15) NOT NULL,
Sinopsis_Pub varchar (400) NOT NULL, 
Fecha_Pub DATE NOT NULL, 
PRIMARY KEY (Titulo_Pub, Autor), 
CONSTRAINT FK_Autor FOREIGN KEY (Autor) REFERENCES Usuario (Cuenta));


--Descripcion
Create Table Descripcion
(Descripcion VARCHAR (200) NOT NULL,
Cuenta_Desc VARCHAR (15) NOT NULL,
PRIMARY KEY (Descripcion, Cuenta_Desc),
CONSTRAINT FK_Desc FOREIGN KEY (Cuenta_Desc) REFERENCES Usuario (Cuenta));


--Capacidad Habilidad
Create table Capacidad_Hab
(CI varchar (15) NOT NULL,
Habilidad varchar (20) NOT NULL,
Primary key (CI, Habilidad),
CONSTRAINT HAB_CI FOREIGN KEY (CI) REFERENCES Persona (CI),
CONSTRAINT HAB_HAB FOREIGN KEY (Habilidad) REFERENCES Habilidad (Habilidad));


--Desempeña 
Create Table Desempeña 
(CI_Rol varchar (15) NOT NULL,
Des_Rol varchar (20) NOT NULL, 
Primary key (CI_Rol, Des_Rol), 
CONSTRAINT FK_CI_Rol FOREIGN KEY (CI_Rol) REFERENCES Persona (CI),
CONSTRAINT FK_Des_Rol FOREIGN KEY (Des_Rol) REFERENCES Rol (Tipo_Rol));

--Nexo 
Create table Nexo 
(Cod_Inst_Nexo varchar (10) NOT NULL,
CI_Nexo varchar (15) NOT NULL,
Primary Key (Cod_Inst_Nexo, CI_Nexo),
CONSTRAINT FK_COD_INST_NEXO FOREIGN KEY (Cod_Inst_Nexo) REFERENCES Entidad_Institucional (Cod_Inst),
CONSTRAINT FK_CI_NEXO FOREIGN KEY (CI_Nexo) REFERENCES Persona (CI));


--tIEMPO Duracion 
Create table Tiempo_Duracion 
(Inst_T varchar (10) NOT NULL,
CI_T varchar (15) NOT NULL,
Rol_T varchar (20) NOT NULL,
Fecha_Inicio DATE NOT NULL,
Fecha_Fin DATE,
PRIMARY KEY (INST_T, CI_T, ROL_T),
CONSTRAINT FK_Nexo FOREIGN KEY (Inst_T, CI_T) REFERENCES Nexo (Cod_Inst_Nexo, CI_Nexo),
CONSTRAINT FK_Desempena FOREIGN KEY (CI_T, ROL_T) REFERENCES Desempeña (CI_Rol, Des_Rol),
CONSTRAINT CK_Fecha_Duracion CHECK (Fecha_Fin IS NULL OR Fecha_Fin >= Fecha_Inicio));


--Notificaciones 
CREATE TABLE Notificaciones 
(id_notificacion SERIAL PRIMARY KEY,
id_usuario_destino VARCHAR(15) NOT NULL REFERENCES Usuario(Cuenta),
mensaje TEXT NOT NULL,
tipo_alerta VARCHAR(20) NOT NULL,
fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP);

--Asistencia a evento 
CREATE TABLE Asistencia_Evento (
    Usuario_Asistente VARCHAR(15) NOT NULL,
    Nombre_Evento VARCHAR(30) NOT NULL,
    Fecha_Evento DATE NOT NULL,
    Usuario_Evento_Org VARCHAR(15) NOT NULL, 
    Fecha_Registro TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    Asistencia_Confirmada BOOLEAN DEFAULT TRUE,
    
    PRIMARY KEY (Usuario_Asistente, Nombre_Evento, Fecha_Evento, Usuario_Evento_Org),
    
    CONSTRAINT FK_Usuario_Asistente FOREIGN KEY (Usuario_Asistente) REFERENCES Usuario (Cuenta),
    CONSTRAINT FK_Evento_Asistido FOREIGN KEY (Nombre_Evento, Fecha_Evento, Usuario_Evento_Org) 
    REFERENCES Evento (Nombre_Evento, Fecha_Evento, Usuario_Evento)
);

--Denuncias 
CREATE TABLE Denuncia (
    id_denuncia SERIAL PRIMARY KEY,
    usuario_denunciante VARCHAR(15) NOT NULL REFERENCES Usuario(Cuenta),
    usuario_denunciado VARCHAR(15) NOT NULL REFERENCES Usuario(Cuenta),
    motivo VARCHAR(100),
    fecha_denuncia DATE DEFAULT CURRENT_DATE,
    estatus VARCHAR(20) DEFAULT 'Pendiente' CHECK (estatus IN ('Pendiente', 'Revisada', 'Descartada'))
);



--TRIGGERS Y PROCEDIMIENTOS ALMACENADOS 


--Trigger Actualizar Numero Reacciones 

CREATE OR REPLACE FUNCTION F_Actualizar_Nro_Reacciones()
RETURNS TRIGGER AS $$
DECLARE
    reaccion_anterior VARCHAR(15);
BEGIN
    IF TG_OP = 'INSERT' THEN
        IF NEW.Reaccion = 'Me Gusta' THEN
            UPDATE Contenido
            SET Nro_Me_Gusta = Nro_Me_Gusta + 1
            WHERE Usuario_Creador = NEW.Usuario_Contenido
              AND FechaHora_Creacion = NEW.FechaHora_Contenido;
        ELSIF NEW.Reaccion = 'No Me Gusta' THEN
            UPDATE Contenido
            SET Nro_No_Me_Gusta = Nro_No_Me_Gusta + 1
            WHERE Usuario_Creador = NEW.Usuario_Contenido
              AND FechaHora_Creacion = NEW.FechaHora_Contenido;
        END IF;

    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.Reaccion = NEW.Reaccion THEN
            RETURN NEW;
        END IF;

        IF OLD.Reaccion = 'Me Gusta' THEN
            UPDATE Contenido
            SET Nro_Me_Gusta = Nro_Me_Gusta - 1
            WHERE Usuario_Creador = OLD.Usuario_Contenido
              AND FechaHora_Creacion = OLD.FechaHora_Contenido;
        ELSIF OLD.Reaccion = 'No Me Gusta' THEN
            UPDATE Contenido
            SET Nro_No_Me_Gusta = Nro_No_Me_Gusta - 1
            WHERE Usuario_Creador = OLD.Usuario_Contenido
              AND FechaHora_Creacion = OLD.FechaHora_Contenido;
        END IF;

        IF NEW.Reaccion = 'Me Gusta' THEN
            UPDATE Contenido
            SET Nro_Me_Gusta = Nro_Me_Gusta + 1
            WHERE Usuario_Creador = NEW.Usuario_Contenido
              AND FechaHora_Creacion = NEW.FechaHora_Contenido;
        ELSIF NEW.Reaccion = 'No Me Gusta' THEN
            UPDATE Contenido
            SET Nro_No_Me_Gusta = Nro_No_Me_Gusta + 1
            WHERE Usuario_Creador = NEW.Usuario_Contenido
              AND FechaHora_Creacion = NEW.FechaHora_Contenido;
        END IF;
        
    END IF;
    RETURN NEW; 
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER T_Nro_Reacciones 
AFTER INSERT OR UPDATE OF Reaccion ON Reacciona
FOR EACH ROW
EXECUTE FUNCTION F_Actualizar_Nro_Reacciones();



--Procedimiento Almacenado

CREATE OR REPLACE PROCEDURE Gestionar_Post_SP(
    p_usuario_editor VARCHAR(15),      
    p_usuario_creador VARCHAR(15),      
    p_fecha_creacion TIMESTAMP,         
    p_operacion VARCHAR(10),            
    p_cuerpo_nuevo VARCHAR(300) DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_existe BOOLEAN;
    v_creador_real VARCHAR(15);
BEGIN
    
    SELECT EXISTS (
        SELECT 1
        FROM Contenido
        WHERE Usuario_Creador = p_usuario_creador
          AND FechaHora_Creacion = p_fecha_creacion
    ), Usuario_Creador
    INTO v_existe, v_creador_real
    FROM Contenido
    WHERE Usuario_Creador = p_usuario_creador
      AND FechaHora_Creacion = p_fecha_creacion;


    IF NOT v_existe THEN
        RAISE EXCEPTION 'ERROR: El post especificado no existe. (Creador: %, Fecha: %)', 
            p_usuario_creador, p_fecha_creacion;
    END IF;

    IF p_usuario_editor <> v_creador_real THEN
        RAISE EXCEPTION 'ERROR: Solo el creador (%) puede modificar o eliminar este post.', v_creador_real;
    END IF;


    IF p_operacion = 'UPDATE' THEN
        IF p_cuerpo_nuevo IS NULL OR TRIM(p_cuerpo_nuevo) = '' THEN
             RAISE EXCEPTION 'ERROR: El nuevo cuerpo del post no puede estar vacío para la operación UPDATE.';
        END IF;
        
        UPDATE Contenido
        SET Cuerpo_Contenido = p_cuerpo_nuevo
        WHERE Usuario_Creador = p_usuario_creador
          AND FechaHora_Creacion = p_fecha_creacion;
        
        RAISE NOTICE 'EXITO: Post actualizado correctamente por %.', p_usuario_editor;
    
    ELSIF p_operacion = 'DELETE' THEN
        
        DELETE FROM Contenido
        WHERE Usuario_Creador = p_usuario_creador
          AND FechaHora_Creacion = p_fecha_creacion;
        
        RAISE NOTICE 'EXITO: Post eliminado correctamente por %.', p_usuario_editor;
        
    ELSE
        RAISE EXCEPTION 'ERROR: Operación no válida. Solo puede usar "UPDATE" o "DELETE".';
    END IF;

END;
$$;


--Trigger Relación Simetrica
CREATE OR REPLACE FUNCTION fn_insert_relacion_simetrica()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.Tipo_Relacion = 'Amistad' THEN
        
        IF NOT EXISTS (
            SELECT 1 FROM Se_Relaciona
            WHERE Usuario_Receptor = NEW.Usuario_Solicitante
              AND Usuario_Solicitante = NEW.Usuario_Receptor
        ) THEN
            INSERT INTO Se_Relaciona
            (Usuario_Receptor, Usuario_Solicitante, Tipo_Relacion, Fecha_Relacion)
            VALUES
            (NEW.Usuario_Solicitante, NEW.Usuario_Receptor, 'Amistad', NEW.Fecha_Relacion);
        END IF;

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_relacion_simetrica
AFTER INSERT ON Se_Relaciona
FOR EACH ROW
EXECUTE FUNCTION fn_insert_relacion_simetrica();


--Stored Procedure Sugerir Candidatos 
CREATE OR REPLACE PROCEDURE sugerir_candidatos(
    p_carrera VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    SELECT 
        p.CI,
        p.Persona_Usuario AS usuario,
        p.Primer_Nombre || ' ' || p.Primer_Apellido AS nombre,
        COUNT(h.Habilidad) AS coincidencias_hab
    FROM Persona p
    JOIN Nexo n ON n.CI_Nexo = p.CI
    LEFT JOIN Capacidad_Hab h ON h.CI = p.CI
    WHERE n.Cod_Inst_Nexo = p_carrera
    GROUP BY p.CI, p.Persona_Usuario, p.Primer_Nombre, p.Primer_Apellido
    ORDER BY coincidencias_hab DESC;
END;
$$;

--Función es miembro
CREATE OR REPLACE FUNCTION fn_es_miembro(
    p_usuario VARCHAR,
    p_grupo VARCHAR
)
RETURNS BOOLEAN AS $$
DECLARE
    existe INT;
BEGIN
    SELECT COUNT(*) INTO existe
    FROM Grupo_Participa
    WHERE Nombre_Grupo = p_grupo
      AND Usuario_Miembro = p_usuario;

    RETURN existe > 0;
END;
$$ LANGUAGE plpgsql;


--Funcion y Trigger Notificar Cambio Estado de Notificación

CREATE OR REPLACE FUNCTION fn_notificar_cambio_estado()
RETURNS TRIGGER AS $$
DECLARE
    mensaje_notificacion TEXT;
    tipo_alerta_final VARCHAR(20);
BEGIN
    IF (NEW.estado IN ('Aceptada', 'Rechazada')) AND (OLD.estado IS DISTINCT FROM NEW.estado) THEN
        IF NEW.estado = 'Aceptada' THEN
            mensaje_notificacion := 'Tu solicitud de relación ha sido aceptada.';
            tipo_alerta_final := 'Aceptada';
        ELSE
            mensaje_notificacion := 'Tu solicitud de relación ha sido rechazada.';
            tipo_alerta_final := 'Rechazada';
        END IF;

        INSERT INTO Notificaciones (id_usuario_destino, mensaje, tipo_alerta)
        VALUES (NEW.Usuario_Solicitante, mensaje_notificacion, tipo_alerta_final);
        
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notificar_estado_relacion
AFTER UPDATE ON Se_Relaciona 
FOR EACH ROW
EXECUTE FUNCTION fn_notificar_cambio_estado();


--Porcedimiento Almacenado Registrar asistencia a evento
CREATE OR REPLACE PROCEDURE sp_registrar_asistencia_evento(
    p_nombre_evento VARCHAR(30),
    p_fecha_evento DATE,
    p_usuario_organizador VARCHAR(15),
    p_lista_asistentes VARCHAR(15)[]
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_asistente VARCHAR(15);
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM Evento 
        WHERE Nombre_Evento = p_nombre_evento 
          AND Fecha_Evento = p_fecha_evento 
          AND Usuario_Evento = p_usuario_organizador
    ) THEN
        RAISE EXCEPTION 'El evento especificado no existe.';
    END IF;

    FOREACH v_asistente IN ARRAY p_lista_asistentes
    LOOP
        INSERT INTO Asistencia_Evento (Usuario_Asistente, Nombre_Evento, Fecha_Evento, Usuario_Evento_Org)
        VALUES (v_asistente, p_nombre_evento, p_fecha_evento, p_usuario_organizador)
        ON CONFLICT (Usuario_Asistente, Nombre_Evento, Fecha_Evento, Usuario_Evento_Org) DO NOTHING;
    END LOOP;
    
EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Error al registrar la asistencia: %', SQLERRM;
END;
$$;


--Inserts 

--Usuario 

INSERT INTO Usuario (Cuenta) VALUES ('qdmancha.22');
INSERT INTO Usuario (Cuenta) VALUES ('smpanza.22');
INSERT INTO Usuario (Cuenta) VALUES ('chbenengeli.19');
INSERT INTO Usuario (Cuenta) VALUES ('dtoboso.22');
INSERT INTO Usuario (Cuenta) VALUES ('mcervantes.02');
INSERT INTO Usuario (Cuenta) VALUES ('jgmaestro.95');
INSERT INTO Usuario (Cuenta) VALUES ('cjmarlow.20');
INSERT INTO Usuario (Cuenta) VALUES ('Lfguichard.15');
INSERT INTO Usuario (Cuenta) VALUES ('aagromeko.17');
INSERT INTO Usuario (Cuenta) VALUES ('yazhivago.14');
INSERT INTO Usuario (Cuenta) VALUES ('telawrence.04');
INSERT INTO Usuario (Cuenta) VALUES ('mercantil.20');
INSERT INTO Usuario (Cuenta) VALUES ('empolar.20');
INSERT INTO Usuario (Cuenta) VALUES ('ccfemsa.20');
INSERT INTO Usuario (Cuenta) VALUES ('banesco.20');
INSERT INTO Usuario (Cuenta) VALUES ('mercer.20');
INSERT INTO Usuario (Cuenta) VALUES ('embfan.20');
INSERT INTO Usuario (Cuenta) VALUES ('embpol.20');
INSERT INTO Usuario (Cuenta) VALUES ('embcor.20');
INSERT INTO Usuario (Cuenta) VALUES ('duncan.20');
INSERT INTO Usuario (Cuenta) VALUES ('pepsico.20');
INSERT INTO Usuario (Cuenta) VALUES ('bestiaSQL.25');


--Lugar

INSERT INTO Lugar (ISO_Lugar, Nombre_L, Tipo_L, ISO_Superior) VALUES
('VE', 'Venezuela', 'País', NULL),
('CD', 'R.D. del Congo', 'País', NULL),
('SA', 'Arabia Saudita', 'País', NULL),
('SA-02', 'La Meca', 'Estado', 'SA'),
('VE-A', 'Distrito Capital', 'Ciudad', 'VE'),
('VE-F', 'Bolívar', 'Estado', 'VE'),
('CD-KN', 'Kinshasa', 'Estado', 'CD');


--Persona 

INSERT INTO Persona (CI, Persona_Usuario, Primer_Nombre, Segundo_Nombre, Primer_Apellido, Segundo_Apellido, Sexo, Ubicacion_Persona) VALUES
('30000001', 'qdmancha.22', 'Quijote', NULL, 'de la Mancha', 'Cervantes', 'Masculino', 'VE-A'),
('30000002', 'smpanza.22', 'Sancho', 'Miguel', 'Panza', 'Saavedra', 'Masculino', 'VE-A'),
('30000003', 'chbenengeli.19', 'Cede', 'Hamete', 'Benengeli', 'Bittar', 'Masculino', 'VE-F'),
('30000004', 'dtoboso.22', 'Dulcinea', NULL, 'Del Toboso', 'Henares', 'Femenino', 'VE-A'),
('30000005', 'mcervantes.02', 'Miguel', NULL, 'De Cervantes', 'Saavedra', 'Masculino', 'VE-A'),
('30000006', 'jgmaestro.95', 'Jesús', NULL, 'Gonzalez', 'Maestro', 'Masculino', 'VE-A'),
('30000007', 'cjmarlow.20', 'Charlie', 'Joseph', 'Marlow', 'Conrad', 'Masculino', 'CD-KN'),
('30000008', 'Lfguichard.15', 'Larissa', 'Fiodorovna', 'Guichard', 'Antipova', 'Femenino', 'VE-A'),
('30000009', 'aagromeko.17', 'Antonina', 'Alexandrova', 'Gromeko', 'Zhivago', 'Femenino', 'VE-A'),
('30000010', 'yazhivago.14', 'Yuri', 'Andreievitch', 'Jivago', 'Pasternak', 'Masculino', 'VE-A'),
('30000000', 'telawrence.04', 'Thomas', 'Eduardo', 'Lawrence', 'Junner', 'Masculino', 'SA-02'),
('99999999', 'bestiaSQL.25', 'La', 'Bestia', 'del', 'SQL', 'Masculino', 'VE-A');


--Rol 
Insert into Rol (Tipo_Rol) Values 
('Estudiante'), ('Profesor'), ('Empleado'), ('Egresado');

--Desempeña 
INSERT INTO Desempeña (CI_Rol, Des_Rol) VALUES
('30000001', 'Estudiante'),
('30000002', 'Estudiante'),
('30000003', 'Estudiante'),
('30000004', 'Estudiante'),
('30000005', 'Profesor'),
('30000006', 'Profesor'),
('30000008', 'Empleado'),
('30000008', 'Profesor'),
('30000009', 'Empleado'),
('30000010', 'Empleado'),
('30000007', 'Egresado'),
('30000000', 'Egresado'),
('30000000', 'Profesor'),
('99999999', 'Profesor');


--Idioma 
INSERT INTO Idioma (ISO_Idioma, Idioma) VALUES
('es', 'Español'),
('en', 'Inglés'),
('de', 'Alemán'),
('fr', 'Francés'),
('ar', 'Árabe'),
('ru', 'Ruso');

--Habla 
INSERT INTO Habla (CI_Persona, ISO_Idioma, Nivel_Fluidez) VALUES
('30000001', 'es', 'Nativo'),
('30000002', 'es', 'Nativo'),
('30000003', 'es', 'Nativo'),
('30000003', 'ar', 'Avanzado'),
('30000004', 'es', 'Nativo'),
('30000004', 'fr', 'Intermedio'),
('30000005', 'es', 'Nativo'),
('30000005', 'fr', 'Avanzado'),
('30000005', 'en', 'Intermedio'),
('30000006', 'es', 'Nativo'),
('30000007', 'es', 'Nativo'),
('30000007', 'en', 'Nativo'),
('30000007', 'fr', 'Básico'),
('30000008', 'es', 'Nativo'),
('30000008', 'ru', 'Avanzado'),
('30000008', 'fr', 'Básico'),
('30000009', 'es', 'Nativo'),
('30000009', 'ru', 'Avanzado'),
('30000009', 'fr', 'Avanzado'),
('30000010', 'es', 'Nativo'),
('30000010', 'ru', 'Avanzado'),
('30000000', 'es', 'Nativo'),
('30000000', 'en', 'Avanzado'),
('30000000', 'ar', 'Avanzado'),
('99999999', 'es', 'Nativo'),
('99999999', 'en', 'Avanzado');

--Habilidad 
INSERT INTO Habilidad (Habilidad) VALUES
('Trabajo en Equipo'),
('Pintura'), 
('Idiomas'),
('Dibujo'), 
('Canto'), 
('Música'),
('Navegación'), 
('Excel'),
('Microsoft Office'),
('Fotografía'),
('Edición de Video'),
('Liderazgo'), 
('Programación'),
('Trabajo Bajo Presión'),
('Matemáticas');

--Capacidad Hab
INSERT INTO Capacidad_Hab (CI, Habilidad) VALUES
('30000001', 'Trabajo en Equipo'),
('30000002', 'Trabajo Bajo Presión'),
('30000003', 'Programación'),
('30000004', 'Pintura'),
('30000005', 'Idiomas'),
('30000005', 'Liderazgo'),
('30000006', 'Programación'),
('30000006', 'Matemáticas'),
('30000007', 'Navegación'),
('30000007', 'Trabajo Bajo Presión'),
('30000008', 'Música'),
('30000009', 'Fotografía'),
('30000009', 'Edición de Video'),
('30000010', 'Liderazgo'),
('30000010', 'Música'),
('30000000', 'Liderazgo'),
('30000000', 'Matemáticas'),
('99999999', 'Programación'),
('99999999', 'Matemáticas');


--Organizacion asociada
INSERT INTO Organizacion_Asociada (RIF, OA_Usuario, Nombre_O, Ubicacion_OA) VALUES
('310000001', 'empolar.20', 'Empresas Polar', 'VE-A'),
('310000002', 'ccfemsa.20', 'Coca Cola', 'VE-A'),
('310000003', 'banesco.20', 'Banesco', 'VE-A'),
('310000004', 'mercantil.20', 'Mercantil', 'VE-A'),
('310000005', 'mercer.20', 'Mercer', 'VE-A'),
('310000006', 'embfan.20', 'Embajada de Francia', 'VE-A'),
('310000007', 'embpol.20', 'Embajada de Polonia', 'VE-A'),
('310000008', 'embcor.20', 'Embajada de Corea', 'VE-A'),
('310000009', 'duncan.20', 'Duncan', 'VE-A'),
('310000010', 'pepsico.20', 'Pepsico', 'VE-A');

--entidad institucional
INSERT INTO Entidad_Institucional (Cod_Inst, Nombre_Ent_Inst, Cod_Inst_Sup) VALUES
('FACES', 'Facultad de Ciencias Economicas y Sociales', NULL),
('DERE', 'Facultad de Derecho', NULL),
('FING', 'Facultad de Ingeniería', NULL),
('FHYE', 'Facultad de Humanidad y Educación', NULL),
('SECR', 'Secretaría', NULL),
('VIAD', 'Vicerrectorado Administrativo', NULL),
('SSST', 'Servicio de Seguridad y Salud en el Trabajo', NULL),
('CISO', 'Escuela de Ciencias Sociales', 'FACES'),
('ECON', 'Economía', 'FACES'),
('ADCON', 'Administracion y Contaduría', 'FACES'),
('INFO', 'Ingeniería Informatica', 'FING'),
('TELE', 'Ingeniería Telecomunicaciones', 'FING'),
('ARQU', 'Arquitectura', 'FING'),
('CIVI', 'Ingeniería Civil', 'FING'),
('MECA', 'Ingeniería Mecatrónica', 'FING'),
('INDU', 'Ingeniería Industrial', 'FING'),
('COMU', 'Comunicación Social', 'FHYE'),
('LETR', 'Letras', 'FHYE'),
('PSIC', 'Psicología', 'FHYE'),
('EDUC', 'Educación', 'FHYE'),
('DIGE', 'Direccion de Gestón Estudinatil', 'SECR'),
('DIAF', 'Dirección de Administración y Finanzas', 'VIAD'),
('SEME', 'Servicios Medicos UCAB', 'SSST');


--Nexo 
INSERT INTO Nexo (CI_Nexo, Cod_Inst_Nexo) VALUES
('30000001', 'COMU'),
('30000002', 'INFO'),
('30000003', 'DERE'),
('30000004', 'PSIC'),
('30000005', 'LETR'),
('30000006', 'EDUC'),
('30000006', 'LETR'),
('30000007', 'MECA'),
('30000008', 'ADCON'),
('30000008', 'DIAF'),
('30000009', 'DIGE'),
('30000010', 'SEME'),
('30000000', 'CISO'),
('30000000', 'LETR'),
('99999999', 'INFO');

--Publicacion 
INSERT INTO Publicacion (Titulo_Pub, Autor, Sinopsis_Pub, Fecha_Pub) VALUES
('El ingenioso hidalgo Don Quijote de la Mancha', 'mcervantes.02', 'Primera parte de la novela cumbre de la literatura española, que narra las aventuras de un hidalgo enloquecido que se cree caballero andante.', '1605-01-16'),
('Novelas Ejemplares', 'mcervantes.02', 'Colección de doce novelas cortas que abordan temas morales y sociales con maestría.', '1613-09-02'),
('Los trabajos de Persiles y Sigismunda', 'mcervantes.02', 'Novela póstuma de género bizantino, que relata la peregrinación de dos príncipes nórdicos.', '1617-04-16'),
('Crítica de la Razón Literaria', 'jgmaestro.95', 'Obra fundamental que sienta las bases teóricas de la Literatura Materialista. Un análisis filosófico de la ficción.', '2011-03-25'),
('La Literatura, un arte del lenguaje', 'jgmaestro.95', 'Ensayo que aborda la naturaleza lingüística y material de la obra literaria y su relación con el sistema literario.', '2007-06-15'),
('Siete Pilares de Sabiduría', 'telawrence.04', 'Memorias de T. E. Lawrence sobre su participación en la Gran Revuelta Árabe contra el Imperio Otomano durante la Primera Guerra Mundial.', '1926-11-20');


--tiempo duración 

INSERT INTO Tiempo_Duracion (CI_T, Rol_T, Inst_T, Fecha_Inicio, Fecha_Fin) VALUES
('30000001', 'Estudiante', 'COMU', '2022-09-15', NULL),
('30000002', 'Estudiante', 'INFO', '2022-09-15', NULL),
('30000003', 'Estudiante', 'DERE', '2019-09-18', NULL),
('30000004', 'Estudiante', 'PSIC', '2022-09-15', NULL),
('30000005', 'Profesor', 'LETR', '2018-01-10', NULL),
('30000006', 'Profesor', 'EDUC', '2019-09-01', '2024-07-30'),
('30000006', 'Profesor', 'LETR', '2024-09-01', NULL),
('30000007', 'Egresado', 'MECA', '2020-03-01', '2025-07-21'),
('30000008', 'Profesor', 'ADCON', '2022-04-12', NULL),
('30000008', 'Empleado', 'DIAF', '2023-01-01', '2024-01-01'),
('30000009', 'Empleado', 'DIGE', '2021-11-20', NULL),
('30000010', 'Empleado', 'SEME', '2020-10-01', NULL),
('30000000', 'Egresado', 'CISO', '2004-09-01', '2009-07-30'),
('30000000', 'Profesor', 'LETR', '2023-09-01', NULL),
('99999999', 'Profesor', 'INFO', '2025-10-01', NULL);


--descripcion cuenta

INSERT INTO Descripcion (Descripcion, Cuenta_Desc) VALUES
('Hidalgo soñador, buscador incansable de la justicia y la aventura. Dedicado al estudio de la literatura caballeresca y la comunicación.', 'qdmancha.22'),
('Escudero pragmático con gran sentido común y apego a la realidad. Experto en navegación terrestre y supervivencia bajo presión.', 'smpanza.22'),
('Historiador y cronista del mundo antiguo. Dedicado a la preservación de documentos históricos y la investigación genealógica.', 'chbenengeli.19'),
('Joven entusiasta de las artes y la psicología. Se especializa en la representación pictórica del comportamiento humano.', 'dtoboso.22'),
('Profesor de Letras, autor de obras clásicas y experto en la narrativa del Siglo de Oro español. Foco en la enseñanza de idiomas.', 'mcervantes.02'),
('Profesor e investigador de la Literatura Materialista. Experto en filosofía, matemáticas y el análisis crítico de textos literarios.', 'jgmaestro.95'),
('Egresado de ingeniería mecatrónica, actualmente navegando por el Congo. Interesado en el comercio marítimo y el trabajo en ambientes extremos.', 'cjmarlow.20'),
('Empleado administrativo y estudiante avanzada de contaduría. Posee experiencia en gestión financiera y dirección de oficinas.', 'Lfguichard.15'),
('Especialista en fotografía documental y edición de video. Trabaja en la gestión de registros estudiantiles y la comunicación audiovisual.', 'aagromeko.17'),
('Médico en formación, dedicado a los servicios de salud y seguridad. Interesado en la gestión hospitalaria y la música clásica.', 'yazhivago.14'),
('Egresado de Ciencias Sociales. Historiador y estratega militar, con profundo conocimiento de la cultura árabe y liderazgo en el desierto.', 'telawrence.04'),
('Líder en el sector de alimentos y bebidas. Interesada en el talento joven y la responsabilidad social empresarial.', 'empolar.20'),
('Embotelladora de Coca Cola. Gran presencia en Latinoamérica. Enfocada en logística, distribución y sostenibilidad.', 'ccfemsa.20'),
('Institución bancaria con enfoque en banca digital y servicios financieros para pymes y corporaciones.', 'banesco.20'),
('Banco universal con fuerte trayectoria en el país. Especialistas en crédito, inversión y manejo de cuentas corporativas.', 'mercantil.20'),
('Consultora global en recursos humanos, beneficios y gestión de talento. Especializada en encuestas salariales y compensaciones.', 'mercer.20'),
('Representación diplomática de Francia. Promoción de la cultura, los negocios y la educación superior en el país.', 'embfan.20'),
('Representación diplomática de Polonia. Fomento de relaciones bilaterales y apoyo a estudiantes y profesionales polacos.', 'embpol.20'),
('Representación diplomática de Corea. Impulso del intercambio tecnológico y cultural, especialmente en áreas de ingeniería.', 'embcor.20'),
('Empresa de manufactura y distribución de productos de caucho y neumáticos.', 'duncan.20'),
('Compañía global de alimentos y bebidas, rival directo de Coca Cola. Foco en innovación de snacks y bebidas energéticas.', 'pepsico.20'),
('El mejor experto en SQL del continente. Foco en DDL, DML y optimización de consultas complejas en PostgreSQL.', 'bestiaSQL.25');


--Se relaciona
INSERT INTO Se_Relaciona (Usuario_Receptor, Usuario_Solicitante, Tipo_Relacion, Fecha_Relacion) VALUES
('bestiaSQL.25', 'qdmancha.22', 'Seguimiento', '2025-06-01'),
('bestiaSQL.25', 'smpanza.22', 'Seguimiento', '2025-06-01'),
('bestiaSQL.25', 'dtoboso.22', 'Seguimiento', '2025-06-01'),
('jgmaestro.95', 'mcervantes.02', 'Seguimiento', '2025-06-02'),
('mcervantes.02', 'jgmaestro.95', 'Seguimiento', '2025-06-02'),
('mcervantes.02', 'telawrence.04', 'Seguimiento', '2025-06-02'),
('telawrence.04', 'cjmarlow.20', 'Seguimiento', '2025-06-02'),
('Lfguichard.15', 'aagromeko.17', 'Seguimiento', '2025-06-02'),
('aagromeko.17', 'Lfguichard.15', 'Amistad', '2025-06-03'),
('aagromeko.17', 'yazhivago.14', 'Amistad', '2025-06-03'), 
('yazhivago.14', 'aagromeko.17', 'Amistad', '2025-06-03'),
('dtoboso.22', 'mcervantes.02', 'Seguimiento', '2025-06-03'),
('mcervantes.02', 'dtoboso.22', 'Seguimiento', '2025-06-03'),
('bestiaSQL.25', 'empolar.20', 'Seguimiento', '2025-06-05'),
('bestiaSQL.25', 'ccfemsa.20', 'Seguimiento', '2025-06-05'),
('mcervantes.02', 'banesco.20', 'Seguimiento', '2025-06-05'),
('mcervantes.02', 'mercantil.20', 'Seguimiento', '2025-06-05'),
('ccfemsa.20', 'pepsico.20', 'Seguimiento', '2025-06-05'),
('embfan.20', 'qdmancha.22', 'Seguimiento', '2025-06-06'),
('embpol.20', 'smpanza.22', 'Seguimiento', '2025-06-06'),
('duncan.20', 'cjmarlow.20', 'Seguimiento', '2025-06-06'),
('qdmancha.22', 'Lfguichard.15', 'Seguimiento', '2025-06-07'),
('qdmancha.22', 'aagromeko.17', 'Seguimiento', '2025-06-07'),
('chbenengeli.19', 'jgmaestro.95', 'Seguimiento', '2025-06-07'),
('jgmaestro.95', 'chbenengeli.19', 'Seguimiento', '2025-06-07');


--Evento
INSERT INTO Evento (Nombre_Evento, Fecha_Evento, Usuario_Evento, Desc_Evento) VALUES
('Feria del Emprendimiento Polar', '2026-03-15', 'empolar.20', 'Concurso nacional para financiar nuevas ideas de negocio en alimentos y tecnología.'),
('Taller de Innovacion Cervecera', '2026-06-20', 'empolar.20', 'Sesión técnica para ingenieros sobre procesos de producción automatizados.'),
('Jornada de Becas Erasmus', '2026-02-10', 'embfan.20', 'Sesión informativa sobre oportunidades de estudio e investigación en Francia.'),
('Noche de Cine Frances', '2026-04-05', 'embfan.20', 'Proyección especial de películas clásicas y contemporáneas.'),
('Foro de Banca Digital', '2026-07-01', 'banesco.20', 'Conferencia sobre el futuro de las transacciones financieras y la ciberseguridad.'),
('Dia de la Sostenibilidad', '2026-09-03', 'ccfemsa.20', 'Jornada dedicada a la gestión de recursos hídricos y reciclaje.'),
('Reto Snacks Innovadores', '2026-11-18', 'pepsico.20', 'Concurso de ideas para nuevos productos de aperitivos y bebidas.');

--Chat
INSERT INTO Chat (Nombre_Chat) VALUES ('Equipo de Desarrollo SQL');
INSERT INTO Chat (Nombre_Chat) VALUES ('Grupo de Estudio DDL');
INSERT INTO Chat (Nombre_Chat) VALUES ('Consultas y Soporte TI');

--chat_miembro 
INSERT INTO Chat_Miembro (Chat_Participa, Fecha_Chat, Usuario_Chat, Rol_Chat) VALUES
('Consultas y Soporte TI', (Select Fecha_Creacion_Chat from chat where nombre_chat = 'Consultas y Soporte TI'), 'dtoboso.22', 'Creador'),
('Consultas y Soporte TI', (Select Fecha_Creacion_Chat from chat where nombre_chat = 'Consultas y Soporte TI'), 'mcervantes.02', 'Administrador'),
('Consultas y Soporte TI', (Select Fecha_Creacion_Chat from chat where nombre_chat = 'Consultas y Soporte TI'), 'jgmaestro.95', 'Miembro');

INSERT INTO Chat_Miembro (Chat_Participa, Fecha_Chat, Usuario_Chat, Rol_Chat) VALUES
('Grupo de Estudio DDL', (Select Fecha_Creacion_Chat from chat where nombre_chat = 'Grupo de Estudio DDL'), 'qdmancha.22', 'Creador'),
('Grupo de Estudio DDL', (Select Fecha_Creacion_Chat from chat where nombre_chat = 'Grupo de Estudio DDL'), 'smpanza.22', 'Miembro'),
('Grupo de Estudio DDL', (Select Fecha_Creacion_Chat from chat where nombre_chat = 'Grupo de Estudio DDL'), 'chbenengeli.19', 'Miembro');

INSERT INTO Chat_Miembro (Chat_Participa, Fecha_Chat, Usuario_Chat, Rol_Chat) VALUES
('Equipo de Desarrollo SQL', (Select Fecha_Creacion_Chat from chat where nombre_chat = 'Equipo de Desarrollo SQL'), 'bestiaSQL.25', 'Creador'),
('Equipo de Desarrollo SQL', (Select Fecha_Creacion_Chat from chat where nombre_chat = 'Equipo de Desarrollo SQL'), 'cjmarlow.20', 'Administrador'),
('Equipo de Desarrollo SQL', (Select Fecha_Creacion_Chat from chat where nombre_chat = 'Equipo de Desarrollo SQL'), 'aagromeko.17', 'Miembro');


--grupo 
INSERT INTO Grupo (Nombre_Grupo, Desc_Grupo, Tipo_Grupo) VALUES
('Buscando Trabajo', 'Grupo dedicado a compartir ofertas de empleo, consejos de carrera y networking profesional.', 'Público'),
('Tutorías y Ayuda Académica', 'Espacio para solicitar y ofrecer tutorías sobre temas universitarios y académicos.', 'Público'),
('Cine y Literatura Clásica', 'Discusión y análisis de obras del Siglo de Oro, novelas caballerescas y cine de culto.', 'Privado'),
('Desarrolladores SQL y NoSQL', 'Comunidad para compartir tips, resolver dudas y discutir las últimas tendencias en bases de datos.', 'Público'),
('Práctica de Idioma Polaco', 'Reuniones virtuales para practicar y mejorar el nivel de conversación en el idioma polaco.', 'Privado');


--grupo participa
INSERT INTO Grupo_Participa (Nombre_Grupo, Usuario_Miembro, Rol_Miembro) VALUES
('Buscando Trabajo', 'mercer.20', 'Fundador'),
('Buscando Trabajo', 'empolar.20', 'Moderador'),
('Buscando Trabajo', 'telawrence.04', 'Miembro'),
('Tutorías y Ayuda Académica', 'mcervantes.02', 'Fundador'),
('Tutorías y Ayuda Académica', 'jgmaestro.95', 'Moderador'),
('Tutorías y Ayuda Académica', 'qdmancha.22', 'Miembro'),
('Cine y Literatura Clásica', 'mcervantes.02', 'Fundador'),
('Cine y Literatura Clásica', 'dtoboso.22', 'Miembro'),
('Cine y Literatura Clásica', 'chbenengeli.19', 'Miembro'),
('Desarrolladores SQL y NoSQL', 'bestiaSQL.25', 'Fundador'),
('Desarrolladores SQL y NoSQL', 'jgmaestro.95', 'Miembro'),
('Práctica de Idioma Polaco', 'embpol.20', 'Fundador'),
('Práctica de Idioma Polaco', 'Lfguichard.15', 'Miembro'),
('Práctica de Idioma Polaco', 'smpanza.22', 'Miembro');

--contenido 
INSERT INTO Contenido (Usuario_Creador, Cuerpo_Contenido, Nro_Me_Gusta, Nro_No_Me_Gusta) VALUES
('bestiaSQL.25', 'El mejor truco para optimizar consultas complejas en PostgreSQL es usar el comando EXPLAIN ANALYZE. ¡Siempre revisen sus planes de ejecución!', 45, 1),
('mcervantes.02', 'La lectura del Quijote es una obligación moral para todo hispanohablante. ¿Cuál es su capítulo favorito y por qué?', 30, 0),
('empolar.20', '¡Abrimos convocatoria para el programa de pasantías de verano! Buscamos talento en ingeniería y administración. Revisa los requisitos en nuestro perfil.', 55, 3),
('smpanza.22', 'Comiendo morcillas con vino, la verdad no se siente. Mucho mejor que correr detrás de molinos que solo traen problemas.', 12, 5),
('jgmaestro.95', 'La razón literaria no es subjetiva, es una estructura material. Todo texto es una máquina. #TeoríaLiteraria', 8, 2),
('ccfemsa.20', 'Nuestro compromiso con la sostenibilidad en el manejo del agua es primordial. Innovando en procesos de reciclaje hídrico cada día.', 22, 1),
('Lfguichard.15', '¡Terminé mi diplomado en contaduría! El trabajo bajo presión sí da sus frutos, aunque quede agotada. 🎉', 18, 0),
('telawrence.04', 'La vasta soledad del desierto enseña más estrategia que mil libros de texto. El liderazgo se forja en la adversidad.', 35, 2),
('aagromeko.17', 'Subiendo un nuevo reel con los mejores momentos del último evento universitario. Edición de video terminada justo a tiempo.', 15, 0),
('qdmancha.22', '¿Qué mejor locura que creer que el mundo puede cambiar? ¡A seguir luchando por la justicia!', 25, 10);

--reacciona
INSERT INTO Reacciona (Usuario_Reacciona, Usuario_Contenido, FechaHora_Contenido, Reaccion) VALUES
('Lfguichard.15', 'bestiaSQL.25', (SELECT FechaHora_Creacion FROM Contenido WHERE Usuario_Creador = 'bestiaSQL.25' AND Cuerpo_Contenido LIKE 'El mejor truco%'), 'No Me Gusta'),
('bestiaSQL.25', 'mcervantes.02', (SELECT FechaHora_Creacion FROM Contenido WHERE Usuario_Creador = 'mcervantes.02' AND Cuerpo_Contenido LIKE 'La lectura del Quijote%'), 'Me Gusta'),
('aagromeko.17', 'bestiaSQL.25', (SELECT FechaHora_Creacion FROM Contenido WHERE Usuario_Creador = 'bestiaSQL.25' AND Cuerpo_Contenido LIKE 'El mejor truco%'), 'Me Gusta'),
('bestiaSQL.25', 'empolar.20', (SELECT FechaHora_Creacion FROM Contenido WHERE Usuario_Creador = 'empolar.20' AND Cuerpo_Contenido LIKE '¡Abrimos convocatoria%'), 'Me Gusta'),
('qdmancha.22', 'smpanza.22', (SELECT FechaHora_Creacion FROM Contenido WHERE Usuario_Creador = 'smpanza.22' AND Cuerpo_Contenido LIKE 'Comiendo morcillas%'), 'No Me Gusta'),
('cjmarlow.20', 'telawrence.04', (SELECT FechaHora_Creacion FROM Contenido WHERE Usuario_Creador = 'telawrence.04' AND Cuerpo_Contenido LIKE 'La vasta soledad%'), 'Me Gusta');


--Denuncias 
INSERT INTO Denuncia (usuario_denunciante, usuario_denunciado, motivo) VALUES 
('smpanza.22', 'qdmancha.22', 'Comportamiento errático con molinos'),
('dtoboso.22', 'qdmancha.22', 'Acoso romántico no solicitado');



--REPORTES

--Reporte conectividad 
CREATE OR REPLACE VIEW reporte_conectividad_red AS
SELECT 
    u.Cuenta AS Usuario,
    COUNT(sr_recibida.Usuario_Solicitante) AS Total_Seguidores,
    COUNT(sr_enviada.Usuario_Receptor) AS Total_Seguidos,
    (COUNT(sr_recibida.Usuario_Solicitante) + COUNT(sr_enviada.Usuario_Receptor)) AS Total_Conexiones
FROM 
    Usuario u
LEFT JOIN 
    Se_Relaciona sr_recibida ON u.Cuenta = sr_recibida.Usuario_Receptor -- Relaciones que recibe
LEFT JOIN 
    Se_Relaciona sr_enviada ON u.Cuenta = sr_enviada.Usuario_Solicitante -- Relaciones que hace
GROUP BY 
    u.Cuenta
ORDER BY 
    Total_Conexiones DESC;
	

--Reporte Actividad de Denuncias 
CREATE OR REPLACE VIEW reporte_actividad_denuncias AS
SELECT 
    usuario_denunciado AS Usuario_Reportado,
    COUNT(*) AS Cantidad_Denuncias,
    STRING_AGG(motivo, ' | ') AS Motivos -- Junta todos los motivos en una sola línea
FROM 
    Denuncia
GROUP BY 
    usuario_denunciado
ORDER BY 
    Cantidad_Denuncias DESC;

SELECT 
    usuario_denunciado AS "Usuario Reportado",
    COUNT(*) AS "Total Denuncias",
    STRING_AGG(motivo, ' | ') AS "Motivos Agrupados" -- Agrupa los motivos para facilitar la lectura
FROM 
    Denuncia
GROUP BY 
    usuario_denunciado
ORDER BY 
    "Total Denuncias" DESC;
	
SELECT 
    u.Cuenta AS "Usuario",
    COUNT(DISTINCT recibida.Usuario_Solicitante) AS "Seguidores",
    COUNT(DISTINCT enviada.Usuario_Receptor) AS "Seguidos",
    (COUNT(DISTINCT recibida.Usuario_Solicitante) + COUNT(DISTINCT enviada.Usuario_Receptor)) AS "Total Conexiones"
FROM 
    Usuario u
LEFT JOIN 
    Se_Relaciona recibida ON u.Cuenta = recibida.Usuario_Receptor   -- Conteo de seguidores (relaciones recibidas)
LEFT JOIN 
    Se_Relaciona enviada ON u.Cuenta = enviada.Usuario_Solicitante   -- Conteo de seguidos (relaciones enviadas)
GROUP BY 
    u.Cuenta
ORDER BY 
    "Total Conexiones" DESC;
	

--Reporte ver Perfil

--Funcion Contar Seguidores

CREATE OR REPLACE FUNCTION contar_seguimiento(
    p_cuenta_usuario VARCHAR(15)
)
RETURNS TABLE (
    total_seguidores BIGINT, 
    total_seguidos BIGINT     
)
AS $$
SELECT 
    (
        SELECT COUNT(*)
        FROM Se_Relaciona
        WHERE 
            Usuario_Receptor = p_cuenta_usuario 
            AND Tipo_Relacion = 'Seguimiento'
    ),

    (
        SELECT COUNT(*)
        FROM Se_Relaciona
        WHERE 
            Usuario_Solicitante = p_cuenta_usuario 
            AND Tipo_Relacion = 'Seguimiento'
    );
$$ LANGUAGE sql;


--Funcion Obtener Perfil Usuario 
CREATE OR REPLACE FUNCTION obtener_perfil_usuario(
    p_cuenta_usuario VARCHAR(15) 
)
RETURNS TABLE (
    Nombre_Usuario VARCHAR(15),
    Tipo_Perfil TEXT,
    Nombre_Completo TEXT, 
    Ubicacion TEXT,
    Descripcion TEXT,
    total_seguidores BIGINT,
    total_seguidos BIGINT
)
AS $$
SELECT
    
    U.Cuenta,
    CASE
        WHEN P.Persona_Usuario IS NOT NULL THEN 'Persona'::TEXT
        WHEN OA.OA_Usuario IS NOT NULL THEN 'Organización Asociada'::TEXT
        ELSE 'Indefinido/Base'::TEXT
    END,
    
   
    COALESCE(
        TRIM(
            P.Primer_Nombre || ' ' || 
            CASE WHEN P.Segundo_Nombre IS NOT NULL THEN P.Segundo_Nombre || ' ' ELSE '' END || 
            P.Primer_Apellido || ' ' || 
            P.Segundo_Apellido
        ),
        OA.Nombre_O,
        'N/A'
    ),
    
   
    COALESCE(
        L_P.Nombre_L,
        L_OA.Nombre_L,
        'Ubicación Desconocida'
    ),
    
   
    D.Descripcion,
    
    
    CS.total_seguidores,
    CS.total_seguidos

FROM 
    Usuario U

LEFT JOIN 
    Persona P ON U.Cuenta = P.Persona_Usuario
LEFT JOIN 
    Lugar L_P ON P.Ubicacion_Persona = L_P.ISO_Lugar

LEFT JOIN 
    Organizacion_Asociada OA ON U.Cuenta = OA.OA_Usuario
LEFT JOIN 
    Lugar L_OA ON OA.Ubicacion_OA = L_OA.ISO_Lugar

LEFT JOIN
    Descripcion D ON U.Cuenta = D.Cuenta_Desc

LEFT JOIN LATERAL 
    contar_seguimiento(U.Cuenta) CS ON TRUE 

WHERE 
    U.Cuenta = p_cuenta_usuario;

$$ LANGUAGE sql;



--Funcion Select * From Contenido + Nombre Usuario 
CREATE OR REPLACE FUNCTION obtener_posts_por_autor(
    p_usuario_creador VARCHAR(15)      
)
RETURNS TABLE (
    Autor_Usuario VARCHAR(15),          
    Autor_Nombre_Completo TEXT,         
    Fecha_Publicacion TIMESTAMP,        
    Me_Gusta INTEGER,                   
    No_Me_Gusta INTEGER,                
    Cuerpo_Post VARCHAR(300)            
)
AS $$
SELECT
    C.Usuario_Creador,
    
    COALESCE(
        
        TRIM(
            P.Primer_Nombre || ' ' || 
            CASE WHEN P.Segundo_Nombre IS NOT NULL THEN P.Segundo_Nombre || ' ' ELSE '' END || 
            P.Primer_Apellido || ' ' || 
            P.Segundo_Apellido
        ),
        
        OA.Nombre_O,
        'Autor Desconocido'
    ),
    
    C.FechaHora_Creacion,
    C.Nro_Me_Gusta,
    C.Nro_No_Me_Gusta,
    C.Cuerpo_Contenido

FROM 
    Contenido C


JOIN 
    Usuario U ON C.Usuario_Creador = U.Cuenta

LEFT JOIN 
    Persona P ON U.Cuenta = P.Persona_Usuario

LEFT JOIN 
    Organizacion_Asociada OA ON U.Cuenta = OA.OA_Usuario

WHERE 
    C.Usuario_Creador = p_usuario_creador

ORDER BY
    C.FechaHora_Creacion DESC;

$$ LANGUAGE sql;


--Reporte de Buscador 
CREATE OR REPLACE FUNCTION buscar_contenido_por_texto(
    p_termino_busqueda TEXT      
)
RETURNS TABLE (
    Autor_Usuario VARCHAR(15),          
    Autor_Nombre_Completo TEXT,         
    Fecha_Publicacion TIMESTAMP,        
    Me_Gusta INTEGER,                 
    No_Me_Gusta INTEGER,               
    Cuerpo_Post VARCHAR(300)            
)
AS $$
SELECT
    C.Usuario_Creador,
    
    COALESCE(
        
        TRIM(
            P.Primer_Nombre || ' ' || 
            CASE WHEN P.Segundo_Nombre IS NOT NULL THEN P.Segundo_Nombre || ' ' ELSE '' END || 
            P.Primer_Apellido || ' ' || 
            P.Segundo_Apellido
        ),
        
        OA.Nombre_O,
        'Autor Desconocido'
    ),
    
    C.FechaHora_Creacion,
    C.Nro_Me_Gusta,
    C.Nro_No_Me_Gusta,
    C.Cuerpo_Contenido

FROM 
    Contenido C

JOIN 
    Usuario U ON C.Usuario_Creador = U.Cuenta

LEFT JOIN 
    Persona P ON U.Cuenta = P.Persona_Usuario

LEFT JOIN 
    Organizacion_Asociada OA ON U.Cuenta = OA.OA_Usuario

WHERE 
    C.Cuerpo_Contenido ILIKE '%' || p_termino_busqueda || '%'
    
ORDER BY
    C.FechaHora_Creacion DESC;

$$ LANGUAGE sql;


--Vistas Reportes
-- Crecimiento de la Comunidad.
CREATE OR REPLACE VIEW vw_crecimiento_comunidad AS
SELECT
    ei.Cod_Inst AS codigo_carrera,
    ei.Nombre_Ent_Inst AS nombre_carrera,
    COUNT(DISTINCT n.CI_Nexo) AS total_miembros
FROM Nexo n
JOIN Entidad_Institucional ei 
    ON ei.Cod_Inst = n.Cod_Inst_Nexo
GROUP BY ei.Cod_Inst, ei.Nombre_Ent_Inst
ORDER BY total_miembros DESC;


-- Rendimiento de Grupos
CREATE OR REPLACE VIEW vw_rendimiento_grupos AS
SELECT
    g.Nombre_Grupo,
    g.Tipo_Grupo,
    COUNT(gp.Usuario_Miembro) AS total_miembros,
    SUM(CASE WHEN gp.Rol_Miembro = 'Fundador' THEN 1 ELSE 0 END) AS fundadores,
    SUM(CASE WHEN gp.Rol_Miembro = 'Moderador' THEN 1 ELSE 0 END) AS moderadores,
    SUM(CASE WHEN gp.Rol_Miembro = 'Miembro' THEN 1 ELSE 0 END) AS miembros
FROM Grupo g
LEFT JOIN Grupo_Participa gp ON g.Nombre_Grupo = gp.Nombre_Grupo
GROUP BY g.Nombre_Grupo, g.Tipo_Grupo
ORDER BY total_miembros DESC;

-- Diáspora y Concentración Profesional.
CREATE OR REPLACE VIEW vw_diaspora_concentracion AS
SELECT
    l.Nombre_L AS ubicacion,
    l.Tipo_L AS tipo_ubicacion,
    COUNT(DISTINCT p.CI) AS total_personas,
    array_agg(DISTINCT d.Des_Rol) AS roles_presentes,
    array_agg(DISTINCT n.Cod_Inst_Nexo) AS carreras_presentes
FROM Persona p
JOIN Lugar l ON p.Ubicacion_Persona = l.ISO_Lugar
LEFT JOIN Desempeña d ON d.CI_Rol = p.CI
LEFT JOIN Nexo n ON n.CI_Nexo = p.CI
GROUP BY l.Nombre_L, l.Tipo_L
ORDER BY total_personas DESC;


--Seguridad

--Roles

--Usuario común 
CREATE ROLE Rol_Usuario_App NOLOGIN;

GRANT INSERT, DELETE ON Habla TO Rol_Usuario_App;
GRANT UPDATE (ISO_Idioma, Nivel_Fluidez) ON Habla TO Rol_Usuario_App;

GRANT INSERT, DELETE ON Capacidad_Hab TO Rol_Usuario_App;
GRANT UPDATE (Habilidad) ON Capacidad_Hab TO Rol_Usuario_App;

GRANT INSERT, UPDATE, DELETE ON Publicacion TO Rol_Usuario_App;

GRANT UPDATE (ubicacion_persona) ON Persona TO Rol_Usuario_App;

GRANT EXECUTE ON PROCEDURE Gestionar_Post_SP TO Rol_Usuario_App;

--Moderador

CREATE ROLE Moderador NOLOGIN;

GRANT UPDATE, DELETE ON Grupo TO Moderador;

GRANT UPDATE, DELETE ON Chat TO Moderador;

GRANT UPDATE, DELETE ON Evento TO Moderador;

GRANT UPDATE, DELETE ON Contenido TO Moderador;

--Administrador de Chat

CREATE ROLE Administrador_Chat NOLOGIN;

GRANT INSERT, UPDATE, DELETE ON Chat TO Administrador_Chat;
GRANT INSERT, UPDATE, DELETE ON Chat_Miembro TO Administrador_Chat;

--Usuarios 

CREATE USER usuario_app WITH PASSWORD 'UsuarioApp1' LOGIN;
GRANT Rol_Usuario_App TO usuario_app;


CREATE USER moderador_usuario WITH PASSWORD 'Moderador1' LOGIN;
GRANT Moderador TO moderador_usuario;


CREATE USER administrador_chat_usuario WITH PASSWORD 'AdminChat1' LOGIN;
GRANT Administrador_Chat TO administrador_chat_usuario;

