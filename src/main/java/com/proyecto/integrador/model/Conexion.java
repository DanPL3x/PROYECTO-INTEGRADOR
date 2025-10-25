package com.proyecto.integrador.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Conexion {

    private static Connection connection;

    // Llámalo como instancia o estático; método público para compatibilidad con uso actual.
    public Connection getConnection() {
        return getConnectionStatic();
    }

    // Método estático sincronizado para obtener la conexión (reutiliza conexión existente)
    public static synchronized Connection getConnectionStatic() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) return connection;
            } catch (SQLException e) {
                // intentar recrear
                connection = null;
            }
        }

        Properties properties = new Properties();

        // 1) Revisar System properties primero (permitir inyección desde App/FXLauncher)
        String sysUrl = System.getProperty("db.url");
        String sysUser = System.getProperty("db.user");
        String sysPass = System.getProperty("db.password");

        if (sysUrl != null && sysUser != null) {
            properties.setProperty("db.url", sysUrl);
            properties.setProperty("db.user", sysUser);
            if (sysPass != null) properties.setProperty("db.password", sysPass);
            System.out.println("Configuración de BD cargada desde System properties");
        } else {
            // 2) Intentar cargar fichero de configuración desde varias rutas
            InputStream is = null;
            try {
                File f1 = new File("integrador/config.properties");
                File f2 = new File("config.properties");
                if (f1.exists()) {
                    System.out.println("Cargando configuración desde: " + f1.getAbsolutePath());
                    is = new FileInputStream(f1);
                } else if (f2.exists()) {
                    System.out.println("Cargando configuración desde: " + f2.getAbsolutePath());
                    is = new FileInputStream(f2);
                } else {
                    System.out.println("No se encontró config en disco; intentando classpath '/config.properties'");
                    is = Conexion.class.getClassLoader().getResourceAsStream("config.properties");
                    if (is == null) {
                        // intentar nombre alternativo en classpath
                        is = Conexion.class.getClassLoader().getResourceAsStream("/config.properties");
                    }
                }

                if (is != null) {
                    properties.load(is);
                    System.out.println("Configuración de BD cargada desde fichero/recursos");
                } else {
                    System.out.println("No se halló ningún archivo de configuración (integrador/config.properties, config.properties o classpath).");
                }
            } catch (FileNotFoundException e) {
                System.out.println("Archivo de configuración no encontrado: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error leyendo archivo de configuración: " + e.getMessage());
            } finally {
                if (is != null) {
                    try { is.close(); } catch (IOException ex) { /* ignora */ }
                }
            }
        }

        // 3) Determinar las claves posibles y elegir valores (orden de preferencia)
        String url = firstNonNull(
                properties.getProperty("db.url"),
                properties.getProperty("URL"),
                properties.getProperty("url"),
                properties.getProperty("jdbc.url"),
                System.getProperty("URL"),
                System.getProperty("url")
        );

        String username = firstNonNull(
                properties.getProperty("db.user"),
                properties.getProperty("USERNAME"),
                properties.getProperty("username"),
                properties.getProperty("db.username"),
                System.getProperty("USERNAME"),
                System.getProperty("username")
        );

        String password = firstNonNull(
                properties.getProperty("db.password"),
                properties.getProperty("PASSWORD"),
                properties.getProperty("password"),
                properties.getProperty("db.password"),
                System.getProperty("PASSWORD"),
                System.getProperty("password")
        );

        if (url == null || username == null) {
            System.out.println("Faltan credenciales de BD. Asegúrate de definir db.url y db.user en config.properties o System properties.");
            return null;
        }

        // Normalizar URL: asegurar parámetros comunes para evitar errores de conexión/SSL/autenticación
        url = normalizeJdbcUrl(url);

        // Cargar driver y conectar
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC no encontrado: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        try {
            System.out.println("Intentando conectar a: " + url + " con usuario " + username);
            connection = DriverManager.getConnection(url, username, password != null ? password : "");
            System.out.println("Conexión establecida correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState() + "  ErrorCode: " + e.getErrorCode());
            // Mensaje frecuente: plugin de autenticación (caching_sha2_password / sha256) o parámetros SSL/clave pública
            System.out.println("Sugerencia: si el error es 'Authentication plugin' o 'UnableToConnect', verifica:");
            System.out.println(" - Que la URL JDBC incluya allowPublicKeyRetrieval=true y useSSL=false si es un entorno local");
            System.out.println(" - O que el usuario de la BD esté usando mysql_native_password, o actualiza el conector MySQL");
            e.printStackTrace();
            connection = null;
        }

        return connection;
    }

    // Nuevo método público para probar la conexión (devuelve true si se conecta correctamente)
    public static boolean testConnection() {
        try {
            Connection c = getConnectionStatic();
            if (c == null) {
                System.out.println("Resultado: conexión es null. Revisa config.properties y credenciales.");
                return false;
            }
            if (c.isClosed()) {
                System.out.println("Resultado: la conexión está cerrada.");
                return false;
            }
            System.out.println("Resultado: Conexión establecida correctamente.");
            // Mostrar URL y usuario (sin contraseña) para diagnóstico
            try {
                String url = firstNonNull(
                        System.getProperty("db.url"),
                        System.getProperty("URL"),
                        System.getProperty("url")
                );
                String user = firstNonNull(
                        System.getProperty("db.user"),
                        System.getProperty("USERNAME"),
                        System.getProperty("username")
                );
                if (url != null) System.out.println("URL utilizada: " + normalizeJdbcUrl(url));
                if (user != null) System.out.println("Usuario: " + user);
            } catch (Exception ex) {
                // no bloquear la prueba por problemas de diagnóstico
            }
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException durante testConnection: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error inesperado durante testConnection: " + e.getMessage());
            return false;
        }
    }

    // Main para ejecutar la prueba desde la línea de comandos:
    public static void main(String[] args) {
        System.out.println("Iniciando prueba de conexión a la BD...");
        boolean ok = testConnection();
        if (!ok) {
            System.out.println("La prueba falló. Verifica las siguientes cosas:");
            System.out.println(" - Archivo config.properties (URL/USERNAME/PASSWORD) en el working dir o src/main/resources");
            System.out.println(" - Que el servidor MySQL esté en ejecución y accesible");
            System.out.println(" - Que el usuario y contraseña sean correctos y tengan permisos");
            System.out.println("Ejecuta el programa con: java -cp <classpath> com.proyecto.integrador.model.Conexion");
        } else {
            System.out.println("Prueba completada con éxito.");
        }
    }

    // Añade parámetros necesarios a la URL JDBC si no están presentes
    private static String normalizeJdbcUrl(String url) {
        if (url == null) return null;
        String lower = url.toLowerCase();
        // añadir serverTimezone si falta
        if (!lower.contains("servertimezone=")) {
            url = appendUrlParam(url, "serverTimezone", "UTC");
        }
        // añadir useSSL si falta
        if (!lower.contains("usessl=")) {
            url = appendUrlParam(url, "useSSL", "false");
        }
        // añadir allowPublicKeyRetrieval si falta (útil para autenticación caché/public key)
        if (!lower.contains("allowpublickeyretrieval=")) {
            url = appendUrlParam(url, "allowPublicKeyRetrieval", "true");
        }
        return url;
    }

    private static String appendUrlParam(String url, String key, String value) {
        if (url.contains("?")) {
            if (url.endsWith("&") || url.endsWith("?")) return url + key + "=" + value;
            else return url + "&" + key + "=" + value;
        } else {
            return url + "?" + key + "=" + value;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    // Helper: devuelve el primer valor no nulo y no vacío
    private static String firstNonNull(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return null;
    }
}
