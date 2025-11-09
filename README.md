# Sistema de Seguridad Ciudadana - Cali

## Integrantes del Proyecto
- Sebastian Almendra
- Sebastian Abadia
- Daniel Campo

## Estado Actual del Proyecto

### Funcionalidades Implementadas
- Estructura básica del sistema con arquitectura MVC
- Conexión a base de datos MySQL
- Login de administrador
- CRUD básico para algunas entidades
- Interfaz inicial en JavaFX

### Pendientes por Implementar

#### Consultas de Usuario
- [ ] Estadísticas de delitos por hora y día
- [ ] Mapa de calor de zonas peligrosas
- [ ] Filtros de búsqueda por tipo de delito
- [ ] Búsqueda de CAIs cercanos por ubicación
- [ ] Alertas de zonas peligrosas
- [ ] Reportes históricos de criminalidad
- [ ] Comparativas entre zonas/barrios

#### Panel de Administración
- [ ] Completar CRUD para todas las entidades
  - [ ] Gestión de usuarios
  - [ ] Gestión de roles
  - [ ] Gestión de permisos
- [ ] Validaciones de datos en formularios
- [ ] Logs de auditoría
- [ ] Gestión de respaldos
- [ ] Reportes administrativos

#### Mejoras de Interfaz (JavaFX)
- [ ] Paleta de colores más amigable
- [ ] Iconos y elementos visuales
- [ ] Gráficos estadísticos
- [ ] Tablas con ordenamiento y filtros
- [ ] Formularios con validación en tiempo real
- [ ] Mensajes de feedback al usuario
- [ ] Responsive design
- [ ] Temas claro/oscuro

#### Optimizaciones Técnicas
- [ ] Cache de consultas frecuentes
- [ ] Paginación de resultados
- [ ] Mejora de tiempos de respuesta
- [ ] Manejo de errores más robusto
- [ ] Testing unitario y de integración

## Tecnologías Utilizadas
- Java 11+
- JavaFX
- MySQL
- Maven

## Configuración del Proyecto
1. Clonar repositorio
2. Configurar `config.properties` con credenciales de BD
3. Ejecutar script de base de datos
4. Compilar con Maven
5. Ejecutar aplicación

## Ejecutar la aplicación (JavaFX)

Opción recomendada (Maven, configura y descarga OpenJFX automáticamente):

1. Asegúrate de tener Maven instalado (mvn en PATH) y JDK 11+ (se recomienda JDK 17+ o JDK 21 según tu entorno).
2. En la carpeta del proyecto (donde está el pom.xml) ejecuta:
   mvn clean javafx:run
   Esto descargará las dependencias OpenJFX y lanza la app.

Problema frecuente en consola:
- Si ves: "faltan los componentes de JavaFX runtime y son necesarios para ejecutar esta aplicación" => significa que estás ejecutando sin las librerías JavaFX en el module-path/ classpath.

Ejecución desde IDE (si no usas Maven):
- Descarga el SDK de JavaFX (OpenJFX) desde https://openjfx.io/.
- Descomprime, por ejemplo en C:\javafx-sdk-21\lib
- Configura la Run Configuration de tu IDE (VM arguments) con:
  --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml
- Asegúrate de usar la misma versión de JDK (por ejemplo JDK 21) y la versión del SDK de JavaFX compatible.

Notas:
- Si usas mvn javafx:run el plugin gestionará el module-path por ti (no es necesario configurar VM args).
- Si tu SO no es Windows, ajusta la propiedad `<javafx.platform>` en el pom.xml a `linux` o `mac` y vuelve a ejecutar mvn.
- Si tienes errores de ``No se encontraron FXML`` comprueba que los ficheros FXML estén en src/main/resources/fxml y que maven los copie al classpath.

Si quieres, yo:
- 1) Ajusto el pom.xml para otra versión de JavaFX o plataforma.
- 2) Creo controladores JavaFX (Controller classes) para las FXML y enlazo la lógica (mejor estructura).
- 3) Te doy los VM args exactos si me indicas la ruta al SDK de JavaFX que descargaste.

## Credenciales por Defecto
admin
admin123



