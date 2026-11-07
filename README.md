# Don Abe Urban Food — Sistema de Gestión de Inventario y Compras

Aplicación de escritorio (Java Swing) para controlar insumos, proveedores, compras
y el kardex de un establecimiento de comidas rápidas. Proyecto integrador,
Institución Universitaria Antonio José Camacho (2026).

## Funcionalidades
- Registro, consulta, actualización y eliminación de insumos (con vencimiento opcional y sin duplicados)
- Alertas de stock bajo con déficit calculado
- Gestión de proveedores
- Registro de compras con actualización transaccional del stock
- Consulta de kardex por insumo y rango de fechas

## Tecnologías
Java 25 (LTS)+, Maven, MySQL 8, JDBC, Java Swing, JUnit 5

## Arquitectura
MVC en capas con DAO: `view` → `controller` → `dao` → MySQL, con `model`, `util` y `conexion`.

## Instalación
1. Instalar JRE/JDK 25 o superior y MySQL 8.
2. Ejecutar `script_bd.sql` en MySQL (crea la base, tablas y datos de ejemplo).
3. Crear un usuario de MySQL con permisos SELECT, INSERT, UPDATE, DELETE sobre `proyecto_inventario`.
4. Copiar `config.properties.example` como `config.properties` y completar usuario y contraseña.
5. Compilar y probar: `mvn clean package`.
6. Ejecutar: `java -jar target/proyecto-inventario-1.0-SNAPSHOT.jar`
   (con `config.properties` en la misma carpeta desde donde se ejecuta).

## Pruebas
`mvn test` ejecuta 54 pruebas unitarias (no requieren base de datos).

## Estructura
src/main/java/com/inventario/{view, controller, dao, model, util, conexion}
src/test/java/com/inventario/{controller, util}

## Equipo
Cristian E. Erazo Bonilla, Jeanpier Manzano Valencia, Justin Hernández Valencia