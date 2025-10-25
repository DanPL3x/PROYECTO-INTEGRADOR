package com.proyecto.integrador.view.ModuloDenuncia;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EliminarDenuncia extends JFrame {
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    final private Font BtFont = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final int ancho = 700;
    private static final int alto = 400;
    JLabel etiqueta1, jlidDen;
    JTextField idDen;
    JButton eliminarDen;

    public EliminarDenuncia() {

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
        etiqueta1 = new JLabel("Bienvenido al Módulo de Eliminar denuncia", SwingConstants.CENTER);
        etiqueta1.setFont(mainFont);
        jlidDen = new JLabel("Id de la denuncia:*", SwingConstants.CENTER);
        jlidDen.setFont(mainFont);
        idDen = new JTextField(3);
        idDen.setFont(mainFont);

        panel.add(etiqueta1);
        panel.add(jlidDen);
        panel.add(idDen);

        add(panel, BorderLayout.NORTH);

    }

    public void contenido2() {

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 1, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(5, 30, 30, 30));

        eliminarDen = new JButton("Eliminar");
        eliminarDen.setFont(BtFont);

        panel2.add(eliminarDen);
        add(panel2, BorderLayout.SOUTH);

    }

    public JTextField getIdDen(){
        return idDen;
    }

    public void ListenerEliminarDen(ActionListener parActionListener) {
        eliminarDen.addActionListener(parActionListener);
        eliminarDen.setActionCommand("EliminarDen");
    }
}
