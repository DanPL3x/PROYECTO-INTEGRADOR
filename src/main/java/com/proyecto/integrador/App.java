package com.proyecto.integrador;

import java.io.*;
import java.util.Properties;
import javax.swing.*;

import com.proyecto.integrador.controller.ControladorProyecto;
import com.proyecto.integrador.model.Riesgo;
import com.proyecto.integrador.view.VistaProyecto;

public class App {
    public static void main(String[] args) {
        // Delegate to JavaFX launcher which also carga config.properties
        FXLauncher.main(args);
    }
}
