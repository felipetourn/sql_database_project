package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;

public class DAOPrestamoImpl implements DAOPrestamo {

	private static Logger logger = LoggerFactory.getLogger(DAOPrestamoImpl.class);
	
	private Connection conexion;
	private ResultSet rs;
	
	public DAOPrestamoImpl(Connection c) {
		this.conexion = c;
	}
	
	
	@Override
	public void crearPrestamo(PrestamoBean prestamo) throws Exception {

		logger.info("Creación o actualizacion del prestamo.");
		logger.debug("prestamo : {}", prestamo.getNroPrestamo());
		logger.debug("fecha : {}", prestamo.getFecha());
		logger.debug("meses : {}", prestamo.getCantidadMeses());
		logger.debug("monto : {}", prestamo.getMonto());
		logger.debug("tasa : {}", prestamo.getTasaInteres());
		logger.debug("interes : {}", prestamo.getInteres());
		logger.debug("cuota : {}", prestamo.getValorCuota());
		logger.debug("legajo : {}", prestamo.getLegajo());
		logger.debug("cliente : {}", prestamo.getNroCliente());
		
		/**   
		 * TODO Crear un Prestamo segun el PrestamoBean prestamo. 
		 *    
		 * 
		 * @throws Exception deberá propagar la excepción si ocurre alguna. Puede capturarla para loguear los errores, ej.
		 *				logger.error("SQLException: " + ex.getMessage());
		 * 				logger.error("SQLState: " + ex.getSQLState());
		 *				logger.error("VendorError: " + ex.getErrorCode());
		 *		   pero luego deberá propagarla para que se encargue el controlador. 
		 */
		
		try {
			String crear_prestamo_sql = "INSERT INTO Prestamo (fecha, cant_meses, monto, tasa_interes, interes, valor_cuota, legajo, nro_cliente) VALUES "
					+ "( CURDATE(), "+prestamo.getCantidadMeses()+", "
							+prestamo.getMonto()+", "+prestamo.getTasaInteres()+", "+prestamo.getInteres()+", "+prestamo.getValorCuota()+", "+prestamo.getLegajo()+", "+prestamo.getNroCliente()+");";
			Statement stmt= conexion.createStatement();
			stmt.executeUpdate(crear_prestamo_sql);
			stmt.close();
			logger.info("Creación extiosa del prestamo.");

		} catch (SQLException ex){
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
		}
	}

	@Override
	public PrestamoBean recuperarPrestamo(int nroPrestamo) throws Exception {
		
		logger.info("Recupera el prestamo nro {}.", nroPrestamo);
		
		/**
		 * TODO Obtiene el prestamo según el id nroPrestamo
		 * 
		 * @param nroPrestamo
		 * @return Un prestamo que corresponde a ese id o null
		 * @throws Exception si hubo algun problema de conexión
		 */		
		PrestamoBean prestamo = null;
		try {
			String recuperar_prestamo_sql = "SELECT * FROM Prestamo where nro_prestamo="+nroPrestamo+";";
			Statement stmt= conexion.createStatement();
			rs = stmt.executeQuery(recuperar_prestamo_sql);
			
			if(rs.next()) {
				prestamo = new PrestamoBeanImpl();
				prestamo.setNroPrestamo(rs.getInt("nro_prestamo"));
				prestamo.setFecha(Fechas.convertirStringADate(rs.getString("fecha")));
				prestamo.setCantidadMeses(rs.getInt("cant_meses"));
				prestamo.setMonto(rs.getDouble("monto"));
				prestamo.setTasaInteres(rs.getDouble("tasa_interes"));
				prestamo.setInteres(rs.getDouble("interes"));
				prestamo.setValorCuota(rs.getDouble("valor_cuota"));
				prestamo.setLegajo(rs.getInt("legajo"));
				prestamo.setNroCliente(rs.getInt("nro_cliente"));
			}
		} catch (SQLException ex){
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
		}
   	
		return prestamo;
	}

}
