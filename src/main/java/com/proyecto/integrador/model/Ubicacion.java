package com.proyecto.integrador.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Ubicacion {

    private Connection conexion;
    private int id_ubicacion;
    private String nombre;
    private String puntoCardinal;
    private String tipo;
    private int comuna;
    private int fk_id_zona;
    private int fk_id_nivel;

    private boolean var;

    public Ubicacion(Connection parConexion) {
        this.conexion = parConexion;
    }

    public String informarUbicacion() {

        StringBuilder resultado = new StringBuilder();

        String query = "Select u.nombre as Nombre, u.punto_card as Punto_Cardinal, u.tipo as Tipo, u.comuna as Comuna, z.zona as Zona, r.riesgo as Nivel_de_riesgo "
                +
                "from ubicacion u " +
                "join zona z on u.fk_id_zona = z.id_zona " +
                "join riesgo r on u.fk_id_nivel = r.id_nivel_riesgo";

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

    public String informarUbicacionAdmin(){
        StringBuilder resultado = new StringBuilder();

        String query = "Select * from ubicacion";

        try (Statement stmt = this.conexion.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnas = metaData.getColumnCount();

            // 1. Encabezados
            for (int i = 1; i <= columnas; i++) {
                //caracteres += metaData.getColumnLabel(i).length();
                String colName = metaData.getColumnLabel(i);
                resultado.append(String.format("%-15s\t", colName)); // ancho fijo para alinear
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
                    resultado.append(String.format("%-15s\t", valor));
                }
                resultado.append("\n");
            }

        } catch (SQLException e) {
            resultado.append("Error al obtener la información: ").append(e.getMessage());
        }

        return resultado.toString();
    }

    public void EditarUbicacion(int parIdUbi, String parNombre, String parPuntoCard, String parTipo, int parComuna, int parFkIdZona, int parFkIdNivel){
        this.id_ubicacion = parIdUbi;
        this.nombre = parNombre;
        this.puntoCardinal = parPuntoCard;
        this.tipo = parTipo;
        this.comuna = parComuna;
        this.fk_id_zona = parFkIdZona;
        this.fk_id_nivel = parFkIdNivel;

        try (PreparedStatement stmt = this.conexion.prepareStatement("UPDATE ubicacion " + //
                                                                    "SET nombre = ?, " + //
                                                                    "punto_card = ?, " + //
                                                                    "tipo = ?, " + //
                                                                    "comuna = ?, " + //
                                                                    "fk_id_zona = ?, " + //
                                                                    "fk_id_nivel = ? " + //
                                                                    "WHERE id_ubicacion = ?")) {

            stmt.setString(1, this.nombre);
            stmt.setString(2, this.puntoCardinal);
            stmt.setString(3, this.tipo);
            stmt.setInt(4, this.comuna);
            stmt.setInt(5, this.fk_id_zona);
            stmt.setInt(6, this.fk_id_nivel);
            stmt.setInt(7, this.id_ubicacion);
            //System.out.println(stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void CrearUbicacion(int parIdUbi, String parNombre, String parPuntoCard, String parTipo, int parComuna, int parFkIdZona, int parFkIdNivel){
        this.id_ubicacion = parIdUbi;
        this.nombre = parNombre;
        this.puntoCardinal = parPuntoCard;
        this.tipo = parTipo;
        this.comuna = parComuna;
        this.fk_id_zona = parFkIdZona;
        this.fk_id_nivel = parFkIdNivel;

        try (PreparedStatement stmt = this.conexion.prepareStatement("insert into ubicacion (" + //
                                                                    "id_ubicacion, " + //
                                                                    "nombre, " + //
                                                                    "punto_card, " + //
                                                                    "tipo, " + //
                                                                    "comuna, " + //
                                                                    "fk_id_zona, " + //
                                                                    "fk_id_nivel) " + //
                                                                    "Values (?, ?, ?, ?, ?, ?, ?)")) {

            stmt.setInt(1, this.id_ubicacion);
            stmt.setString(2, this.nombre);
            stmt.setString(3, this.puntoCardinal);
            stmt.setString(4, this.tipo);
            stmt.setInt(5, this.comuna);
            stmt.setInt(6, this.fk_id_zona);
            stmt.setInt(7, this.fk_id_nivel);
            //System.out.println(stmt);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void EliminarUbicacion(int parIdUbi){
        this.id_ubicacion = parIdUbi;

        try (PreparedStatement stmt = this.conexion.prepareStatement("Delete from ubicacion where id_ubicacion = ?")){
            stmt.setInt(1, this.id_ubicacion);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
