package com.proyecto.integrador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class VistaAdminFXController {

	@FXML
	private void onModuloUbicacion(ActionEvent ev) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/com/proyecto/integrador/modulo_ubicacion.fxml"));
		Stage stage = (Stage) ((Node) ev.getSource()).getScene().getWindow();
		stage.setScene(new Scene(root));
		stage.centerOnScreen();
	}

	@FXML
	private void onBack(ActionEvent ev) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/com/proyecto/integrador/vista_proyecto.fxml"));
		Stage stage = (Stage) ((Node) ev.getSource()).getScene().getWindow();
		stage.setScene(new Scene(root));
		stage.centerOnScreen();
	}
}
