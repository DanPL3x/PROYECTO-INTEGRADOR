package com.proyecto.integrador.view;

import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.concurrent.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.*;

/**
 * Dashboard visual con gráficas estadísticas sobre seguridad
 */
public class DashboardEstadisticas {
    
    private Connection conexion;
    private TabPane tabPane;
    
    public DashboardEstadisticas(Connection conexion) {
        this.conexion = conexion;
    }
    
    public Scene crearEscena(Runnable volverCallback) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");
        
        // Título principal
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        
        Label titulo = new Label("📊 Dashboard de Estadísticas");
        titulo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label subtitulo = new Label("Análisis visual de datos de seguridad en Cali");
        subtitulo.setStyle("-fx-font-size: 16px; -fx-text-fill: #aaaaaa;");
        
        header.getChildren().addAll(titulo, subtitulo);
        root.setTop(header);
        
        // TabPane con diferentes gráficas
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: transparent;");
        
        // Tab 1: Delitos por Tipo (BarChart)
        Tab tabDelitos = new Tab("📊 Delitos por Tipo");
        tabDelitos.setContent(crearVistaDelitosBarChart());
        
        // Tab 2: Distribución de Riesgo (PieChart)
        Tab tabRiesgo = new Tab("🎯 Distribución de Riesgo");
        tabRiesgo.setContent(crearVistaRiesgoPieChart());
        
        // Tab 3: Tendencias Mensuales (LineChart)
        Tab tabTendencias = new Tab("📈 Tendencias Temporales");
        tabTendencias.setContent(crearVistaTendenciasLineChart());
        
        // Tab 4: Top Zonas Peligrosas
        Tab tabZonas = new Tab("⚠️ Zonas Críticas");
        tabZonas.setContent(crearVistaZonasPeligrosas());
        
        tabPane.getTabs().addAll(tabDelitos, tabRiesgo, tabTendencias, tabZonas);
        root.setCenter(tabPane);
        
        // Botón para volver
        HBox footer = new HBox();
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        
        Button btnVolver = new Button("⬅ Volver al Menú");
        btnVolver.setStyle("-fx-font-size: 16px; -fx-background-color: #3498db; -fx-text-fill: white; " +
                          "-fx-padding: 12 30; -fx-background-radius: 8;");
        btnVolver.setOnMouseEntered(e -> btnVolver.setStyle("-fx-font-size: 16px; -fx-background-color: #2980b9; " +
                                                             "-fx-text-fill: white; -fx-padding: 12 30; -fx-background-radius: 8;"));
        btnVolver.setOnMouseExited(e -> btnVolver.setStyle("-fx-font-size: 16px; -fx-background-color: #3498db; " +
                                                            "-fx-text-fill: white; -fx-padding: 12 30; -fx-background-radius: 8;"));
        btnVolver.setOnAction(e -> volverCallback.run());
        
        footer.getChildren().add(btnVolver);
        root.setBottom(footer);
        
        Scene scene = new Scene(root, 1300, 720);
        return scene;
    }
    
    /**
     * Gráfico de barras: Delitos por Tipo
     */
    private VBox crearVistaDelitosBarChart() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95);");
        
        Label titulo = new Label("Distribución de Delitos por Tipo");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tipo de Delito");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Cantidad de Denuncias");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Delitos Registrados por Categoría");
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-font-size: 14px;");
        
        container.getChildren().addAll(titulo, barChart);
        VBox.setVgrow(barChart, Priority.ALWAYS);
        
        // Cargar datos asíncronamente
        cargarDatosDelitos(barChart);
        
        return container;
    }
    
    private void cargarDatosDelitos(BarChart<String, Number> chart) {
        Task<XYChart.Series<String, Number>> task = new Task<XYChart.Series<String, Number>>() {
            @Override
            protected XYChart.Series<String, Number> call() throws Exception {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Denuncias");
                
                String sql = "SELECT t.tipo_delito, COUNT(d.id_denuncia) as total " +
                            "FROM Tipo t " +
                            "LEFT JOIN Denuncia d ON t.id_tipo = d.id_tipo " +
                            "GROUP BY t.tipo_delito " +
                            "ORDER BY total DESC " +
                            "LIMIT 10";
                
                try (Statement stmt = conexion.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        String tipo = rs.getString("tipo_delito");
                        int total = rs.getInt("total");
                        series.getData().add(new XYChart.Data<>(tipo, total));
                    }
                }
                
                return series;
            }
        };
        
        task.setOnSucceeded(e -> {
            chart.getData().add(task.getValue());
        });
        
        new Thread(task).start();
    }
    
    /**
     * Gráfico circular: Distribución de Riesgo
     */
    private VBox crearVistaRiesgoPieChart() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95);");
        
        Label titulo = new Label("Distribución de Zonas por Nivel de Riesgo");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Clasificación de Riesgo por Zona");
        pieChart.setLegendVisible(true);
        pieChart.setStyle("-fx-font-size: 14px;");
        
        container.getChildren().addAll(titulo, pieChart);
        VBox.setVgrow(pieChart, Priority.ALWAYS);
        
        // Cargar datos asíncronamente
        cargarDatosRiesgo(pieChart);
        
        return container;
    }
    
    private void cargarDatosRiesgo(PieChart chart) {
        Task<ObservableList<PieChart.Data>> task = new Task<ObservableList<PieChart.Data>>() {
            @Override
            protected ObservableList<PieChart.Data> call() throws Exception {
                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                
                String sql = "SELECT nr.nombre_nivel, COUNT(z.id_zona) as total " +
                            "FROM NivelRiesgo nr " +
                            "LEFT JOIN Zona z ON nr.id_nivel = z.id_nivel " +
                            "GROUP BY nr.nombre_nivel " +
                            "ORDER BY total DESC";
                
                try (Statement stmt = conexion.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        String nivel = rs.getString("nombre_nivel");
                        int total = rs.getInt("total");
                        pieData.add(new PieChart.Data(nivel + " (" + total + ")", total));
                    }
                }
                
                return pieData;
            }
        };
        
        task.setOnSucceeded(e -> {
            chart.setData(task.getValue());
        });
        
        new Thread(task).start();
    }
    
    /**
     * Gráfico de líneas: Tendencias Temporales
     */
    private VBox crearVistaTendenciasLineChart() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95);");
        
        Label titulo = new Label("Tendencia de Denuncias en el Tiempo");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mes/Año");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Número de Denuncias");
        
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Evolución Temporal de Denuncias");
        lineChart.setCreateSymbols(true);
        lineChart.setStyle("-fx-font-size: 14px;");
        
        container.getChildren().addAll(titulo, lineChart);
        VBox.setVgrow(lineChart, Priority.ALWAYS);
        
        // Cargar datos asíncronamente
        cargarDatosTendencias(lineChart);
        
        return container;
    }
    
    private void cargarDatosTendencias(LineChart<String, Number> chart) {
        Task<XYChart.Series<String, Number>> task = new Task<XYChart.Series<String, Number>>() {
            @Override
            protected XYChart.Series<String, Number> call() throws Exception {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Denuncias");
                
                String sql = "SELECT DATE_FORMAT(fecha_hora, '%Y-%m') as mes, COUNT(*) as total " +
                            "FROM Denuncia " +
                            "GROUP BY mes " +
                            "ORDER BY mes DESC " +
                            "LIMIT 12";
                
                try (Statement stmt = conexion.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    
                    List<XYChart.Data<String, Number>> dataList = new ArrayList<>();
                    while (rs.next()) {
                        String mes = rs.getString("mes");
                        int total = rs.getInt("total");
                        dataList.add(new XYChart.Data<>(mes, total));
                    }
                    
                    // Revertir orden para mostrar cronológicamente
                    Collections.reverse(dataList);
                    series.getData().addAll(dataList);
                }
                
                return series;
            }
        };
        
        task.setOnSucceeded(e -> {
            chart.getData().add(task.getValue());
        });
        
        new Thread(task).start();
    }
    
    /**
     * Vista de Zonas más Peligrosas (BarChart horizontal)
     */
    private VBox crearVistaZonasPeligrosas() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95);");
        
        Label titulo = new Label("Top 10 Zonas con Mayor Índice de Denuncias");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("Zona");
        
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Número de Denuncias");
        
        BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Zonas Críticas de Seguridad");
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-font-size: 14px;");
        
        container.getChildren().addAll(titulo, barChart);
        VBox.setVgrow(barChart, Priority.ALWAYS);
        
        // Cargar datos asíncronamente
        cargarDatosZonasPeligrosas(barChart);
        
        return container;
    }
    
    private void cargarDatosZonasPeligrosas(BarChart<Number, String> chart) {
        Task<XYChart.Series<Number, String>> task = new Task<XYChart.Series<Number, String>>() {
            @Override
            protected XYChart.Series<Number, String> call() throws Exception {
                XYChart.Series<Number, String> series = new XYChart.Series<>();
                series.setName("Denuncias");
                
                String sql = "SELECT z.nombre, COUNT(d.id_denuncia) as total " +
                            "FROM Zona z " +
                            "JOIN Ubicacion u ON z.id_zona = u.id_zona " +
                            "JOIN Denuncia d ON u.id_ubicacion = d.id_ubicacion " +
                            "GROUP BY z.nombre " +
                            "ORDER BY total DESC " +
                            "LIMIT 10";
                
                try (Statement stmt = conexion.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        String zona = rs.getString("nombre");
                        int total = rs.getInt("total");
                        series.getData().add(new XYChart.Data<>(total, zona));
                    }
                }
                
                return series;
            }
        };
        
        task.setOnSucceeded(e -> {
            chart.getData().add(task.getValue());
        });
        
        new Thread(task).start();
    }
}
