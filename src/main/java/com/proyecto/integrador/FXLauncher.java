package com.proyecto.integrador;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Node; // <--- IMPORT ADDED

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

	// NUEVO: tamaño deseado para ventanas de perfil (usuario/admin)
	private static final double PROFILE_W = 1300; // ancho objetivo
	private static final double PROFILE_H = 720;  // alto objetivo

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
		Scene mainScene = new Scene(mainRoot, PROFILE_W, PROFILE_H); // usar dimensiones perfil para toda la app

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
		// Contenido central: título + subtítulo + tiles grandes centradas
		VBox center = new VBox(18);
		center.setPadding(new Insets(32));
		center.setAlignment(Pos.TOP_LEFT);

		// Header (logo + title)
		HBox header = new HBox(12);
		header.setAlignment(Pos.CENTER_LEFT);
		if (appLogo != null) {
			ImageView logoView = new ImageView(appLogo);
			logoView.setFitWidth(64);
			logoView.setFitHeight(64);
			header.getChildren().add(logoView);
		}
		VBox titles = new VBox(2);
		Label title = new Label("Portal Seguridad Cali");
		title.setStyle("-fx-font-size:28px; -fx-font-weight:700; -fx-text-fill:#222;");
		Label subtitle = new Label("Selecciona tu portal de acceso");
		subtitle.setStyle("-fx-font-size:14px; -fx-text-fill:#666;");
		titles.getChildren().addAll(title, subtitle);
		header.getChildren().add(titles);

		// Tiles container: centra y adapta a ancho (2 columnas en desktop)
		FlowPane tiles = new FlowPane();
		tiles.setHgap(28);
		tiles.setVgap(20);
		tiles.setPadding(new Insets(24, 0, 0, 0));
		tiles.setPrefWrapLength(PROFILE_W - 300); // ayuda al wrapping
		tiles.setAlignment(Pos.CENTER_LEFT);

		// Helper local para crear las tarjetas grandes estilo web
		java.util.function.BiFunction<String,String, StackPane> makeCard = (labelText, iconPath) -> {
			StackPane card = new StackPane();
			card.setPrefSize(420, 200);
			card.setMaxSize(420, 200);
			card.setStyle("-fx-background-color: white; -fx-border-color: #e9e9e9; -fx-border-radius:10; -fx-background-radius:10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 6);");
			VBox content = new VBox(10);
			content.setAlignment(Pos.CENTER);
			// intentar cargar icono; si no, emoji fallback
			ImageView iv = null;
			try (InputStream is = getClass().getResourceAsStream(iconPath)) {
				if (is != null) {
					Image img = new Image(is, 96, 96, true, true);
					iv = new ImageView(img);
				}
			} catch (Exception ex) { /* ignora */ }
			if (iv != null) content.getChildren().add(iv);
			else {
				Label emoji = new Label("👤");
				emoji.setStyle("-fx-font-size:64px;");
				content.getChildren().add(emoji);
			}
			Label lbl = new Label(labelText);
			lbl.setStyle("-fx-font-size:18px; -fx-font-weight:700; -fx-text-fill:#222;");
			content.getChildren().add(lbl);
			card.getChildren().add(content);

			// hover
			card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #e1e7f3; -fx-border-radius:10; -fx-background-radius:10; -fx-effect: dropshadow(gaussian, rgba(31,97,204,0.12), 18, 0, 0, 8);"));
			card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #e9e9e9; -fx-border-radius:10; -fx-background-radius:10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 6);"));
			return card;
		};

		StackPane cardUser = makeCard.apply("Usuario", "/images/user_icon_new.png");
		StackPane cardAdmin = makeCard.apply("Administrador", "/images/admin_icon.png");

		// click handlers
		cardUser.setOnMouseClicked(e -> showUserWindow());
		cardAdmin.setOnMouseClicked(e -> primaryStage.setScene(buildAdminLoginScene()));

		tiles.getChildren().addAll(cardUser, cardAdmin);

		// Añadir header y tiles al centro
		center.getChildren().addAll(header, tiles);

		// Pie de página pequeño
		Label footerLabel = new Label("Desarrollado por estudiantes de 4to semestre - Proyecto Integrador");
		footerLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 12px;");
		VBox wrapper = new VBox(12, center, footerLabel);
		wrapper.setPadding(new Insets(12));
		wrapper.setAlignment(Pos.TOP_LEFT);

		// Envolver en el shell para aspecto web (topbar + sidebar)
		Parent shell = createAppShell(wrapper, null); // la createAppShell pone el topbar/side
		return shell;
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

	// Admin login scene (programática) - diseño tipo web/desktop dentro del shell
	private Scene buildAdminLoginScene() {
		// Formulario centrado, ancho fijo, estilo de escritorio
		GridPane form = new GridPane();
		form.setHgap(12);
		form.setVgap(10);
		form.setPadding(new Insets(24));
		form.setMaxWidth(520);
		form.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 6);");

		Label title = new Label("Administrador - Iniciar Sesión");
		title.setStyle("-fx-font-size:20px; -fx-font-weight:700; -fx-text-fill: #222;");
		GridPane.setColumnSpan(title, 2);
		form.add(title, 0, 0);

		Label lblUser = new Label("Usuario:");
		TextField tfUser = new TextField();
		tfUser.setPromptText("Usuario");
		tfUser.setMaxWidth(Double.MAX_VALUE);
		tfUser.setPrefWidth(360);

		Label lblPass = new Label("Contraseña:");
		PasswordField pf = new PasswordField();
		pf.setPromptText("Contraseña");
		pf.setMaxWidth(Double.MAX_VALUE);
		pf.setPrefWidth(360);

		form.add(lblUser, 0, 1);
		form.add(tfUser, 1, 1);
		form.add(lblPass, 0, 2);
		form.add(pf, 1, 2);

		HBox buttons = new HBox(12);
		buttons.setAlignment(Pos.CENTER_LEFT);
		Button btnIngresar = new Button("Ingresar");
		btnIngresar.setStyle("-fx-background-color:#42b983; -fx-text-fill:white; -fx-font-weight:600; -fx-pref-width:120; -fx-pref-height:36;");
		Button btnVolver = new Button("Volver");
		btnVolver.setStyle("-fx-background-color: transparent; -fx-border-color:#ddd; -fx-pref-width:90; -fx-pref-height:36;");
		buttons.getChildren().addAll(btnIngresar, btnVolver);
		form.add(buttons, 1, 3);

		Label lblInfo = new Label();
		lblInfo.setStyle("-fx-text-fill: #d32f2f;");
		GridPane.setColumnSpan(lblInfo, 2);
		form.add(lblInfo, 0, 4);

		// Acciones
		btnVolver.setOnAction(ev -> primaryStage.setScene(new Scene(buildMainMenu(), PROFILE_W, PROFILE_H)));

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
						Platform.runLater(() -> lblInfo.setText("Error al autenticar: " + ex.getMessage()));
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
					primaryStage.setWidth(PROFILE_W);
					primaryStage.setHeight(PROFILE_H);
					primaryStage.centerOnScreen();
				} else {
					lblInfo.setText("Credenciales incorrectas");
				}
			});
			authTask.setOnFailed(t -> lblInfo.setText("Error autenticando: " + authTask.getException().getMessage()));
			new Thread(authTask).start();
		});

		// Envolver el form en el shell para aspecto "web"
		Parent shell = createAppShell(form, "Acceso Administrador");
		return new Scene(shell, PROFILE_W, PROFILE_H);
	}

	// Admin panel (programático) con CRUD basics (lista de tablas, results area, form placeholder)
	private Scene buildAdminScene() {
		// Conservamos el BorderPane 'root' tal cual para el contenido del centro del shell
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(12));

		// Left: selección de tablas
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

		// Center: resultados ahora con container para TableView o TextArea
		BorderPane resultsPane = new BorderPane();
		TextArea txtResultadoArea = new TextArea();
		txtResultadoArea.setEditable(false);
		txtResultadoArea.setWrapText(false);
		txtResultadoArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
		resultsPane.setCenter(txtResultadoArea);

		// Right: formulario CRUD simplificado
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

		// Panel de notificaciones (encima del CRUD formBox)
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
				resultsPane.setCenter(txtResultadoArea);
				txtResultadoArea.setText(full + "\n\n" + (txtResultadoArea.getText() != null ? txtResultadoArea.getText() : ""));
			}
		});

		// insertar notificaciones al inicio del right pane
		right.getChildren().add(0, new VBox(new Label("Notificaciones"), notifList, btnMarkRead, new Separator()));

		// Wiring ejecución de consulta: usa helper que muestra tabla si es SELECT
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
			String sql = "SELECT * FROM `" + tabla.replace("`", "") + "`";
			txtResultadoArea.setText("Ejecutando: " + sql + " ...\n");
			executeQueryAndShowInTable(sql, resultsPane, txtResultadoArea);
		});

		// Construir formulario dinámico al seleccionar tabla
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

		// CRUD handlers (Crear / Actualizar / Eliminar) - mantienen comportamiento previo
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
			t.setOnSucceeded(r -> { infoCrud.setText(t.getValue()); btnEjecutar.fire(); });
			t.setOnFailed(r -> infoCrud.setText("Error: " + t.getException().getMessage()));
			new Thread(t).start();
		});
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
			t.setOnSucceeded(r -> { infoCrud.setText(t.getValue()); btnEjecutar.fire(); });
			t.setOnFailed(r -> infoCrud.setText("Error: " + t.getException().getMessage()));
			new Thread(t).start();
		});
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
		root.setCenter(resultsPane);
		root.setRight(right);

		// Pie de página con ayuda rápida
		HBox footer = new HBox();
		footer.setPadding(new Insets(8));
		footer.setAlignment(Pos.CENTER_LEFT);
		Label help = new Label("Tip: selecciona una tabla a la izquierda y pulsa 'Ejecutar consulta'.");
		help.setStyle("-fx-text-fill:#666;");
		footer.getChildren().add(help);
		root.setBottom(footer);

		// envolver el 'root' en el shell
		Parent shell = createAppShell(root, "Panel Administrativo");
		return new Scene(shell, PROFILE_W, PROFILE_H);
	}

	// Ventana usuario ahora integrada en la ventana principal (no crea Stage nuevo)
	private void showUserWindow() {
		// Construir layout exactamente como antes pero sin Stage nuevo
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(12));
		root.setStyle("-fx-font-family: 'Segoe UI', 'Helvetica Neue', Arial; -fx-font-size: 13px;");

		// LEFT: botones de consultas (mantener estilo y márgenes)
		VBox left = new VBox(12);
		left.setPrefWidth(260);
		left.setPadding(new Insets(16));
		left.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #e6e6e6; -fx-border-width: 0 1 0 0;");
		Label leftTitle = new Label("Consultas rápidas");
		leftTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

		// Helper para crear botones grandes (usa el mismo estilo que antes)
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

		// CENTER: results container (muestra TableView para SELECT o TextArea para fallback)
		BorderPane resultsPane = new BorderPane();
		TextArea resultArea = new TextArea();
		resultArea.setEditable(false);
		resultArea.setWrapText(true);
		resultArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
		resultsPane.setCenter(resultArea);

		// RIGHT: panel de envío (mantener tal cual)
		VBox sendBox = new VBox(10);
		sendBox.setPadding(new Insets(12));
		sendBox.setPrefWidth(320);
		sendBox.setStyle("-fx-background-color: white; -fx-border-color: #e6e6e6; -fx-border-width: 0 0 0 1;");
		Label lblAskTitle = new Label("Escribe tu pregunta o comentario:");
		lblAskTitle.setStyle("-fx-font-weight: 600;");
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

		// Bottom: cerrar (ahora vuelve al menu principal en la misma ventana)
		HBox bottom = new HBox();
		bottom.setPadding(new Insets(10));
		bottom.setAlignment(Pos.CENTER_RIGHT);
		Button btnClose = new Button("Cerrar");
		btnClose.setStyle("-fx-background-color: #e99187; -fx-text-fill: white; -fx-background-radius:6; -fx-pref-height:34;");
		btnClose.setOnAction(e -> {
			// volver al menú principal en la misma ventana
			Parent main = buildMainMenu();
			primaryStage.setScene(new Scene(main, PROFILE_W, PROFILE_H));
			primaryStage.centerOnScreen();
		});
		bottom.getChildren().add(btnClose);

		// Set panes
		root.setLeft(left);
		root.setCenter(resultsPane);
		root.setRight(sendBox);
		root.setBottom(bottom);

		// Asignar handlers a TODOS los botones, usando tabla formateada (executeQueryAndShowInTable)
		bBarrios.setOnAction(e -> {
			String sql = "SELECT z.nombre AS Barrio, COUNT(d.id_denuncia) AS Denuncias FROM Zona z JOIN Ubicacion u ON z.id_zona = u.id_zona JOIN LugarDenuncias l ON u.id_ubicacion = l.id_ubicacion JOIN Denuncia d ON l.id_lugar = d.id_lugar GROUP BY z.nombre ORDER BY Denuncias DESC LIMIT 10";
			resultArea.setText("Ejecutando: Barrios más Peligrosos ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});
		bCAIs.setOnAction(e -> {
			String sql = "SELECT nombre, direccion, telefono FROM LugarDenuncias WHERE nombre LIKE '%CAI%' OR nombre LIKE '%Estación%'";
			resultArea.setText("Ejecutando: CAIs y Estaciones ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});
		bRecientes.setOnAction(e -> {
			String sql = "SELECT d.fecha, d.hora, d.descripcion, z.nombre AS zona FROM Denuncia d JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion JOIN Zona z ON u.id_zona = z.id_zona WHERE d.fecha >= CURDATE() - INTERVAL 1 DAY ORDER BY d.fecha DESC, d.hora DESC";
			resultArea.setText("Ejecutando: Delitos 24h ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});
		bZonasSeguras.setOnAction(e -> {
			String sql = "SELECT DISTINCT z.nombre AS Zona, nr.riesgo AS Nivel FROM Zona z JOIN Ubicacion u ON z.id_zona = u.id_zona JOIN NivelRiesgo nr ON u.id_nivel = nr.id_nivel WHERE nr.riesgo = 'Bajo' LIMIT 50";
			resultArea.setText("Ejecutando: Zonas Seguras (Bajo) ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});
		bDelitos.setOnAction(e -> {
			String sql = "SELECT tipo_delito, COUNT(*) AS Total FROM Delito GROUP BY tipo_delito ORDER BY Total DESC LIMIT 20";
			resultArea.setText("Ejecutando: Delitos Frecuentes ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});
		bHorarios.setOnAction(e -> {
			String sql = "SELECT HOUR(hora) AS Hora, COUNT(*) AS Denuncias FROM Denuncia GROUP BY HOUR(hora) ORDER BY Denuncias DESC LIMIT 24";
			resultArea.setText("Ejecutando: Horarios más Peligrosos ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});
		bLugaresMas.setOnAction(e -> {
			String sql = "SELECT l.nombre AS Lugar, l.direccion AS Direccion, COUNT(d.id_denuncia) AS Denuncias FROM LugarDenuncias l LEFT JOIN Denuncia d ON l.id_lugar = d.id_lugar GROUP BY l.id_lugar ORDER BY Denuncias DESC LIMIT 20";
			resultArea.setText("Ejecutando: Lugares con más Denuncias ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});
		bDenunciasPorBarrio.setOnAction(e -> {
			String sql = "SELECT z.nombre AS Barrio, COUNT(d.id_denuncia) AS Denuncias FROM Denuncia d JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion JOIN Zona z ON u.id_zona = z.id_zona GROUP BY z.nombre ORDER BY Denuncias DESC";
			resultArea.setText("Ejecutando: Denuncias por Barrio ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});
		bDenunciasPorDelito.setOnAction(e -> {
			String sql = "SELECT dl.tipo_delito AS Delito, COUNT(*) AS Total FROM Denuncia_Delito dd JOIN Delito dl ON dd.id_delito = dl.id_delito GROUP BY dl.tipo_delito ORDER BY Total DESC";
			resultArea.setText("Ejecutando: Denuncias por Delito ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});
		bPuntosCard.setOnAction(e -> {
			String sql = "SELECT id_punto_cardinal, nombre FROM PuntoCardinal";
			resultArea.setText("Ejecutando: Puntos Cardinales ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});
		bUbicaciones.setOnAction(e -> {
			String sql = "SELECT id_ubicacion, direccion, id_nivel, id_zona, id_punto_cardinal FROM Ubicacion LIMIT 200";
			resultArea.setText("Ejecutando: Ubicaciones (completo) ...");
			executeQueryAndShowInTable(sql, resultsPane, resultArea);
		});

		// Limpiar y enviar mantienen su comportamiento
		btnClear.setOnAction(e -> txtAsk.clear());
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

		// Envolver la vista del usuario en el shell (topbar + sidebar) y colocar en la ventana principal
		Parent shell = createAppShell(root, "Consultas Ciudadanas");
		Scene scene = new Scene(shell, PROFILE_W, PROFILE_H);
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
	}

	// --- START: NEW: app shell helpers (sidebar + topbar) ---
	// Crear top bar azul similar a dashboard web
	private HBox createTopBar(String pageTitle) {
		HBox top = new HBox();
		top.setStyle("-fx-background-color: #2f98e6; -fx-padding: 12px; -fx-alignment: center-left;");
		top.setSpacing(12);

		// menu icon (simple)
		Label menuIcon = new Label("\u2630"); // hamburger
		menuIcon.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 0 12 0 12;");

		// title
		Label title = new Label(pageTitle != null ? pageTitle : "Portal Seguridad Cali");
		title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: 600;");

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		// user avatar / info on the right
		HBox userBox = new HBox(8);
		userBox.setAlignment(Pos.CENTER_RIGHT);
		ImageView avatar = null;
		if (appLogo != null) {
			try {
				avatar = new ImageView(appLogo);
				avatar.setFitWidth(36);
				avatar.setFitHeight(36);
				avatar.setStyle("-fx-background-radius: 18;");
			} catch (Exception ex) {
				avatar = null;
			}
		}
		Label userLabel = new Label("admin");
		userLabel.setStyle("-fx-text-fill: white; -fx-font-weight: 600;");
		if (avatar != null) userBox.getChildren().addAll(avatar, userLabel);
		else userBox.getChildren().add(userLabel);

		top.getChildren().addAll(menuIcon, title, spacer, userBox);
		return top;
	}

	// Crear sidebar izquierdo con íconos y botones
	private VBox createSidebar() {
		VBox side = new VBox(12);
		side.setStyle("-fx-background-color: #ffffff; -fx-padding: 18px 12px; -fx-border-color: rgba(0,0,0,0.04); -fx-border-width: 0 1 0 0;");
		side.setPrefWidth(220);
		side.setMinWidth(180);

		// logo area
		VBox logoArea = new VBox(6);
		logoArea.setAlignment(Pos.CENTER_LEFT);
		if (appLogo != null) {
			ImageView logo = new ImageView(appLogo);
			logo.setFitWidth(48);
			logo.setFitHeight(48);
			logoArea.getChildren().add(logo);
		}
		Label appName = new Label("Portal Seguridad");
		appName.setStyle("-fx-font-weight:700; -fx-font-size: 14px;");
		logoArea.getChildren().add(appName);

		// nav buttons
		Button btnHome = new Button("Inicio");
		Button btnUsuario = new Button("Consultas");
		Button btnAdmin = new Button("Administración");
		// Nuevo: botón Ayuda
		Button btnHelp = new Button("Ayuda");
		for (Button b : new Button[]{btnHome, btnUsuario, btnAdmin}) {
			b.setMaxWidth(Double.MAX_VALUE);
			b.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-padding:8 12;");
		}
		// estilo coherente para Ayuda
		btnHelp.setMaxWidth(Double.MAX_VALUE);
		btnHelp.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-padding:8 12;");

		// small handlers to show scenes (non-intrusive)
		btnHome.setOnAction(e -> primaryStage.setScene(new Scene(buildMainMenu(), PROFILE_W, PROFILE_H)));
		btnUsuario.setOnAction(e -> showUserWindow());
		btnAdmin.setOnAction(e -> primaryStage.setScene(buildAdminLoginScene()));
		// handler para la vista de ayuda (misma ventana)
		btnHelp.setOnAction(e -> showHelpView());

		// footer small copyright
		Region spacer = new Region();
		VBox.setVgrow(spacer, Priority.ALWAYS);
		Label copy = new Label("\u00A9 2024 Proyecto Integrador");
		copy.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

		side.getChildren().addAll(logoArea, new Separator(), btnHome, btnUsuario, btnAdmin, btnHelp, spacer, copy);
		return side;
	}

	// Nueva vista de Ayuda (se muestra en la misma ventana usando el shell)
	private void showHelpView() {
		VBox help = new VBox(12);
		help.setPadding(new Insets(20));
		help.setAlignment(Pos.TOP_LEFT);

		Label hTitle = new Label("Ayuda / Información");
		hTitle.setStyle("-fx-font-size:20px; -fx-font-weight:700;");
		Label hSub = new Label("Portal Seguridad Cali - Versión 1.0");
		hSub.setStyle("-fx-text-fill:#666;");

		// Desarrolladores (ejemplo)
		VBox devs = new VBox(6);
		devs.getChildren().add(new Label("Desarrolladores:"));
		devs.getChildren().add(new Label(" - Daniel Campo  : 312 7589036"));
		devs.getChildren().add(new Label(" - Sebastian Adabia: 312 2693918"));
		devs.getChildren().add(new Label(" - Juan Sebastian  : 322 5973565"));

		// Información adicional/comp contact
		VBox info = new VBox(6);
		info.getChildren().add(new Label("Contacto del proyecto: proyecto@ejemplo.com"));
		info.getChildren().add(new Label("Licencia: MIT (ejemplo)"));
		info.getChildren().add(new Label("Notas: Esta es una versión de prueba - reporte bugs al equipo."));

		Separator sep = new Separator();

		// Botón para volver al menú principal (misma ventana)
		HBox actions = new HBox(8);
		Button btnVolver = new Button("Volver al menú");
		btnVolver.setStyle("-fx-background-color:#2b7cff; -fx-text-fill:white;");
		btnVolver.setOnAction(e -> primaryStage.setScene(new Scene(buildMainMenu(), PROFILE_W, PROFILE_H)));
		actions.getChildren().add(btnVolver);

		help.getChildren().addAll(hTitle, hSub, sep, devs, new Separator(), info, actions);

		Parent shell = createAppShell(help, "Ayuda");
		primaryStage.setScene(new Scene(shell, PROFILE_W, PROFILE_H));
		primaryStage.centerOnScreen();
	}
	// --- END: NEW: app shell helpers ---

	// ----------------------------------------------------------------
	// Nota: se eliminó aquí la copia duplicada de executeQueryAndShowInTable
	// La implementación única y definitiva se mantiene más abajo en el archivo
	// (busca "Ejecuta la consulta SQL y si es SELECT construye una TableView")
	// ----------------------------------------------------------------

	// Ejecuta la consulta SQL y si es SELECT construye una TableView dinámicamente y la coloca en resultsPane.
	private void executeQueryAndShowInTable(String sql, BorderPane resultsPane, TextArea fallbackArea) {
		// listas finales mutables que llenaremos en el hilo de fondo
		final java.util.List<String> columnNames = new java.util.ArrayList<>();
		final java.util.List<java.util.List<String>> rows = new java.util.ArrayList<>();

		Task<Void> task = new Task<>() {
			@Override
			protected Void call() {
				if (sql == null) {
					Platform.runLater(() -> {
						resultsPane.setCenter(fallbackArea);
						fallbackArea.setText("Consulta vacía");
					});
					return null;
				}
				try {
					if (conexion == null || conexion.isClosed()) {
						conexion = conexionBD.getConnection();
						consultasDB = new ConsultasDB(conexion);
					}

					String trimmed = sql.trim().toUpperCase();
					// Si no es SELECT, usar el formateador textual
					if (!trimmed.startsWith("SELECT")) {
						String res = consultasDB.ejecutarConsultaFormateada(sql);
						Platform.runLater(() -> {
							resultsPane.setCenter(fallbackArea);
							fallbackArea.setText(res);
						});
						return null;
					}

					// Ejecutar SELECT y llenar columnNames y rows
					try (java.sql.Statement st = conexion.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
						 java.sql.ResultSet rs = st.executeQuery(sql)) {

						java.sql.ResultSetMetaData md = rs.getMetaData();
						int cols = md.getColumnCount();
						for (int i = 1; i <= cols; i++) {
							String label = md.getColumnLabel(i);
							columnNames.add(label != null ? label : ("col" + i));
						}

						while (rs.next()) {
							java.util.List<String> row = new java.util.ArrayList<>(columnNames.size());
							for (int i = 1; i <= columnNames.size(); i++) {
								String v = rs.getString(i);
								row.add(v != null ? v : "");
							}
							rows.add(row);
						}
					}
					// Construir TableView en UI thread
					Platform.runLater(() -> {
						TableView<ObservableList<String>> table = new TableView<>();
						table.getColumns().clear();
						for (int i = 0; i < columnNames.size(); i++) {
							final int colIndex = i;
							TableColumn<ObservableList<String>, String> col = new TableColumn<>(columnNames.get(i));
							col.setCellValueFactory(cellData -> {
								ObservableList<String> row = cellData.getValue();
								if (colIndex < row.size()) return new ReadOnlyStringWrapper(row.get(colIndex));
								return new ReadOnlyStringWrapper("");
							});
							col.setStyle("-fx-alignment: CENTER-LEFT;");
							table.getColumns().add(col);
						}

						ObservableList<ObservableList<String>> tableData = FXCollections.observableArrayList();
						for (java.util.List<String> r : rows) {
							tableData.add(FXCollections.observableArrayList(r));
						}
						table.setItems(tableData);
						table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
						table.setPlaceholder(new Label("No hay datos"));

						StackPane wrapper = new StackPane(table);
						wrapper.setPadding(new Insets(6, 8, 6, 8));
						resultsPane.setCenter(wrapper);
					});
				} catch (Exception ex) {
					ex.printStackTrace();
					Platform.runLater(() -> {
						resultsPane.setCenter(fallbackArea);
						fallbackArea.setText("Error ejecutando consulta: " + ex.getMessage());
					});
				}
				return null;
			}
		};
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
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

	// Nuevo helper que devuelve el nombre real de la tabla (implementado)
	private String resolveTableName(String displayName) {
		if (displayName == null) return null;
		String t = tableNameMap.get(displayName);
		if (t != null && !t.trim().isEmpty()) return t;
		// fallback: devolver displayName sin espacios y sin caracteres extra
		return displayName.replaceAll("[^A-Za-z0-9_]", "").replaceAll("\\s+", "");
	}

	// Encapsula cualquier contenido en el shell: topbar + sidebar + center content
	private Parent createAppShell(Node centerContent, String pageTitle) {
		BorderPane shell = new BorderPane();

		// Topbar
		HBox top = createTopBar(pageTitle);
		shell.setTop(top);

		// Sidebar
		VBox side = createSidebar();
		shell.setLeft(side);

		// Center: colocar contenido dentro de un pane con padding y fondo claro
		StackPane centerWrapper = new StackPane();
		centerWrapper.setPadding(new Insets(18));
		centerWrapper.setStyle("-fx-background-color: #eef5fb;"); // fondo suave para el área principal

		if (centerContent != null) {
			// si el contenido es un Node, lo envolvemos en un panel para que no estire
			StackPane contentPane = new StackPane(centerContent);
			contentPane.setStyle("-fx-background-color: transparent;");
			contentPane.setMaxWidth(Double.MAX_VALUE);
			contentPane.setMaxHeight(Double.MAX_VALUE);
			centerWrapper.getChildren().add(contentPane);
		}

		shell.setCenter(centerWrapper);

		// estilo global del shell
		shell.setStyle("-fx-font-family: 'Segoe UI', sans-serif;");

		return shell;
	}

} // fin de clase FXLauncher