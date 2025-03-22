#Archivo batch (banco.sql) para la creación de la 
#Base de datos del proyecto de SQL

# Creo de la Base de Datos
CREATE DATABASE banco;

# selecciono la base de datos sobre la cual voy a hacer modificaciones
USE banco;

# --------------------------------------------------------------------------

CREATE TABLE Ciudad (
cod_postal SMALLINT UNSIGNED NOT NULL,
nombre VARCHAR(30) NOT NULL,

CONSTRAINT PK_ciudad
PRIMARY KEY (cod_postal)

) ENGINE = InnoDB;

CREATE TABLE Sucursal (
    nro_suc SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(30) NOT NULL,
    direccion VARCHAR(30) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    horario VARCHAR(30) NOT NULL,
    cod_postal SMALLINT UNSIGNED NOT NULL,

    CONSTRAINT FK_sucursal_cod_postal
    FOREIGN KEY (cod_postal) REFERENCES Ciudad(cod_postal)
    ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT PK_sucursal
    PRIMARY KEY (nro_suc)

) ENGINE = InnoDB;


CREATE TABLE Empleado (
    legajo SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(30) NOT NULL,
    apellido VARCHAR(30) NOT NULL,
    tipo_doc VARCHAR(20) NOT NULL,
    nro_doc INT UNSIGNED NOT NULL,
    direccion VARCHAR(30) NOT NULL,
    password VARCHAR(32) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    cargo VARCHAR(30) NOT NULL,
    nro_suc SMALLINT UNSIGNED NOT NULL,

    CONSTRAINT FK_empleado_nro_suc
    FOREIGN KEY (nro_suc) REFERENCES Sucursal(nro_suc)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
        
    CONSTRAINT PK_empleado
    PRIMARY KEY (legajo)

) ENGINE = InnoDB;

CREATE TABLE Cliente(
    nro_cliente MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(30) NOT NULL,
    apellido VARCHAR(30) NOT NULL,
    tipo_doc VARCHAR(20) NOT NULL,
    nro_doc INT UNSIGNED NOT NULL,
    direccion VARCHAR(30) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    fecha_nac DATE NOT NULL,

    CONSTRAINT PK_cliente
    PRIMARY KEY (nro_cliente)

) ENGINE = InnoDB;

CREATE TABLE Plazo_fijo (
    nro_plazo INT UNSIGNED NOT NULL AUTO_INCREMENT,
    capital DECIMAL(16,2) UNSIGNED NOT NULL,
    tasa_interes DECIMAL(4,2) UNSIGNED NOT NULL,
    interes DECIMAL(16,2) UNSIGNED NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    nro_suc SMALLINT UNSIGNED NOT NULL,

    CONSTRAINT FK_plazo_fijo_nro_suc
    FOREIGN KEY (nro_suc) REFERENCES Sucursal(nro_suc)
    ON DELETE RESTRICT ON UPDATE RESTRICT,

    CONSTRAINT PK_plazo_fijo
    PRIMARY KEY (nro_plazo)

) ENGINE = InnoDB;

CREATE TABLE Tasa_plazo_fijo (
    periodo SMALLINT UNSIGNED NOT NULL,
    monto_inf DECIMAL(16,2) UNSIGNED NOT NULL,
    monto_sup DECIMAL(16,2) UNSIGNED NOT NULL,
    tasa DECIMAL(4,2) UNSIGNED NOT NULL,

    CONSTRAINT PK_tasa_pf
    PRIMARY KEY (periodo,monto_inf,monto_sup)

) ENGINE = InnoDB;

CREATE TABLE Prestamo (
    nro_prestamo INT UNSIGNED NOT NULL AUTO_INCREMENT,
    cant_meses SMALLINT UNSIGNED NOT NULL,
    fecha DATE NOT NULL,
    monto DECIMAL(10,2) UNSIGNED NOT NULL,
    tasa_interes DECIMAL(4,2) UNSIGNED NOT NULL,
    interes DECIMAL(9,2) UNSIGNED NOT NULL,
    valor_cuota DECIMAL(9,2) UNSIGNED NOT NULL,
    legajo SMALLINT UNSIGNED NOT NULL,
    nro_cliente MEDIUMINT UNSIGNED NOT NULL,

    CONSTRAINT FK_prestamo_nro_cliente
    FOREIGN KEY (nro_cliente) REFERENCES Cliente(nro_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_prestamo_emp_legajo
    FOREIGN KEY (legajo) REFERENCES Empleado(legajo)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_prestamo
    PRIMARY KEY (nro_prestamo)

) ENGINE = InnoDB;

CREATE TABLE Pago (
    nro_prestamo INT UNSIGNED NOT NULL,
    nro_pago SMALLINT UNSIGNED NOT NULL,
    fecha_venc DATE NOT NULL,
    fecha_pago DATE, 

    CONSTRAINT FK_pago_nro_prestamo
    FOREIGN KEY (nro_prestamo) REFERENCES Prestamo(nro_prestamo)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_pago
    PRIMARY KEY (nro_prestamo,nro_pago)

) ENGINE = InnoDB;

CREATE TABLE Tasa_prestamo(
    periodo SMALLINT UNSIGNED NOT NULL,
    monto_inf DECIMAL(10,2) UNSIGNED NOT NULL,
    monto_sup DECIMAL(10,2) UNSIGNED NOT NULL,
    tasa DECIMAL(4,2) UNSIGNED NOT NULL,

    CONSTRAINT PK_tasa_prestamo
    PRIMARY KEY (periodo,monto_inf,monto_sup)

) ENGINE = InnoDB;

CREATE TABLE Caja_ahorro (
    nro_ca INT UNSIGNED NOT NULL AUTO_INCREMENT,
    CBU BIGINT UNSIGNED NOT NULL,
    saldo DECIMAL(16,2) UNSIGNED NOT NULL,

    CONSTRAINT PK_caja_ahorro
    PRIMARY KEY (nro_ca)

)ENGINE = InnoDB;

CREATE TABLE Cliente_CA(
    nro_ca INT UNSIGNED NOT NULL,
    nro_cliente MEDIUMINT UNSIGNED NOT NULL,

    CONSTRAINT FK_cliente_ca_nro_cliente
    FOREIGN KEY (nro_cliente) REFERENCES Cliente(nro_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_cliente_ca_nro_ca
    FOREIGN KEY (nro_ca) REFERENCES Caja_ahorro(nro_ca)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_cliente_ca
    PRIMARY KEY (nro_ca,nro_cliente)

) ENGINE = InnoDB;

CREATE TABLE Tarjeta(
    nro_tarjeta BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    PIN VARCHAR(32) NOT NULL,
    CVT VARCHAR(32) NOT NULL,
    fecha_venc DATE NOT NULL,
    nro_cliente MEDIUMINT UNSIGNED NOT NULL,
    nro_ca INT UNSIGNED NOT NULL,

    CONSTRAINT FK_tarjeta_cliente_ca
    FOREIGN KEY (nro_ca,nro_cliente) REFERENCES Cliente_CA(nro_ca, nro_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_tarjeta
    PRIMARY KEY (nro_tarjeta)

) ENGINE = InnoDB;

CREATE TABLE Caja(
    cod_caja MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,

    CONSTRAINT PK_caja
    PRIMARY KEY (cod_caja)

) ENGINE = InnoDB;

CREATE TABLE Ventanilla (
    cod_caja MEDIUMINT UNSIGNED NOT NULL,
    nro_suc SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,

    CONSTRAINT FK_ventanilla_cod_caja
    FOREIGN KEY (cod_caja) REFERENCES Caja(cod_caja)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_ventanilla_nro_suc
    FOREIGN KEY (nro_suc) REFERENCES Sucursal(nro_suc)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_ventanilla
    PRIMARY KEY (cod_caja)

) ENGINE = InnoDB;

CREATE TABLE ATM (
    cod_caja MEDIUMINT UNSIGNED NOT NULL,
    cod_postal SMALLINT UNSIGNED NOT NULL,
    direccion VARCHAR(30) NOT NULL,

    CONSTRAINT FK_ATM_cod_caja
    FOREIGN KEY (cod_caja) REFERENCES Caja(cod_caja)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_ATM_cod_postal
    FOREIGN KEY (cod_postal) REFERENCES Ciudad(cod_postal)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_ATM
    PRIMARY KEY (cod_caja)

) ENGINE = InnoDB;

CREATE TABLE Transaccion(
    nro_trans INT UNSIGNED NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    monto DECIMAL(16,2) UNSIGNED NOT NULL,

    CONSTRAINT PK_transaccion
    PRIMARY KEY (nro_trans)

) ENGINE = InnoDB;

CREATE TABLE Transaccion_por_caja(
    nro_trans INT UNSIGNED NOT NULL,
    cod_caja MEDIUMINT UNSIGNED NOT NULL,

    CONSTRAINT FK_trans_por_caja_nro_trans
    FOREIGN KEY (nro_trans) REFERENCES Transaccion(nro_trans)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_trans_por_caja_cod_caja
    FOREIGN KEY (cod_caja) REFERENCES Caja(cod_caja)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_trans_por_caja
    PRIMARY KEY (nro_trans)

) ENGINE = InnoDB;

CREATE TABLE Debito(
    nro_trans INT UNSIGNED NOT NULL,
    descripcion TEXT,
    nro_cliente MEDIUMINT UNSIGNED NOT NULL,
    nro_ca INT UNSIGNED NOT NULL,

    CONSTRAINT FK_debito_nro_trans
    FOREIGN KEY (nro_trans) REFERENCES Transaccion(nro_trans)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_debito_cliente_ca
    FOREIGN KEY (nro_ca,nro_cliente) REFERENCES Cliente_CA(nro_ca, nro_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_debito
    PRIMARY KEY (nro_trans)

) ENGINE = InnoDB;

CREATE TABLE Deposito(
    nro_trans INT UNSIGNED NOT NULL,
    nro_ca INT UNSIGNED NOT NULL,

    CONSTRAINT FK_deposito_nro_trans
    FOREIGN KEY (nro_trans) REFERENCES Transaccion_por_caja(nro_trans)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_deposito_nro_ca
    FOREIGN KEY (nro_ca) REFERENCES Caja_ahorro(nro_ca)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_deposito
    PRIMARY KEY (nro_trans)

) ENGINE = InnoDB;

CREATE TABLE Extraccion(
    nro_trans INT UNSIGNED NOT NULL,
    nro_cliente MEDIUMINT UNSIGNED NOT NULL,
    nro_ca INT UNSIGNED NOT NULL,

    CONSTRAINT FK_extraccion_nro_trans
    FOREIGN KEY (nro_trans) REFERENCES Transaccion_por_caja(nro_trans)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_extraccion_cliente_ca
    FOREIGN KEY (nro_ca,nro_cliente) REFERENCES Cliente_CA(nro_ca, nro_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_extraccion
    PRIMARY KEY (nro_trans)

) ENGINE = InnoDB;

CREATE TABLE Transferencia(
    nro_trans INT UNSIGNED NOT NULL,
    nro_cliente MEDIUMINT UNSIGNED NOT NULL,
    origen INT UNSIGNED NOT NULL,
    destino INT UNSIGNED NOT NULL,

    CONSTRAINT FK_transferencia_nro_trans
    FOREIGN KEY (nro_trans) REFERENCES Transaccion_por_caja(nro_trans)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_transferencia_cliente_ca
    FOREIGN KEY (origen,nro_cliente) REFERENCES Cliente_CA(nro_ca,nro_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_transferencia_destino
    FOREIGN KEY (destino) REFERENCES Caja_ahorro(nro_ca)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_transferencia
    PRIMARY KEY (nro_trans)

) ENGINE = InnoDB;

#----------------------------------------------------------
#Creacion relaciones



CREATE TABLE Plazo_cliente(
    nro_plazo INT UNSIGNED NOT NULL,
    nro_cliente MEDIUMINT UNSIGNED NOT NULL,

    CONSTRAINT FK_plazo_cliente_plazo
    FOREIGN KEY (nro_plazo) REFERENCES Plazo_fijo(nro_plazo)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT FK_plazo_cliente_cliente
    FOREIGN KEY (nro_cliente) REFERENCES Cliente(nro_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT PK_plazo_cliente
    PRIMARY KEY (nro_plazo, nro_cliente)

) ENGINE = InnoDB;

#-------------------------------------------------------------------------
# Creación de vistas 
# trans_cajas_ahorro = información relacionada a las transacciones realizadas sobre las cajas de ahorro.

CREATE VIEW trans_cajas_ahorro AS
SELECT
    ca.nro_ca AS nro_ca,            
    ca.saldo AS saldo, 
    t.nro_trans, 
    t.fecha, 
    t.hora, 
    'Debito' AS tipo, 
    t.monto,
    NULL AS cod_caja,
    c.nro_cliente AS nro_cliente,
    c.tipo_doc AS tipo_doc,
    c.nro_doc AS nro_doc,           
    c.nombre AS nombre,             
    c.apellido AS apellido,
    NULL AS destino   
FROM 
    Transaccion t
JOIN 
    Debito d ON t.nro_trans = d.nro_trans
JOIN
    Caja_ahorro ca ON d.nro_ca = ca.nro_ca
JOIN
    Cliente c ON d.nro_cliente = c.nro_cliente

UNION ALL

SELECT
    ca.nro_ca AS nro_ca,            
    ca.saldo AS saldo,  
    t.nro_trans, 
    t.fecha, 
    t.hora, 
    'Deposito' AS tipo, 
    t.monto,
    tc.cod_caja AS cod_caja,
    NULL AS nro_cliente,
    NULL AS tipo_doc,
    NULL AS nro_doc,           
    NULL AS nombre,             
    NULL AS apellido,
    NULL AS destino  
FROM 
    Transaccion t
JOIN 
    Transaccion_por_caja tc ON t.nro_trans = tc.nro_trans
JOIN 
    Deposito dp ON tc.nro_trans = dp.nro_trans
JOIN
    Caja_ahorro ca ON dp.nro_ca = ca.nro_ca

UNION ALL

SELECT
    ca.nro_ca AS nro_ca,            
    ca.saldo AS saldo, 
    t.nro_trans, 
    t.fecha, 
    t.hora, 
    'Extraccion' AS tipo, 
    t.monto,
    tc.cod_caja AS cod_caja,
    c.nro_cliente AS nro_cliente,
    c.tipo_doc AS tipo_doc,
    c.nro_doc AS nro_doc,           
    c.nombre AS nombre,             
    c.apellido AS apellido,
    NULL AS destino   
FROM 
    Transaccion t
JOIN 
    Transaccion_por_caja tc ON t.nro_trans = tc.nro_trans
JOIN 
    Extraccion e ON tc.nro_trans = e.nro_trans
JOIN
    Caja_ahorro ca ON e.nro_ca = ca.nro_ca
JOIN
    Cliente c ON e.nro_cliente = c.nro_cliente

UNION ALL

SELECT
    ca.nro_ca AS nro_ca,            
    ca.saldo AS saldo, 
    t.nro_trans, 
    t.fecha, 
    t.hora, 
    'Transferencia' AS tipo, 
    t.monto,
    tc.cod_caja AS cod_caja,
    c.nro_cliente AS nro_cliente,
    c.tipo_doc AS tipo_doc,
    c.nro_doc AS nro_doc,           
    c.nombre AS nombre,             
    c.apellido AS apellido,
    tf.destino AS destino  
FROM 
    Transaccion t
JOIN 
    Transaccion_por_caja tc ON t.nro_trans = tc.nro_trans
JOIN 
    Transferencia tf ON tc.nro_trans = tf.nro_trans
JOIN
    Caja_ahorro ca ON tf.origen = ca.nro_ca
JOIN
    Cliente c ON tf.nro_cliente = c.nro_cliente;


#---------------------------------------------------
#Creación de usuarios

DROP USER IF EXISTS ''@'localhost';
DROP USER IF EXISTS 'admin'@'localhost';
DROP USER IF EXISTS 'atm'@'%';
DROP USER IF EXISTS 'empleado'@'%';

CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON banco.* TO 'admin'@'localhost' WITH GRANT OPTION;

CREATE USER 'empleado'@'%' IDENTIFIED BY 'empleado';
# Solo consultas
GRANT SELECT ON banco.Empleado TO 'empleado'@'%';
GRANT SELECT ON banco.Sucursal TO 'empleado'@'%';
GRANT SELECT ON banco.Tasa_Plazo_Fijo TO 'empleado'@'%';
GRANT SELECT ON banco.Tasa_Prestamo TO 'empleado'@'%';
# Consultar e insertar
GRANT SELECT, INSERT ON banco.Prestamo TO 'empleado'@'%';
GRANT SELECT, INSERT ON banco.Plazo_fijo TO 'empleado'@'%';
GRANT SELECT, INSERT ON banco.Plazo_cliente TO 'empleado'@'%';
GRANT SELECT, INSERT ON banco.Caja_ahorro TO 'empleado'@'%';
GRANT SELECT, INSERT ON banco.Tarjeta TO 'empleado'@'%';
# Consultar, insertar y modificar
GRANT SELECT, INSERT, UPDATE ON banco.Cliente_CA TO 'empleado'@'%';
GRANT SELECT, INSERT, UPDATE ON banco.Cliente TO 'empleado'@'%';
GRANT SELECT, INSERT, UPDATE ON banco.Pago TO 'empleado'@'%';

CREATE USER 'atm'@'%' IDENTIFIED BY 'atm';
GRANT SELECT ON banco.trans_cajas_ahorro TO 'atm'@'%';
GRANT SELECT, UPDATE ON banco.tarjeta TO 'atm'@'%';

#-------------------------------------------------------------------------

DELIMITER $$


    CREATE PROCEDURE transferencia(IN codigo_caja INT(100),IN nro_tarjeta_origen INT(100), IN caja_destino INT(100), IN monto DECIMAL(16,2), OUT resultado TEXT)
BEGIN
    DECLARE saldo_origen DECIMAL(16,2);
    DECLARE saldo_destino DECIMAL(16,2);
    DECLARE cliente_origen INT(100);
    DECLARE caja_origen INT(100);
    DECLARE mensaje_error TEXT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN 
        GET DIAGNOSTICS CONDITION 1 mensaje_error = MESSAGE_TEXT;
        SET resultado = CONCAT('SQLEXCEPTION!, transaccion abortada', mensaje_error);
        ROLLBACK;
    END;

    START TRANSACTION;
        IF EXISTS (SELECT 1 FROM Tarjeta  WHERE nro_tarjeta = nro_tarjeta_origen)
        AND EXISTS (SELECT * FROM Caja WHERE cod_caja = codigo_caja)
        AND EXISTS (SELECT * FROM Caja_ahorro WHERE nro_ca = caja_destino) THEN
        IF (SELECT COUNT(*) FROM Tarjeta  WHERE nro_tarjeta = nro_tarjeta_origen) != 1 THEN
                SET resultado = 'Error: La tarjeta tiene múltiples cuentas asociadas';
                ROLLBACK;
            ELSE
                SELECT nro_ca INTO caja_origen FROM Tarjeta WHERE nro_tarjeta = nro_tarjeta_origen; 
                SELECT saldo INTO saldo_origen FROM Caja_ahorro WHERE nro_ca = caja_origen FOR UPDATE;
                SELECT saldo INTO saldo_destino FROM Caja_ahorro WHERE nro_ca = caja_destino FOR UPDATE;
                SELECT nro_cliente INTO cliente_origen FROM Cliente_CA WHERE nro_ca = caja_origen;
            END IF;

            IF saldo_origen < monto THEN
                SET resultado = 'Saldo insuficiente en la caja de origen';
                ROLLBACK;
            ELSE
                INSERT INTO Transaccion (fecha, hora, monto) VALUES (CURDATE(), CURTIME(), monto);
                INSERT INTO Transaccion_por_caja (nro_trans, cod_caja) VALUES (LAST_INSERT_ID(), codigo_caja);
                INSERT INTO Transferencia (nro_trans, nro_cliente, origen, destino) VALUES (LAST_INSERT_ID(), cliente_origen, caja_origen, caja_destino);
                INSERT INTO Transaccion (fecha, hora, monto) VALUES (CURDATE(), CURTIME(), monto);
                INSERT INTO Transaccion_por_caja (nro_trans, cod_caja) VALUES (LAST_INSERT_ID(), codigo_caja);
                INSERT INTO Deposito (nro_trans, nro_ca) VALUES (LAST_INSERT_ID(), caja_destino);
                UPDATE Caja_ahorro SET saldo = saldo - monto WHERE nro_ca = caja_origen;
                UPDATE Caja_ahorro SET saldo = saldo + monto WHERE nro_ca = caja_destino;
                SET resultado = 'Transferencia Exitosa';
            END IF;
        ELSE
            SET resultado = 'Caja de origen o destino no encontrada';
            ROLLBACK;
        END IF;
    COMMIT;
END; $$

DELIMITER ;

DELIMITER $$


CREATE PROCEDURE extraccion(IN codigo_caja INT(100), IN nro_tarjeta_origen BIGINT(100), IN monto DECIMAL(16,2), OUT resultado TEXT)
BEGIN
    DECLARE saldo_caja DECIMAL(16,2);
    DECLARE cliente_origen INT(100);
    DECLARE caja_origen INT(100);
    DECLARE mensaje_error TEXT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN 
        GET DIAGNOSTICS CONDITION 1 mensaje_error= MESSAGE_TEXT;
        SET resultado = CONCAT('SQLEXCEPTION!, transaccion abortada ', mensaje_error);
        ROLLBACK;
    END;

    START TRANSACTION;
        IF EXISTS (SELECT * FROM Caja WHERE cod_caja=codigo_caja) 
        AND EXISTS (SELECT 1 FROM Tarjeta  WHERE nro_tarjeta = nro_tarjeta_origen) THEN
            IF (SELECT COUNT(*) FROM Tarjeta  WHERE nro_tarjeta = nro_tarjeta_origen) != 1 THEN
                SET resultado = 'Error: La tarjeta tiene múltiples cuentas asociadas';
                ROLLBACK;
            ELSE
                SELECT nro_ca INTO caja_origen FROM Tarjeta WHERE nro_tarjeta = nro_tarjeta_origen;     
                SELECT nro_cliente INTO cliente_origen FROM Cliente_CA WHERE nro_ca = caja_origen;
                SELECT saldo INTO saldo_caja FROM Caja_ahorro WHERE nro_ca = caja_origen FOR UPDATE;
            END IF;
            
            IF saldo_caja < monto THEN
                SET resultado = 'Saldo insuficiente en la caja';
                ROLLBACK;
            ELSE
                INSERT INTO Transaccion (fecha, hora, monto) VALUES (CURDATE(), CURTIME(), monto);
                INSERT INTO Transaccion_por_caja (nro_trans, cod_caja) VALUES (LAST_INSERT_ID(), codigo_caja);
                INSERT INTO Extraccion (nro_trans, nro_cliente, nro_ca) VALUES (LAST_INSERT_ID(), cliente_origen, caja_origen);
                UPDATE Caja_ahorro SET saldo = saldo - monto WHERE nro_ca = caja_origen;
                SET resultado = 'Extraccion Exitosa';
            END IF;

        ELSE
            SET resultado = 'Caja no encontrada';
            ROLLBACK;
        END IF;
    COMMIT;


END; $$



DELIMITER ;

DELIMITER $$

CREATE TRIGGER pagos_prestamo AFTER INSERT ON PRESTAMO FOR EACH ROW
    BEGIN
        DECLARE contador INT DEFAULT 1;
        WHILE contador <= NEW.cant_meses DO
            INSERT INTO PAGO (nro_prestamo, nro_pago, fecha_venc, fecha_pago) VALUES (NEW.nro_prestamo, contador, DATE_ADD(NEW.fecha, INTERVAL contador MONTH), NULL);
            SET contador = contador + 1;
        END WHILE;
    END; $$

DELIMITER ;

GRANT EXECUTE ON PROCEDURE banco.transferencia TO 'atm'@'%'; 
GRANT EXECUTE ON PROCEDURE banco.extraccion TO 'atm'@'%'; 


