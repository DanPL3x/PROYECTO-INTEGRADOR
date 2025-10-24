package com.proyecto.integrador.view.ModuloUbicacion;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EditarUbicacion extends JFrame {
    final private Font mainFont = new Font("Comic Sans MS", Font.BOLD, 18);
    final private Font BtFont = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final int ancho = 700;
    private static final int alto = 700;
    JLabel etiqueta1, jlidUbi, jlnombreUbi, jlpunto_cardUbi, jltipoUbi, jlcomunaUbi, jlfk_id_zonaUbi, jlfk_id_nivelUbi;
    JTextField idUbi, nombreUbi, punto_cardUbi, tipoUbi, comunaUbi, fk_id_zonaUbi, fk_id_nivelUbi;
    JButton editarUbi;

    public EditarUbicacion() {

        setTitle("Editar Ubicación");
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
        etiqueta1 = new JLabel("Bienvenido al Módulo de editar ubicación", SwingConstants.CENTER);
        etiqueta1.setFont(mainFont);
        panel.add(etiqueta1);

        add(panel, BorderLayout.NORTH);

    }

    public void contenido2() {

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 2, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jlidUbi = new JLabel("Id de la ubicación a editar:*", SwingConstants.CENTER);
        jlidUbi.setFont(mainFont);
        idUbi = new JTextField(3);
        idUbi.setFont(mainFont);

        jlnombreUbi = new JLabel("Nombre de la ubicación:* ", SwingConstants.CENTER);
        jlnombreUbi.setFont(mainFont);
        nombreUbi = new JTextField(5);
        nombreUbi.setFont(mainFont);

        jlpunto_cardUbi = new JLabel("Punto cardinal de la ubicación:* ", SwingConstants.CENTER);
        jlpunto_cardUbi.setFont(mainFont);
        punto_cardUbi = new JTextField(5);
        punto_cardUbi.setFont(mainFont);

        jltipoUbi = new JLabel("Tipo de la ubicación:* ", SwingConstants.CENTER);
        jltipoUbi.setFont(mainFont);
        tipoUbi = new JTextField(5);
        tipoUbi.setFont(mainFont);

        jlcomunaUbi = new JLabel("Comuna de la ubicación: ", SwingConstants.CENTER);
        jlcomunaUbi.setFont(mainFont);
        comunaUbi = new JTextField(5);
        comunaUbi.setFont(mainFont);

        jlfk_id_zonaUbi = new JLabel("Zona de la ubicación:* ", SwingConstants.CENTER);
        jlfk_id_zonaUbi.setFont(mainFont);
        fk_id_zonaUbi = new JTextField(5);
        fk_id_zonaUbi.setFont(mainFont);

        jlfk_id_nivelUbi = new JLabel("Nivel de riesgo de la ubicación:* ", SwingConstants.CENTER);
        jlfk_id_nivelUbi.setFont(mainFont);
        fk_id_nivelUbi = new JTextField(5);
        fk_id_nivelUbi.setFont(mainFont);

        panel2.add(jlidUbi);
        panel2.add(idUbi);
        panel2.add(jlnombreUbi);
        panel2.add(nombreUbi);
        panel2.add(jlpunto_cardUbi);
        panel2.add(punto_cardUbi);
        panel2.add(jltipoUbi);
        panel2.add(tipoUbi);
        panel2.add(jlcomunaUbi);
        panel2.add(comunaUbi);
        panel2.add(jlfk_id_zonaUbi);
        panel2.add(fk_id_zonaUbi);
        panel2.add(jlfk_id_nivelUbi);
        panel2.add(fk_id_nivelUbi);
        add(panel2, BorderLayout.CENTER);

    }

    public void contenido3() {

        JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayout(0, 1, 10, 10));
        panel3.setBorder(BorderFactory.createEmptyBorder(5, 30, 30, 30));

        editarUbi = new JButton("Editar");
        editarUbi.setFont(BtFont);

        panel3.add(editarUbi);
        add(panel3, BorderLayout.SOUTH);

    }

    public JTextField getIdUbi(){
        return idUbi;
    }

    public JTextField getNombreUbi(){
        return nombreUbi;
    }

    public JTextField getPuntoCardUbi(){
        return punto_cardUbi;
    }

    public JTextField getTipoUbi(){
        return tipoUbi;
    }

    public JTextField getComunaUbi(){
        return comunaUbi;
    }

    public JTextField getIdZonaUbi(){
        return fk_id_zonaUbi;
    }

    public JTextField getIdNivelUbi(){
        return fk_id_nivelUbi;
    }

    public void ListenerEditarUbi(ActionListener parActionListener) {
        editarUbi.addActionListener(parActionListener);
        editarUbi.setActionCommand("EditarUbi");
    }
}
