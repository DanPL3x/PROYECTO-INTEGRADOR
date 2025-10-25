package com.proyecto.integrador.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Tipo {
    private Connection conexion;

    public Tipo(Connection connection) {
        this.conexion = connection;
    }

    public String informarTipos() {
        StringBuilder resultado = new StringBuilder();
        try {
            String query = "SELECT id_tipo, tipo FROM Tipo";
            PreparedStatement stmt = conexion.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.append("ID: ").append(rs.getInt("id_tipo"))
                        .append(", Tipo: ").append(rs.getString("tipo"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al consultar tipos: " + e.getMessage();
        }
        return resultado.toString();
    }
}
