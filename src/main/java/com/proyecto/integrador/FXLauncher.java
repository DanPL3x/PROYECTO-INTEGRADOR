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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.proyecto.integrador.model.Conexion;
import com.proyecto.integrador.model.ConsultasDB;
import com.proyecto.integrador.model.AdminService;
import com.proyecto.integrador.model.AdminAuth;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class FXLauncher extends Application {
    private Stage primaryStage;
    private Conexion conexionBD;
    private Connection conexion;
    private ConsultasDB consultasDB;

    // escenas admin
    private Scene adminLoginScene;
    private Scene adminScene;

    // Ventana/tamaño unificados
    private static final int WINDOW_W = 900;
    private static final int WINDOW_H = 700;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Portal Seguridad Cali");

        // Inicializar conexión
        conexionBD = new Conexion();
        conexion = conexionBD.getConnection();
        consultasDB = new ConsultasDB(conexion);

        // Asegurar admin por defecto
        AdminService.ensureDefaultAdmin(conexion, "admin", "admin123");

        // preparar escenas admin (login y panel)
        adminLoginScene = buildAdminLoginScene();
        adminScene = buildAdminScene();

        // Mostrar menú principal
        Scene mainScene = new Scene(createMainMenu(), WINDOW_W, WINDOW_H);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    // Menú principal
    private Parent createMainMenu() {
        VBox mainRoot = new VBox(20);
        mainRoot.setPadding(new Insets(20));
        mainRoot.setAlignment(Pos.CENTER);

        Label title = new Label("Seleccione su Rol");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button btnUsuario = new Button("Consultas Usuario");
        btnUsuario.setStyle("-fx-font-size: 16px; -fx-min-width: 200px;");

        Button btnAdmin = new Button("Acceso Administrador");
        btnAdmin.setStyle("-fx-font-size: 16px; -fx-min-width: 200px;");

        // Eventos
        btnUsuario.setOnAction(e -> mostrarVistaUsuario()); // abrir ventana de usuario (misma dimensión)
        btnAdmin.setOnAction(e -> primaryStage.setScene(adminLoginScene));

        mainRoot.getChildren().addAll(title, btnUsuario, btnAdmin);
        return mainRoot;
    }

    // Nueva vista de usuario: ventana independiente con muchos botones de consulta (sin chat)
    private void mostrarVistaUsuario() {
        // Crear nueva Stage independiente, con tamaño consistente
        Stage userStage = new Stage();
        userStage.setTitle("Consultas Usuario - Seguridad (Cali)");
        userStage.initOwner(primaryStage);
        userStage.initModality(Modality.NONE);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        // Left: panel de botones de consulta (lista ampliada)
        ScrollPane leftScroll = new ScrollPane();
        leftScroll.setFitToWidth(true);
        VBox left = new VBox(10);
        left.setPrefWidth(320);
        left.setPadding(new Insets(8));
        left.setStyle("-fx-background-color: #f7f7f7;");

        Label leftTitle = new Label("Consultas rápidas");
        leftTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Definir botones de consulta adicionales para usuarios/turistas/habitantes
        Button bBarrios = new Button("Barrios más Peligrosos");
        Button bCAIs = new Button("CAIs y Estaciones");
        Button bRecientes = new Button("Delitos 24h");
        Button bZonasSeguras = new Button("Zonas Seguras (Bajo)");
        Button bDelitos = new Button("Delitos Frecuentes");
        Button bHorarios = new Button("Horarios más Peligrosos");
        Button bLugaresMas = new Button("Lugares con más Denuncias");
        Button bDenunciasPorBarrio = new Button("Denuncias por Barrio");
        Button bDenunciasPorDelito = new Button("Denuncias por Delito");
        Button bPuntosCard = new Button("Puntos Cardinales");
        Button bUbicaciones = new Button("Listar Ubicaciones (completo)");

        // Estilo y tamaño
        Button[] allBtns = { bBarrios, bCAIs, bRecientes, bZonasSeguras, bDelitos, bHorarios, bLugaresMas, bDenunciasPorBarrio, bDenunciasPorDelito, bPuntosCard, bUbicaciones };
        for (Button btn : allBtns) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #53575aff; -fx-text-fill: white; -fx-font-size: 13px;");
        }

        left.getChildren().addAll(leftTitle);
        left.getChildren().addAll(allBtns);
        leftScroll.setContent(left);

        // Center: área de resultados
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setStyle("-fx-font-family: monospace;");

        // Bottom: botón cerrar ventana
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(6));
        bottom.setAlignment(Pos.CENTER_RIGHT);
        Button btnClose = new Button("Cerrar");
        btnClose.setStyle("-fx-background-color: #e99187ff; -fx-text-fill: white;");
        btnClose.setOnAction(e -> userStage.close());
        bottom.getChildren().add(btnClose);

        root.setLeft(leftScroll);
        root.setCenter(resultArea);
        root.setBottom(bottom);

        Scene scene = new Scene(root, WINDOW_W, WINDOW_H);
        userStage.setScene(scene);
        userStage.show();

        // Helper para ejecutar consultas en background y escribir resultados
        java.util.function.BiConsumer<String,String> runQuery = (label, sql) -> {
            resultArea.setText("Ejecutando: " + label + " ...\n");
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
            task.setOnSucceeded(ev -> resultArea.setText(task.getValue()));
            task.setOnFailed(ev -> resultArea.setText("Error: " + task.getException().getMessage()));
            new Thread(task).start();
        };

        // Mapear botones a consultas SQL (ajusta si tus nombres de tablas/columnas difieren)
        bBarrios.setOnAction(e -> runQuery.accept("Barrios más Peligrosos",
                "SELECT z.nombre AS Barrio, COUNT(d.id_denuncia) AS Denuncias " +
                        "FROM Zona z JOIN Ubicacion u ON z.id_zona = u.id_zona " +
                        "JOIN LugarDenuncias l ON u.id_ubicacion = l.id_ubicacion " +
                        "JOIN Denuncia d ON l.id_lugar = d.id_lugar " +
                        "GROUP BY z.nombre ORDER BY Denuncias DESC LIMIT 10"));

        bCAIs.setOnAction(e -> runQuery.accept("CAIs y Estaciones",
                "SELECT nombre, direccion, telefono FROM LugarDenuncias WHERE nombre LIKE '%CAI%' OR nombre LIKE '%Estación%'"));

        bRecientes.setOnAction(e -> runQuery.accept("Delitos Recientes (24h)",
                "SELECT d.fecha, d.hora, d.descripcion, z.nombre AS zona " +
                        "FROM Denuncia d JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar " +
                        "JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion " +
                        "JOIN Zona z ON u.id_zona = z.id_zona " +
                        "WHERE d.fecha >= CURDATE() - INTERVAL 1 DAY ORDER BY d.fecha DESC, d.hora DESC"));

        bZonasSeguras.setOnAction(e -> runQuery.accept("Zonas Seguras (Bajo)",
                "SELECT DISTINCT z.nombre AS Zona, nr.riesgo AS Nivel " +
                        "FROM Zona z JOIN Ubicacion u ON z.id_zona = u.id_zona " +
                        "JOIN NivelRiesgo nr ON u.id_nivel = nr.id_nivel " +
                        "WHERE nr.riesgo = 'Bajo' LIMIT 50"));

        bDelitos.setOnAction(e -> runQuery.accept("Delitos Frecuentes",
                "SELECT tipo_delito, COUNT(*) AS Total FROM Delito GROUP BY tipo_delito ORDER BY Total DESC LIMIT 20"));

        bHorarios.setOnAction(e -> runQuery.accept("Horarios más Peligrosos",
                "SELECT HOUR(hora) AS Hora, COUNT(*) AS Denuncias FROM Denuncia GROUP BY HOUR(hora) ORDER BY Denuncias DESC LIMIT 24"));

        bLugaresMas.setOnAction(e -> runQuery.accept("Lugares con más Denuncias",
                "SELECT l.nombre AS Lugar, l.direccion AS Direccion, COUNT(d.id_denuncia) AS Denuncias " +
                        "FROM LugarDenuncias l LEFT JOIN Denuncia d ON l.id_lugar = d.id_lugar " +
                        "GROUP BY l.id_lugar ORDER BY Denuncias DESC LIMIT 20"));

        bDenunciasPorBarrio.setOnAction(e -> runQuery.accept("Denuncias por Barrio",
                "SELECT z.nombre AS Barrio, COUNT(d.id_denuncia) AS Denuncias " +
                        "FROM Denuncia d JOIN LugarDenuncias l ON d.id_lugar = l.id_lugar " +
                        "JOIN Ubicacion u ON l.id_ubicacion = u.id_ubicacion " +
                        "JOIN Zona z ON u.id_zona = z.id_zona " +
                        "GROUP BY z.nombre ORDER BY Denuncias DESC"));

        bDenunciasPorDelito.setOnAction(e -> runQuery.accept("Denuncias por Delito",
                "SELECT dl.tipo_delito AS Delito, COUNT(*) AS Total " +
                        "FROM Denuncia_Delito dd JOIN Delito dl ON dd.id_delito = dl.id_delito " +
                        "GROUP BY dl.tipo_delito ORDER BY Total DESC"));

        bPuntosCard.setOnAction(e -> runQuery.accept("Puntos Cardinales",
                "SELECT id_punto_cardinal, nombre FROM PuntoCardinal"));

        bUbicaciones.setOnAction(e -> runQuery.accept("Ubicaciones (completo)",
                "SELECT id_ubicacion, direccion, id_nivel, id_zona, id_punto_cardinal FROM Ubicacion LIMIT 200"));
    }

    // Construye la escena de login admin
    private Scene buildAdminLoginScene() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        Label lbl = new Label("Login Administrador");
        lbl.setStyle("-fx-font-size:16px;");
        GridPane.setColumnSpan(lbl, 2);

        Label luser = new Label("Usuario:");
        TextField tfUser = new TextField();
        Label lpass = new Label("Contraseña:");
        PasswordField pf = new PasswordField();

        Button btnIngresar = new Button("Ingresar");
        Button btnVolver = new Button("Volver");

        Label lblInfo = new Label();
        lblInfo.setStyle("-fx-text-fill: red;");

        grid.add(lbl, 0, 0);
        grid.add(luser, 0, 1);
        grid.add(tfUser, 1, 1);
        grid.add(lpass, 0, 2);
        grid.add(pf, 1, 2);
        grid.add(btnIngresar, 0, 3);
        grid.add(btnVolver, 1, 3);
        grid.add(lblInfo, 0, 4, 2, 1);

        btnVolver.setOnAction(ev -> {
            tfUser.clear();
            pf.clear();
            lblInfo.setText("");
            primaryStage.setScene(new Scene(createMainMenu(), 600, 400));
        });

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
                        // refrescar conexión si es necesario
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

        return new Scene(grid, 420, 240);
    }

    // Construye la escena del panel admin (ejecutar SQL y consultas)
    private Scene buildAdminScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Left: lista de tablas (radio) para consultas rápidas
        VBox left = new VBox(8);
        left.setPadding(new Insets(0, 10, 0, 0));
        left.setPrefWidth(220);

        String[] tablas = {
                "Delitos", "Denuncias", "Denuncia_Delito",
                "NivelRiesgo", "PuntoCardinal", "Tipo",
                "Ubicacion", "Zona", "LugarDenuncias"
        };

        ToggleGroup tg = new ToggleGroup();
        for (String t : tablas) {
            RadioButton rb = new RadioButton(t);
            rb.setUserData(t);
            rb.setToggleGroup(tg);
            left.getChildren().add(rb);
        }

        Button btnEjecutar = new Button("Ejecutar consulta");
        btnEjecutar.setMaxWidth(Double.MAX_VALUE);
        left.getChildren().addAll(new Separator(), btnEjecutar);

        // Center: resultados
        TextArea txtResultado = new TextArea();
        txtResultado.setEditable(false);
        txtResultado.setWrapText(false);
        txtResultado.setStyle("-fx-font-family: monospace;");

        // Right: PANEL CRUD dinámico (reemplaza el SQL area clásico)
        VBox right = new VBox(8);
        right.setPadding(new Insets(8));
        right.setPrefWidth(420);

        Label crudTitle = new Label("CRUD - Seleccione tabla y acciones");
        crudTitle.setStyle("-fx-font-weight:bold;");
        // Lugar para formulario dinámico
        ScrollPane formScroll = new ScrollPane();
        formScroll.setFitToWidth(true);
        VBox formBox = new VBox(8);
        formBox.setPadding(new Insets(6));
        formScroll.setContent(formBox);
        formScroll.setPrefHeight(300);

        // Controles CRUD
        HBox actions = new HBox(8);
        Button btnCreate = new Button("Crear");
        Button btnUpdate = new Button("Actualizar");
        Button btnDelete = new Button("Eliminar");
        Button btnRefresh = new Button("Refrescar datos");
        actions.getChildren().addAll(btnCreate, btnUpdate, btnDelete, btnRefresh);

        Label infoCrud = new Label();
        infoCrud.setWrapText(true);

        right.getChildren().addAll(crudTitle, new Label("Formulario:"), formScroll, actions, new Separator(), infoCrud);

        root.setLeft(left);
        root.setCenter(txtResultado);
        root.setRight(right);

        // Cuando se selecciona una tabla, construir formulario dinámico
        tg.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            formBox.getChildren().clear();
            infoCrud.setText("");
            if (newT == null) return;
            String tabla = (String) newT.getUserData();
            // obtener columnas
            try {
                java.util.List<String> cols = getTableColumns(tabla);
                if (cols.isEmpty()) {
                    infoCrud.setText("No se pudieron obtener columnas de " + tabla);
                    return;
                }
                // crear campos: usar map column->TextField almacenado como userData
                for (String col : cols) {
                    HBox row = new HBox(6);
                    Label lbl = new Label(col);
                    lbl.setPrefWidth(140);
                    TextField tf = new TextField();
                    tf.setPrefWidth(220);
                    tf.setUserData(col);
                    row.getChildren().addAll(lbl, tf);
                    formBox.getChildren().add(row);
                }
                // Mostrar hint sobre PK
                String pk = getPrimaryKeyColumn(tabla);
                infoCrud.setText("Tabla: " + tabla + (pk!=null ? "  |  PK: " + pk : "  |  PK no detectada, use el primer campo para identificar registros"));
            } catch (Exception ex) {
                ex.printStackTrace();
                infoCrud.setText("Error cargando esquema: " + ex.getMessage());
            }
        });

        // Ejecutar consulta rápida (leer)
        btnEjecutar.setOnAction(ev -> {
            Toggle selected = tg.getSelectedToggle();
            if (selected == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Seleccione una tabla antes de ejecutar.", ButtonType.OK);
                a.showAndWait();
                return;
            }
            String tabla = (String) selected.getUserData();
            txtResultado.setText("Ejecutando consulta: " + tabla + " ...\n");
            Task<String> task = new Task<>() {
                @Override
                protected String call() {
                    try {
                        if (conexion == null || conexion.isClosed()) {
                            conexion = conexionBD.getConnection();
                            consultasDB = new ConsultasDB(conexion);
                        }
                        return consultasDB.consultarTabla(tabla);
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

        // CRUD actions: Crear
        btnCreate.setOnAction(ev -> {
            Toggle selected = tg.getSelectedToggle();
            if (selected == null) { infoCrud.setText("Seleccione una tabla primero."); return; }
            String tabla = (String) selected.getUserData();
            // recolectar valores
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
            t.setOnSucceeded(rr -> {
                infoCrud.setText(t.getValue());
                // refrescar vista
                btnEjecutar.fire();
            });
            t.setOnFailed(rr -> infoCrud.setText("Error: " + t.getException().getMessage()));
            new Thread(t).start();
        });

        // CRUD actions: Actualizar
        btnUpdate.setOnAction(ev -> {
            Toggle selected = tg.getSelectedToggle();
            if (selected == null) { infoCrud.setText("Seleccione una tabla primero."); return; }
            String tabla = (String) selected.getUserData();
            String pk = getPrimaryKeyColumn(tabla);
            java.util.Map<String,String> values = collectFormValues(formBox);
            if (values.isEmpty()) { infoCrud.setText("Formulario vacío."); return; }
            // identificar clave primaria value
            String pkCol = (pk!=null) ? pk : values.keySet().iterator().next();
            String pkVal = values.get(pkCol);
            if (pkVal==null || pkVal.isEmpty()) { infoCrud.setText("Debe proporcionar el valor de la clave primaria ("+pkCol+") para actualizar."); return; }
            infoCrud.setText("Actualizando " + tabla + " ...");
            Task<String> t = new Task<>() {
                @Override
                protected String call() {
                    try {
                        executeUpdate(tabla, values.toString(), pkCol, pkVal);
                        return "Registro actualizado correctamente.";
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return "Error actualizando: " + ex.getMessage();
                    }
                }

                private void executeUpdate(String tabla,String values, String pkCol, String pkVal) {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException("Unimplemented method 'executeUpdate'");
                }
            };
            t.setOnSucceeded(rr -> {
                infoCrud.setText(t.getValue());
                btnEjecutar.fire();
            });
            t.setOnFailed(rr -> infoCrud.setText("Error: " + t.getException().getMessage()));
            new Thread(t).start();
        });

        // CRUD actions: Eliminar
        btnDelete.setOnAction(ev -> {
            Toggle selected = tg.getSelectedToggle();
            if (selected == null) { infoCrud.setText("Seleccione una tabla primero."); return; }
            String tabla = (String) selected.getUserData();
            String pk = getPrimaryKeyColumn(tabla);
            java.util.Map<String,String> values = collectFormValues(formBox);
            String pkCol = (pk!=null) ? pk : (values.keySet().isEmpty() ? null : values.keySet().iterator().next());
            if (pkCol==null) { infoCrud.setText("No hay columna para identificar registro."); return; }
            String pkVal = values.get(pkCol);
            if (pkVal==null || pkVal.isEmpty()) { infoCrud.setText("Debe proporcionar el valor de la clave primaria ("+pkCol+") para eliminar."); return; }
            // confirm
            boolean confirm = AlertHelperConfirm("Confirmar", "¿Eliminar registro con "+pkCol+" = "+pkVal+"?");
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

                private void executeDelete(String tabla, String pkCol, String pkVal) {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException("Unimplemented method 'executeDelete'");
                }
            };
            t.setOnSucceeded(rr -> { infoCrud.setText(t.getValue()); btnEjecutar.fire(); });
            t.setOnFailed(rr -> infoCrud.setText("Error: " + t.getException().getMessage()));
            new Thread(t).start();
        });

        // Refrescar (simple re-run)
        btnRefresh.setOnAction(ev -> btnEjecutar.fire());

        return new Scene(root, 1000, 700);
    }

    private boolean AlertHelperConfirm(String string, String string2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'AlertHelperConfirm'");
    }

    // Helper: collect values from formBox (VBox of HBox{Label,TextField})
    private java.util.Map<String,String> collectFormValues(VBox formBox) {
        java.util.Map<String,String> map = new java.util.LinkedHashMap<>();
        for (javafx.scene.Node rowNode : formBox.getChildren()) {
            if (!(rowNode instanceof HBox)) continue;
            HBox row = (HBox) rowNode;
            TextField tf = null;
            Label lbl = null;
            for (Node n : row.getChildren()) {
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

    // Helper: get PK column name (tries INFORMATION_SCHEMA)
    private String getPrimaryKeyColumn(String table) {
        if (conexion==null) return null;
        try (java.sql.PreparedStatement ps = conexion.prepareStatement(
                "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND CONSTRAINT_NAME = 'PRIMARY'")) {
            ps.setString(1, table);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    // Helper: get columns of table (names)
    private java.util.List<String> getTableColumns(String table) throws Exception {
        java.util.List<String> cols = new java.util.ArrayList<>();
        if (conexion==null) throw new Exception("Conexión nula");
        String sql = "SELECT * FROM " + table + " LIMIT 1";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            for (int i=1;i<=md.getColumnCount();i++) cols.add(md.getColumnLabel(i));
        }
        // If table empty, fallback to metadata from DatabaseMetaData
        if (cols.isEmpty()) {
            try (ResultSet rs = conexion.getMetaData().getColumns(null, null, table, null)) {
                while (rs.next()) cols.add(rs.getString("COLUMN_NAME"));
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
            if (cols.length()>0) { cols.append(","); params.append(","); }
            cols.append(c);
            params.append("?)");

            vals.add(values.get(c));
        }
        String sql = "INSERT INTO " + table + " (" + cols.toString() + ") VALUES (" + params.toString() + ")";
        try (java.sql.PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i=0;i<vals.size();i++) {
                ps.setString(i+1, vals.get(i));
            }
            ps.executeUpdate();
        }
    }

}
