package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;

public class DAOClienteImpl implements DAOCliente {

	private static Logger logger = LoggerFactory.getLogger(DAOClienteImpl.class);
	
	private Connection conexion;
	private ResultSet rs = null;
	
	public DAOClienteImpl(Connection c) {
		this.conexion = c;
	}
	
	@Override
	public ClienteBean recuperarCliente(String tipoDoc, int nroDoc) throws Exception {

		logger.info("recupera el cliente con documento de tipo {} y nro {}.", tipoDoc, nroDoc);
		
		/**
		 * TODO Recuperar el cliente que tenga un documento que se corresponda con los parámetros recibidos.  
		 *		Deberá generar o propagar una excepción si no existe dicho cliente o hay un error de conexión.		
		 */
		
		
		ClienteBean cliente = new ClienteBeanImpl();

		try {
			String recuperar_cliente_sql = "SELECT * FROM Cliente where tipo_doc='"+tipoDoc+"' AND + nro_doc="+nroDoc+";";
			Statement stmt= conexion.createStatement();
			rs = stmt.executeQuery(recuperar_cliente_sql);
			
			if(!rs.next())
				throw new Exception("Error, no se encontró un cliente con "+tipoDoc+" "+nroDoc);
			else{
				cliente.setNroCliente(rs.getInt("nro_cliente"));
				cliente.setApellido(rs.getString("apellido"));
				cliente.setNombre(rs.getString("nombre"));
				cliente.setTipoDocumento(rs.getString("tipo_doc"));
				cliente.setNroDocumento(rs.getInt("nro_doc"));
				cliente.setDireccion(rs.getString("direccion"));
				cliente.setTelefono(rs.getString("telefono"));
				cliente.setFechaNacimiento(Fechas.convertirStringADate(rs.getString("fecha_nac")));
			}
			
			stmt.close();
		} catch (SQLException ex){
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
		}

		return cliente;		
	}

	@Override
	public ClienteBean recuperarCliente(Integer nroCliente) throws Exception {
		logger.info("recupera el cliente por nro de cliente.");
		
		/**
		 * TODO Recuperar el cliente que tenga un número de cliente de acuerdo al parámetro recibido.  
		 *		Deberá generar o propagar una excepción si no existe dicho cliente o hay un error de conexión.		
		 */

		ClienteBean cliente = new ClienteBeanImpl();
		
		try {
			String recuperar_cliente_sql = "SELECT * FROM Cliente where nro_cliente="+nroCliente+";";
			Statement stmt= conexion.createStatement();
			rs = stmt.executeQuery(recuperar_cliente_sql);
			
			 if(!rs.next())
			 	throw new Exception("Error, no se encontró un cliente con nro_cliente="+nroCliente);
			 else{
			 	cliente.setNroCliente(rs.getInt("nro_cliente"));
				cliente.setApellido(rs.getString("apellido"));
				cliente.setNombre(rs.getString("nombre"));
				cliente.setTipoDocumento(rs.getString("tipo_doc"));
				cliente.setNroDocumento(rs.getInt("nro_doc"));
				cliente.setDireccion(rs.getString("direccion"));
				cliente.setTelefono(rs.getString("telefono"));
				cliente.setFechaNacimiento(Fechas.convertirStringADate(rs.getString("fecha_nac")));
			 }
			 
			stmt.close();
		} catch (SQLException ex){
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
		}
		
		return cliente;		
	}

}
