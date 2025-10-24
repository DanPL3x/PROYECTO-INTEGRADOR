package com.proyecto.integrador.view.ModuloLugarDenuncia;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ModuloLugarDenuncia extends JFrame {
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    final private Font BtFont = new Font("Comic Sans MS", Font.BOLD, 14);
    final private Font TaFont = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final int ancho = 1200;
    private static final int alto = 550;
    JLabel etiqueta1, etiqueta2;
    JTextArea chatAdmin;
    JButton consultarLugarDenuncia, editarLugarDenuncia, crearLugarDenuncia, eliminarLugarDenuncia, RegresarLugDen;

    public ModuloLugarDenuncia() {

        setTitle("Módulo Lugar Denuncia");
        setSize(ancho, alto);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        contenido1();
        contenido2();
        contenido3();
    }

    public void contenido1() {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 10, 5, 10));
        etiqueta1 = new JLabel("Bienvenido al Módulo del lugar denuncia", SwingConstants.CENTER);
        etiqueta1.setFont(mainFont);

        chatAdmin = new JTextArea(10, 1);
        chatAdmin.setFont(TaFont);
        chatAdmin.setLineWrap(true); // Que haga salto de línea automático
        chatAdmin.setWrapStyleWord(true); // Que lo haga al final de las palabras
        chatAdmin.setEditable(false);
        JScrollPane scrollChatAdmin = new JScrollPane(chatAdmin);
        panel.add(etiqueta1, BorderLayout.NORTH);
        panel.add(scrollChatAdmin, BorderLayout.CENTER);

        add(panel, BorderLayout.NORTH);

    }

    public void contenido2() {

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(2, 2, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(5, 30, 30, 30));

        consultarLugarDenuncia = new JButton("Lista de lugares Denuncia");
        consultarLugarDenuncia.setFont(BtFont);
        editarLugarDenuncia = new JButton("Editar Lugar Denuncia");
        editarLugarDenuncia.setFont(BtFont);
        crearLugarDenuncia = new JButton("Crear Lugar Denuncia");
        crearLugarDenuncia.setFont(BtFont);
        eliminarLugarDenuncia = new JButton("Eliminar Lugar Denuncia");
        eliminarLugarDenuncia.setFont(BtFont);

        panel2.add(consultarLugarDenuncia);
        panel2.add(editarLugarDenuncia);
        panel2.add(crearLugarDenuncia);
        panel2.add(eliminarLugarDenuncia);
        add(panel2, BorderLayout.CENTER);

    }

    public void contenido3() {
        JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayout(0, 1, 10, 10));
        panel3.setBorder(BorderFactory.createEmptyBorder(5, 500, 30, 500));

        RegresarLugDen = new JButton("Regresar");
        RegresarLugDen.setFont(BtFont);
        panel3.add(RegresarLugDen);

        add(panel3, BorderLayout.SOUTH);
    }

    public void ListenerRegresarModLugDen(ActionListener parActionListener) {
        RegresarLugDen.addActionListener(parActionListener);
        RegresarLugDen.setActionCommand("RegresarLugDen");
    }

    public void ListenerConsultaModLugarDenuncia(ActionListener parActionListener) {
        consultarLugarDenuncia.addActionListener(parActionListener);
        consultarLugarDenuncia.setActionCommand("ConsultarModLugDen");
    }

    public void ListenerEditarModLugarDenuncia(ActionListener parActionListener) {
        editarLugarDenuncia.addActionListener(parActionListener);
        editarLugarDenuncia.setActionCommand("EditarModLugDen");
    }

    public void ListenerCrearModLugarDenuncia(ActionListener parActionListener) {
        crearLugarDenuncia.addActionListener(parActionListener);
        crearLugarDenuncia.setActionCommand("CrearModLugDen");
    }

    public void ListenerEliminarModLugarDenuncia(ActionListener parActionListener) {
        eliminarLugarDenuncia.addActionListener(parActionListener);
        eliminarLugarDenuncia.setActionCommand("EliminarModLugDen");
    }

    public void setChatAdmin(String parTexto){
        chatAdmin.setText(parTexto);
    }
}
