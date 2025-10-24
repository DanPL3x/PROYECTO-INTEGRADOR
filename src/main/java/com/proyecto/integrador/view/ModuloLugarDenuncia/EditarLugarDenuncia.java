package com.proyecto.integrador.view.ModuloLugarDenuncia;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EditarLugarDenuncia extends JFrame {
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    final private Font BtFont = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final int ancho = 1000;
    private static final int alto = 700;
    JLabel etiqueta1, jlidLugDen, jlnombreLugDen, jltelefonoLugDen, jldireccionLugDen, jlfk_id_ubi;
    JTextField idLugDen, nombreLugDen, telefonoLugDen, direccionLugDen, fk_id_ubi;
    JButton editarLugDen;

    public EditarLugarDenuncia() {

        setTitle("Crear Denuncia");
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
        panel.setBorder(BorderFactory.createEmptyBorder(50, 10, 10, 10));
        etiqueta1 = new JLabel("Bienvenido al Módulo de crear denuncia", SwingConstants.CENTER);
        etiqueta1.setFont(mainFont);
        panel.add(etiqueta1);

        add(panel, BorderLayout.NORTH);

    }

    public void contenido2() {

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 2, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jlidLugDen = new JLabel("Id del Lugar a editar:*", SwingConstants.CENTER);
        jlidLugDen.setFont(mainFont);
        idLugDen = new JTextField(3);
        idLugDen.setFont(mainFont);

        jlnombreLugDen = new JLabel("Nombre del Lugar:* ", SwingConstants.CENTER);
        jlnombreLugDen.setFont(mainFont);
        nombreLugDen = new JTextField(5);
        nombreLugDen.setFont(mainFont);

        jltelefonoLugDen = new JLabel("Teléfono del lugar:* ", SwingConstants.CENTER);
        jltelefonoLugDen.setFont(mainFont);
        telefonoLugDen = new JTextField(5);
        telefonoLugDen.setFont(mainFont);

        jldireccionLugDen = new JLabel("Dirección del Lugar:* ", SwingConstants.CENTER);
        jldireccionLugDen.setFont(mainFont);
        direccionLugDen = new JTextField(5);
        direccionLugDen.setFont(mainFont);

        jlfk_id_ubi = new JLabel("Id de la ubicación del Lugar:* ", SwingConstants.CENTER);
        jlfk_id_ubi.setFont(mainFont);
        fk_id_ubi = new JTextField(5);
        fk_id_ubi.setFont(mainFont);

        panel2.add(jlidLugDen);
        panel2.add(idLugDen);
        panel2.add(jlnombreLugDen);
        panel2.add(nombreLugDen);
        panel2.add(jltelefonoLugDen);
        panel2.add(telefonoLugDen);
        panel2.add(jldireccionLugDen);
        panel2.add(direccionLugDen);
        panel2.add(jlfk_id_ubi);
        panel2.add(fk_id_ubi);
        add(panel2, BorderLayout.CENTER);

    }

    public void contenido3() {

        JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayout(0, 1, 10, 10));
        panel3.setBorder(BorderFactory.createEmptyBorder(5, 30, 30, 30));

        editarLugDen = new JButton("Editar");
        editarLugDen.setFont(BtFont);

        panel3.add(editarLugDen);
        add(panel3, BorderLayout.SOUTH);

    }

    public JTextField getIdLugDen(){
        return idLugDen;
    }

    public JTextField getNombreLugDen(){
        return nombreLugDen;
    }

    public JTextField getTelefonoLugDen(){
        return telefonoLugDen;
    }

    public JTextField getDireccionLugDen(){
        return direccionLugDen;
    }

    public JTextField getIdUbicacionLugDen(){
        return fk_id_ubi;
    }

    public void ListenerEditarLugDen(ActionListener parActionListener) {
        editarLugDen.addActionListener(parActionListener);
        editarLugDen.setActionCommand("EditarLugDen");
    }
}
