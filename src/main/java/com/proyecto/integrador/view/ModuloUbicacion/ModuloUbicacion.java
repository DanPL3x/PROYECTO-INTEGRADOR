package com.proyecto.integrador.view.ModuloUbicacion;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ModuloUbicacion extends JFrame {
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    final private Font BtFont = new Font("Comic Sans MS", Font.BOLD, 14);
    final private Font TaFont = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final int ancho = 1200;
    private static final int alto = 550;
    JLabel etiqueta1, etiqueta2;
    JTextArea chatAdmin;
    JButton consultarUbicacion, editarUbicacion, crearUbicacion, eliminarUbicacion, RegresarUbi;

    public ModuloUbicacion() {

        setTitle("Módulo Ubicación");
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
        etiqueta1 = new JLabel("Bienvenido al Módulo de ubicación", SwingConstants.CENTER);
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

        consultarUbicacion = new JButton("Lista de Ubicaciones");
        consultarUbicacion.setFont(BtFont);
        editarUbicacion = new JButton("Editar Ubicación");
        editarUbicacion.setFont(BtFont);
        crearUbicacion = new JButton("Crear Ubicación");
        crearUbicacion.setFont(BtFont);
        eliminarUbicacion = new JButton("Eliminar Ubicación");
        eliminarUbicacion.setFont(BtFont);

        panel2.add(consultarUbicacion);
        panel2.add(editarUbicacion);
        panel2.add(crearUbicacion);
        panel2.add(eliminarUbicacion);
        add(panel2, BorderLayout.CENTER);

    }

    public void contenido3() {
        JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayout(0, 1, 10, 10));
        panel3.setBorder(BorderFactory.createEmptyBorder(5, 500, 30, 500));

        RegresarUbi = new JButton("Regresar");
        RegresarUbi.setFont(BtFont);
        panel3.add(RegresarUbi);

        add(panel3, BorderLayout.SOUTH);
    }

    public void ListenerRegresarModUbi(ActionListener parActionListener) {
        RegresarUbi.addActionListener(parActionListener);
        RegresarUbi.setActionCommand("RegresarUbi");
    }


    public void ListenerConsultaModUbicacion(ActionListener parActionListener) {
        consultarUbicacion.addActionListener(parActionListener);
        consultarUbicacion.setActionCommand("ConsultarModUbi");
    }

    public void ListenerEditarModUbicacion(ActionListener parActionListener) {
        editarUbicacion.addActionListener(parActionListener);
        editarUbicacion.setActionCommand("EditarModUbi");
    }

    public void ListenerCrearModUbicacion(ActionListener parActionListener) {
        crearUbicacion.addActionListener(parActionListener);
        crearUbicacion.setActionCommand("CrearModUbi");
    }

    public void ListenerEliminarModUbicacion(ActionListener parActionListener) {
        eliminarUbicacion.addActionListener(parActionListener);
        eliminarUbicacion.setActionCommand("EliminarModUbi");
    }

    public void setChatAdmin(String parTexto){
        chatAdmin.setText(parTexto);
    }
}
