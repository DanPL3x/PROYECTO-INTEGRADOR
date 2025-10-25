package com.proyecto.integrador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class VistaProyectoFXController {

	@FXML
	private void onAdmin(ActionEvent ev) throws IOException {
		openScene(ev, "/com/proyecto/integrador/login.fxml");
	}

	@FXML
	private void onUser(ActionEvent ev) throws IOException {
		openScene(ev, "/com/proyecto/integrador/vista_usuario.fxml");
	}

	private void openScene(ActionEvent ev, String fxmlPath) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
		Stage stage = (Stage) ((Node) ev.getSource()).getScene().getWindow();
		stage.setScene(new Scene(root));
		stage.centerOnScreen();
	}
}
