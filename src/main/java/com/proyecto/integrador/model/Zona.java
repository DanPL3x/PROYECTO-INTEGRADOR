package com.proyecto.integrador.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Zona {
    private Connection conexion;

    public Zona(Connection connection) {
        this.conexion = connection;
    }

    public String informarZonas() {
        StringBuilder resultado = new StringBuilder();
        try {
            String query = "SELECT id_zona, zona, comuna_vereda, fk_id_tipo FROM Zona";
            PreparedStatement stmt = conexion.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.append("ID: ").append(rs.getInt("id_zona"))
                        .append(", Zona: ").append(rs.getString("zona"))
                        .append(", Comuna/Vereda: ").append(rs.getString("comuna_vereda"))
                        .append(", Tipo: ").append(rs.getInt("fk_id_tipo"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al consultar zonas: " + e.getMessage();
        }
        return resultado.toString();
    }

    // Métodos CRUD
    public void CrearZona(int id, String zona, String comuna, int idTipo) {
        try {
            String query = "INSERT INTO Zona (id_zona, zona, comuna_vereda, fk_id_tipo) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conexion.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setString(2, zona);
            stmt.setString(3, comuna);
            stmt.setInt(4, idTipo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
