package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;

public class DAOEmpleadoImpl implements DAOEmpleado {

	private static Logger logger = LoggerFactory.getLogger(DAOEmpleadoImpl.class);
	
	private Connection conexion;
	private ResultSet rs;
	
	public DAOEmpleadoImpl(Connection c) {
		this.conexion = c;
	}


	@Override
	public EmpleadoBean recuperarEmpleado(int legajo) throws Exception {
		logger.info("Recupera el empleado que corresponde al legajo {}.", legajo);
		
		/**
		 * TODO Debe recuperar los datos del empleado que corresponda al legajo pasado como parámetro.
		 *      Si no existe deberá retornar null y 
		 *      De ocurre algun error deberá generar una excepción.		 * 
		 */		
		
		EmpleadoBean empleado = null;
		
		try {
			String recuperar_pagos_sql = "SELECT * FROM Empleado where legajo="+legajo+";";
			Statement stmt= conexion.createStatement();
			rs = stmt.executeQuery(recuperar_pagos_sql);
			
			if(rs.next()) {
				empleado = new EmpleadoBeanImpl();
				empleado.setLegajo(rs.getInt("legajo"));
				empleado.setApellido(rs.getString("apellido"));
				empleado.setNombre(rs.getString("nombre"));
				empleado.setTipoDocumento(rs.getString("tipo_doc"));
				empleado.setNroDocumento(rs.getInt("nro_doc"));
				empleado.setDireccion(rs.getString("direccion"));
				empleado.setTelefono(rs.getString("telefono"));
				empleado.setCargo(rs.getString("cargo"));
				empleado.setPassword(rs.getString("password"));
				empleado.setNroSucursal(rs.getInt("nro_suc"));
			} else
				throw new Exception("Error, no se encuentra el Empleado de legajo "+legajo);
		} catch (SQLException ex){
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
			throw new Exception("Error en la consulta SQL al recuperar al cliente");
		}
		
		return empleado;
	}

}
