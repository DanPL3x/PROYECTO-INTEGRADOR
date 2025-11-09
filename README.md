# Sistema de Seguridad Ciudadana - Cali

Se actualizó el portal visual (mejoras en UI/UX del menú, panel administrador y vista usuario). El proyecto ahora incluye:
- Interfaz mejorada en JavaFX (tiles, iconos, barra de estado, panel de notificaciones).
- Persistencia de notificaciones en la BD.
- CRUD desde el panel administrativo (dinámico por tablas).
- Formulario en el portal usuario para enviar consultas/denuncias que llegan al administrador.

A continuación se incluye un script SQL listo para ejecutarse en MySQL/MariaDB que crea la base de datos y tablas mínimas necesarias para que la aplicación funcione tal cual en entorno de desarrollo.

IMPORTANTE: Ajusta `config.properties` (ejemplo más abajo) con las credenciales correctas antes de ejecutar la app.

## Crear la base de datos (script SQL)

Guarda el siguiente contenido en un archivo `db_create.sql` y ejecútalo con:
mysql -u root -p < db_create.sql

```sql
-- filepath: c:\Users\Daniel\Documents\Proyecto-integrador\proyect\db_create.sql
CREATE DATABASE IF NOT EXISTS seguridad_cali CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE seguridad_cali;

-- Administradores (para login)
CREATE TABLE IF NOT EXISTS admin (
  id_admin INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL, -- guarda contraseña (puede ser plain para prueba; en producción hash)
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Notificaciones (mensajes usuario -> admin)
CREATE TABLE IF NOT EXISTS notifications (
  id INT AUTO_INCREMENT PRIMARY KEY,
  sender VARCHAR(100) NOT NULL,
  message TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  is_read TINYINT(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tablas de ejemplo del dominio
CREATE TABLE IF NOT EXISTS PuntoCardinal (
  id_punto_cardinal INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS NivelRiesgo (
  id_nivel INT AUTO_INCREMENT PRIMARY KEY,
  riesgo VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS Zona (
  id_zona INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS LugarDenuncias (
  id_lugar INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  direccion VARCHAR(255),
  telefono VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS Ubicacion (
  id_ubicacion INT AUTO_INCREMENT PRIMARY KEY,
  direccion VARCHAR(255),
  id_nivel INT,
  id_zona INT,
  id_punto_cardinal INT,
  FOREIGN KEY (id_nivel) REFERENCES NivelRiesgo(id_nivel) ON DELETE SET NULL,
  FOREIGN KEY (id_zona) REFERENCES Zona(id_zona) ON DELETE SET NULL,
  FOREIGN KEY (id_punto_cardinal) REFERENCES PuntoCardinal(id_punto_cardinal) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS Delito (
  id_delito INT AUTO_INCREMENT PRIMARY KEY,
  tipo_delito VARCHAR(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS Denuncia (
  id_denuncia INT AUTO_INCREMENT PRIMARY KEY,
  id_lugar INT,
  fecha DATE,
  hora TIME,
  descripcion TEXT,
  FOREIGN KEY (id_lugar) REFERENCES LugarDenuncias(id_lugar) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS Denuncia_Delito (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_denuncia INT,
  id_delito INT,
  FOREIGN KEY (id_denuncia) REFERENCES Denuncia(id_denuncia) ON DELETE CASCADE,
  FOREIGN KEY (id_delito) REFERENCES Delito(id_delito) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Datos de ejemplo mínimos
INSERT IGNORE INTO admin (username, password) VALUES ('admin','admin123');

INSERT IGNORE INTO PuntoCardinal (nombre) VALUES ('Norte'),('Sur'),('Este'),('Oeste');
INSERT IGNORE INTO NivelRiesgo (riesgo) VALUES ('Bajo'),('Medio'),('Alto');
INSERT IGNORE INTO Zona (nombre) VALUES ('Zona Centro'),('Zona Norte'),('Zona Sur');
INSERT IGNORE INTO LugarDenuncias (nombre,direccion,telefono) VALUES
  ('CAI Centro','Cra 1 #1-1','1234567'),
  ('Estación Norte','Cl 2 #2-2','2345678');

INSERT IGNORE INTO Delito (tipo_delito) VALUES ('Robo'),('Hurto'),('Vandalismo');

-- Opcional: un ejemplo de denuncia vinculada
INSERT INTO Denuncia (id_lugar, fecha, hora, descripcion) VALUES (1, CURDATE(), CURTIME(), 'Denuncia de prueba');
INSERT INTO Denuncia_Delito (id_denuncia, id_delito) VALUES (LAST_INSERT_ID(), 1);
```

## Archivo de configuración (config.properties)

Crea `config.properties` en la raíz del proyecto o en `integrador/config.properties`. Ejemplo:

```properties
# filepath: c:\Users\Daniel\Documents\Proyecto-integrador\proyect\config.properties
db.url=jdbc:mysql://localhost:3306/seguridad_cali
db.user=root
db.password=tu_contraseña
```

- Si prefieres pasar parámetros por System properties (útil para CI), usa `-Ddb.url=... -Ddb.user=... -Ddb.password=...`.

## Pasos rápidos para levantar el proyecto localmente
1. Importa/abre el proyecto en tu IDE o usa Maven.
2. Crea la BD ejecutando `db_create.sql` (ver arriba).
3. Crea `config.properties` con las credenciales correctas.
4. Ejecuta:
   - Con Maven: `mvn clean javafx:run`
   - O desde IDE asegurándote de añadir JavaFX en module-path si no usas el plugin.
5. Usuario admin por defecto: `admin` / `admin123` (cámbialo en producción).

Si quieres, puedo:
- Generar un archivo `db_create.sql` en el repo (c:.../proyect/db_create.sql).
- Ajustar el script para una configuración de MySQL específica (puerto, usuario no-root).
- Añadir migraciones (Flyway) para mantener la estructura de la BD en entornos productivos.





