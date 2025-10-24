package com.proyecto.integrador.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Login extends JFrame{
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    private static final int ancho = 500;
    private static final int alto = 400;
    JLabel etiqueta1, etiqueta2, lbUser, lbPass;
    JTextField user;
    JPasswordField pass;
    JButton login, RegresarLogin;

    public Login(){

        setTitle("Chat_Seguridad_Cali - VISTA ADMINISTRADOR");
        setSize(ancho, alto);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        contenido1();
        contenido2();
    }

    public void contenido1() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 30));

        etiqueta1 = new JLabel("Login", SwingConstants.CENTER);
        etiqueta1.setFont(mainFont);

        lbUser = new JLabel("Usuario");
        lbUser.setFont(mainFont);
        user = new JTextField();
        user.setFont(mainFont);

        lbPass = new JLabel("Contraseña");
        lbPass.setFont(mainFont);
        pass = new JPasswordField();
        
        panel.add(etiqueta1);
        panel.add(lbUser);
        panel.add(user);
        panel.add(lbPass);
        panel.add(pass);
        add(panel, BorderLayout.NORTH);
    }

    public void contenido2() {
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 1, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(0, 100, 30, 100));

        login = new JButton("Iniciar Sesión");
        login.setFont(mainFont);
        RegresarLogin = new JButton("Regresar");
        RegresarLogin.setFont(mainFont);
        panel2.add(login);
        panel2.add(RegresarLogin);
        add(panel2, BorderLayout.SOUTH);
    }

    public void ListenerRegresarLogin(ActionListener parActionListener) {
        RegresarLogin.addActionListener(parActionListener);
        RegresarLogin.setActionCommand("RegresarLogin");
    }

    public JTextField getUsuario(){
        return user;
    }

    public JPasswordField getContraseña(){
        return pass;
    }

    public void ListenerLogin(ActionListener parActionListener) {
        login.addActionListener(parActionListener);
        login.setActionCommand("Login");
    }

}
