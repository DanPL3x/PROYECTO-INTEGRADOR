package com.proyecto.integrador.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class VistaProyecto extends JFrame {

    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    private static final int ancho = 700;
    private static final int alto = 300;
    JLabel etiqueta1, etiqueta2;
    JButton admin, user;

    public VistaProyecto() {

        setTitle("Chat_Seguridad_Cali");
        setSize(ancho, alto);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        contenido1();
        contenido2();

    }

    public void contenido1() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        etiqueta1 = new JLabel("Bienvenido a Chat_Seguridad_Cali", SwingConstants.CENTER);
        etiqueta1.setFont(mainFont);
        etiqueta2 = new JLabel(
                "<html><div style='text-align:center;'>Si trabajas con nostros, da clic en el botón de 'Administrador',<br>de lo contrario haz clic en 'Usuario'</div></html>",
                SwingConstants.CENTER);
        etiqueta2.setFont(mainFont);

        panel.add(etiqueta1);
        panel.add(etiqueta2);

        add(panel, BorderLayout.NORTH);

    }

    public void contenido2() {

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(1, 2, 10, 0));
        panel2.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        admin = new JButton("Administrador");
        admin.setFont(mainFont);
        user = new JButton("Usuario");
        user.setFont(mainFont);
        panel2.add(admin);
        panel2.add(user);

        add(panel2, BorderLayout.SOUTH);
    }

    public void ListenerLogin(ActionListener parActionListener) {
        admin.addActionListener(parActionListener);
        admin.setActionCommand("admin");
    }

    public void ListenerUser(ActionListener parActionListener) {
        user.addActionListener(parActionListener);
        user.setActionCommand("user");
    }
}
