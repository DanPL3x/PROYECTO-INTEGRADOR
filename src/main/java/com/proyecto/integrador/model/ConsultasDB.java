package com.proyecto.integrador.model;

import java.sql.*;

public class ConsultasDB {
    private Connection conexion;

    public ConsultasDB(Connection conexion) {
        this.conexion = conexion;
    }

    public String consultarTabla(String nombreTabla) {
        StringBuilder resultado = new StringBuilder();
        String query = switch(nombreTabla) {
            case "Delitos" -> 
                "SELECT id_delito, tipo_delito FROM Delito";
            case "Denuncias" -> 
                "SELECT d.id_denuncia, d.fecha, d.hora, d.descripcion, l.nombre as lugar " +
                "FROM Denuncia d JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar";
            case "DenunciasporDelito" -> 
                "SELECT d.fecha, d.hora, dl.tipo_delito, d.descripcion " +
                "FROM Denuncia d " +
                "JOIN Denuncia_Delito dd ON d.id_denuncia = dd.id_denuncia " +
                "JOIN Delito dl ON dd.id_delito = dl.id_delito";
            case "NivelesdeRiesgo" -> 
                "SELECT id_nivel, riesgo FROM NivelRiesgo";
            case "PuntosCardinales" -> 
                "SELECT id_punto_cardinal, nombre FROM PuntoCardinal";
            case "Tipos" -> 
                "SELECT id_tipo, tipo FROM Tipo";
            case "Ubicaciones" -> 
                "SELECT u.direccion, pc.nombre as punto_cardinal, z.nombre as zona, nr.riesgo " +
                "FROM Ubicacion u " +
                "JOIN PuntoCardinal pc ON u.id_punto_cardinal = pc.id_punto_cardinal " +
                "JOIN Zona z ON u.id_zona = z.id_zona " +
                "JOIN NivelRiesgo nr ON u.id_nivel = nr.id_nivel";
            case "Zonas" -> 
                "SELECT z.nombre, z.comuna_vereda, t.tipo " +
                "FROM Zona z JOIN Tipo t ON z.id_tipo = t.id_tipo";
            default -> 
                throw new IllegalArgumentException("Tabla no reconocida: " + nombreTabla);
        };

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Headers
            for (int i = 1; i <= columnCount; i++) {
                resultado.append(String.format("%-25s", metaData.getColumnLabel(i)));
            }
            resultado.append("\n").append("-".repeat(columnCount * 25)).append("\n");
            
            // Data
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    resultado.append(String.format("%-25s", rs.getString(i)));
                }
                resultado.append("\n");
            }
            
        } catch (SQLException e) {
            return "Error consultando " + nombreTabla + ": " + e.getMessage();
        }
        
        return resultado.toString();
    }

    // Nuevas consultas rápidas pensadas para usuarios/turistas
    public String consultarQuick(String action) {
        StringBuilder resultado = new StringBuilder();
        String query = null;

        switch (action) {
            case "BarriosPeligrosos":
                // Zonas con mayor número de denuncias (top 10)
                query = "SELECT z.nombre AS Zona, nr.riesgo AS Nivel, COUNT(d.id_denuncia) AS Denuncias " +
                        "FROM Denuncia d " +
                        "JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar " +
                        "JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion " +
                        "JOIN Zona z ON u.id_zona = z.id_zona " +
                        "JOIN NivelRiesgo nr ON u.id_nivel = nr.id_nivel " +
                        "GROUP BY z.id_zona, nr.riesgo " +
                        "ORDER BY Denuncias DESC " +
                        "LIMIT 10";
                break;

            case "LugaresMasDenuncias":
                // Lugares (CAI/estación) con más denuncias
                query = "SELECT l.nombre AS Lugar, l.direccion AS Direccion, COUNT(d.id_denuncia) AS Denuncias " +
                        "FROM LugarDenuncias l " +
                        "LEFT JOIN Denuncia d ON l.id_lugar = d.id_lugar " +
                        "GROUP BY l.id_lugar " +
                        "ORDER BY Denuncias DESC " +
                        "LIMIT 10";
                break;

            case "DenunciasRecientesZona":
                // Denuncias recientes con zona asociada (últimas 20)
                query = "SELECT d.fecha AS Fecha, d.hora AS Hora, z.nombre AS Zona, d.descripcion AS Descripcion " +
                        "FROM Denuncia d " +
                        "JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar " +
                        "JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion " +
                        "JOIN Zona z ON u.id_zona = z.id_zona " +
                        "ORDER BY d.fecha DESC, d.hora DESC " +
                        "LIMIT 20";
                break;

            case "HotspotsPorNivel":
                // Conteo de denuncias por nivel de riesgo (útil para ver áreas de alto riesgo)
                query = "SELECT nr.riesgo AS NivelRiesgo, COUNT(d.id_denuncia) AS Denuncias " +
                        "FROM Denuncia d " +
                        "JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar " +
                        "JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion " +
                        "JOIN NivelRiesgo nr ON u.id_nivel = nr.id_nivel " +
                        "GROUP BY nr.id_nivel, nr.riesgo " +
                        "ORDER BY Denuncias DESC";
                break;

            default:
                return "Consulta rápida no reconocida: " + action;
        }

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                resultado.append(String.format("%-30s", md.getColumnLabel(i)));
            }
            resultado.append("\n").append("-".repeat(cols * 30)).append("\n");

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    resultado.append(String.format("%-30s", rs.getString(i)));
                }
                resultado.append("\n");
            }

        } catch (SQLException e) {
            return "Error ejecutando consulta rápida (" + action + "): " + e.getMessage();
        }

        return resultado.toString();
    }

    public String ejecutarConsultaFormateada(String sql) throws SQLException {
        StringBuilder result = new StringBuilder();
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            // Headers
            for (int i = 1; i <= cols; i++) {
                result.append(String.format("%-25s", md.getColumnLabel(i)));
            }
            result.append("\n");

            // Separator
            result.append("-".repeat(25 * cols)).append("\n");

            // Data
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    result.append(String.format("%-25s", rs.getString(i)));
                }
                result.append("\n");
            }
        }
        
        return result.toString();
    }

    // ========== STORED PROCEDURES ==========

    /**
     * Inserta una denuncia y la asocia con un delito usando el SP InsertarDenuncia
     * @param fecha Fecha de la denuncia
     * @param hora Hora de la denuncia
     * @param descripcion Descripción del incidente
     * @param idLugar ID del lugar donde ocurrió
     * @param idDelito ID del tipo de delito
     * @return ID de la denuncia insertada, o -1 si hay error
     */
    public int insertarDenunciaConDelito(Date fecha, Time hora, String descripcion, int idLugar, int idDelito) {
        String sql = "{CALL InsertarDenuncia(?, ?, ?, ?, ?)}";
        
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setDate(1, fecha);
            stmt.setTime(2, hora);
            stmt.setString(3, descripcion);
            stmt.setInt(4, idLugar);
            stmt.setInt(5, idDelito);
            
            stmt.execute();
            
            // Obtener el ID generado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
            // Si no se puede obtener con getGeneratedKeys, usar LAST_INSERT_ID()
            try (Statement st = conexion.createStatement();
                 ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID()")) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error en insertarDenunciaConDelito: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }

    /**
     * Consulta denuncias con información detallada usando el SP ConsultarDenuncias
     * @return ResultSet con las denuncias (debe cerrarse después de usar)
     */
    public ResultSet consultarDenunciasDetalladas() throws SQLException {
        String sql = "{CALL ConsultarDenuncias()}";
        CallableStatement stmt = conexion.prepareCall(sql);
        return stmt.executeQuery();
    }

    /**
     * Actualiza los datos de una denuncia existente usando el SP ActualizarDenuncia
     * @param idDenuncia ID de la denuncia a actualizar
     * @param fecha Nueva fecha
     * @param hora Nueva hora
     * @param descripcion Nueva descripción
     * @param idLugar Nuevo ID de lugar
     * @return true si se actualizó correctamente
     */
    public boolean actualizarDenuncia(int idDenuncia, Date fecha, Time hora, String descripcion, int idLugar) {
        String sql = "{CALL ActualizarDenuncia(?, ?, ?, ?, ?)}";
        
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setInt(1, idDenuncia);
            stmt.setDate(2, fecha);
            stmt.setTime(3, hora);
            stmt.setString(4, descripcion);
            stmt.setInt(5, idLugar);
            
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error en actualizarDenuncia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Consulta zonas con información completa usando el SP ConsultarZonas
     * @return ResultSet con las zonas (debe cerrarse después de usar)
     */
    public ResultSet consultarZonasDetalladas() throws SQLException {
        String sql = "{CALL ConsultarZonas()}";
        CallableStatement stmt = conexion.prepareCall(sql);
        return stmt.executeQuery();
    }

    /**
     * Consulta zonas filtradas por nivel de riesgo usando el SP ConsultarZonasPorRiesgo
     * @param idNivel ID del nivel de riesgo (1-3)
     * @return ResultSet con las zonas (debe cerrarse después de usar)
     */
    public ResultSet consultarZonasPorNivelRiesgo(int idNivel) throws SQLException {
        String sql = "{CALL ConsultarZonasPorRiesgo(?)}";
        CallableStatement stmt = conexion.prepareCall(sql);
        stmt.setInt(1, idNivel);
        return stmt.executeQuery();
    }

    /**
     * Registra una denuncia con validación del lugar usando el SP RegistrarDenunciaSegura
     * @param fecha Fecha de la denuncia
     * @param hora Hora de la denuncia
     * @param descripcion Descripción del incidente
     * @param idLugar ID del lugar donde ocurrió
     * @return Mensaje de resultado (éxito o error)
     */
    public String registrarDenunciaSegura(Date fecha, Time hora, String descripcion, int idLugar) {
        String sql = "{CALL RegistrarDenunciaSegura(?, ?, ?, ?)}";
        
        try (CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setDate(1, fecha);
            stmt.setTime(2, hora);
            stmt.setString(3, descripcion);
            stmt.setInt(4, idLugar);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("mensaje");
            }
            
        } catch (SQLException e) {
            return "⚠️ Error: " + e.getMessage();
        }
        
        return "⚠️ Error desconocido al registrar denuncia";
    }

    /**
     * Obtiene estadísticas de delitos más frecuentes usando el SP DelitosMasFrecuentes
     * @return ResultSet con delitos y frecuencias (debe cerrarse después de usar)
     */
    public ResultSet obtenerDelitosMasFrecuentes() throws SQLException {
        String sql = "{CALL DelitosMasFrecuentes()}";
        CallableStatement stmt = conexion.prepareCall(sql);
        return stmt.executeQuery();
    }

    /**
     * Consulta barrios filtrados por nivel de riesgo usando el SP ConsultarBarriosPorRiesgo
     * @param nivelRiesgo Nivel de riesgo (1-3)
     * @return ResultSet con los barrios (debe cerrarse después de usar)
     */
    public ResultSet consultarBarriosPorRiesgo(int nivelRiesgo) throws SQLException {
        String sql = "{CALL ConsultarBarriosPorRiesgo(?)}";
        CallableStatement stmt = conexion.prepareCall(sql);
        stmt.setInt(1, nivelRiesgo);
        return stmt.executeQuery();
    }

    /**
     * Versión formateada para consola de DelitosMasFrecuentes
     * @return String con el reporte formateado
     */
    public String reporteDelitosMasFrecuentes() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("\n═══════════════════════════════════════════════════\n");
        resultado.append("       DELITOS MÁS FRECUENTES - REPORTE\n");
        resultado.append("═══════════════════════════════════════════════════\n\n");
        
        try (ResultSet rs = obtenerDelitosMasFrecuentes()) {
            resultado.append(String.format("%-40s %-15s\n", "TIPO DE DELITO", "FRECUENCIA"));
            resultado.append("-".repeat(55)).append("\n");
            
            while (rs.next()) {
                String delito = rs.getString("delito");
                int frecuencia = rs.getInt("frecuencia");
                resultado.append(String.format("%-40s %-15d\n", delito, frecuencia));
            }
            
        } catch (SQLException e) {
            return "⚠️ Error al generar reporte: " + e.getMessage();
        }
        
        resultado.append("\n═══════════════════════════════════════════════════\n");
        return resultado.toString();
    }
}
