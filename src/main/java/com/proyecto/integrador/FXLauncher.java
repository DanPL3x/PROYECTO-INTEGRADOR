package com.proyecto.integrador;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class FXLauncher extends Application {

	// Carga config.properties (classpath o archivo en proyecto)
	private static void loadConfig() {
		Properties props = new Properties();
		try (InputStream in = FXLauncher.class.getResourceAsStream("/config.properties")) {
			if (in != null) {
				props.load(in);
			} else {
				File f = new File("config.properties");
				if (f.exists()) {
					try (InputStream fin = new FileInputStream(f)) {
						props.load(fin);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!props.isEmpty()) {
			if (props.getProperty("db.url") != null) System.setProperty("db.url", props.getProperty("db.url"));
			if (props.getProperty("db.user") != null) System.setProperty("db.user", props.getProperty("db.user"));
			if (props.getProperty("db.password") != null) System.setProperty("db.password", props.getProperty("db.password"));
		} else {
			System.err.println("config.properties no encontrado o vacío. Asegúrate de proveer db.url, db.user, db.password.");
		}
	}

	public static void main(String[] args) {
		loadConfig();
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/com/proyecto/integrador/vista_proyecto.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Proyecto - Seleccione rol (JavaFX)");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
