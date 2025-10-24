package com.proyecto.integrador.controller;
//Se importar los paquetes de mysql connector

import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import com.proyecto.integrador.model.*;
import com.proyecto.integrador.view.*;
import com.proyecto.integrador.view.ModuloUbicacion.EditarUbicacion;
import com.proyecto.integrador.view.ModuloUbicacion.ModuloUbicacion;
import com.proyecto.integrador.view.ModuloUbicacion.CrearUbicacion;
import com.proyecto.integrador.view.ModuloUbicacion.EliminarUbicacion;
import com.proyecto.integrador.view.ModuloDenuncia.ModuloDenuncia;
import com.proyecto.integrador.view.ModuloDenuncia.EditarDenuncia;
import com.proyecto.integrador.view.ModuloDenuncia.CrearDenuncia;
import com.proyecto.integrador.view.ModuloDenuncia.EliminarDenuncia;
import com.proyecto.integrador.view.ModuloLugarDenuncia.ModuloLugarDenuncia;
import com.proyecto.integrador.view.ModuloLugarDenuncia.EditarLugarDenuncia;
import com.proyecto.integrador.view.ModuloLugarDenuncia.CrearLugarDenuncia;
import com.proyecto.integrador.view.ModuloLugarDenuncia.EliminarLugarDenuncia;

import javax.swing.JOptionPane;


public class ControladorProyecto {

    private VistaProyecto vista;
    private Login login;
    private VistaUsuario vistaUser;
    private VistaAdmin vistaAdmin;
    private ModuloUbicacion modUbi;
    private EditarUbicacion editUbi;
    private CrearUbicacion crearUbi;
    private EliminarUbicacion eliminarUbi;
    private ModuloDenuncia modDen;
    private EditarDenuncia editDen;
    private CrearDenuncia crearDen;
    private EliminarDenuncia eliminarDen;
    private ModuloLugarDenuncia modLugDen;
    private EditarLugarDenuncia editLugDen;
    private CrearLugarDenuncia crearLugDen;
    private EliminarLugarDenuncia eliminarLugDen;
    private Riesgo riesgo;

    private String texto;
    private String usuario;
    private String pass;
    private boolean var;

    Conexion conexionBD = new Conexion();
    Connection conexion = conexionBD.getConnection();

    // Se la pasás al modelo
    Ubicacion ubicacion = new Ubicacion(conexion);
    LugarDenuncia lugarD = new LugarDenuncia(conexion);
    DelitoDenuncia delitoDenuncia = new DelitoDenuncia(conexion);
    Denuncia denuncia = new Denuncia(conexion);

    // Y ahora llamás al método

    public ControladorProyecto(Riesgo parRiesgo, VistaProyecto miVista) {
        this.riesgo = parRiesgo;
        this.vista = miVista;
        this.vista.ListenerLogin(new Listener());
        this.vista.ListenerUser(new Listener());
    }

    private class Listener implements ActionListener {
        public void actionPerformed(ActionEvent evento) {
            String comando = evento.getActionCommand();
            try {
                if (comando.equals("admin")) {
                    login = new Login();
                    login.ListenerLogin(new Listener());
                    login.ListenerRegresarLogin(new Listener());
                    login.setVisible(true);
                    vista.dispose();
                } else if (comando.equals("user")) {
                    vistaUser = new VistaUsuario();
                    vistaUser.ListenerConsultaLugares(new Listener());
                    vistaUser.ListenerDelitoComun(new Listener());
                    vistaUser.ListenerDenunciaReciente(new Listener());
                    vistaUser.ListenerLugarDenuncia(new Listener());
                    vistaUser.ListenerRegresar(new Listener());
                    vistaUser.setVisible(true);
                    vista.dispose();
                }
                switch (comando) {
                    case "Login":
                        usuario = login.getUsuario().getText();
                        pass = new String(login.getContraseña().getPassword());
                        User user = new User(conexion);
                        if (user.userAutenticacion(usuario, pass)) {
                            JOptionPane.showMessageDialog(null, "Bienvenido", "", JOptionPane.INFORMATION_MESSAGE);
                            vistaAdmin = new VistaAdmin();
                            vistaAdmin.ListenerModuloUbicacion(new Listener());
                            vistaAdmin.ListenerModuloDenuncia(new Listener());
                            vistaAdmin.ListenerModuloLugarDenuncia(new Listener());
                            vistaAdmin.ListenerRegresarAdmin(new Listener());
                            vistaAdmin.setVisible(true);
                            login.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Credenciales incorrectas", "",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                        break;
                    case "RegresarLogin":
                        login.dispose();
                        vista.setVisible(true);
                        break;
                    case "RegresarAdmin":
                        vistaAdmin.dispose();
                        vista.setVisible(true);
                        break;
                    case "Lugares":
                        texto = ubicacion.informarUbicacion();
                        vistaUser.setChat(texto);
                        break;
                    case "LugarDenuncia":
                        texto = lugarD.informarLugar();
                        vistaUser.setChat(texto);
                        break;
                    case "DelitoComun":
                        texto = delitoDenuncia.informarDelitoComun();
                        vistaUser.setChat(texto);
                        break;
                    case "DenunciaReciente":
                        texto = denuncia.informarDenunciaReciente();
                        vistaUser.setChat(texto);
                        break;
                    case "Regresar":
                        vistaUser.dispose();
                        vista.setVisible(true);
                        break;
                    case "ModuloUbicacion":
                        modUbi = new ModuloUbicacion();
                        modUbi.ListenerConsultaModUbicacion(new Listener());
                        modUbi.ListenerEditarModUbicacion(new Listener());
                        modUbi.ListenerCrearModUbicacion(new Listener());
                        modUbi.ListenerEliminarModUbicacion(new Listener());
                        modUbi.ListenerRegresarModUbi(new Listener());
                        modUbi.setVisible(true);
                        vistaAdmin.dispose();
                        break;
                    case "ConsultarModUbi":
                        texto = ubicacion.informarUbicacionAdmin();
                        modUbi.setChatAdmin(texto);
                        break;
                    case "EditarModUbi":
                        editUbi = new EditarUbicacion();
                        editUbi.ListenerEditarUbi(new Listener());
                        editUbi.setVisible(true);
                        // texto = ubicacion.informarUbicacionAdmin();
                        // modUbi.setChatAdmin(texto);
                        break;
                    case "EditarUbi":
                        String EditIdUbi = editUbi.getIdUbi().getText();
                        String EditNombreUbi = editUbi.getNombreUbi().getText();
                        String EditPuntoCardUbi = editUbi.getPuntoCardUbi().getText();
                        String EditTipoUbi = editUbi.getTipoUbi().getText();
                        String EditComuna = editUbi.getComunaUbi().getText();
                        String EditIdZonaUbi = editUbi.getIdZonaUbi().getText();
                        String EditIdNivelUbi = editUbi.getIdNivelUbi().getText();
                        if (EditIdUbi.isEmpty() || EditNombreUbi.isEmpty() || EditPuntoCardUbi.isEmpty()
                                || EditTipoUbi.isEmpty() || EditComuna.isEmpty()
                                || EditIdZonaUbi.isEmpty() || EditIdNivelUbi.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar los campos obligatorios", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (!isNumeric(EditIdUbi) || !isNumeric(EditComuna) || !isNumeric(EditIdZonaUbi)
                                || !isNumeric(EditIdNivelUbi)) {
                            JOptionPane.showMessageDialog(null, "Los campos ID/Comuna/Zona/Nivel deben ser numéricos", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            ubicacion.EditarUbicacion(Integer.parseInt(EditIdUbi), EditNombreUbi,
                                    EditPuntoCardUbi,
                                    EditTipoUbi,
                                    Integer.parseInt(EditComuna), Integer.parseInt(EditIdZonaUbi),
                                    Integer.parseInt(EditIdNivelUbi));

                            JOptionPane.showMessageDialog(null, "Registro editado con éxito!!!", "",
                                    JOptionPane.INFORMATION_MESSAGE);
                            editUbi.dispose();

                        }
                        break;
                    case "CrearModUbi":
                        crearUbi = new CrearUbicacion();
                        crearUbi.ListenerCrearUbi(new Listener());
                        crearUbi.setVisible(true);
                        // texto = ubicacion.informarUbicacionAdmin();
                        // modUbi.setChatAdmin(texto);
                        break;
                    case "CrearUbi":
                        String CrearIdUbi = crearUbi.getIdUbi().getText();
                        String CrearNombreUbi = crearUbi.getNombreUbi().getText();
                        String CrearPuntoCardUbi = crearUbi.getPuntoCardUbi().getText();
                        String CrearTipoUbi = crearUbi.getTipoUbi().getText();
                        String CrearComuna = crearUbi.getComunaUbi().getText();
                        String CrearIdZonaUbi = crearUbi.getIdZonaUbi().getText();
                        String CrearIdNivelUbi = crearUbi.getIdNivelUbi().getText();
                        if (CrearIdUbi.isEmpty() || CrearNombreUbi.isEmpty() || CrearPuntoCardUbi.isEmpty()
                                || CrearTipoUbi.isEmpty() || CrearComuna.isEmpty()
                                || CrearIdZonaUbi.isEmpty() || CrearIdNivelUbi.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar los campos obligatorios", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (!isNumeric(CrearIdUbi) || !isNumeric(CrearComuna) || !isNumeric(CrearIdZonaUbi)
                                || !isNumeric(CrearIdNivelUbi)) {
                            JOptionPane.showMessageDialog(null, "Los campos ID/Comuna/Zona/Nivel deben ser numéricos", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            ubicacion.CrearUbicacion(Integer.parseInt(CrearIdUbi), CrearNombreUbi,
                                    CrearPuntoCardUbi,
                                    CrearTipoUbi,
                                    Integer.parseInt(CrearComuna), Integer.parseInt(CrearIdZonaUbi),
                                    Integer.parseInt(CrearIdNivelUbi));

                            JOptionPane.showMessageDialog(null, "Registro creado con éxito!!!", "",
                                    JOptionPane.INFORMATION_MESSAGE);
                            crearUbi.dispose();
                        }
                        break;
                    case "EliminarModUbi":
                        eliminarUbi = new EliminarUbicacion();
                        eliminarUbi.ListenerEliminarUbi(new Listener());
                        eliminarUbi.setVisible(true);
                        break;
                    case "EliminarUbi":
                        String EliminarIdUbi = eliminarUbi.getIdUbi().getText();
                        if (EliminarIdUbi.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar la ID del registro que desea borrar", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (!isNumeric(EliminarIdUbi)) {
                            JOptionPane.showMessageDialog(null, "La ID debe ser un número válido", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            int opc = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar este registro?",
                                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                            if (opc == JOptionPane.YES_OPTION) {
                                ubicacion.EliminarUbicacion(Integer.parseInt(EliminarIdUbi));

                                JOptionPane.showMessageDialog(null, "Registro eliminado con éxito!!!", "",
                                        JOptionPane.INFORMATION_MESSAGE);
                                eliminarUbi.dispose();
                            } else {
                                JOptionPane.showMessageDialog(null, "Se canceló la acción", "",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }

                        }
                        break;
                    case "RegresarUbi":
                        modUbi.dispose();
                        vistaAdmin.setVisible(true);
                        break;
                    case "ModuloDenuncia":
                        modDen = new ModuloDenuncia();
                        modDen.ListenerConsultaModDenuncia(new Listener());
                        modDen.ListenerEditarModDenuncia(new Listener());
                        modDen.ListenerCrearModDenuncia(new Listener());
                        modDen.ListenerEliminarModDenuncia(new Listener());
                        modDen.ListenerRegresarModDenuncia(new Listener());
                        modDen.setVisible(true);
                        vistaAdmin.dispose();
                        break;
                    case "ConsultarModDen":
                        texto = denuncia.informarDenunciaAdmin();
                        modDen.setChatAdmin(texto);
                        break;
                    case "EditarModDen":
                        editDen = new EditarDenuncia();
                        editDen.ListenerEditarDen(new Listener());
                        editDen.setVisible(true);
                        break;
                    case "EditarDen":
                        String EditIdDen = editDen.getIdDen().getText();
                        String EditFechaDen = editDen.getFechaDen().getText();
                        String EditHoraDen = editDen.getHoraDen().getText();
                        String EditDescripcionDen = editDen.getDescripcionDen().getText();
                        String EditIdLugarDen = editDen.getIdLugarDen().getText();
                        if (EditIdDen.isEmpty() || EditFechaDen.isEmpty() || EditHoraDen.isEmpty()
                                || EditDescripcionDen.isEmpty()
                                || EditIdLugarDen.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar los campos obligatorios", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (!isNumeric(EditIdDen) || !isNumeric(EditIdLugarDen)) {
                            JOptionPane.showMessageDialog(null, "Los campos ID deben ser numéricos", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            denuncia.EditarDenuncia(Integer.parseInt(EditIdDen), EditFechaDen, EditHoraDen,
                                    EditDescripcionDen, Integer.parseInt(EditIdLugarDen));

                            JOptionPane.showMessageDialog(null, "Registro editado con éxito!!!", "",
                                    JOptionPane.INFORMATION_MESSAGE);
                            editDen.dispose();
                        }
                        break;
                    case "CrearModDen":
                        crearDen = new CrearDenuncia();
                        crearDen.ListenerCrearDen(new Listener());
                        crearDen.setVisible(true);
                        break;
                    case "CrearDen":
                        String CrearIdDen = crearDen.getIdDen().getText();
                        String CrearFechaDen = crearDen.getFechaDen().getText();
                        String CrearHoraDen = crearDen.getHoraDen().getText();
                        String CrearDescripcionDen = crearDen.getDescripcionDen().getText();
                        String CrearIdLugarDen = crearDen.getIdLugarDen().getText();
                        if (CrearIdDen.isEmpty() || CrearFechaDen.isEmpty() || CrearHoraDen.isEmpty()
                                || CrearDescripcionDen.isEmpty()
                                || CrearIdLugarDen.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar los campos obligatorios", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (!isNumeric(CrearIdDen) || !isNumeric(CrearIdLugarDen)) {
                            JOptionPane.showMessageDialog(null, "Los campos ID deben ser numéricos", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            denuncia.CrearDenuncia(Integer.parseInt(CrearIdDen), CrearFechaDen, CrearHoraDen,
                                    CrearDescripcionDen, Integer.parseInt(CrearIdLugarDen));

                            JOptionPane.showMessageDialog(null, "Registro creado con éxito!!!", "",
                                    JOptionPane.INFORMATION_MESSAGE);
                            crearDen.dispose();
                        }
                        break;
                    case "EliminarModDen":
                        eliminarDen = new EliminarDenuncia();
                        eliminarDen.ListenerEliminarDen(new Listener());
                        eliminarDen.setVisible(true);
                        break;
                    case "EliminarDen":
                        String EliminarIdDen = eliminarDen.getIdDen().getText();
                        if (EliminarIdDen.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar la ID del registro que desea borrar", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (!isNumeric(EliminarIdDen)) {
                            JOptionPane.showMessageDialog(null, "La ID debe ser un número válido", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            int opc = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar este registro?",
                                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                            if (opc == JOptionPane.YES_OPTION) {
                                denuncia.EliminarDenuncia(Integer.parseInt(EliminarIdDen));

                                JOptionPane.showMessageDialog(null, "Registro eliminado con éxito!!!", "",
                                        JOptionPane.INFORMATION_MESSAGE);
                                eliminarDen.dispose();
                            }else {
                                JOptionPane.showMessageDialog(null, "Se canceló la acción", "",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        break;
                    case "ModuloLugarDenuncia":
                        modLugDen = new ModuloLugarDenuncia();
                        modLugDen.ListenerConsultaModLugarDenuncia(new Listener());
                        modLugDen.ListenerEditarModLugarDenuncia(new Listener());
                        modLugDen.ListenerCrearModLugarDenuncia(new Listener());
                        modLugDen.ListenerEliminarModLugarDenuncia(new Listener());
                        modLugDen.ListenerRegresarModLugDen(new Listener());
                        modLugDen.setVisible(true);
                        vistaAdmin.dispose();
                        break;
                    case "ConsultarModLugDen":
                        texto = lugarD.informarLugarDenunciaAdmin();
                        modLugDen.setChatAdmin(texto);
                        break;
                    case "EditarModLugDen":
                        editLugDen = new EditarLugarDenuncia();
                        editLugDen.ListenerEditarLugDen(new Listener());
                        editLugDen.setVisible(true);
                        break;
                    case "EditarLugDen":
                        String EditIdLugDen = editLugDen.getIdLugDen().getText();
                        String EditNombreLugDen = editLugDen.getNombreLugDen().getText();
                        String EditTelefonoLugDen = editLugDen.getTelefonoLugDen().getText();
                        String EditDireccionLugDen = editLugDen.getDireccionLugDen().getText();
                        String EditIdUbiLugDen = editLugDen.getIdUbicacionLugDen().getText();
                        if (EditIdLugDen.isEmpty() || EditNombreLugDen.isEmpty() || EditTelefonoLugDen.isEmpty()
                                || EditDireccionLugDen.isEmpty()
                                || EditIdUbiLugDen.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar los campos obligatorios", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (!isNumeric(EditIdLugDen) || !isNumeric(EditTelefonoLugDen)
                                || !isNumeric(EditIdUbiLugDen)) {
                            JOptionPane.showMessageDialog(null, "ID/Telefono/Ubicación deben ser numéricos", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            lugarD.EditarLugarDenuncia(Integer.parseInt(EditIdLugDen), EditNombreLugDen,
                                    Integer.parseInt(EditTelefonoLugDen),
                                    EditDireccionLugDen, Integer.parseInt(EditIdUbiLugDen));

                            JOptionPane.showMessageDialog(null, "Registro editado con éxito!!!", "",
                                    JOptionPane.INFORMATION_MESSAGE);
                            editLugDen.dispose();
                        }
                        break;
                    case "CrearModLugDen":
                        crearLugDen = new CrearLugarDenuncia();
                        crearLugDen.ListenerCrearLugDen(new Listener());
                        crearLugDen.setVisible(true);
                        break;
                    case "CrearLugDen":
                        String CrearIdLugDen = crearLugDen.getIdLugDen().getText();
                        String CrearNombreLugDen = crearLugDen.getNombreLugDen().getText();
                        String CrearTelefonoLugDen = crearLugDen.getTelefonoLugDen().getText();
                        String CrearDireccionLugDen = crearLugDen.getDireccionLugDen().getText();
                        String CrearIdUbiLugDen = crearLugDen.getIdUbicacionLugDen().getText();
                        if (CrearIdLugDen.isEmpty() || CrearNombreLugDen.isEmpty() || CrearTelefonoLugDen.isEmpty()
                                || CrearDireccionLugDen.isEmpty()
                                || CrearIdUbiLugDen.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar los campos obligatorios", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (!isNumeric(CrearIdLugDen) || !isNumeric(CrearTelefonoLugDen)
                                || !isNumeric(CrearIdUbiLugDen)) {
                            JOptionPane.showMessageDialog(null, "ID/Telefono/Ubicación deben ser numéricos", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            lugarD.CrearLugarDenuncia(Integer.parseInt(CrearIdLugDen), CrearNombreLugDen,
                                    Integer.parseInt(CrearTelefonoLugDen),
                                    CrearDireccionLugDen, Integer.parseInt(CrearIdUbiLugDen));

                            JOptionPane.showMessageDialog(null, "Registro creado con éxito!!!", "",
                                    JOptionPane.INFORMATION_MESSAGE);
                            crearLugDen.dispose();
                        }
                        break;
                    case "EliminarModLugDen":
                        eliminarLugDen = new EliminarLugarDenuncia();
                        eliminarLugDen.ListenerEliminarLugDen(new Listener());
                        eliminarLugDen.setVisible(true);
                        break;
                    case "EliminarLugDen":
                        String EliminarIdLugDen = eliminarLugDen.getIdLugDen().getText();
                        if (EliminarIdLugDen.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar la ID del registro que desea borrar", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (!isNumeric(EliminarIdLugDen)) {
                            JOptionPane.showMessageDialog(null, "La ID debe ser un número válido", "",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            int opc = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar este registro?",
                                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                            if (opc == JOptionPane.YES_OPTION) {
                                lugarD.EliminarLugarDenuncia(Integer.parseInt(EliminarIdLugDen));

                                JOptionPane.showMessageDialog(null, "Registro eliminado con éxito!!!", "",
                                        JOptionPane.INFORMATION_MESSAGE);
                                eliminarLugDen.dispose();
                            }else {
                                JOptionPane.showMessageDialog(null, "Se canceló la acción", "",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (

            Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Nuevo método auxiliar para validar números antes de parsear
    private boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

}
