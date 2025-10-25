package com.proyecto.integrador.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class User {
    private String user;
    private String pass;
    private Connection conexion;

    private int caracteres;

    public User(Connection parConexion) {
        this.conexion = parConexion;
    }

    public boolean userAutenticacion(String parUsuario, String parPass) {
        this.user = parUsuario;
        this.pass = parPass;
        boolean autenticado = false;

        try (PreparedStatement stmt = this.conexion.prepareStatement("SELECT * FROM usuario WHERE usuario = ? AND contraseña = ?")) {

            stmt.setString(1, this.user);
            stmt.setString(2, this.pass);
            //System.out.println(stmt);
            ResultSet rs = stmt.executeQuery();
            autenticado = rs.next(); // si hay un resultado, se autenticó

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return autenticado;
    }

}
