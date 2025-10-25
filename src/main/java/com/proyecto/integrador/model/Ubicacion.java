package com.proyecto.integrador.model;

import java.sql.*;

public class Ubicacion {
    private Connection conexion;
    private int id_ubicacion;
    private String direccion;
    private int id_nivel;
    private int id_zona;
    private int id_punto_cardinal;

    public Ubicacion(Connection conexion) {
        this.conexion = conexion;
    }

    public String informarUbicacion() {
        StringBuilder resultado = new StringBuilder();
        String query =
                "SELECT u.direccion, pc.nombre as punto_cardinal, " +
                        "z.nombre as zona, nr.riesgo as nivel_riesgo " +
                        "FROM Ubicacion u " +
                        "JOIN PuntoCardinal pc ON u.id_punto_cardinal = pc.id_punto_cardinal " +
                        "JOIN Zona z ON u.id_zona = z.id_zona " +
                        "JOIN NivelRiesgo nr ON u.id_nivel = nr.id_nivel";

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnas = metaData.getColumnCount();

            // 1. Encabezados
            for (int i = 1; i <= columnas; i++) {
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

    public void CrearUbicacion(int id, String crearNombreUbi, String crearPuntoCardUbi, String direccion, int idNivel, int idZona, int idPuntoCard) {
        String sql = "INSERT INTO Ubicacion (id_ubicacion, direccion, id_nivel, id_zona, id_punto_cardinal) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, direccion);
            stmt.setInt(3, idNivel);
            stmt.setInt(4, idZona);
            stmt.setInt(5, idPuntoCard);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void EditarUbicacion(int parIdUbi, String parDireccion, int parIdNivel, int parIdZona, int parIdPuntoCard) {
        this.id_ubicacion = parIdUbi;
        this.direccion = parDireccion;
        this.id_nivel = parIdNivel;
        this.id_zona = parIdZona;
        this.id_punto_cardinal = parIdPuntoCard;

        try (PreparedStatement stmt = this.conexion.prepareStatement("UPDATE ubicacion " + //
                "SET direccion = ?, " + //
                "id_nivel = ?, " + //
                "id_zona = ?, " + //
                "id_punto_cardinal = ? " + //
                "WHERE id_ubicacion = ?")) {

            stmt.setString(1, this.direccion);
            stmt.setInt(2, this.id_nivel);
            stmt.setInt(3, this.id_zona);
            stmt.setInt(4, this.id_punto_cardinal);
            stmt.setInt(5, this.id_ubicacion);
            //System.out.println(stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void EliminarUbicacion(int parIdUbi) {
        this.id_ubicacion = parIdUbi;

        try (PreparedStatement stmt = this.conexion.prepareStatement("Delete from ubicacion where id_ubicacion = ?")) {
            stmt.setInt(1, this.id_ubicacion);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String informarPuntosCardinales() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'informarPuntosCardinales'");
    }
    public String informarUbicacionAdmin() {

        return null;
    }

    public void CrearUbicacion(int int1, String crearNombreUbi, int int2, int int3, int int4) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'CrearUbicacion'");
    }

}

