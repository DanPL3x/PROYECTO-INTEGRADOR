package com.proyecto.integrador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import com.proyecto.integrador.model.Conexion;
import com.proyecto.integrador.model.Ubicacion;

import java.sql.Connection;
import java.io.IOException;

public class ModuloUbicacionFXController {

	@FXML private TextArea txtListado;
	@FXML private TextField tfId, tfNombre, tfPunto, tfTipo, tfComuna, tfZona, tfNivel;

	private Conexion conexionBD = new Conexion();
	private Connection conexion = conexionBD.getConnection();
	private Ubicacion ubicacion = new Ubicacion(conexion);
    private String CrearNombreUbi;
    private String CrearPuntoCardUbi;

    @FXML
	private void onListar(ActionEvent ev) {
		try {
			txtListado.setText(ubicacion.informarUbicacion());
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	@FXML
	private void onCrear(ActionEvent ev) {
		try {
			String id = tfId.getText();
			String nombre = tfNombre.getText();
			String punto = tfPunto.getText();
			String tipo = tfTipo.getText();
			String comuna = tfComuna.getText();
			String zona = tfZona.getText();
			String nivel = tfNivel.getText();

			if (id.isEmpty() || nombre.isEmpty() || punto.isEmpty() || tipo.isEmpty() || comuna.isEmpty() || zona.isEmpty() || nivel.isEmpty()) {
				showError("Debe ingresar los campos obligatorios");
				return;
			}
			ubicacion.CrearUbicacion(Integer.parseInt(id), CrearNombreUbi, CrearPuntoCardUbi, nombre, Integer.parseInt(comuna), Integer.parseInt(zona), Integer.parseInt(nivel));
			Alert a = new Alert(Alert.AlertType.INFORMATION, "Registro creado con éxito");
			a.showAndWait();
			onListar(ev);
		} catch (NumberFormatException nfe) {
			showError("Los campos ID/Comuna/Zona/Nivel deben ser numéricos");
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	@FXML
	private void onBack(ActionEvent ev) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/com/proyecto/integrador/vista_admin.fxml"));
		Stage stage = (Stage) ((Node) ev.getSource()).getScene().getWindow();
		stage.setScene(new Scene(root));
		stage.centerOnScreen();
	}

	private void showError(String msg) {
		Alert a = new Alert(Alert.AlertType.ERROR, msg);
		a.showAndWait();
	}
}
