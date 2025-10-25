package com.proyecto.integrador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import com.proyecto.integrador.model.Conexion;
import com.proyecto.integrador.model.Ubicacion;
import com.proyecto.integrador.model.LugarDenuncia;
import com.proyecto.integrador.model.DelitoDenuncia;
import com.proyecto.integrador.model.Denuncia;

import java.sql.Connection;
import java.io.IOException;

public class VistaUsuarioFXController {

	@FXML private TextArea txtChat;

	private Conexion conexionBD = new Conexion();
	private Connection conexion = conexionBD.getConnection();
	private Ubicacion ubicacion = new Ubicacion(conexion);
	private LugarDenuncia lugarD = new LugarDenuncia(conexion);
	private DelitoDenuncia delitoDenuncia = new DelitoDenuncia(conexion);
	private Denuncia denuncia = new Denuncia(conexion);

	@FXML
	private void onLugares(ActionEvent ev) {
		try {
			txtChat.setText(ubicacion.informarUbicacion());
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	@FXML
	private void onLugarDenuncia(ActionEvent ev) {
		try {
			txtChat.setText(lugarD.informarLugar());
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	@FXML
	private void onDelitoComun(ActionEvent ev) {
		try {
			txtChat.setText(delitoDenuncia.informarDelitoComun());
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	@FXML
	private void onDenunciaReciente(ActionEvent ev) {
		try {
			txtChat.setText(denuncia.informarDenunciaReciente());
		} catch (Exception ex) {
			showError(ex.getMessage());
		}
	}

	@FXML
	private void onBack(ActionEvent ev) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/com/proyecto/integrador/vista_proyecto.fxml"));
		Stage stage = (Stage) ((Node) ev.getSource()).getScene().getWindow();
		stage.setScene(new Scene(root));
		stage.centerOnScreen();
	}

	private void showError(String msg) {
		Alert a = new Alert(Alert.AlertType.ERROR, msg);
		a.showAndWait();
	}
}
