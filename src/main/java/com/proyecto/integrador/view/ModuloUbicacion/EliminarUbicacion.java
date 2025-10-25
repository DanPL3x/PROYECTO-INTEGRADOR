package com.proyecto.integrador.view.ModuloUbicacion;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EliminarUbicacion extends JFrame {
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    final private Font BtFont = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final int ancho = 700;
    private static final int alto = 400;
    JLabel etiqueta1, jlidUbi;
    JTextField idUbi;
    JButton eliminarUbi;

    public EliminarUbicacion() {

        setTitle("Eliminar Ubicación");
        setSize(ancho, alto);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        contenido1();
        contenido2();
    }

    public void contenido1() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 100, 10, 100));
        etiqueta1 = new JLabel("Bienvenido al Módulo de Eliminar ubicación", SwingConstants.CENTER);
        etiqueta1.setFont(mainFont);
        jlidUbi = new JLabel("Id de la ubicación:*", SwingConstants.CENTER);
        jlidUbi.setFont(mainFont);
        idUbi = new JTextField(3);
        idUbi.setFont(mainFont);

        panel.add(etiqueta1);
        panel.add(jlidUbi);
        panel.add(idUbi);

        add(panel, BorderLayout.NORTH);

    }

    public void contenido2() {

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 1, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(5, 30, 30, 30));

        eliminarUbi = new JButton("Eliminar");
        eliminarUbi.setFont(BtFont);

        panel2.add(eliminarUbi);
        add(panel2, BorderLayout.SOUTH);

    }

    public JTextField getIdUbi(){
        return idUbi;
    }

    public void ListenerEliminarUbi(ActionListener parActionListener) {
        eliminarUbi.addActionListener(parActionListener);
        eliminarUbi.setActionCommand("EliminarUbi");
    }
}
