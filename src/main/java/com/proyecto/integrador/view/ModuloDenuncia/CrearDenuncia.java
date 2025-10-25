package com.proyecto.integrador.view.ModuloDenuncia;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CrearDenuncia extends JFrame {
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    final private Font BtFont = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final int ancho = 1000;
    private static final int alto = 700;
    JLabel etiqueta1, jlidDen, jlfechaDen, jlhoraDen, jldescripcionDen, jlfk_id_lugarDen;
    JTextField idDen, fechaDen, horaDen, descripcionDen, fk_id_lugarDen;
    JButton crearDen;

    public CrearDenuncia() {

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

        jlidDen = new JLabel("Id de la denuncia a crear:*", SwingConstants.CENTER);
        jlidDen.setFont(mainFont);
        idDen = new JTextField(3);
        idDen.setFont(mainFont);

        jlfechaDen = new JLabel("Fecha de la denuncia(YYYY-MM-DD):* ", SwingConstants.CENTER);
        jlfechaDen.setFont(mainFont);
        fechaDen = new JTextField(5);
        fechaDen.setFont(mainFont);

        jlhoraDen = new JLabel("Hora de la denuncia(Formato 24hrs HH:MM:SS):* ", SwingConstants.CENTER);
        jlhoraDen.setFont(mainFont);
        horaDen = new JTextField(5);
        horaDen.setFont(mainFont);

        jldescripcionDen = new JLabel("Descripción de la denuncia:* ", SwingConstants.CENTER);
        jldescripcionDen.setFont(mainFont);
        descripcionDen = new JTextField(5);
        descripcionDen.setFont(mainFont);

        jlfk_id_lugarDen = new JLabel("Lugar donde se hizo la denuncia:* ", SwingConstants.CENTER);
        jlfk_id_lugarDen.setFont(mainFont);
        fk_id_lugarDen = new JTextField(5);
        fk_id_lugarDen.setFont(mainFont);

        panel2.add(jlidDen);
        panel2.add(idDen);
        panel2.add(jlfechaDen);
        panel2.add(fechaDen);
        panel2.add(jlhoraDen);
        panel2.add(horaDen);
        panel2.add(jldescripcionDen);
        panel2.add(descripcionDen);
        panel2.add(jlfk_id_lugarDen);
        panel2.add(fk_id_lugarDen);
        add(panel2, BorderLayout.CENTER);

    }

    public void contenido3() {

        JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayout(0, 1, 10, 10));
        panel3.setBorder(BorderFactory.createEmptyBorder(5, 30, 30, 30));

        crearDen = new JButton("Crear");
        crearDen.setFont(BtFont);

        panel3.add(crearDen);
        add(panel3, BorderLayout.SOUTH);

    }

    public JTextField getIdDen(){
        return idDen;
    }

    public JTextField getFechaDen(){
        return fechaDen;
    }

    public JTextField getHoraDen(){
        return horaDen;
    }

    public JTextField getDescripcionDen(){
        return descripcionDen;
    }

    public JTextField getIdLugarDen(){
        return fk_id_lugarDen;
    }

    public void ListenerCrearDen(ActionListener parActionListener) {
        crearDen.addActionListener(parActionListener);
        crearDen.setActionCommand("CrearDen");
    }
}
