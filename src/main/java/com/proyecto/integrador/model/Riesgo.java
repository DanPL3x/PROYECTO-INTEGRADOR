package com.proyecto.integrador.model;
public class Riesgo {
    public Riesgo(){}

    private int id_nivel_riesgo;
    private int riesgo;
    String query;

    public String informarNivelRiesgo(){
        this.query = "Select riesgo from riesgo";
        return query;
    }

    public String registrarRiesgo(){
        this.query = "insert into riesgo(id_nivel_riesgo, riesgo) values(5, 5)";
        return query;
    }
}
