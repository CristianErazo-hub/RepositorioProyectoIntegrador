CREATE DATABASE IF NOT EXISTS proyecto_inventario CHARACTER SET utf8mb4;
USE proyecto_inventario;

CREATE TABLE IF NOT EXISTS INSUMOS (
    id_insumo         INT AUTO_INCREMENT PRIMARY KEY,
    nombre            VARCHAR(100)  NOT NULL,
    unidad_medida     VARCHAR(30)   NOT NULL,
    stock_actual      DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock_minimo      DECIMAL(10,2) NOT NULL DEFAULT 0,
    fecha_vencimiento DATE          NULL,
    CONSTRAINT uq_insumos_nombre       UNIQUE (nombre),
    CONSTRAINT ck_insumos_stock_actual CHECK (stock_actual >= 0),
    CONSTRAINT ck_insumos_stock_minimo CHECK (stock_minimo >= 0)
);

CREATE TABLE IF NOT EXISTS TERCERO (
    id_tercero INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    telefono   VARCHAR(20),
    correo     VARCHAR(100),
    direccion  VARCHAR(150),
    tipo       VARCHAR(20)  NOT NULL,
    CONSTRAINT ck_tercero_tipo CHECK (tipo IN ('PROVEEDOR', 'RESPONSABLE'))
);

CREATE TABLE IF NOT EXISTS TIPO_DOCUMENTO (
    tipo_documento_id INT AUTO_INCREMENT PRIMARY KEY,
    descripcion       VARCHAR(50) NOT NULL,
    efecto_kardex     INT         NOT NULL,
    CONSTRAINT uq_tipo_documento_desc   UNIQUE (descripcion),
    CONSTRAINT ck_tipo_documento_efecto CHECK (efecto_kardex IN (-1, 1))
);

CREATE TABLE IF NOT EXISTS DOCUMENTO (
    id_documento      INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento_id INT  NOT NULL,
    id_tercero        INT  NOT NULL,
    fecha             DATE NOT NULL,
    observacion       VARCHAR(200),
    FOREIGN KEY (tipo_documento_id) REFERENCES TIPO_DOCUMENTO(tipo_documento_id),
    FOREIGN KEY (id_tercero)        REFERENCES TERCERO(id_tercero)
);

CREATE TABLE IF NOT EXISTS KARDEX (
    id_kardex        INT AUTO_INCREMENT PRIMARY KEY,
    id_documento     INT           NOT NULL,
    id_insumo        INT           NOT NULL,
    cantidad         DECIMAL(10,2) NOT NULL,
    stock_anterior   DECIMAL(10,2) NOT NULL,
    stock_nuevo      DECIMAL(10,2) NOT NULL,
    fecha_movimiento DATE          NOT NULL,
    FOREIGN KEY (id_documento) REFERENCES DOCUMENTO(id_documento),
    FOREIGN KEY (id_insumo)    REFERENCES INSUMOS(id_insumo),
    INDEX idx_kardex_insumo_fecha (id_insumo, fecha_movimiento)
);

-- Reservadas para la fase de producción (aún sin módulo en la aplicación)
CREATE TABLE IF NOT EXISTS PRODUCTOS (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200),
    precio      DECIMAL(10,2)
);

CREATE TABLE IF NOT EXISTS RECETA (
    id_receta   INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT           NOT NULL,
    id_insumo   INT           NOT NULL,
    cantidad    DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_producto) REFERENCES PRODUCTOS(id_producto),
    FOREIGN KEY (id_insumo)   REFERENCES INSUMOS(id_insumo)
);

-- Datos iniciales obligatorios
INSERT IGNORE INTO TIPO_DOCUMENTO (descripcion, efecto_kardex) VALUES ('COMPRA', 1);
INSERT IGNORE INTO TIPO_DOCUMENTO (descripcion, efecto_kardex) VALUES ('PRODUCCION', -1);
INSERT IGNORE INTO TIPO_DOCUMENTO (descripcion, efecto_kardex) VALUES ('AJUSTE', -1);

-- Datos de ejemplo (ficticios) para probar el sistema
INSERT IGNORE INTO TERCERO (nombre, telefono, correo, direccion, tipo) VALUES
    ('Distribuidora Ejemplo', '3001234567', 'ventas@ejemplo.com', 'Calle 1 #2-3', 'PROVEEDOR'),
    ('Proveedor Demo',        '3109876543', 'demo@ejemplo.com',   'Carrera 4 #5-6', 'PROVEEDOR');

INSERT IGNORE INTO INSUMOS (nombre, unidad_medida, stock_actual, stock_minimo, fecha_vencimiento) VALUES
    ('Arroz',                'Kg',     50,  10, NULL),
    ('Aceite',               'Litros', 30,   5, '2027-03-01'),
    ('Papa',                 'Kg',    100,  10, '2026-12-20'),
    ('Lechuga',              'Kg',     20,  10, '2026-11-20'),
    ('Carne para hamburguesa','Kg',    90,  10, '2026-12-15'),
    ('Pan de hamburguesa',   'Unidad',  8,  10, '2026-11-25');