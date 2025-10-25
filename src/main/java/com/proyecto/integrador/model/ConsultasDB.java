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
}
