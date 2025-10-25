package com.proyecto.integrador.model;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class LugarDenuncia {
    private Connection conexion;

    private int id_lugar;
    private String nombre;
    private int telefono;
    private String direccion;
    private int fk_id_ubi;

    public LugarDenuncia(Connection parConexion) {
        this.conexion = parConexion;
    }

    public String informarLugar() {

        StringBuilder resultado = new StringBuilder();

        String query = "Select ld.nombre as Nombre, ld.telefono as Teléfono, ld.direccion as Dirección, u.nombre as Ubicación "+
                        "from lugar_denuncia ld "+
                        "join ubicacion u on ld.fk_id_ubicacion = u.id_ubicacion";

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

     public String informarLugarDenunciaAdmin() {

        StringBuilder resultado = new StringBuilder();

        String query = "Select * from lugar_denuncia";

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

    public void EditarLugarDenuncia(int parIdLugDen, String parNombre, int parTelefono, String parDireccion, int parFkIdUbi){
        this.id_lugar = parIdLugDen;
        this.nombre = parNombre;
        this.telefono = parTelefono;
        this.direccion = parDireccion;
        this.fk_id_ubi = parFkIdUbi;

        try (PreparedStatement stmt = this.conexion.prepareStatement("UPDATE lugar_denuncia " + //
                                                                    "SET nombre = ?, " + //
                                                                    "telefono = ?, " + //
                                                                    "direccion = ?, " + //
                                                                    "fk_id_ubicacion = ? " + //
                                                                    "WHERE id_lugar = ?")) {

            stmt.setString(1, this.nombre);
            stmt.setInt(2, this.telefono);
            stmt.setString(3, this.direccion);
            stmt.setInt(4, this.fk_id_ubi);
            stmt.setInt(5, this.id_lugar);
            //System.out.println(stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CrearLugarDenuncia(int parIdLugDen, String parNombre, int parTelefono, String parDireccion, int parFkIdUbi){
        this.id_lugar = parIdLugDen;
        this.nombre = parNombre;
        this.telefono = parTelefono;
        this.direccion = parDireccion;
        this.fk_id_ubi = parFkIdUbi;

        try (PreparedStatement stmt = this.conexion.prepareStatement("insert into lugar_denuncia (" + //
                                                                    "id_lugar, " + //
                                                                    "nombre, " + //
                                                                    "telefono, " + //
                                                                    "direccion, " + //
                                                                    "fk_id_ubicacion) " + //
                                                                    "Values (?, ?, ?, ?, ?)")) {

            stmt.setInt(1, this.id_lugar);
            stmt.setString(2, this.nombre);
            stmt.setInt(3, this.telefono);
            stmt.setString(4, this.direccion);
            stmt.setInt(5, this.fk_id_ubi);
            //System.out.println(stmt);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void EliminarLugarDenuncia(int parIdLugDen){
        this.id_lugar = parIdLugDen;

        try (PreparedStatement stmt = this.conexion.prepareStatement("Delete from lugar_denuncia where id_lugar = ?")){
            stmt.setInt(1, this.id_lugar);
            stmt.executeUpdate();
            System.out.println(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
