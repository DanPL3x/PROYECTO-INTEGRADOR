package com.proyecto.integrador.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import java.sql.*;
import java.time.format.DateTimeFormatter;

/**
 * Formulario público para que ciudadanos reporten incidentes sin autenticación
 */
public class FormularioPublicoDenuncia {
    
    private Connection conexion;
    private Stage primaryStage;
    
    // Campos del formulario
    private ComboBox<String> cbZona;
    private ComboBox<String> cbUbicacion;
    private ComboBox<String> cbTipoDelito;
    private ComboBox<String> cbPuntoCardinal;
    private TextArea txtDescripcion;
    private TextField txtNombreReportante;
    private TextField txtContacto;
    private DatePicker dpFecha;
    private Spinner<Integer> spHora;
    private Spinner<Integer> spMinuto;
    
    public FormularioPublicoDenuncia(Connection conexion, Stage primaryStage) {
        this.conexion = conexion;
        this.primaryStage = primaryStage;
    }
    
    public Scene crearEscena(Runnable volverCallback) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");
        
        // Título
        Label titulo = new Label("📢 Reportar Incidente");
        titulo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label subtitulo = new Label("Ayuda a tu comunidad reportando incidentes de seguridad");
        subtitulo.setStyle("-fx-font-size: 16px; -fx-text-fill: #aaaaaa;");
        
        // ScrollPane para el formulario
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        VBox formulario = crearFormulario();
        scrollPane.setContent(formulario);
        
        // Botones de acción
        HBox botonesAccion = crearBotonesAccion(volverCallback);
        
        root.getChildren().addAll(titulo, subtitulo, scrollPane, botonesAccion);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        Scene scene = new Scene(root, 1300, 720);
        return scene;
    }
    
    private VBox crearFormulario() {
        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setMaxWidth(800);
        form.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 10;");
        
        // Información del reportante
        Label seccionReportante = new Label("👤 Información de Contacto (Opcional)");
        seccionReportante.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        txtNombreReportante = new TextField();
        txtNombreReportante.setPromptText("Nombre (opcional)");
        txtNombreReportante.setStyle("-fx-font-size: 15px; -fx-pref-height: 40px;");
        
        txtContacto = new TextField();
        txtContacto.setPromptText("Teléfono o Email (opcional)");
        txtContacto.setStyle("-fx-font-size: 15px; -fx-pref-height: 40px;");
        
        Separator sep1 = new Separator();
        
        // Información del incidente
        Label seccionIncidente = new Label("📍 Detalles del Incidente");
        seccionIncidente.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Ayuda visual
        Label ayudaInfo = new Label("💡 Puede seleccionar opciones de la lista O escribir directamente sus propios valores");
        ayudaInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: #2980b9; -fx-font-style: italic; " +
                          "-fx-background-color: #ebf5fb; -fx-padding: 8px; -fx-background-radius: 5;");
        ayudaInfo.setWrapText(true);
        
        // Zona
        Label lblZona = new Label("Zona *");
        lblZona.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        cbZona = new ComboBox<>();
        cbZona.setEditable(true); // Permite escribir libremente
        cbZona.setPromptText("Seleccione o escriba la zona (ej: San Antonio)");
        cbZona.setMaxWidth(Double.MAX_VALUE);
        cbZona.setStyle("-fx-font-size: 15px; -fx-pref-height: 40px; " +
                       "-fx-background-color: #e3f2fd; -fx-text-fill: #000000; " +
                       "-fx-prompt-text-fill: #666666; -fx-background-radius: 5;");
        cargarZonas();
        
        // Ubicación
        Label lblUbicacion = new Label("Ubicación *");
        lblUbicacion.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        cbUbicacion = new ComboBox<>();
        cbUbicacion.setEditable(true); // Permite escribir libremente
        cbUbicacion.setPromptText("Seleccione o escriba la ubicación (ej: Calle 5 #10-20)");
        cbUbicacion.setMaxWidth(Double.MAX_VALUE);
        cbUbicacion.setStyle("-fx-font-size: 15px; -fx-pref-height: 40px; " +
                            "-fx-background-color: #fff3e0; -fx-text-fill: #000000; " +
                            "-fx-prompt-text-fill: #666666; -fx-background-radius: 5;");
        cbZona.setOnAction(e -> cargarUbicaciones());
        
        // Punto Cardinal
        Label lblPuntoCardinal = new Label("Punto Cardinal *");
        lblPuntoCardinal.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        cbPuntoCardinal = new ComboBox<>();
        cbPuntoCardinal.setEditable(true); // Permite escribir libremente
        cbPuntoCardinal.setPromptText("Seleccione o escriba (Norte, Sur, Este, Oeste)");
        cbPuntoCardinal.setMaxWidth(Double.MAX_VALUE);
        cbPuntoCardinal.setStyle("-fx-font-size: 15px; -fx-pref-height: 40px; " +
                                "-fx-background-color: #f3e5f5; -fx-text-fill: #000000; " +
                                "-fx-prompt-text-fill: #666666; -fx-background-radius: 5;");
        cargarPuntosCardinales();
        
        // Tipo de Delito
        Label lblTipo = new Label("Tipo de Delito *");
        lblTipo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        cbTipoDelito = new ComboBox<>();
        cbTipoDelito.setEditable(true); // Permite escribir libremente
        cbTipoDelito.setPromptText("Seleccione o escriba (ej: Robo, Hurto, Asalto)");
        cbTipoDelito.setMaxWidth(Double.MAX_VALUE);
        cbTipoDelito.setStyle("-fx-font-size: 15px; -fx-pref-height: 40px; " +
                             "-fx-background-color: #ffebee; -fx-text-fill: #000000; " +
                             "-fx-prompt-text-fill: #666666; -fx-background-radius: 5;");
        cargarTiposDelito();
        
        // Fecha y Hora
        Label lblFechaHora = new Label("Fecha y Hora del Incidente *");
        lblFechaHora.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        
        HBox fechaHoraBox = new HBox(10);
        dpFecha = new DatePicker();
        dpFecha.setPromptText("Fecha");
        dpFecha.setMaxWidth(Double.MAX_VALUE);
        dpFecha.setStyle("-fx-font-size: 15px;");
        HBox.setHgrow(dpFecha, Priority.ALWAYS);
        
        spHora = new Spinner<>(0, 23, 12);
        spHora.setEditable(true);
        spHora.setPrefWidth(100);
        spHora.setStyle("-fx-font-size: 15px;");
        
        Label lblDosPuntos = new Label(":");
        lblDosPuntos.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        spMinuto = new Spinner<>(0, 59, 0);
        spMinuto.setEditable(true);
        spMinuto.setPrefWidth(100);
        spMinuto.setStyle("-fx-font-size: 15px;");
        
        fechaHoraBox.getChildren().addAll(dpFecha, new Label("Hora:"), spHora, lblDosPuntos, spMinuto);
        
        // Descripción
        Label lblDescripcion = new Label("Descripción del Incidente *");
        lblDescripcion.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Describa lo sucedido con el mayor detalle posible...");
        txtDescripcion.setPrefRowCount(5);
        txtDescripcion.setWrapText(true);
        txtDescripcion.setStyle("-fx-font-size: 15px;");
        
        // Nota legal
        Label notaLegal = new Label("* Campos obligatorios. La información será revisada por las autoridades.");
        notaLegal.setStyle("-fx-font-size: 13px; -fx-text-fill: #130febff; -fx-font-style: italic;");
        
        form.getChildren().addAll(
            seccionReportante,
            txtNombreReportante,
            txtContacto,
            sep1,
            seccionIncidente,
            ayudaInfo,
            lblZona, cbZona,
            lblUbicacion, cbUbicacion,
            lblPuntoCardinal, cbPuntoCardinal,
            lblTipo, cbTipoDelito,
            lblFechaHora, fechaHoraBox,
            lblDescripcion, txtDescripcion,
            notaLegal
        );
        
        return form;
    }
    
    private HBox crearBotonesAccion(Runnable volverCallback) {
        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);
        botones.setPadding(new Insets(10));
        
        Button btnEnviar = new Button("✅ Enviar Reporte");
        btnEnviar.setStyle("-fx-font-size: 16px; -fx-background-color: #27ae60; -fx-text-fill: white; " +
                          "-fx-padding: 12 40; -fx-background-radius: 8; -fx-font-weight: bold;");
        btnEnviar.setOnMouseEntered(e -> btnEnviar.setStyle("-fx-font-size: 16px; -fx-background-color: #229954; " +
                                                             "-fx-text-fill: white; -fx-padding: 12 40; -fx-background-radius: 8; -fx-font-weight: bold;"));
        btnEnviar.setOnMouseExited(e -> btnEnviar.setStyle("-fx-font-size: 16px; -fx-background-color: #27ae60; " +
                                                            "-fx-text-fill: white; -fx-padding: 12 40; -fx-background-radius: 8; -fx-font-weight: bold;"));
        btnEnviar.setOnAction(e -> enviarReporte());
        
        Button btnLimpiar = new Button("🔄 Limpiar Formulario");
        btnLimpiar.setStyle("-fx-font-size: 16px; -fx-background-color: #f39c12; -fx-text-fill: white; " +
                           "-fx-padding: 12 30; -fx-background-radius: 8;");
        btnLimpiar.setOnMouseEntered(e -> btnLimpiar.setStyle("-fx-font-size: 16px; -fx-background-color: #e67e22; " +
                                                               "-fx-text-fill: white; -fx-padding: 12 30; -fx-background-radius: 8;"));
        btnLimpiar.setOnMouseExited(e -> btnLimpiar.setStyle("-fx-font-size: 16px; -fx-background-color: #f39c12; " +
                                                              "-fx-text-fill: white; -fx-padding: 12 30; -fx-background-radius: 8;"));
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        
        Button btnVolver = new Button("⬅ Volver al Menú");
        btnVolver.setStyle("-fx-font-size: 16px; -fx-background-color: #95a5a6; -fx-text-fill: white; " +
                          "-fx-padding: 12 30; -fx-background-radius: 8;");
        btnVolver.setOnMouseEntered(e -> btnVolver.setStyle("-fx-font-size: 16px; -fx-background-color: #7f8c8d; " +
                                                             "-fx-text-fill: white; -fx-padding: 12 30; -fx-background-radius: 8;"));
        btnVolver.setOnMouseExited(e -> btnVolver.setStyle("-fx-font-size: 16px; -fx-background-color: #95a5a6; " +
                                                            "-fx-text-fill: white; -fx-padding: 12 30; -fx-background-radius: 8;"));
        btnVolver.setOnAction(e -> volverCallback.run());
        
        botones.getChildren().addAll(btnEnviar, btnLimpiar, btnVolver);
        return botones;
    }
    
    private void cargarZonas() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try (Statement stmt = conexion.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT DISTINCT nombre FROM Zona ORDER BY nombre")) {
                    while (rs.next()) {
                        String zona = rs.getString("nombre");
                        javafx.application.Platform.runLater(() -> cbZona.getItems().add(zona));
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    private void cargarUbicaciones() {
        String zonaSeleccionada = cbZona.getValue();
        if (zonaSeleccionada == null) return;
        
        cbUbicacion.getItems().clear();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String sql = "SELECT u.nombre_direccion FROM Ubicacion u " +
                            "JOIN Zona z ON u.id_zona = z.id_zona WHERE z.nombre = ?";
                try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                    pstmt.setString(1, zonaSeleccionada);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        String ubicacion = rs.getString("nombre_direccion");
                        javafx.application.Platform.runLater(() -> cbUbicacion.getItems().add(ubicacion));
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    private void cargarPuntosCardinales() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try (Statement stmt = conexion.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT nombre FROM PuntoCardinal ORDER BY id_punto")) {
                    while (rs.next()) {
                        String punto = rs.getString("nombre");
                        javafx.application.Platform.runLater(() -> cbPuntoCardinal.getItems().add(punto));
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    private void cargarTiposDelito() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try (Statement stmt = conexion.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT tipo_delito FROM Tipo ORDER BY tipo_delito")) {
                    while (rs.next()) {
                        String tipo = rs.getString("tipo_delito");
                        javafx.application.Platform.runLater(() -> cbTipoDelito.getItems().add(tipo));
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    private void enviarReporte() {
        // Validar campos obligatorios
        if (!validarFormulario()) {
            return;
        }
        
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                // Obtener o crear IDs de zona, ubicación, tipo y punto cardinal
                Integer idZona = obtenerOCrearIdZona(cbZona.getValue());
                Integer idUbicacion = obtenerOCrearIdUbicacion(cbUbicacion.getValue(), idZona);
                Integer idTipo = obtenerOCrearIdTipo(cbTipoDelito.getValue());
                Integer idPunto = obtenerOCrearIdPuntoCardinal(cbPuntoCardinal.getValue());
                
                if (idZona == null || idUbicacion == null || idTipo == null || idPunto == null) {
                    return false;
                }
                
                // Construir fecha y hora
                String fecha = dpFecha.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String hora = String.format("%02d:%02d:00", spHora.getValue(), spMinuto.getValue());
                String fechaHora = fecha + " " + hora;
                
                // Insertar denuncia
                String sql = "INSERT INTO Denuncia (id_ubicacion, id_tipo, id_punto, fecha_hora, descripcion, " +
                            "nombre_reportante, contacto_reportante, estado) VALUES (?, ?, ?, ?, ?, ?, ?, 'Pendiente')";
                
                try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                    pstmt.setInt(1, idUbicacion);
                    pstmt.setInt(2, idTipo);
                    pstmt.setInt(3, idPunto);
                    pstmt.setString(4, fechaHora);
                    pstmt.setString(5, txtDescripcion.getText());
                    pstmt.setString(6, txtNombreReportante.getText().isEmpty() ? "Anónimo" : txtNombreReportante.getText());
                    pstmt.setString(7, txtContacto.getText().isEmpty() ? null : txtContacto.getText());
                    
                    int filasAfectadas = pstmt.executeUpdate();
                    return filasAfectadas > 0;
                }
            }
        };
        
        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                mostrarMensaje("Éxito", "✅ Reporte Enviado", 
                              "Gracias por tu reporte. Las autoridades revisarán la información.", 
                              Alert.AlertType.INFORMATION);
                limpiarFormulario();
            } else {
                mostrarMensaje("Error", "❌ Error al Enviar", 
                              "No se pudo guardar el reporte. Verifica los datos.", 
                              Alert.AlertType.ERROR);
            }
        });
        
        task.setOnFailed(e -> {
            mostrarMensaje("Error", "❌ Error del Sistema", 
                          "Ocurrió un error: " + task.getException().getMessage(), 
                          Alert.AlertType.ERROR);
        });
        
        new Thread(task).start();
    }
    
    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();
        
        if (cbZona.getValue() == null || cbZona.getValue().trim().isEmpty()) 
            errores.append("• Ingrese una zona\n");
        if (cbUbicacion.getValue() == null || cbUbicacion.getValue().trim().isEmpty()) 
            errores.append("• Ingrese una ubicación\n");
        if (cbPuntoCardinal.getValue() == null || cbPuntoCardinal.getValue().trim().isEmpty()) 
            errores.append("• Ingrese un punto cardinal\n");
        if (cbTipoDelito.getValue() == null || cbTipoDelito.getValue().trim().isEmpty()) 
            errores.append("• Ingrese el tipo de delito\n");
        if (dpFecha.getValue() == null) errores.append("• Seleccione la fecha\n");
        if (txtDescripcion.getText().trim().isEmpty()) errores.append("• Ingrese una descripción\n");
        
        if (errores.length() > 0) {
            mostrarMensaje("Validación", "⚠️ Campos Incompletos", errores.toString(), Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }
    
    private void limpiarFormulario() {
        txtNombreReportante.clear();
        txtContacto.clear();
        cbZona.setValue(null);
        cbUbicacion.getItems().clear();
        cbUbicacion.setValue(null);
        cbPuntoCardinal.setValue(null);
        cbTipoDelito.setValue(null);
        dpFecha.setValue(null);
        spHora.getValueFactory().setValue(12);
        spMinuto.getValueFactory().setValue(0);
        txtDescripcion.clear();
    }
    
    /**
     * Obtiene o crea una zona. Si no existe, la crea con nivel de riesgo "Medio" por defecto
     */
    private Integer obtenerOCrearIdZona(String nombreZona) throws SQLException {
        // Primero intentar obtener
        String sqlSelect = "SELECT id_zona FROM Zona WHERE nombre = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sqlSelect)) {
            pstmt.setString(1, nombreZona);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id_zona");
        }
        
        // Si no existe, crear nueva zona con nivel de riesgo "Medio" (id_nivel = 2 normalmente)
        String sqlInsert = "INSERT INTO Zona (nombre, comuna_vereda, id_nivel) VALUES (?, 'Por definir', 2)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nombreZona);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return null;
    }
    
    /**
     * Obtiene o crea una ubicación asociada a una zona
     */
    private Integer obtenerOCrearIdUbicacion(String nombreUbicacion, Integer idZona) throws SQLException {
        // Primero intentar obtener
        String sqlSelect = "SELECT id_ubicacion FROM Ubicacion WHERE nombre_direccion = ? AND id_zona = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sqlSelect)) {
            pstmt.setString(1, nombreUbicacion);
            pstmt.setInt(2, idZona);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id_ubicacion");
        }
        
        // Si no existe, crear nueva ubicación
        String sqlInsert = "INSERT INTO Ubicacion (id_zona, nombre_direccion) VALUES (?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, idZona);
            pstmt.setString(2, nombreUbicacion);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return null;
    }
    
    /**
     * Obtiene o crea un tipo de delito
     */
    private Integer obtenerOCrearIdTipo(String tipoDelito) throws SQLException {
        // Primero intentar obtener
        String sqlSelect = "SELECT id_tipo FROM Tipo WHERE tipo_delito = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sqlSelect)) {
            pstmt.setString(1, tipoDelito);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id_tipo");
        }
        
        // Si no existe, crear nuevo tipo
        String sqlInsert = "INSERT INTO Tipo (tipo_delito) VALUES (?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, tipoDelito);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return null;
    }
    
    /**
     * Obtiene o crea un punto cardinal
     */
    private Integer obtenerOCrearIdPuntoCardinal(String nombrePunto) throws SQLException {
        // Primero intentar obtener
        String sqlSelect = "SELECT id_punto FROM PuntoCardinal WHERE nombre = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sqlSelect)) {
            pstmt.setString(1, nombrePunto);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id_punto");
        }
        
        // Si no existe, crear nuevo punto cardinal
        String sqlInsert = "INSERT INTO PuntoCardinal (nombre) VALUES (?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nombrePunto);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return null;
    }
    
    private void mostrarMensaje(String titulo, String encabezado, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }
}
