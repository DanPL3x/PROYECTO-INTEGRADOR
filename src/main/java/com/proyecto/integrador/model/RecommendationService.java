package com.proyecto.integrador.model;

import java.sql.*;

/**
 * Servicio para generar recomendaciones de seguridad basadas en el nivel de riesgo
 * y proporcionar información detallada sobre zonas
 */
public class RecommendationService {
    private Connection conexion;

    public RecommendationService(Connection conexion) {
        this.conexion = conexion;
    }

    /**
     * Obtiene recomendaciones de seguridad según el nivel de riesgo
     */
    public String getRecomendacionesPorNivel(String nivelRiesgo) {
        return switch(nivelRiesgo.toLowerCase()) {
            case "alto", "alta" -> """
                ⚠️ NIVEL DE RIESGO ALTO
                
                Recomendaciones de seguridad:
                • Evite transitar por esta zona en horas nocturnas (después de las 7 PM)
                • No porte objetos de valor visibles (joyas, celulares costosos, laptops)
                • Manténgase en zonas iluminadas y concurridas
                • Prefiera transitar acompañado(a)
                • Tenga las líneas de emergencia a la mano:
                  - Policía Nacional: 123
                  - CAI Móvil: 112
                  - Línea Única de Emergencias: 123
                • Considere usar rutas alternativas más seguras
                • Evite distracciones con el celular mientras camina
                • Conozca la ubicación de los CAI cercanos
                """;
            
            case "medio", "media", "moderado", "moderada" -> """
                ⚡ NIVEL DE RIESGO MEDIO
                
                Recomendaciones de seguridad:
                • Mantenga precaución al transitar por esta zona
                • Evite usar el celular mientras camina
                • Prefiera transitar en horas de luz del día
                • Esté atento a su entorno y personas sospechosas
                • No exhiba objetos de valor innecesariamente
                • Informe a alguien sobre su ruta y hora estimada de llegada
                • Guarde copias digitales de documentos importantes
                • Conozca las rutas de salida y lugares seguros cercanos
                """;
            
            case "bajo", "baja" -> """
                ✅ NIVEL DE RIESGO BAJO
                
                Esta zona presenta bajo índice de criminalidad:
                • Zona relativamente segura para transitar
                • Mantenga las precauciones básicas habituales
                • Reporte cualquier actividad sospechosa a las autoridades
                • Evite transitar solo(a) en horas muy tardías
                • Mantenga sus pertenencias vigiladas en lugares públicos
                • Sea consciente de su entorno
                """;
            
            default -> """
                ℹ️ INFORMACIÓN NO DISPONIBLE
                
                No hay suficiente información sobre el nivel de riesgo en esta zona.
                Se recomienda:
                • Mantener precauciones generales de seguridad
                • Consultar con autoridades locales
                • Reportar incidentes para mejorar la base de datos
                """;
        };
    }

    /**
     * Busca información detallada de una zona específica
     */
    public ZonaInfo buscarZona(String nombreZona) {
        ZonaInfo info = new ZonaInfo();
        
        String query = """
            SELECT 
                z.nombre AS zona,
                z.comuna_vereda,
                nr.riesgo AS nivel_riesgo,
                t.tipo AS tipo_zona,
                COUNT(DISTINCT d.id_denuncia) AS total_denuncias,
                COUNT(DISTINCT l.id_lugar) AS lugares_denuncia
            FROM Zona z
            LEFT JOIN Tipo t ON z.id_tipo = t.id_tipo
            LEFT JOIN Ubicacion u ON z.id_zona = u.id_zona
            LEFT JOIN NivelRiesgo nr ON u.id_nivel = nr.id_nivel
            LEFT JOIN LugarDenuncias l ON u.id_ubicacion = l.id_ubicacion
            LEFT JOIN Denuncia d ON l.id_lugar = d.id_lugar
            WHERE z.nombre LIKE ?
            GROUP BY z.id_zona, nr.id_nivel
            LIMIT 1
            """;

        try (PreparedStatement stmt = conexion.prepareStatement(query)) {
            stmt.setString(1, "%" + nombreZona + "%");
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                info.nombre = rs.getString("zona");
                info.comunaVereda = rs.getString("comuna_vereda");
                info.nivelRiesgo = rs.getString("nivel_riesgo");
                info.tipoZona = rs.getString("tipo_zona");
                info.totalDenuncias = rs.getInt("total_denuncias");
                info.lugaresDenuncia = rs.getInt("lugares_denuncia");
                info.encontrada = true;
                
                // Obtener delitos más comunes en esta zona
                info.delitosComunes = obtenerDelitosComunes(info.nombre);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar zona: " + e.getMessage());
        }
        
        return info;
    }

    /**
     * Obtiene los delitos más comunes en una zona específica
     */
    private String obtenerDelitosComunes(String nombreZona) {
        StringBuilder resultado = new StringBuilder();
        
        String query = """
            SELECT 
                dl.tipo_delito,
                COUNT(*) AS cantidad
            FROM Denuncia d
            JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar
            JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion
            JOIN Zona z ON u.id_zona = z.id_zona
            JOIN Denuncia_Delito dd ON d.id_denuncia = dd.id_denuncia
            JOIN Delito dl ON dd.id_delito = dl.id_delito
            WHERE z.nombre = ?
            GROUP BY dl.tipo_delito
            ORDER BY cantidad DESC
            LIMIT 5
            """;

        try (PreparedStatement stmt = conexion.prepareStatement(query)) {
            stmt.setString(1, nombreZona);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                resultado.append("• ")
                         .append(rs.getString("tipo_delito"))
                         .append(" (")
                         .append(rs.getInt("cantidad"))
                         .append(" casos)\n");
            }
            
            if (resultado.length() == 0) {
                return "No hay datos de delitos registrados en esta zona";
            }
            
        } catch (SQLException e) {
            return "Error al obtener delitos: " + e.getMessage();
        }
        
        return resultado.toString();
    }

    /**
     * Obtiene lugares seguros para denunciar cerca de una zona
     */
    public String obtenerLugaresParaDenunciar(String nombreZona) {
        StringBuilder resultado = new StringBuilder();
        
        String query = """
            SELECT DISTINCT
                l.nombre,
                l.direccion,
                l.telefono
            FROM LugarDenuncias l
            JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion
            JOIN Zona z ON u.id_zona = z.id_zona
            WHERE z.nombre LIKE ?
            ORDER BY l.nombre
            LIMIT 5
            """;

        try (PreparedStatement stmt = conexion.prepareStatement(query)) {
            stmt.setString(1, "%" + nombreZona + "%");
            ResultSet rs = stmt.executeQuery();
            
            resultado.append("📍 Lugares para denunciar cerca:\n\n");
            int count = 0;
            
            while (rs.next()) {
                count++;
                resultado.append(count).append(". ")
                         .append(rs.getString("nombre"))
                         .append("\n   📧 ").append(rs.getString("direccion"))
                         .append("\n   ☎️  ").append(rs.getString("telefono"))
                         .append("\n\n");
            }
            
            if (count == 0) {
                return "No hay lugares de denuncia registrados en esta zona.\n" +
                       "Línea Nacional: 123 (Emergencias)";
            }
            
        } catch (SQLException e) {
            return "Error al obtener lugares: " + e.getMessage();
        }
        
        return resultado.toString();
    }

    /**
     * Genera un reporte completo de seguridad para una zona
     */
    public String generarReporteCompleto(String nombreZona) {
        ZonaInfo info = buscarZona(nombreZona);
        
        if (!info.encontrada) {
            return "❌ Zona no encontrada: " + nombreZona + "\n\n" +
                   "Sugerencias:\n" +
                   "• Verifique la ortografía del nombre\n" +
                   "• Intente con palabras clave (ej: 'San Antonio', 'Aguablanca')\n" +
                   "• Consulte la lista de zonas disponibles";
        }

        StringBuilder reporte = new StringBuilder();
        reporte.append("═══════════════════════════════════════════════\n");
        reporte.append("   REPORTE DE SEGURIDAD - ").append(info.nombre.toUpperCase()).append("\n");
        reporte.append("═══════════════════════════════════════════════\n\n");
        
        reporte.append("📍 Ubicación: ").append(info.nombre).append("\n");
        reporte.append("🏘️  Comuna/Vereda: ").append(info.comunaVereda != null ? info.comunaVereda : "N/A").append("\n");
        reporte.append("🏙️  Tipo: ").append(info.tipoZona != null ? info.tipoZona : "N/A").append("\n");
        reporte.append("⚠️  Nivel de Riesgo: ").append(info.nivelRiesgo != null ? info.nivelRiesgo.toUpperCase() : "NO DISPONIBLE").append("\n");
        reporte.append("📊 Total Denuncias: ").append(info.totalDenuncias).append("\n");
        reporte.append("🏢 Lugares de Denuncia: ").append(info.lugaresDenuncia).append("\n\n");
        
        reporte.append("───────────────────────────────────────────────\n");
        reporte.append("🔴 DELITOS MÁS FRECUENTES:\n");
        reporte.append("───────────────────────────────────────────────\n");
        reporte.append(info.delitosComunes).append("\n");
        
        reporte.append("───────────────────────────────────────────────\n");
        reporte.append(getRecomendacionesPorNivel(info.nivelRiesgo != null ? info.nivelRiesgo : ""));
        reporte.append("\n───────────────────────────────────────────────\n\n");
        
        reporte.append(obtenerLugaresParaDenunciar(nombreZona));
        
        return reporte.toString();
    }

    /**
     * Clase interna para almacenar información de una zona
     */
    public static class ZonaInfo {
        public boolean encontrada = false;
        public String nombre;
        public String comunaVereda;
        public String nivelRiesgo;
        public String tipoZona;
        public int totalDenuncias;
        public int lugaresDenuncia;
        public String delitosComunes;
    }
}
