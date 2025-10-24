package com.proyecto.integrador.view.ModuloDenuncia;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ModuloDenuncia extends JFrame {
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    final private Font BtFont = new Font("Comic Sans MS", Font.BOLD, 14);
    final private Font TaFont = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final int ancho = 1200;
    private static final int alto = 550;
    JLabel etiqueta1, etiqueta2;
    JTextArea chatAdmin;
    JButton consultarDenuncia, editarDenuncia, crearDenuncia, eliminarDenuncia, RegresarModDen;

    public ModuloDenuncia() {

        setTitle("Módulo Denuncia");
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
        etiqueta1 = new JLabel("Bienvenido al Módulo de denuncia", SwingConstants.CENTER);
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

        consultarDenuncia = new JButton("Lista de Denuncias");
        consultarDenuncia.setFont(BtFont);
        editarDenuncia = new JButton("Editar Denuncia");
        editarDenuncia.setFont(BtFont);
        crearDenuncia = new JButton("Crear Denuncia");
        crearDenuncia.setFont(BtFont);
        eliminarDenuncia = new JButton("Eliminar Denuncia");
        eliminarDenuncia.setFont(BtFont);

        panel2.add(consultarDenuncia);
        panel2.add(editarDenuncia);
        panel2.add(crearDenuncia);
        panel2.add(eliminarDenuncia);
        add(panel2, BorderLayout.CENTER);

    }

    public void contenido3() {
        JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayout(0, 1, 10, 10));
        panel3.setBorder(BorderFactory.createEmptyBorder(5, 500, 30, 500));

        RegresarModDen = new JButton("Regresar");
        RegresarModDen.setFont(BtFont);
        panel3.add(RegresarModDen);

        add(panel3, BorderLayout.SOUTH);
    }

    public void ListenerRegresarModDenuncia(ActionListener parActionListener) {
        RegresarModDen.addActionListener(parActionListener);
        RegresarModDen.setActionCommand("RegresarModDen");
    }

    public void ListenerConsultaModDenuncia(ActionListener parActionListener) {
        consultarDenuncia.addActionListener(parActionListener);
        consultarDenuncia.setActionCommand("ConsultarModDen");
    }

    public void ListenerEditarModDenuncia(ActionListener parActionListener) {
        editarDenuncia.addActionListener(parActionListener);
        editarDenuncia.setActionCommand("EditarModDen");
    }

    public void ListenerCrearModDenuncia(ActionListener parActionListener) {
        crearDenuncia.addActionListener(parActionListener);
        crearDenuncia.setActionCommand("CrearModDen");
    }

    public void ListenerEliminarModDenuncia(ActionListener parActionListener) {
        eliminarDenuncia.addActionListener(parActionListener);
        eliminarDenuncia.setActionCommand("EliminarModDen");
    }

    public void setChatAdmin(String parTexto){
        chatAdmin.setText(parTexto);
    }
}
