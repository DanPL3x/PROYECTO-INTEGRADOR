package com.proyecto.integrador.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class Denuncia {
    private Connection conexion;

    private int id_denuncia;
    private String fecha;
    private String hora;
    private String descripcion;
    private int fk_id_lugar; 
    private boolean var;

    public Denuncia(Connection parConexion) {
        this.conexion = parConexion;
    }

    public String informarDenunciaReciente() {

        StringBuilder resultado = new StringBuilder();

        String query = "SELECT den.fecha AS Fecha, den.hora AS Hora, den.descripcion as Descripción, ld.nombre as Lugar_denuncia, u.nombre as Ubicación " +
                "FROM denuncia den " +
                "JOIN lugar_denuncia ld ON ld.id_lugar = den.fk_id_lugar " +
                "JOIN ubicacion u on u.id_ubicacion = ld.fk_id_ubicacion " +
                "ORDER BY den.fecha DESC " +
                "LIMIT 1";

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
                resultado.append("------------------"); // misma cantidad que el ancho de columna
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

    public String informarDenunciaAdmin(){
        StringBuilder resultado = new StringBuilder();

        String query = "Select * from denuncia";

        try (Statement stmt = this.conexion.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnas = metaData.getColumnCount();

            // 1. Encabezados
            for (int i = 1; i <= columnas; i++) {
                //caracteres += metaData.getColumnLabel(i).length();
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

    public void EditarDenuncia(int parIdDen, String parFecha, String parHora, String parDescripcion, int parFkIdLugarDen){
        this.id_denuncia = parIdDen;
        this.fecha = parFecha;
        this.hora = parHora;
        this.descripcion = parDescripcion;
        this.fk_id_lugar = parFkIdLugarDen;

        try (PreparedStatement stmt = this.conexion.prepareStatement("UPDATE denuncia " + //
                                                                    "SET fecha = ?, " + //
                                                                    "hora = ?, " + //
                                                                    "descripcion = ?, " + //
                                                                    "fk_id_lugar = ? " + //
                                                                    "WHERE id_denuncia = ?")) {

            stmt.setString(1, this.fecha);
            stmt.setString(2, this.hora);
            stmt.setString(3, this.descripcion);
            stmt.setInt(4, this.fk_id_lugar);
            stmt.setInt(5, this.id_denuncia);
            //System.out.println(stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CrearDenuncia(int parIdDen, String parFecha, String parHora, String parDescripcion, int parFkIdLugarDen){
        this.id_denuncia = parIdDen;
        this.fecha = parFecha;
        this.hora = parHora;
        this.descripcion = parDescripcion;
        this.fk_id_lugar = parFkIdLugarDen;

        try (PreparedStatement stmt = this.conexion.prepareStatement("insert into denuncia (" + //
                                                                    "id_denuncia, " + //
                                                                    "fecha, " + //
                                                                    "hora, " + //
                                                                    "descripcion, " + //
                                                                    "fk_id_lugar) " + //
                                                                    "Values (?, ?, ?, ?, ?)")) {

            stmt.setInt(1, this.id_denuncia);
            stmt.setString(2, this.fecha);
            stmt.setString(3, this.hora);
            stmt.setString(4, this.descripcion);
            stmt.setInt(5, this.fk_id_lugar);
            //System.out.println(stmt);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void EliminarDenuncia(int parIdDen){
        this.id_denuncia = parIdDen;

        try (PreparedStatement stmt = this.conexion.prepareStatement("Delete from denuncia where id_denuncia = ?")){
            stmt.setInt(1, this.id_denuncia);
            stmt.executeUpdate();
            System.out.println(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
