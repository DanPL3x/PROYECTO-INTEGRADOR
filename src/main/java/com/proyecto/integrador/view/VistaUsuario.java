package com.proyecto.integrador.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class VistaUsuario extends JFrame {
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    final private Font BtFont = new Font("Comic Sans MS", Font.BOLD, 14);
    final private Font TaFont = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final int ancho = 1200;
    private static final int alto = 550;
    JLabel etiqueta1;
    JTextArea chat;
    JButton consultaLugares, ConsultaLugarDenuncia, ConsultaDelitoComun, ConsultaDenunciaReciente, Regresar;
    JButton btnZonas, btnTipos, btnPuntosCardinales;
    private JTextArea txtResultado;
    private JButton btnRegresar;
    private JPanel panelBotones;

    public VistaUsuario() {

        setTitle("Chat_Seguridad_Cali - VISTA USUARIO");
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
        panel.setBorder(BorderFactory.createEmptyBorder(80, 30, 5, 30));

        etiqueta1 = new JLabel("Chat_Seguridad_Cali", SwingConstants.CENTER);
        etiqueta1.setFont(mainFont);

        chat = new JTextArea("Hola, ¿Qué desea consultar?", 10, 1);
        chat.setFont(TaFont);
        chat.setLineWrap(true); // Que haga salto de línea automático
        chat.setWrapStyleWord(true); // Que lo haga al final de las palabras
        chat.setEditable(false);
        JScrollPane scrollChat = new JScrollPane(chat);
        
        panel.add(etiqueta1, BorderLayout.NORTH);
        panel.add(scrollChat, BorderLayout.CENTER);

        add(panel, BorderLayout.NORTH);
    }

    public void contenido2() {
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(2, 2, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(5, 30, 30, 30));

        consultaLugares = new JButton("Lugares de Cali");
        consultaLugares.setFont(BtFont);
        ConsultaLugarDenuncia = new JButton("Lugares para denunciar");
        ConsultaLugarDenuncia.setFont(BtFont);
        ConsultaDelitoComun = new JButton("Delito más común");
        ConsultaDelitoComun.setFont(BtFont);
        ConsultaDenunciaReciente = new JButton("Denuncia más reciente realizada");
        ConsultaDenunciaReciente.setFont(BtFont);
        btnZonas = new JButton("Consultar Zonas");
        btnTipos = new JButton("Consultar Tipos");
        btnPuntosCardinales = new JButton("Consultar Puntos Cardinales");
        panel2.add(consultaLugares);
        panel2.add(ConsultaLugarDenuncia);
        panel2.add(ConsultaDelitoComun);
        panel2.add(ConsultaDenunciaReciente);
        panel2.add(btnZonas);
        panel2.add(btnTipos);
        panel2.add(btnPuntosCardinales);

        add(panel2, BorderLayout.CENTER);
    }

    public void contenido3() {
        JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayout(0, 1, 10, 10));
        panel3.setBorder(BorderFactory.createEmptyBorder(5, 500, 30, 500));

        Regresar = new JButton("Regresar");
        Regresar.setFont(BtFont);
        panel3.add(Regresar);

        add(panel3, BorderLayout.SOUTH);
    }

    public void ListenerRegresar(ActionListener parActionListener) {
        Regresar.addActionListener(parActionListener);
        Regresar.setActionCommand("Regresar");
    }

    public void ListenerConsultaLugares(ActionListener parActionListener) {
        consultaLugares.addActionListener(parActionListener);
        consultaLugares.setActionCommand("Lugares");
    }

    public void ListenerLugarDenuncia(ActionListener parActionListener) {
        ConsultaLugarDenuncia.addActionListener(parActionListener);
        ConsultaLugarDenuncia.setActionCommand("LugarDenuncia");
    }

    public void ListenerDelitoComun(ActionListener parActionListener) {
        ConsultaDelitoComun.addActionListener(parActionListener);
        ConsultaDelitoComun.setActionCommand("DelitoComun");
    }

    public void ListenerDenunciaReciente(ActionListener parActionListener) {
        ConsultaDenunciaReciente.addActionListener(parActionListener);
        ConsultaDenunciaReciente.setActionCommand("DenunciaReciente");
    }

    public void ListenerConsultaZonas(ActionListener listener) {
        btnZonas.setActionCommand("Zonas");
        btnZonas.addActionListener(listener);
    }

    public void ListenerConsultaTipos(ActionListener listener) {
        btnTipos.setActionCommand("Tipos");
        btnTipos.addActionListener(listener);
    }

    public void ListenerConsultaPuntosCardinales(ActionListener listener) {
        btnPuntosCardinales.setActionCommand("PuntosCardinales");
        btnPuntosCardinales.addActionListener(listener);
    }

    public void setChat(String parTexto){
        chat.setText(parTexto);
    }

    // Métodos para agregar listeners
    public void setResultado(String texto) {
        txtResultado.setText(texto);
    }

    public void addTablaListener(String tabla, ActionListener listener) {
        for (Component c : panelBotones.getComponents()) {
            if (c instanceof JButton && ((JButton)c).getActionCommand().equals(tabla)) {
                ((JButton)c).addActionListener(listener);
            }
        }
    }

    public void addRegresarListener(ActionListener listener) {
        btnRegresar.addActionListener(listener);
    }
}
