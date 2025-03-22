USE banco;

-- Tabla Ciudad
INSERT INTO Ciudad (cod_postal, nombre) 
VALUES 
(8000, 'Bahia Blanca'),
(8001, 'Buenos Aires'),
(8002, 'Rosario');

-- Tabla Sucursal
INSERT INTO Sucursal (nro_suc, nombre, direccion, telefono, horario, cod_postal)
VALUES 
(1, 'Sucursal Centro', 'Calle Central 123', '456789123', '9:00-18:00', 8000),
(2, 'Sucursal Norte', 'Avenida Norte 456', '789456123', '9:00-18:00', 8001),
(3, 'Sucursal Rosario', 'Avenida Belgrano 456', '458910214', '9:00-18:00', 8002);

-- Tabla Empleado
INSERT INTO Empleado (legajo, apellido, nombre, tipo_doc, nro_doc, direccion, telefono, cargo, password, nro_suc) 
VALUES 
(21, 'Fernandez', 'Lucia', 'DNI', 33445566, 'Calle Nueva 789', '123987456', 'Gerente', MD5('empleado'), 1),
(22, 'Martinez', 'Jose', 'DNI', 55667788, 'Avenida Principal 123', '987123654', 'Cajero', MD5('empleado'), 2);

-- Tabla Cliente
INSERT INTO Cliente (nro_cliente, apellido, nombre, tipo_doc, nro_doc, direccion, telefono, fecha_nac) 
VALUES 
(11, 'Gomez', 'Juan', 'DNI', 12345678, 'Calle 123', '123456789', '1990-01-01'),
(12, 'Perez', 'Maria', 'DNI', 87654321, 'Avenida 456', '987654321', '1985-05-15'),
(13, 'Lopez', 'Carlos', 'DNI', 11223344, 'Calle Falsa 456', '112233445', '1992-10-20'),
(14, 'Gonzalez', 'Ana', 'DNI', 55667788, 'Avenida Principal 123', '987123654', '1985-05-15');

-- Tabla Plazo_Fijo
INSERT INTO Plazo_Fijo (nro_plazo, capital, fecha_inicio, fecha_fin, tasa_interes, interes, nro_suc)
VALUES 
(700001, 5000.00, '2023-01-01', '2023-06-30', 2.5, 125.00, 1),
(700002, 10000.00, '2023-02-01', '2023-07-31', 3.0, 300.00, 2);

-- Tabla Tasa_Plazo_Fijo
INSERT INTO Tasa_Plazo_Fijo (periodo, monto_inf, monto_sup, tasa)
VALUES 
(30, 1000.00, 10000.00, 2.0),
(60, 11.00, 50000.00, 3.0);

-- Tabla Prestamo
INSERT INTO Prestamo (nro_prestamo, fecha, cant_meses, monto, tasa_interes, interes, valor_cuota, legajo, nro_cliente)
VALUES 
(51, '2023-01-01', 12, 10000.00, 5.5, 550.00, 870.00, 21, 11),
(52, '2023-06-15', 24, 20000.00, 4.8, 960.00, 920.00, 22, 12),
(53, '2023-10-01', 12, 15000.00, 5.5, 828.00, 1319.00, 21, 13);

-- Tabla Pago
INSERT INTO Pago (nro_prestamo, nro_pago, fecha_venc, fecha_pago)
VALUES 
(51, 1, '2024-02-01', '2024-02-01'),
(51, 2, '2024-03-01', '2024-03-01'),
(51, 3, '2024-04-01', NULL),
(51, 4, '2024-05-01', NULL),
(51, 5, '2024-06-01', NULL),
(51, 6, '2024-07-01', NULL),
(51, 7, '2024-08-01', NULL),
(51, 8, '2024-09-01', NULL),
(51, 9, '2024-10-01', NULL),
(51, 10, '2024-11-01', NULL),
(51, 11, '2024-12-01', NULL),
(51, 12, '2025-01-01', NULL),
(53, 1, '2024-02-01', '2024-02-01'),
(53, 2, '2024-03-01', '2024-03-01'),
(53, 3, '2024-04-01', NULL),
(53, 4, '2024-05-01', NULL),
(53, 5, '2024-06-01', NULL),
(53, 6, '2024-07-01', NULL),
(53, 7, '2024-08-01', NULL),
(53, 8, '2024-09-01', NULL),
(53, 9, '2024-10-01', NULL),
(53, 10, '2024-11-01', NULL),
(53, 11, '2024-12-01', NULL),
(53, 12, '2025-01-01', NULL),
(52, 1, '2024-10-15', '2024-10-10'),
(52, 2, '2024-08-15', NULL),
(52, 3, '2024-09-15', NULL);

-- Tabla Tasa_Prestamo
INSERT INTO Tasa_Prestamo (periodo, monto_inf, monto_sup, tasa)
VALUES 
(12, 5000.00, 20000.00, 5.5),
(24, 20001.00, 50000.00, 4.8),
(36, 50001.00, 100000.00, 4.5);

-- Tabla Caja_Ahorro
INSERT INTO Caja_Ahorro (nro_ca, CBU, saldo)
VALUES 
(31, 123456789012345678, 10000.50),
(32, 987654321098765432, 500.75),
(33, 123456789182736491, 1500.75);

-- Tabla Cliente_CA
INSERT INTO Cliente_CA (nro_cliente, nro_ca)
VALUES 
(11, 31),
(12, 32),
(13, 33);

-- Tabla Tarjeta
INSERT INTO Tarjeta (nro_tarjeta, PIN, CVT, fecha_venc, nro_cliente, nro_ca)
VALUES 
(123, MD5('123'), MD5('789'), '2025-01-01', 11, 31),
(456, MD5('456'), MD5('123'), '2025-01-01', 12, 32),
(789, MD5('789'), MD5('456'), '2025-01-01', 13, 33);

-- Tabla Caja
INSERT INTO Caja (cod_caja)
VALUES 
(100),
(101),
(102);

-- Tabla Ventanilla
INSERT INTO Ventanilla (cod_caja, nro_suc)
VALUES 
(100, 1),
(101, 2),
(102, 3);

-- Tabla ATM
INSERT INTO ATM (cod_caja, cod_postal, direccion)
VALUES 
(100, 8000, 'Calle ATM 1'),
(101, 8001, 'Avenida ATM 2'),
(102, 8002, 'Avenida ATM 3');

-- Tabla Transaccion
INSERT INTO Transaccion (nro_trans, fecha, hora, monto)
VALUES 
(61, '2024-10-01', '12:00:00', 100.00),
(62, '2024-10-02', '14:30:00', 200.00),
(63, '2024-10-03', '15:20:00', 250.00),
(64, '2024-10-04', '16:40:00', 300.00),
(65, '2024-10-04', '17:00:00', 1300.00);

-- Tabla Transaccion_por_caja
INSERT INTO Transaccion_por_caja (nro_trans, cod_caja)
VALUES 
(61, 100),
(62, 101),
(63, 102),
(64, 102),
(65, 102);

-- Tabla Debito
INSERT INTO Debito (nro_trans, descripcion, nro_cliente, nro_ca)
VALUES 
(61, 'Pago Servicio', 11, 31),
(62, 'Compra Supermercado', 12, 32);

-- Tabla Deposito
INSERT INTO Deposito (nro_trans, nro_ca)
VALUES 
(61, 31);

-- Tabla Extraccion
INSERT INTO Extraccion (nro_trans, nro_cliente, nro_ca)
VALUES 
(62, 12, 32),
(65, 11, 31);

-- Tabla Transferencia
INSERT INTO Transferencia (nro_trans, nro_cliente, origen, destino)
VALUES
(63, 11, 31, 32), 
(64, 13, 33, 31);
