package com.proyecto.integrador.view;

import com.proyecto.integrador.model.ConsultasDB;
import com.proyecto.integrador.model.RecommendationService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;

public class UserFXView {

	private final Stage stage;
	private final ConsultasDB consultasDB;
	private final RecommendationService recommendationService;

	public UserFXView(Connection conexion) {
		this.consultasDB = new ConsultasDB(conexion);
		this.recommendationService = new RecommendationService(conexion);
		this.stage = new Stage();
		this.stage.setTitle("Portal Seguridad Cali - Consultas Ciudadanas");

		VBox root = new VBox(12);
		root.setPadding(new Insets(16));
		root.setAlignment(Pos.TOP_CENTER);

		Label title = new Label("🛡️ Portal Seguridad Cali - Consultas Ciudadanas");
		title.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:#2c3e50;");

		// NUEVA SECCIÓN: Búsqueda de zona con recomendaciones
		VBox searchSection = new VBox(8);
		searchSection.setPadding(new Insets(12));
		searchSection.setStyle("-fx-background-color: #e8f4f8; -fx-background-radius: 8;");
		
		Label searchTitle = new Label("🔍 Buscar información de seguridad por zona");
		searchTitle.setStyle("-fx-font-size:16px; -fx-font-weight:600;");
		
		HBox searchBox = new HBox(10);
		searchBox.setAlignment(Pos.CENTER);
		TextField searchField = new TextField();
		searchField.setPromptText("Escriba el nombre de la zona (ej: San Antonio, Aguablanca...)");
		searchField.setPrefWidth(400);
		searchField.setStyle("-fx-font-size:15px;");
		
		Button btnSearch = new Button("🔎 Buscar");
		btnSearch.setStyle("-fx-background-color:#3498db; -fx-text-fill:white; -fx-font-size:15px; -fx-pref-width:120; -fx-pref-height:35;");
		
		Button btnListZones = new Button("📋 Ver todas las zonas");
		btnListZones.setStyle("-fx-background-color:#9b59b6; -fx-text-fill:white; -fx-font-size:14px; -fx-pref-height:35;");
		
		searchBox.getChildren().addAll(searchField, btnSearch, btnListZones);
		searchSection.getChildren().addAll(searchTitle, searchBox);

		TextArea resultArea = new TextArea();
		resultArea.setEditable(false);
		resultArea.setWrapText(true);
		resultArea.setPrefRowCount(20);
		resultArea.setStyle("-fx-font-family: monospace; -fx-font-size:14px;");

		// Acción del botón buscar
		btnSearch.setOnAction(e -> {
			String zona = searchField.getText().trim();
			if (!zona.isEmpty()) {
				buscarZonaConRecomendaciones(zona, resultArea);
			} else {
				resultArea.setText("⚠️ Por favor ingrese el nombre de una zona para buscar.");
			}
		});
		
		// Acción del botón listar zonas
		btnListZones.setOnAction(e -> {
			runQueryAsync("SELECT nombre, comuna_vereda FROM Zona ORDER BY nombre", "Lista de Zonas", resultArea);
		});
		
		// Permitir buscar con Enter
		searchField.setOnAction(e -> btnSearch.fire());

		Label consultasTitle = new Label("📊 Consultas Rápidas Predefinidas");
		consultasTitle.setStyle("-fx-font-size:16px; -fx-font-weight:600; -fx-text-fill:#2c3e50;");

		// Panel con botones (2 columnas)
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(6));
		grid.setAlignment(Pos.CENTER);

		// Definir consultas y botones
		addQueryButton(grid, 0, 0, "Barrios más Peligrosos",
				"SELECT z.nombre AS Barrio, COUNT(d.id_denuncia) AS Denuncias " +
				"FROM Zona z JOIN Ubicacion u ON z.id_zona = u.id_zona " +
				"JOIN LugarDenuncias l ON u.id_ubicacion = l.id_ubicacion " +
				"JOIN Denuncia d ON l.id_lugar = d.id_lugar " +
				"GROUP BY z.nombre ORDER BY Denuncias DESC LIMIT 10", resultArea);

		addQueryButton(grid, 1, 0, "CAIs y estaciones",
				"SELECT nombre, direccion, telefono FROM LugarDenuncias " +
				"WHERE nombre LIKE '%CAI%' OR nombre LIKE '%Estación%'", resultArea);

		addQueryButton(grid, 0, 1, "Delitos recientes (24h)",
				"SELECT d.fecha, d.hora, d.descripcion, z.nombre AS zona " +
				"FROM Denuncia d JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar " +
				"JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion " +
				"JOIN Zona z ON u.id_zona = z.id_zona " +
				"WHERE d.fecha >= CURDATE() - INTERVAL 1 DAY ORDER BY d.fecha DESC, d.hora DESC LIMIT 50", resultArea);

		addQueryButton(grid, 1, 1, "Zonas seguras (nivel Bajo)",
				"SELECT DISTINCT z.nombre AS Zona, nr.riesgo AS Nivel " +
				"FROM Zona z JOIN Ubicacion u ON z.id_zona = u.id_zona " +
				"JOIN NivelRiesgo nr ON u.id_nivel = nr.id_nivel " +
				"WHERE nr.riesgo = 'Bajo' LIMIT 50", resultArea);

		addQueryButton(grid, 0, 2, "Delitos más frecuentes",
				"SELECT tipo_delito, COUNT(*) AS Total FROM Delito GROUP BY tipo_delito ORDER BY Total DESC LIMIT 20", resultArea);

		addQueryButton(grid, 1, 2, "Horarios con más denuncias",
				"SELECT HOUR(hora) AS Hora, COUNT(*) AS Denuncias FROM Denuncia GROUP BY HOUR(hora) ORDER BY Denuncias DESC LIMIT 24", resultArea);

		Button btnClose = new Button("❌ Cerrar");
		btnClose.setOnAction(e -> stage.close());
		btnClose.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size:15px; -fx-pref-width:120; -fx-pref-height:40;");

		root.getChildren().addAll(title, new Separator(), searchSection, new Separator(), consultasTitle, grid, new Separator(), resultArea, btnClose);

		Scene scene = new Scene(root, 900, 600);
		stage.setScene(scene);
		stage.initModality(Modality.NONE);
		stage.setMaximized(true); // Maximizar ventana
		stage.setFullScreenExitHint(""); // Ocultar mensaje "Presiona ESC para salir"
		stage.setFullScreen(true); // Pantalla completa
	}

	private void addQueryButton(GridPane grid, int col, int row, String label, String sql, TextArea resultArea) {
		Button btn = new Button(label);
		btn.setPrefWidth(380);
		btn.setOnAction(e -> runQueryAsync(sql, label, resultArea));
		btn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 13px;");
		grid.add(btn, col, row);
	}

	private void runQueryAsync(String sql, String title, TextArea resultArea) {
		resultArea.setText("Ejecutando: " + title + "...\n");
		Task<String> task = new Task<>() {
			@Override
			protected String call() {
				try {
					return consultasDB.ejecutarConsultaFormateada(sql);
				} catch (Exception ex) {
					ex.printStackTrace();
					return "Error al ejecutar consulta: " + ex.getMessage();
				}
			}
		};
		task.setOnSucceeded(ev -> resultArea.setText(task.getValue()));
		task.setOnFailed(ev -> resultArea.setText("Error: " + task.getException().getMessage()));
		new Thread(task).start();
	}

	/**
	 * Busca información de una zona y muestra recomendaciones de seguridad
	 */
	private void buscarZonaConRecomendaciones(String nombreZona, TextArea resultArea) {
		resultArea.setText("🔍 Buscando información de: " + nombreZona + "...\n");
		
		Task<String> task = new Task<>() {
			@Override
			protected String call() {
				try {
					return recommendationService.generarReporteCompleto(nombreZona);
				} catch (Exception ex) {
					ex.printStackTrace();
					return "❌ Error al buscar zona: " + ex.getMessage();
				}
			}
		};
		
		task.setOnSucceeded(ev -> resultArea.setText(task.getValue()));
		task.setOnFailed(ev -> resultArea.setText("❌ Error: " + task.getException().getMessage()));
		new Thread(task).start();
	}

	public void show() {
		Platform.runLater(() -> stage.show());
	}
}
