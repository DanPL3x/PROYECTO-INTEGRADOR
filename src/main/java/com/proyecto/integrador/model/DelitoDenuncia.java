package com.proyecto.integrador.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DelitoDenuncia {
    private Connection conexion;

    private int caracteres;

    public DelitoDenuncia(Connection parConexion) {
        this.conexion = parConexion;
    }

    public String informarDelitoComun() {

        StringBuilder resultado = new StringBuilder();

        String query = "SELECT d.tipo_delito AS Delito, den.descripcion AS Descripción, u.nombre as Ubicación " +
                "FROM delito_denuncia dd " +
                "JOIN delito d ON dd.fk_id_delito = d.id_delito " +
                "JOIN denuncia den ON dd.fk_id_denuncia = den.id_denuncia " +
                "JOIN lugar_denuncia ld on ld.id_lugar = den.fk_id_lugar "+
                "JOIN ubicacion u on u.id_ubicacion = ld.fk_id_ubicacion "+
                "WHERE dd.fk_id_delito = ( " +
                "SELECT fk_id_delito " +
                "FROM delito_denuncia " +
                "GROUP BY fk_id_delito " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT 1 " +
                ")";

        try (Statement stmt = this.conexion.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnas = metaData.getColumnCount();

            // 1. Encabezados
            for (int i = 1; i <= columnas; i++) {
                // caracteres += metaData.getColumnLabel(i).length();
                String colName = metaData.getColumnLabel(i);
                resultado.append(String.format("%-20s\t", colName)); // ancho fijo para alinear
            }
            resultado.append("\n");

            // 2. Línea separadora
            for (int i = 1; i <= columnas; i++) {
                resultado.append("--------------------"); // misma cantidad que el ancho de columna
            }
            resultado.append("\n");

            // 3. Filas con datos
            while (rs.next()) {
                for (int i = 1; i <= columnas; i++) {
                    String valor = rs.getString(i);
                    resultado.append(String.format("%-20s\t", valor));
                }
                resultado.append("\n");
            }

        } catch (SQLException e) {
            resultado.append("Error al obtener la información: ").append(e.getMessage());
        }

        return resultado.toString();
    }
}
