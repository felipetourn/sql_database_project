package banco.modelo.empleado.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.utils.Fechas;

public class DAOClienteMorosoImpl implements DAOClienteMoroso {

	private static Logger logger = LoggerFactory.getLogger(DAOClienteMorosoImpl.class);
	
	private Connection conexion;
	
	public DAOClienteMorosoImpl(Connection c) {
		this.conexion = c;
	}
	
	@Override
	public ArrayList<ClienteMorosoBean> recuperarClientesMorosos() throws Exception {
		
		logger.info("Busca los clientes morosos.");
		
		DAOPrestamo daoPrestamo = new DAOPrestamoImpl(this.conexion);		
		DAOCliente daoCliente = new DAOClienteImpl(this.conexion);
		
		/**
		 * TODO Deberá recuperar un listado de clientes morosos los cuales consisten de un bean ClienteMorosoBeanImpl
		 *      deberá indicar para dicho cliente cual es el prestamo sobre el que está moroso y la cantidad de cuotas que 
		 *      tiene atrasadas. En todos los casos deberá generar excepciones que será capturadas por el controlador
		 *      si hay algún error que necesita ser informado al usuario. 
		 */

		ArrayList<ClienteMorosoBean> morosos = new ArrayList<ClienteMorosoBean>();
		
		try {
			String recuperar_prestamos_sql = "SELECT * FROM Prestamo;";
			Statement stmt= conexion.createStatement();
			ResultSet rs = stmt.executeQuery(recuperar_prestamos_sql);
			
			while(rs.next()) {
				String recuperar_pagos_prestamos_sql = "SELECT * FROM Pago WHERE nro_prestamo="+rs.getInt("nro_prestamo")+" AND fecha_venc<CURDATE() AND fecha_pago IS NULL;";
				stmt= conexion.createStatement();
				ResultSet rs_pagos = stmt.executeQuery(recuperar_pagos_prestamos_sql);
				int cant_cuotas_atrasado=0;
				int cuota_a_pagar=-1;
				while(rs_pagos.next() && cant_cuotas_atrasado!=2) {
					if(cuota_a_pagar==-1)
						cuota_a_pagar=rs_pagos.getInt("nro_pago");
					cant_cuotas_atrasado++;
				}
						
				if(cant_cuotas_atrasado==2) {
					ClienteMorosoBean moroso = new ClienteMorosoBeanImpl();
					moroso.setPrestamo(daoPrestamo.recuperarPrestamo(rs.getInt("nro_prestamo")));
					moroso.setCliente(daoCliente.recuperarCliente(rs.getInt("nro_cliente")));
					moroso.setCantidadCuotasAtrasadas(rs.getInt("cant_meses") - cuota_a_pagar +1);
					morosos.add(moroso);
				}
			}
			
			stmt.close();
		} catch (SQLException ex){
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
			throw new Exception ("Error en la consulta SQL de clientes morosos");
		}
		
		return morosos;
	}


}

