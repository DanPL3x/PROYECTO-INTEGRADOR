package com.proyecto.integrador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import com.proyecto.integrador.model.Conexion;
import com.proyecto.integrador.model.User;

import java.io.IOException;
import java.sql.Connection;

public class LoginFXController {

	@FXML private TextField txtUsuario;
	@FXML private PasswordField txtPassword;

	@FXML
	private void onLogin(ActionEvent ev) throws IOException {
		String usuario = txtUsuario.getText();
		String pass = txtPassword.getText();

		Conexion conexionBD = new Conexion();
		Connection conexion = conexionBD.getConnection();
		User user = new User(conexion);

		boolean ok = false;
		try {
			ok = user.userAutenticacion(usuario, pass);
		} catch (Exception ex) {
			showError("Error al autenticar: " + ex.getMessage());
			return;
		}

		if (ok) {
			// navegar a vista admin
			Parent root = FXMLLoader.load(getClass().getResource("/com/proyecto/integrador/vista_admin.fxml"));
			Stage stage = (Stage) ((Node) ev.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.centerOnScreen();
		} else {
			showError("Credenciales incorrectas");
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
