package com.proyecto.integrador.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class VistaAdmin extends JFrame{
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    private static final int ancho = 900;
    private static final int alto = 300;
    JLabel etiqueta1, etiqueta2, lbUser, lbPass;
    JButton ubicacion, denuncia, lugarDenuncia, RegresarAdmin;

    public VistaAdmin(){

        setTitle("Chat_Seguridad_Cali - VISTA ADMINISTRADOR");
        setSize(ancho, alto);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        contenido1();
        contenido2();
        contenido3();
    }

    public void contenido1() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 30, 5, 30));

        etiqueta1 = new JLabel("Bienvenido", SwingConstants.CENTER);
        etiqueta1.setFont(mainFont);
        etiqueta2 = new JLabel("¿Qué acción desea realizar?", SwingConstants.CENTER);
        etiqueta2.setFont(mainFont);
        
        panel.add(etiqueta1);
        panel.add(etiqueta2);
        add(panel, BorderLayout.NORTH);
    }

    public void contenido2() {
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 3, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(10, 30, 5, 30));

        ubicacion = new JButton("Ubiaciones");
        ubicacion.setFont(mainFont);
        denuncia = new JButton("Denuncias");
        denuncia.setFont(mainFont);
        lugarDenuncia = new JButton("Lugares para denunciar");
        lugarDenuncia.setFont(mainFont);
        
        panel2.add(ubicacion);
        panel2.add(denuncia);
        panel2.add(lugarDenuncia);
        add(panel2, BorderLayout.CENTER);
    }

    public void contenido3() {
        JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayout(0, 1, 10, 10));
        panel3.setBorder(BorderFactory.createEmptyBorder(10, 100, 30, 100));

        RegresarAdmin = new JButton("Regresar");
        RegresarAdmin.setFont(mainFont);
        panel3.add(RegresarAdmin);

        add(panel3, BorderLayout.SOUTH);
    }

    public void ListenerRegresarAdmin(ActionListener parActionListener) {
        RegresarAdmin.addActionListener(parActionListener);
        RegresarAdmin.setActionCommand("RegresarAdmin");
    }

    public void ListenerModuloUbicacion(ActionListener parActionListener) {
        ubicacion.addActionListener(parActionListener);
        ubicacion.setActionCommand("ModuloUbicacion");
    }

    public void ListenerModuloDenuncia(ActionListener parActionListener) {
        denuncia.addActionListener(parActionListener);
        denuncia.setActionCommand("ModuloDenuncia");
    }

    public void ListenerModuloLugarDenuncia(ActionListener parActionListener) {
        lugarDenuncia.addActionListener(parActionListener);
        lugarDenuncia.setActionCommand("ModuloLugarDenuncia");
    }

}
