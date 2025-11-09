package com.proyecto.integrador;

import java.lang.reflect.Method;

public class Bootstrap {
	// Entry point independiente sin referencias directas a javafx.* para evitar
	// ClassNotFoundError al cargar la clase si JavaFX no está en el module-path/classpath.
	public static void main(String[] args) {
		System.out.println("Bootstrap: comprobando disponibilidad de JavaFX...");

		// Informar versión Java y classpath mínimo
		System.out.println("Java version: " + System.getProperty("java.version"));
		System.out.println("java.class.path: " + System.getProperty("java.class.path"));

		try {
			// Comprobar que la clase javafx.application.Application está accesible
			Class.forName("javafx.application.Application");
			System.out.println("JavaFX runtime detectado en classpath/module-path.");

			// Cargar la clase principal de la app (FXLauncher) por reflexión
			Class<?> appClass = Class.forName("com.proyecto.integrador.FXLauncher");

			// Invocar Application.launch(appClass, args) por reflexión para evitar enlace estático
			Class<?> fxAppClass = Class.forName("javafx.application.Application");
			Method launch = fxAppClass.getMethod("launch", Class.class, String[].class);
			System.out.println("Lanzando aplicación JavaFX (" + appClass.getName() + ")...");
			launch.invoke(null, appClass, (Object) args);
		} catch (ClassNotFoundException cnf) {
			System.err.println("JavaFX NO está disponible en el runtime.");
			System.err.println("Causa: " + cnf.getMessage());
			System.err.println();
			System.err.println("Soluciones:");
			System.err.println("- Si usas Maven: ejecuta `mvn clean javafx:run -Djavafx.platform=win` (ajusta platform).");
			System.err.println("- Si ejecutas el JAR directamente, usa los VM args:");
			System.err.println("  --module-path \"<path-to-javafx-lib>\" --add-modules javafx.controls,javafx.fxml");
			System.err.println("- Ejemplo (Windows):");
			System.err.println("  java --module-path \"C:\\javafx-sdk-21\\lib\" --add-modules javafx.controls,javafx.fxml -cp target\\proyect-1.0-SNAPSHOT.jar com.proyecto.integrador.Bootstrap");
			System.err.println();
			System.err.println("Si quieres, pega aquí la traza completa de error o la salida de este bootstrap.");
			System.exit(1);
		} catch (ReflectiveOperationException ex) {
			System.err.println("Error al intentar lanzar la aplicación JavaFX por reflexión:");
			ex.printStackTrace();
			System.exit(2);
		}
	}
}
