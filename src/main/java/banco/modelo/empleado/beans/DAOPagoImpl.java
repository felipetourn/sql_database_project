package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;


public class DAOPagoImpl implements DAOPago {

	private static Logger logger = LoggerFactory.getLogger(DAOPagoImpl.class);
	
	private Connection conexion;
	
	public DAOPagoImpl(Connection c) {
		this.conexion = c;
	}

	@Override
	public ArrayList<PagoBean> recuperarPagos(int nroPrestamo) throws Exception {
		logger.info("Inicia la recuperacion de los pagos del prestamo {}", nroPrestamo);
		
		/** 
		 * TODO Recupera todos los pagos del prestamo (pagos e impagos) del prestamo nroPrestamo
		 * 	    Si ocurre algún error deberá propagar una excepción.
		 */
		ArrayList<PagoBean> lista = new ArrayList<PagoBean>();
		
		try {
			String recuperar_pagos_sql = "SELECT P.cant_meses, P.fecha, PA.* FROM Prestamo P JOIN Pago PA ON P.nro_prestamo=PA.nro_prestamo WHERE P.nro_prestamo="+nroPrestamo;
			Statement stmt= conexion.createStatement();
			ResultSet rs = stmt.executeQuery(recuperar_pagos_sql);	
			
			while(rs.next()) {
				PagoBean fila = new PagoBeanImpl();
				fila.setNroPrestamo(nroPrestamo);
				fila.setNroPago(rs.getInt("nro_pago"));
				fila.setFechaVencimiento(Fechas.convertirStringADate(rs.getString("fecha_venc")));
				fila.setFechaPago(Fechas.convertirStringADate(rs.getString("fecha_pago")));
				lista.add(fila);
			}
			
			stmt.close();
			
		} catch (SQLException ex){
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
		}
		
		return lista;
	}

	@Override
	public void registrarPagos(int nroPrestamo, List<Integer> cuotasAPagar)  throws Exception {

		logger.info("Inicia el pago de las {} cuotas del prestamo {}", cuotasAPagar.size(), nroPrestamo);

		/**
		 * TODO Registra los pagos de cuotas definidos en cuotasAPagar.
		 * 
		 * nroPrestamo asume que está validado
		 * cuotasAPagar Debe verificar que las cuotas a pagar no estén pagas (fecha_pago = NULL)
		 * @throws Exception Si hubo error en la conexión
		 */
		
		try {
			String recuperar_pagos_sql = "SELECT * FROM Pago WHERE nro_prestamo="+nroPrestamo+" AND fecha_pago IS NULL;";
			Statement stmt= conexion.createStatement();
			ResultSet rs = stmt.executeQuery(recuperar_pagos_sql);
			
			while(rs.next()) {
				if(cuotasAPagar.contains(rs.getInt("nro_pago"))){
					String ingresar_pago_sql = "UPDATE Pago SET fecha_pago=CURDATE() WHERE nro_prestamo = "+nroPrestamo+" AND nro_pago="+rs.getInt("nro_pago")+";";
					stmt= conexion.createStatement();
					stmt.executeUpdate(ingresar_pago_sql);
					logger.info("Se paga la cuota "+rs.getInt("nro_pago")+" del prestamo numero "+nroPrestamo);
				}
			}
			
			stmt.close();
			
		} catch (SQLException ex){
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
		}

	}
}
