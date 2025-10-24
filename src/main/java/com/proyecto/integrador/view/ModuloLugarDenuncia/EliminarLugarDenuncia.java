package com.proyecto.integrador.view.ModuloLugarDenuncia;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EliminarLugarDenuncia extends JFrame {
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    final private Font BtFont = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final int ancho = 700;
    private static final int alto = 400;
    JLabel etiqueta1, jlidLugDen;
    JTextField idLugDen;
    JButton eliminarLugDen;

    public EliminarLugarDenuncia() {

        setTitle("Eliminar Denuncia");
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
        etiqueta1 = new JLabel("Bienvenido al Módulo de Eliminar Lugar denuncia", SwingConstants.CENTER);
        etiqueta1.setFont(mainFont);
        jlidLugDen = new JLabel("Id del Lugar:*", SwingConstants.CENTER);
        jlidLugDen.setFont(mainFont);
        idLugDen = new JTextField(3);
        idLugDen.setFont(mainFont);

        panel.add(etiqueta1);
        panel.add(jlidLugDen);
        panel.add(idLugDen);

        add(panel, BorderLayout.NORTH);

    }

    public void contenido2() {

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 1, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(5, 30, 30, 30));

        eliminarLugDen = new JButton("Eliminar");
        eliminarLugDen.setFont(BtFont);

        panel2.add(eliminarLugDen);
        add(panel2, BorderLayout.SOUTH);

    }

    public JTextField getIdLugDen(){
        return idLugDen;
    }

    public void ListenerEliminarLugDen(ActionListener parActionListener) {
        eliminarLugDen.addActionListener(parActionListener);
        eliminarLugDen.setActionCommand("EliminarLugDen");
    }
}
