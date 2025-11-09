package com.proyecto.integrador;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.proyecto.integrador.model.Conexion;
import com.proyecto.integrador.model.ConsultasDB;
import com.proyecto.integrador.model.AdminService;
import com.proyecto.integrador.model.AdminAuth;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import com.proyecto.integrador.model.Notification;
import com.proyecto.integrador.model.NotificationDAO;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

import java.io.InputStream;
import java.sql.Connection;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Font;
import javafx.scene.control.Tooltip;
import java.util.Map;
import java.util.HashMap;

public class FXLauncher extends Application {
	private Stage primaryStage;
	private Conexion conexionBD;
	private Connection conexion;
	private ConsultasDB consultasDB;

	private Scene adminScene;

	// NUEVO: DAO y lista observable para notificaciones persistentes
	private NotificationDAO notificationDAO;
	private ObservableList<Notification> notifications;

	private Label statusLabel; // nuevo: barra de estado

	// NUEVO: map displayName -> real DB table name
	private Map<String,String> tableNameMap = new HashMap<>();

	// NUEVO: imagen del logo de la aplicación
	private Image appLogo;

	private static final int WINDOW_W = 900;
	private static final int WINDOW_H = 700;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Portal Seguridad Cali");

		// Cargar logo desde resources/images/logo.png (coloca el fichero allí)
		try (InputStream lis = getClass().getResourceAsStream("/images/logo.png")) {
			if (lis != null) {
				appLogo = new Image(lis);
				// usar el icono en la ventana principal (barra del SO)
				primaryStage.getIcons().add(appLogo);
			} else {
				System.out.println("Logo no encontrado en /images/logo.png — se usará fallback.");
			}
		} catch (Exception ex) {
			System.out.println("Error cargando logo: " + ex.getMessage());
		}

		// Inicializar conexión
		conexionBD = new Conexion();
		conexion = conexionBD.getConnection();
		consultasDB = new ConsultasDB(conexion);

		// Inicializar NotificationDAO y cargar notificaciones desde BD
		try {
			notificationDAO = new NotificationDAO(conexion);
			List<Notification> list = notificationDAO.listAll();
			notifications = FXCollections.observableArrayList(list);
		} catch (Exception ex) {
			ex.printStackTrace();
			notifications = FXCollections.observableArrayList();
		}

		// Asegurar admin por defecto
		AdminService.ensureDefaultAdmin(conexion, "admin", "admin123");

		// Inicializar map de nombres (display -> tabla real)
		tableNameMap.put("Delitos", "Delito");
		tableNameMap.put("Denuncias", "Denuncia");
		tableNameMap.put("Denuncia_Delito", "Denuncia_Delito");
		tableNameMap.put("NivelRiesgo", "NivelRiesgo");
		tableNameMap.put("PuntoCardinal", "PuntoCardinal");
		tableNameMap.put("Tipo", "Tipo");
		tableNameMap.put("Ubicacion", "Ubicacion");
		tableNameMap.put("Zona", "Zona");
		tableNameMap.put("LugarDenuncias", "LugarDenuncias");
		// puedes añadir más mapeos si el nombre mostrado difiere de la tabla real

		// Construir escenas programáticamente (sin FXML)
		Parent mainRoot = buildMainMenu();
		Scene mainScene = new Scene(mainRoot, WINDOW_W, WINDOW_H);

		// Crear escena admin y dejarla lista
		adminScene = buildAdminScene();

		// actualizar estado de conexión en la barra de estado (si existe)
		if (statusLabel != null) {
			boolean ok = conexion != null;
			statusLabel.setText(ok ? "BD: Conectada" : "BD: No conectada");
		}

		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	// Menú principal programático (mockup card centrado) - ahora con MenuBar y status bar
	private Parent buildMainMenu() {
		// MenuBar simple
		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("Archivo");
		MenuItem miExit = new MenuItem("Salir");
		miExit.setOnAction(e -> {
			Conexion.closeConnection();
			Platform.exit();
		});
		menuFile.getItems().add(miExit);
		Menu menuHelp = new Menu("Ayuda");
		MenuItem miAbout = new MenuItem("Acerca");
		miAbout.setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "Portal Seguridad Cali\nVersión de prueba").showAndWait());
		menuHelp.getItems().add(miAbout);
		menuBar.getMenus().addAll(menuFile, menuHelp);

		// contenido principal (tarjeta)
		StackPane cardHolder = new StackPane();
		cardHolder.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f4ff, #e8f0ff);");
		VBox card = new VBox(18);
		card.setMaxWidth(760);
		card.setPadding(new Insets(36));
		card.setAlignment(Pos.TOP_CENTER);
		card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 20, 0, 0, 6);");

		// Reemplazar o añadir cabecera para incluir logo junto al título
		// -- start header change --
		HBox header = new HBox(12);
		header.setAlignment(Pos.CENTER_LEFT);
		if (appLogo != null) {
			ImageView logoView = new ImageView(appLogo);
			logoView.setFitWidth(56);
			logoView.setFitHeight(56);
			header.getChildren().add(logoView);
		}
		VBox titles = new VBox(2);
		Label title = new Label("Portal Seguridad Cali");
		title.setFont(Font.font(24));
		title.setStyle("-fx-font-weight: 700; -fx-text-fill: #222;");
		Label subtitle = new Label("Selecciona tu portal de acceso");
		subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
		titles.getChildren().addAll(title, subtitle);
		header.getChildren().add(titles);
		// insertar el header dentro de la card (sustituye el title/subtitle simples)
		// ...existing code that builds 'card'...
		// en lugar de: card.getChildren().addAll(title, subtitle, tiles, footerCard);
		// usar:
		card.getChildren().clear();
		
		// Tiles y footer (nombres locales no confunden con campos de clase)
		HBox tilesBox = new HBox(24);
		tilesBox.setAlignment(Pos.CENTER);

		// crea tiles (intenta cargar imágenes desde resources/images; si no existen usa emoji de respaldo)
		VBox tileUser = makeTile("Usuario", "#4da6ff", "/images/user_icon_new.png", "👤");
		VBox tileAdmin = makeTile("Administrador", "#42b983", "/images/admin_icon.png", "👨‍💼");
		Tooltip.install(tileUser, new Tooltip("Entrar al portal de consultas ciudadanas"));
		Tooltip.install(tileAdmin, new Tooltip("Acceder al panel administrativo"));
		tileUser.setOnMouseClicked(e -> showUserWindow());
		tileAdmin.setOnMouseClicked(e -> primaryStage.setScene(buildAdminLoginScene()));
		tilesBox.getChildren().addAll(tileUser, tileAdmin);

		Label footerLabel = new Label("Desarrollado por estudiantes de 4to semestre - Proyecto Integrador");
		footerLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 12px;");

		// Poblar el card de forma segura (añadir sólo nodes no nulos)
		card.getChildren().clear();
		if (header != null) card.getChildren().add(header);
		if (tilesBox != null) card.getChildren().add(tilesBox);
		if (footerLabel != null) card.getChildren().add(footerLabel);

		// Asegurar que el card esté dentro del cardHolder
		cardHolder.getChildren().clear();
		cardHolder.getChildren().add(card);
		// -- end header change --

		// Status bar
		HBox statusBar = new HBox();
		statusBar.setPadding(new Insets(6));
		statusBar.setStyle("-fx-background-color:#f5f5f5; -fx-border-color:#e0e0e0; -fx-border-width:1 0 0 0;");
		statusLabel = new Label("BD: Desconocida");
		statusLabel.setStyle("-fx-text-fill:#333;");
		statusBar.getChildren().add(statusLabel);

		// Componer layout vertical: menubar, card, status
		VBox root = new VBox();
		root.getChildren().addAll(menuBar, cardHolder, statusBar);
		VBox.setVgrow(cardHolder, Priority.ALWAYS);

		// Estética: fuente global pequeña para botones
		root.setStyle("-fx-font-family: 'Segoe UI', sans-serif;");

		return root;
	}

	// crea tile con imagen (resourcePath) o emojiFallback
	private VBox makeTile(String labelText, String color, String resourcePath, String emojiFallback) {
		VBox tile = new VBox(10);
		tile.setAlignment(Pos.CENTER);
		tile.setPadding(new Insets(18));
		tile.setPrefSize(260, 140);
		tile.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 4);");

		// intento cargar imagen
		ImageView iconView = null;
		try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
			if (is != null) {
				Image img = new Image(is, 56, 56, true, true);
				iconView = new ImageView(img);
			}
		} catch (Exception ex) {
			// ignora y usará emoji
		}

		if (iconView != null) {
			tile.getChildren().add(iconView);
		} else {
			Label emojiLabel = new Label(emojiFallback);
			emojiLabel.setStyle("-fx-font-size:40px; -fx-text-fill: " + color + ";");
			tile.getChildren().add(emojiLabel);
		}

		Label lbl = new Label(labelText);
		lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #222; -fx-font-weight: 600;");
		tile.getChildren().add(lbl);

		tile.setOnMouseEntered(e -> tile.setStyle("-fx-background-color: #fbfbff; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 16, 0, 0, 8);"));
		tile.setOnMouseExited(e -> tile.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 4);"));
		return tile;
	}

	// Admin login scene (programática)
	private Scene buildAdminLoginScene() {
		StackPane root = new StackPane();
		root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f4ff, #e8f0ff);");

		VBox card = new VBox(12);
		card.setMaxWidth(460);
		card.setPadding(new Insets(26));
		card.setAlignment(Pos.CENTER);
		card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 16, 0, 0, 8);");

		Label title = new Label("Administrador - Iniciar Sesión");
		title.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #222;");

		TextField tfUser = new TextField();
		tfUser.setPromptText("Usuario");
		tfUser.setMaxWidth(300);

		PasswordField pf = new PasswordField();
		pf.setPromptText("Contraseña");
		pf.setMaxWidth(300);

		HBox buttons = new HBox(12);
		buttons.setAlignment(Pos.CENTER);

		Button btnIngresar = new Button("Ingresar");
		btnIngresar.setStyle("-fx-background-color: #42b983; -fx-text-fill: white;");
		Button btnVolver = new Button("Volver");
		btnVolver.setStyle("-fx-background-color: transparent; -fx-border-color: #ddd;");

		buttons.getChildren().addAll(btnIngresar, btnVolver);

		Label lblInfo = new Label();
		lblInfo.setStyle("-fx-text-fill: #d32f2f;");

		btnVolver.setOnAction(ev -> primaryStage.setScene(new Scene(buildMainMenu(), WINDOW_W, WINDOW_H)));

		btnIngresar.setOnAction(ev -> {
			String usuario = tfUser.getText().trim();
			String pass = pf.getText();
			if (usuario.isEmpty() || pass.isEmpty()) {
				lblInfo.setText("Ingrese usuario y contraseña.");
				return;
			}
			lblInfo.setText("Autenticando...");
			Task<Boolean> authTask = new Task<>() {
				@Override
				protected Boolean call() {
					try {
						if (conexion == null || conexion.isClosed()) {
							conexion = conexionBD.getConnection();
							consultasDB = new ConsultasDB(conexion);
						}
						return AdminAuth.authenticate(conexion, usuario, pass);
					} catch (Exception ex) {
						ex.printStackTrace();
						return false;
					}
				}
			};
			authTask.setOnSucceeded(t -> {
				boolean ok = authTask.getValue();
				if (ok) {
					tfUser.clear();
					pf.clear();
					lblInfo.setText("");
					primaryStage.setScene(adminScene);
				} else {
					lblInfo.setText("Credenciales incorrectas");
				}
			});
			authTask.setOnFailed(t -> lblInfo.setText("Error autenticando: " + authTask.getException().getMessage()));
			new Thread(authTask).start();
		});

		card.getChildren().addAll(title, tfUser, pf, buttons, lblInfo);
		root.getChildren().add(card);
		StackPane.setAlignment(card, Pos.CENTER);
		return new Scene(root, WINDOW_W, WINDOW_H);
	}

	// Admin panel (programático) con CRUD basics (lista de tablas, results area, form placeholder)
	private Scene buildAdminScene() {
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(12));

		// Cabecera: añadir logo a la izquierda si existe
		HBox header = new HBox(12);
		header.setPadding(new Insets(8));
		header.setAlignment(Pos.CENTER_LEFT);

		if (appLogo != null) {
			ImageView adminLogo = new ImageView(appLogo);
			adminLogo.setFitWidth(48);
			adminLogo.setFitHeight(48);
			header.getChildren().add(adminLogo);
		} else {
			Label emoji = new Label("👨‍💼");
			emoji.setStyle("-fx-font-size:36px;");
			header.getChildren().add(emoji);
		}

		Label title = new Label("Panel Administrativo");
		title.setStyle("-fx-font-size:18px; -fx-font-weight:700;");
		Label subtitle = new Label("Gestión de datos y consultas");
		subtitle.setStyle("-fx-text-fill: #666;");

		VBox titleBox = new VBox(2, title, subtitle);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		// Badge notificaciones: bind al tamaño de 'notifications'
		Label notifBadge = new Label();
		notifBadge.getStyleClass().add("badge");
		notifBadge.setStyle("-fx-background-color:#e74c3c; -fx-text-fill:white; -fx-padding:4 8; -fx-background-radius:12;");
		if (notifications != null) {
			notifBadge.textProperty().bind(Bindings.size(notifications).asString());
			Tooltip.install(notifBadge, new Tooltip("Notificaciones pendientes"));
		} else {
			notifBadge.setText("0");
		}

		Button btnLogout = new Button("Cerrar sesión");
		btnLogout.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
		btnLogout.setOnAction(e -> primaryStage.setScene(new Scene(buildMainMenu(), WINDOW_W, WINDOW_H)));

		header.getChildren().addAll(titleBox, spacer, notifBadge, btnLogout);

		root.setTop(header);

		// Left: selección de tablas (igual funcionalidad que antes)
		VBox left = new VBox(8);
		left.setPadding(new Insets(0, 10, 0, 0));
		left.setPrefWidth(240);

		String[] tablas = {"Delitos", "Denuncias", "Denuncia_Delito", "NivelRiesgo", "PuntoCardinal", "Tipo", "Ubicacion", "Zona", "LugarDenuncias"};
		ToggleGroup tg = new ToggleGroup();
		for (String t : tablas) {
			RadioButton rb = new RadioButton(t);
			rb.setUserData(t);
			rb.setToggleGroup(tg);
			rb.setStyle("-fx-font-size: 13px;");
			left.getChildren().add(rb);
		}

		Button btnEjecutar = new Button("Ejecutar consulta");
		btnEjecutar.setMaxWidth(Double.MAX_VALUE);
		btnEjecutar.setStyle("-fx-background-color:#2b7cff; -fx-text-fill:white;");
		left.getChildren().addAll(new Separator(), btnEjecutar);

		// Center: resultados (texto formateado)
		TextArea txtResultado = new TextArea();
		txtResultado.setEditable(false);
		txtResultado.setWrapText(false);
		txtResultado.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

		// Right: formulario CRUD simplificado (mantengo formBox para compatibilidad)
		VBox right = new VBox(8);
		right.setPadding(new Insets(8));
		right.setPrefWidth(420);
		Label crudTitle = new Label("CRUD - Formulario");
		crudTitle.setStyle("-fx-font-weight:bold;");

		ScrollPane formScroll = new ScrollPane();
		formScroll.setFitToWidth(true);
		VBox formBox = new VBox(8);
		formBox.setPadding(new Insets(6));
		formScroll.setContent(formBox);
		formScroll.setPrefHeight(320);

		HBox actions = new HBox(8);
		Button btnCreate = new Button("Crear");
		Button btnUpdate = new Button("Actualizar");
		Button btnDelete = new Button("Eliminar");
		Button btnRefresh = new Button("Refrescar");
		for (Button b : new Button[]{btnCreate, btnUpdate, btnDelete, btnRefresh}) {
			b.setStyle("-fx-background-radius:6; -fx-pref-width:90; -fx-pref-height:34;");
		}
		actions.getChildren().addAll(btnCreate, btnUpdate, btnDelete, btnRefresh);

		Label infoCrud = new Label();
		infoCrud.setWrapText(true);

		right.getChildren().addAll(crudTitle, new Label("Formulario:"), formScroll, actions, new Separator(), infoCrud);

		// NUEVO: panel de notificaciones (encima del CRUD formBox)
		ListView<Notification> notifList = new ListView<>(notifications);
		notifList.setPrefHeight(140);
		notifList.setPlaceholder(new Label("No hay notificaciones"));

		Button btnMarkRead = new Button("Marcar leído");
		btnMarkRead.setStyle("-fx-background-color:#f39c12; -fx-text-fill:white;");
		btnMarkRead.setOnAction(ev -> {
			Notification sel = notifList.getSelectionModel().getSelectedItem();
			if (sel != null) {
				try {
					notificationDAO.markAsRead(sel.getId());
					notifications.remove(sel);
				} catch (Exception ex) {
					ex.printStackTrace();
					new Alert(Alert.AlertType.ERROR, "Error marcando como leído: " + ex.getMessage(), ButtonType.OK).showAndWait();
				}
			}
		});

		// mostrar contenido completo al seleccionar
		notifList.setOnMouseClicked((MouseEvent me) -> {
			Notification sel = notifList.getSelectionModel().getSelectedItem();
			if (sel != null) {
				DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				String full = String.format("NOTIFICACIÓN\nRemitente: %s\nFecha: %s\n\n%s",
						sel.getSender(), sel.getTimestamp().format(fmt), sel.getMessage());
				txtResultado.setText(full + "\n\n" + txtResultado.getText());
			}
		});

		// insertar notificaciones al inicio del right pane
		right.getChildren().add(0, new VBox(new Label("Notificaciones"), notifList, btnMarkRead, new Separator()));

		// Wiring ejecución de consulta (ahora hace SELECT * FROM la tabla seleccionada)
		btnEjecutar.setOnAction(ev -> {
			Toggle selected = tg.getSelectedToggle();
			if (selected == null) {
				new Alert(Alert.AlertType.WARNING, "Seleccione una tabla antes de ejecutar.", ButtonType.OK).showAndWait();
				return;
			}
			String display = (String) selected.getUserData();
			String tabla = resolveTableName(display);
			if (tabla == null || tabla.trim().isEmpty()) {
				new Alert(Alert.AlertType.WARNING, "Nombre de tabla inválido.", ButtonType.OK).showAndWait();
				return;
			}
			// Construir SQL: SELECT * FROM `tabla`
			String sql = "SELECT * FROM `" + tabla.replace("`", "") + "`";
			txtResultado.setText("Ejecutando: " + sql + " ...\n");
			Task<String> task = new Task<>() {
				@Override
				protected String call() {
					try {
						if (conexion == null || conexion.isClosed()) {
							conexion = conexionBD.getConnection();
							consultasDB = new ConsultasDB(conexion);
						}
						return consultasDB.ejecutarConsultaFormateada(sql);
					} catch (Exception ex) {
						ex.printStackTrace();
						return "Error ejecutando consulta: " + ex.getMessage();
					}
				}
			};
			task.setOnSucceeded(t -> txtResultado.setText(task.getValue()));
			task.setOnFailed(t -> txtResultado.setText("Error en consulta: " + task.getException().getMessage()));
			new Thread(task).start();
		});

		// --- NUEVO: construir formulario dinámico al seleccionar tabla ---
		tg.selectedToggleProperty().addListener((obs, oldT, newT) -> {
			formBox.getChildren().clear();
			infoCrud.setText("");
			if (newT == null) return;
			String display = (String) newT.getUserData();
			String tabla = resolveTableName(display);
			try {
				java.util.List<String> cols = getTableColumns(tabla);
				if (cols.isEmpty()) {
					infoCrud.setText("No se pudieron obtener columnas de " + display + " (tabla: " + tabla + ")");
					return;
				}
				for (String col : cols) {
					HBox row = new HBox(8);
					row.setAlignment(Pos.CENTER_LEFT);
					Label lbl = new Label(col);
					lbl.setPrefWidth(150);
					TextField tf = new TextField();
					tf.setPrefWidth(220);
					tf.setPromptText(col);
					// guardar nombre de columna en userData para recoger después
					tf.setUserData(col);
					row.getChildren().addAll(lbl, tf);
					formBox.getChildren().add(row);
				}
				String pk = getPrimaryKeyColumn(tabla);
				infoCrud.setText("Tabla: " + display + " (tabla BD: " + tabla + ")" + (pk != null ? "  |  PK: " + pk : "  |  PK no detectada."));
			} catch (Exception ex) {
				ex.printStackTrace();
				infoCrud.setText("Error cargando esquema: " + ex.getMessage());
			}
		});

		// --- CRUD: Crear ---
		btnCreate.setOnAction(ev -> {
			Toggle selected = tg.getSelectedToggle();
			if (selected == null) { infoCrud.setText("Seleccione una tabla primero."); return; }
			String display = (String) selected.getUserData();
			String tabla = resolveTableName(display);
			java.util.Map<String,String> values = collectFormValues(formBox);
			if (values.isEmpty()) { infoCrud.setText("Formulario vacío."); return; }
			infoCrud.setText("Creando en " + tabla + " ...");
			Task<String> t = new Task<>() {
				@Override
				protected String call() {
					try {
						executeInsert(tabla, values);
						return "Registro creado correctamente.";
					} catch (Exception ex) {
						ex.printStackTrace();
						return "Error creando: " + ex.getMessage();
					}
				}
			};
			t.setOnSucceeded(r -> {
				infoCrud.setText(t.getValue());
				btnEjecutar.fire(); // refrescar lista
			});
			t.setOnFailed(r -> infoCrud.setText("Error: " + t.getException().getMessage()));
			new Thread(t).start();
		});

		// --- CRUD: Actualizar ---
		btnUpdate.setOnAction(ev -> {
			Toggle selected = tg.getSelectedToggle();
			if (selected == null) { infoCrud.setText("Seleccione una tabla primero."); return; }
			String display = (String) selected.getUserData();
			String tabla = resolveTableName(display);
			String pk = getPrimaryKeyColumn(tabla);
			java.util.Map<String,String> values = collectFormValues(formBox);
			if (values.isEmpty()) { infoCrud.setText("Formulario vacío."); return; }
			String pkCol = (pk != null) ? pk : values.keySet().iterator().next();
			String pkVal = values.get(pkCol);
			if (pkVal==null || pkVal.isEmpty()) { infoCrud.setText("Debe proporcionar el valor de la clave primaria ("+pkCol+") para actualizar."); return; }
			infoCrud.setText("Actualizando " + tabla + " ...");
			Task<String> t = new Task<>() {
				@Override
				protected String call() {
					try {
						executeUpdate(tabla, values, pkCol, pkVal);
						return "Registro actualizado correctamente.";
					} catch (Exception ex) {
						ex.printStackTrace();
						return "Error actualizando: " + ex.getMessage();
					}
				}
			};
			t.setOnSucceeded(r -> {
				infoCrud.setText(t.getValue());
				btnEjecutar.fire();
			});
			t.setOnFailed(r -> infoCrud.setText("Error: " + t.getException().getMessage()));
			new Thread(t).start();
		});

		// --- CRUD: Eliminar ---
		btnDelete.setOnAction(ev -> {
			Toggle selected = tg.getSelectedToggle();
			if (selected == null) { infoCrud.setText("Seleccione una tabla primero."); return; }
			String display = (String) selected.getUserData();
			String tabla = resolveTableName(display);
			String pk = getPrimaryKeyColumn(tabla);
			java.util.Map<String,String> values = collectFormValues(formBox);
			String pkCol = (pk!=null) ? pk : (values.keySet().isEmpty() ? null : values.keySet().iterator().next());
			if (pkCol==null) { infoCrud.setText("No hay columna para identificar registro."); return; }
			String pkVal = values.get(pkCol);
			if (pkVal==null || pkVal.isEmpty()) { infoCrud.setText("Debe proporcionar el valor de la clave primaria ("+pkCol+") para eliminar."); return; }
			boolean confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar registro con "+pkCol+" = "+pkVal+"?", ButtonType.YES, ButtonType.NO).showAndWait().filter(b->b==ButtonType.YES).isPresent();
			if (!confirm) { infoCrud.setText("Eliminación cancelada."); return; }
			infoCrud.setText("Eliminando ...");
			Task<String> t = new Task<>() {
				@Override
				protected String call() {
					try {
						executeDelete(tabla, pkCol, pkVal);
						return "Registro eliminado correctamente.";
					} catch (Exception ex) {
						ex.printStackTrace();
						return "Error eliminando: " + ex.getMessage();
					}
				}
			};
			t.setOnSucceeded(r -> { infoCrud.setText(t.getValue()); btnEjecutar.fire(); });
			t.setOnFailed(r -> infoCrud.setText("Error: " + t.getException().getMessage()));
			new Thread(t).start();
		});

		root.setLeft(left);
		root.setCenter(txtResultado);
		root.setRight(right);

		// Pie de página con ayuda rápida
		HBox footer = new HBox();
		footer.setPadding(new Insets(8));
		footer.setAlignment(Pos.CENTER_LEFT);
		Label help = new Label("Tip: selecciona una tabla a la izquierda y pulsa 'Ejecutar consulta'.");
		help.setStyle("-fx-text-fill:#666;");
		footer.getChildren().add(help);
		root.setBottom(footer);

		return new Scene(root, 1000, 700);
	}

	// Ventana usuario programática (mejorada: fuentes, botones grandes y campo Nombre)
	private void showUserWindow() {
		Stage userStage = new Stage();
		userStage.initOwner(primaryStage);
		userStage.initModality(Modality.NONE);
		userStage.setTitle("Consultas Usuario - Seguridad (Cali)");

		BorderPane root = new BorderPane();
		root.setPadding(new Insets(12));
		// Fuente general más legible para este entorno
		root.setStyle("-fx-font-family: 'Segoe UI', 'Helvetica Neue', Arial; -fx-font-size: 13px;");

		// establecer icono del Stage usuario (si cargado)
		if (appLogo != null) {
			userStage.getIcons().add(appLogo);
		}

		// LEFT: botones de consultas con estilo más cómodo
		VBox left = new VBox(10);
		left.setPrefWidth(300);
		left.setPadding(new Insets(12));
		left.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #e6e6e6; -fx-border-width: 0 1 0 0;");
		Label leftTitle = new Label("Consultas rápidas");
		leftTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

		// Helper para crear botones grandes
		java.util.function.Function<String, Button> makeBigBtn = (text) -> {
			Button b = new Button(text);
			b.setMaxWidth(Double.MAX_VALUE);
			b.setStyle("-fx-background-color:#4a4e50; -fx-text-fill:white; -fx-background-radius:8; -fx-padding:10 12; -fx-font-size:13px;");
			b.setTooltip(new Tooltip("Ver " + text));
			return b;
		};

		Button bBarrios = makeBigBtn.apply("Barrios más Peligrosos");
		Button bCAIs = makeBigBtn.apply("CAIs y Estaciones");
		Button bRecientes = makeBigBtn.apply("Delitos 24h");
		Button bZonasSeguras = makeBigBtn.apply("Zonas Seguras (Bajo)");
		Button bDelitos = makeBigBtn.apply("Delitos Frecuentes");
		Button bHorarios = makeBigBtn.apply("Horarios más Peligrosos");
		Button bLugaresMas = makeBigBtn.apply("Lugares con más Denuncias");
		Button bDenunciasPorBarrio = makeBigBtn.apply("Denuncias por Barrio");
		Button bDenunciasPorDelito = makeBigBtn.apply("Denuncias por Delito");
		Button bPuntosCard = makeBigBtn.apply("Puntos Cardinales");
		Button bUbicaciones = makeBigBtn.apply("Listar Ubicaciones (completo)");

		left.getChildren().addAll(leftTitle, bBarrios, bCAIs, bRecientes, bZonasSeguras, bDelitos, bHorarios, bLugaresMas, bDenunciasPorBarrio, bDenunciasPorDelito, bPuntosCard, bUbicaciones);

		// CENTER: área de resultados con mayor padding
		TextArea resultArea = new TextArea();
		resultArea.setEditable(false);
		resultArea.setWrapText(true);
		resultArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
		BorderPane.setMargin(resultArea, new Insets(8));

		// RIGHT: panel de envío (mejorado)
		VBox sendBox = new VBox(10);
		sendBox.setPadding(new Insets(12));
		sendBox.setPrefWidth(320);
		sendBox.setStyle("-fx-background-color: white; -fx-border-color: #e6e6e6; -fx-border-width: 0 0 0 1;");

		Label lblAskTitle = new Label("Escribe tu pregunta o comentario:");
		lblAskTitle.setStyle("-fx-font-weight: 600;");

		// Campo nombre opcional
		Label lblName = new Label("Nombre (opcional):");
		TextField tfName = new TextField();
		tfName.setPromptText("Tu nombre");
		tfName.setMaxWidth(Double.MAX_VALUE);
		tfName.setStyle("-fx-padding:6; -fx-background-radius:6;");

		TextArea txtAsk = new TextArea();
		txtAsk.setPromptText("Escribe aquí... (pregunta o comentario)");
		txtAsk.setPrefRowCount(6);
		txtAsk.setWrapText(true);
		txtAsk.setStyle("-fx-padding:6; -fx-background-radius:6;");

		HBox sendRow = new HBox(8);
		sendRow.setAlignment(Pos.CENTER_LEFT);
		Button btnSend = new Button("Enviar al administrador");
		btnSend.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-pref-height: 36; -fx-pref-width:160;");
		btnSend.setTooltip(new Tooltip("Enviar mensaje al administrador"));
		Button btnClear = new Button("Limpiar");
		btnClear.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white; -fx-background-radius: 8; -fx-pref-height: 36;");
		sendRow.getChildren().addAll(btnSend, btnClear);

		Label lblHelp = new Label("Tu mensaje llegará al panel administrativo y se guardará para seguimiento.");
		lblHelp.setStyle("-fx-font-size:11px; -fx-text-fill:#666;");

		sendBox.getChildren().addAll(lblAskTitle, lblName, tfName, txtAsk, sendRow, lblHelp);

		// Pie con botón cerrar más visible
		HBox bottom = new HBox();
		bottom.setPadding(new Insets(10));
		bottom.setAlignment(Pos.CENTER_RIGHT);
		Button btnClose = new Button("Cerrar");
		btnClose.setStyle("-fx-background-color: #e99187; -fx-text-fill: white; -fx-background-radius:6; -fx-pref-height:34;");
		btnClose.setOnAction(e -> userStage.close());
		bottom.getChildren().add(btnClose);

		root.setLeft(left);
		root.setCenter(resultArea);
		root.setRight(sendBox);
		root.setBottom(bottom);

		// Mapear consultas (reutiliza runQueryAsync)
		bBarrios.setOnAction(e -> runQueryAsync(resultArea, "Barrios más Peligrosos",
				"SELECT z.nombre AS Barrio, COUNT(d.id_denuncia) AS Denuncias FROM Zona z JOIN Ubicacion u ON z.id_zona = u.id_zona JOIN LugarDenuncias l ON u.id_ubicacion = l.id_ubicacion JOIN Denuncia d ON l.id_lugar = d.id_lugar GROUP BY z.nombre ORDER BY Denuncias DESC LIMIT 10"));
		bCAIs.setOnAction(e -> runQueryAsync(resultArea, "CAIs y Estaciones",
				"SELECT nombre, direccion, telefono FROM LugarDenuncias WHERE nombre LIKE '%CAI%' OR nombre LIKE '%Estación%'"));
		bRecientes.setOnAction(e -> runQueryAsync(resultArea, "Delitos Recientes (24h)",
				"SELECT d.fecha, d.hora, d.descripcion, z.nombre AS zona FROM Denuncia d JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion JOIN Zona z ON u.id_zona = z.id_zona WHERE d.fecha >= CURDATE() - INTERVAL 1 DAY ORDER BY d.fecha DESC, d.hora DESC"));
		bZonasSeguras.setOnAction(e -> runQueryAsync(resultArea, "Zonas Seguras (Bajo)",
				"SELECT DISTINCT z.nombre AS Zona, nr.riesgo AS Nivel FROM Zona z JOIN Ubicacion u ON z.id_zona = u.id_zona JOIN NivelRiesgo nr ON u.id_nivel = nr.id_nivel WHERE nr.riesgo = 'Bajo' LIMIT 50"));
		bDelitos.setOnAction(e -> runQueryAsync(resultArea, "Delitos Frecuentes",
				"SELECT tipo_delito, COUNT(*) AS Total FROM Delito GROUP BY tipo_delito ORDER BY Total DESC LIMIT 20"));
		bHorarios.setOnAction(e -> runQueryAsync(resultArea, "Horarios más Peligrosos",
				"SELECT HOUR(hora) AS Hora, COUNT(*) AS Denuncias FROM Denuncia GROUP BY HOUR(hora) ORDER BY Denuncias DESC LIMIT 24"));
		bLugaresMas.setOnAction(e -> runQueryAsync(resultArea, "Lugares con más Denuncias",
				"SELECT l.nombre AS Lugar, l.direccion AS Direccion, COUNT(d.id_denuncia) AS Denuncias FROM LugarDenuncias l LEFT JOIN Denuncia d ON l.id_lugar = d.id_lugar GROUP BY l.id_lugar ORDER BY Denuncias DESC LIMIT 20"));
		bDenunciasPorBarrio.setOnAction(e -> runQueryAsync(resultArea, "Denuncias por Barrio",
				"SELECT z.nombre AS Barrio, COUNT(d.id_denuncia) AS Denuncias FROM Denuncia d JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion JOIN Zona z ON u.id_zona = z.id_zona GROUP BY z.nombre ORDER BY Denuncias DESC"));
		bDenunciasPorDelito.setOnAction(e -> runQueryAsync(resultArea, "Denuncias por Delito",
				"SELECT dl.tipo_delito AS Delito, COUNT(*) AS Total FROM Denuncia_Delito dd JOIN Delito dl ON dd.id_delito = dl.id_delito GROUP BY dl.tipo_delito ORDER BY Total DESC"));
		bPuntosCard.setOnAction(e -> runQueryAsync(resultArea, "Puntos Cardinales",
				"SELECT id_punto_cardinal, nombre FROM PuntoCardinal"));
		bUbicaciones.setOnAction(e -> runQueryAsync(resultArea, "Ubicaciones (completo)",
				"SELECT id_ubicacion, direccion, id_nivel, id_zona, id_punto_cardinal FROM Ubicacion LIMIT 200"));

		// Limpiar
		btnClear.setOnAction(e -> txtAsk.clear());

		// Habilitar btnSend solo si hay texto
		btnSend.disableProperty().bind(txtAsk.textProperty().isEmpty());

		// Envío: persistir en BD y actualizar lista observable
		btnSend.setOnAction(ev -> {
			String text = txtAsk.getText();
			if (text == null || text.trim().isEmpty()) {
				new Alert(Alert.AlertType.WARNING, "Ingrese un texto antes de enviar.", ButtonType.OK).showAndWait();
				return;
			}
			String sender = (tfName.getText() != null && !tfName.getText().trim().isEmpty()) ? tfName.getText().trim() : "Usuario";
			try {
				Notification created = notificationDAO.insertNotification(sender, text.trim());
				if (created != null) {
					notifications.add(0, created);
				}
				txtAsk.clear();
				new Alert(Alert.AlertType.INFORMATION, "Su mensaje fue enviado al administrador.", ButtonType.OK).showAndWait();
			} catch (Exception ex) {
				ex.printStackTrace();
				new Alert(Alert.AlertType.ERROR, "Error enviando mensaje: " + ex.getMessage(), ButtonType.OK).showAndWait();
			}
		});

		Scene scene = new Scene(root, WINDOW_W, WINDOW_H);
		userStage.setScene(scene);
		userStage.show();
	}

	// Ejecuta consulta en background y vuelca a TextArea
	private void runQueryAsync(TextArea target, String label, String sql) {
		target.setText("Ejecutando: " + label + " ...\n");
		Task<String> task = new Task<>() {
			@Override
			protected String call() {
				try {
					if (conexion == null || conexion.isClosed()) {
						conexion = conexionBD.getConnection();
						consultasDB = new ConsultasDB(conexion);
					}
					return consultasDB.ejecutarConsultaFormateada(sql);
				} catch (Exception ex) {
					ex.printStackTrace();
					return "Error ejecutando consulta: " + ex.getMessage();
				}
			}
		};
		task.setOnSucceeded(ev -> target.setText(task.getValue()));
		task.setOnFailed(ev -> target.setText("Error: " + task.getException().getMessage()));
		new Thread(task).start();
	}

	public static void main(String[] args) {
		launch(args);
	}

	// ------------------- Helpers CRUD y metadata -------------------

	// Recolecta valores del formBox (VBox de HBox{Label,TextField})
	private java.util.Map<String,String> collectFormValues(VBox formBox) {
		java.util.Map<String,String> map = new java.util.LinkedHashMap<>();
		for (javafx.scene.Node rowNode : formBox.getChildren()) {
			if (!(rowNode instanceof HBox)) continue;
			HBox row = (HBox) rowNode;
			TextField tf = null;
			Label lbl = null;
			for (javafx.scene.Node n : row.getChildren()) {
				if (n instanceof Label) lbl = (Label)n;
				if (n instanceof TextField) tf = (TextField)n;
			}
			if (lbl!=null && tf!=null) {
				String col = lbl.getText();
				String val = tf.getText();
				map.put(col, val);
			}
		}
		return map;
	}

	// Obtiene nombre de la PK usando DatabaseMetaData
	private String getPrimaryKeyColumn(String table) {
		if (conexion == null) return null;
		try {
			java.sql.DatabaseMetaData md = conexion.getMetaData();
			try (java.sql.ResultSet rs = md.getPrimaryKeys(null, null, table)) {
				if (rs.next()) return rs.getString("COLUMN_NAME");
			}
		} catch (Exception e) {
			// ignore
		}
		// fallback: intentar consultar INFORMATION_SCHEMA
		try (java.sql.PreparedStatement ps = conexion.prepareStatement(
				"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND CONSTRAINT_NAME = 'PRIMARY'")) {
			ps.setString(1, table);
			try (java.sql.ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getString(1);
			}
		} catch (Exception ex) { /* ignore */ }
		return null;
	}

	// Obtener columnas de tabla
	private java.util.List<String> getTableColumns(String table) throws Exception {
		java.util.List<String> cols = new java.util.ArrayList<>();
		if (conexion==null) throw new Exception("Conexión nula");
		String sql = "SELECT * FROM `" + table + "` LIMIT 1";
		try (java.sql.Statement st = conexion.createStatement(); java.sql.ResultSet rs = st.executeQuery(sql)) {
			java.sql.ResultSetMetaData md = rs.getMetaData();
			for (int i=1;i<=md.getColumnCount();i++) cols.add(md.getColumnLabel(i));
		} catch (java.sql.SQLException e) {
			// si la tabla está vacía, usar DatabaseMetaData
			try (java.sql.ResultSet rs = conexion.getMetaData().getColumns(null, null, table, null)) {
				while (rs.next()) cols.add(rs.getString("COLUMN_NAME"));
			}
		}
		// Si sigue vacía, intenta INFORMATION_SCHEMA
		if (cols.isEmpty()) {
			try (java.sql.PreparedStatement ps = conexion.prepareStatement(
					"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION")) {
				ps.setString(1, table);
				try (java.sql.ResultSet rs = ps.executeQuery()) {
					while (rs.next()) cols.add(rs.getString(1));
				}
			}
		}
		return cols;
	}

	// Execute INSERT
	private void executeInsert(String table, java.util.Map<String,String> values) throws Exception {
		if (values.isEmpty()) throw new Exception("Sin valores");
		StringBuilder cols = new StringBuilder();
		StringBuilder params = new StringBuilder();
		java.util.List<String> vals = new java.util.ArrayList<>();
		for (String c : values.keySet()) {
			if (cols.length()>0) { cols.append(", "); params.append(", "); }
			cols.append("`").append(c).append("`");
			params.append("?");
			vals.add(values.get(c));
		}
		String sql = "INSERT INTO `" + table + "` (" + cols.toString() + ") VALUES (" + params.toString() + ")";
		try (java.sql.PreparedStatement ps = conexion.prepareStatement(sql)) {
			for (int i=0;i<vals.size();i++) ps.setString(i+1, vals.get(i));
			ps.executeUpdate();
		}
	}

	// Execute UPDATE using pkCol=pkVal
	private void executeUpdate(String table, java.util.Map<String,String> values, String pkCol, String pkVal) throws Exception {
		if (values.isEmpty()) throw new Exception("Sin valores");
		StringBuilder set = new StringBuilder();
		java.util.List<String> vals = new java.util.ArrayList<>();
		for (String c : values.keySet()) {
			if (c.equals(pkCol)) continue; // skip pk
			if (set.length()>0) set.append(", ");
			set.append("`").append(c).append("` = ?");
			vals.add(values.get(c));
		}
		if (set.length()==0) throw new Exception("No hay campos para actualizar");
		String sql = "UPDATE `" + table + "` SET " + set + " WHERE `" + pkCol + "` = ?";
		try (java.sql.PreparedStatement ps = conexion.prepareStatement(sql)) {
			int i=1;
			for (String v : vals) ps.setString(i++, v);
			ps.setString(i, pkVal);
			ps.executeUpdate();
		}
	}

	// Execute DELETE
	private void executeDelete(String table, String pkCol, String pkVal) throws Exception {
		String sql = "DELETE FROM `" + table + "` WHERE `" + pkCol + "` = ?";
		try (java.sql.PreparedStatement ps = conexion.prepareStatement(sql)) {
			ps.setString(1, pkVal);
			ps.executeUpdate();
		}
	}

	// Nuevo helper que devuelve el nombre real de la tabla
	private String resolveTableName(String displayName) {
		if (displayName == null) return null;
		String t = tableNameMap.get(displayName);
		if (t != null && !t.trim().isEmpty()) return t;
		// fallback: devolver displayName sin espacios y sin caracteres extra
		return displayName.replaceAll("\\s+", "");
	}
}
