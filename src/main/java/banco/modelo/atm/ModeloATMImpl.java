package banco.modelo.atm;

import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.modelo.ModeloImpl;
import banco.utils.Fechas;


public class ModeloATMImpl extends ModeloImpl implements ModeloATM {
	
	private static Logger logger = LoggerFactory.getLogger(ModeloATMImpl.class);	

	private String tarjeta = null;   // mantiene la tarjeta del cliente actual
	private Integer codigoATM = null;
	private ResultSet rs = null;
	
	/*
	 * La información del cajero ATM se recupera del archivo que se encuentra definido en ModeloATM.CONFIG
	 */
	public ModeloATMImpl() {
		logger.debug("Se crea el modelo ATM.");

		logger.debug("Recuperación de la información sobre el cajero");
		
		Properties prop = new Properties();
		try (FileInputStream file = new FileInputStream(ModeloATM.CONFIG))
		{
			logger.debug("Se intenta leer el archivo de propiedades {}",ModeloATM.CONFIG);
			prop.load(file);

			codigoATM = Integer.valueOf(prop.getProperty("atm.codigo.cajero"));

			logger.debug("Código cajero ATM: {}", codigoATM);
			}
		catch(Exception ex)
		{
        	logger.error("Se produjo un error al recuperar el archivo de propiedades {}.",ModeloATM.CONFIG); 
		}
		return;
	}

	@Override
	public boolean autenticarUsuarioAplicacion(String tarjeta, String pin) throws Exception	{
		
		logger.info("Se intenta autenticar la tarjeta {} con pin {}", tarjeta, pin);
		
		try {
			String consulta_tarjeta_valida_sql = "SELECT * FROM Tarjeta T WHERE T.nro_tarjeta = "+tarjeta+" AND T.pin= MD5("+pin+")";
			rs = consulta(consulta_tarjeta_valida_sql);
				  
				if (!rs.next()) {
					logger.info("El PIN ingresado es incorrecto");
		            return false;
		        } else {
		        	logger.info("El PIN ingresado es correcto");
		    		this.tarjeta = tarjeta;
		    		conectar("atm","atm");
		            return true;
		        }
			}
		catch (java.sql.SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new Exception("Error en la conexión.");
		}
	}
		
	@Override
	public Double obtenerSaldo() throws Exception
	{
		logger.info("Se intenta obtener el saldo de cliente {}", 3);

		if (this.tarjeta == null ) {
			throw new Exception("El cliente no ingresó la tarjeta");
		}
		
		Double saldo = null;
		
		try {
			String consulta_saldo_sql = "SELECT saldo FROM trans_cajas_ahorro TCA JOIN Tarjeta T ON T.nro_ca=TCA.nro_ca WHERE T.nro_tarjeta = "+tarjeta;
			rs = consulta(consulta_saldo_sql);
			
			if(rs==null)
				throw new Exception("Error en la consulta SQL del saldo del cliente");
			
				if (rs.next()) {
		            saldo = rs.getDouble("saldo");
		            logger.info("El saldo de la cuenta asociada a la tarjeta " + tarjeta + " es: $" + saldo);
		        } else {
		        	logger.info("No se encontró una cuenta asociada con el número de tarjeta: " + tarjeta);
		        }
			}
		catch (java.sql.SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			}
		
		return saldo;
	}	

	@Override
	public ArrayList<TransaccionCajaAhorroBean> cargarUltimosMovimientos() throws Exception {
		return this.cargarUltimosMovimientos(ModeloATM.ULTIMOS_MOVIMIENTOS_CANTIDAD);
	}	
	
	@Override
	public ArrayList<TransaccionCajaAhorroBean> cargarUltimosMovimientos(int cantidad) throws Exception {
		/**
		 * TODO Deberá recuperar los ultimos movimientos del cliente, 
		 * 		la cantidad de movimientos a recuperar está definida en el parámetro (int cantidad).
		 * 		Debe capturar la excepción SQLException y propagar una Exception más amigable. 
		 */
		logger.info("Busca las ultimas {} transacciones en la BD de la tarjeta",cantidad);
		ArrayList<TransaccionCajaAhorroBean> lista = new ArrayList<TransaccionCajaAhorroBean>();
		
		try {
			String consulta_ultimos_mov_sql = "SELECT tca.* FROM trans_cajas_ahorro TCA JOIN Tarjeta T WHERE TCA.nro_ca = T.nro_ca AND T.nro_tarjeta = "+tarjeta+" ORDER BY fecha DESC, hora DESC LIMIT "+cantidad;
			rs = consulta(consulta_ultimos_mov_sql);
			
			while(rs.next()) {
				String fecha = rs.getString("fecha");
				String hora = rs.getString("hora");
				Double monto = rs.getDouble("monto");
				int cod_caja = rs.getInt("cod_caja");
				int destino = rs.getInt("destino");
				String tipo = rs.getString("tipo");
				TransaccionCajaAhorroBean fila = new TransaccionCajaAhorroBeanImpl();
				fila.setTransaccionFechaHora(Fechas.convertirStringADate(fecha,hora));
				fila.setTransaccionTipo(tipo);
				if(tipo.equals("Debito") || tipo.equals("Transferencia") || tipo.equals("Extraccion"))
					monto = monto*(-1);
				fila.setTransaccionMonto(monto);
				fila.setTransaccionCodigoCaja(cod_caja);
				fila.setCajaAhorroDestinoNumero(destino);
				lista.add(fila);
			}
			}
		catch (java.sql.SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new Exception("Error en la consulta SQL de los últimos movimientos del cliente.");
			}
		
		return lista;
	}	
	
	@Override
	public ArrayList<TransaccionCajaAhorroBean> cargarMovimientosPorPeriodo(Date desde, Date hasta)
			throws Exception {
		/**
		 * TODO Deberá recuperar los ultimos movimientos del cliente que se han realizado entre las fechas indicadas.
		 * 		Debe capturar la excepción SQLException y propagar una Exception más amigable. 
		 * 		Debe generar excepción sin las fechas son erroneas (ver descripción en interface)
		 */
		if (desde == null) {
			throw new Exception("El inicio del período no puede estar vacío");
		}
		if (hasta == null) {
			throw new Exception("El fin del período no puede estar vacío");
		}
		if (desde.after(hasta)) {
			throw new Exception("El inicio del período no puede ser posterior al fin del período");
		}	
		
		Date fechaActual = new Date();
		if (desde.after(fechaActual)) {
			throw new Exception("El inicio del período no puede ser posterior a la fecha actual");
		}	
		if (hasta.after(fechaActual)) {
			throw new Exception("El fin del período no puede ser posterior a la fecha actual");
		}

		ArrayList<TransaccionCajaAhorroBean> lista = new ArrayList<TransaccionCajaAhorroBean>();
		
		try {
			String consulta_ultimos_mov_sql = "SELECT tca.* FROM trans_cajas_ahorro TCA JOIN Tarjeta T WHERE TCA.nro_ca = T.nro_ca"
					+ " AND T.nro_tarjeta = "+tarjeta+" AND TCA.fecha BETWEEN '"+Fechas.convertirDateAStringDB(desde)+"' AND '"+Fechas.convertirDateAStringDB(hasta)+"' ORDER BY fecha DESC, hora DESC;";

			rs = consulta(consulta_ultimos_mov_sql);
			
			while(rs.next()) {
				String fecha = rs.getString("fecha");
				String hora = rs.getString("hora");
				Double monto = rs.getDouble("monto");
				int cod_caja = rs.getInt("cod_caja");
				int destino = rs.getInt("destino");
				String tipo = rs.getString("tipo");
				TransaccionCajaAhorroBean fila = new TransaccionCajaAhorroBeanImpl();
				fila.setTransaccionFechaHora(Fechas.convertirStringADate(fecha,hora));
				fila.setTransaccionTipo(tipo);
				if(tipo.equals("Debito") || tipo.equals("Transferencia") || tipo.equals("Extraccion"))
					monto = monto*(-1);
				fila.setTransaccionMonto(monto);
				fila.setTransaccionCodigoCaja(cod_caja);
				fila.setCajaAhorroDestinoNumero(destino);
				lista.add(fila);
			}
		} catch (SQLException ex) {
			logger.error("SQLException: {}", ex.getMessage());
			logger.error("SQLState: {}", ex.getSQLState());
			logger.error("VendorError: {}", ex.getErrorCode());
			throw new Exception("Error en la consulta SQL por los movientos de la tarjeta entre dos fechas.");
		}

		logger.debug("Retorna una lista con {} elementos", lista.size());
		
		return lista;
	}
	
	@Override
	public Double extraer(Double monto) throws Exception {
		logger.info("Realiza la extraccion de ${} sobre la cuenta", monto);
		
		if (this.codigoATM == null) {
			throw new Exception("Hubo un error al recuperar la información sobre el ATM.");
		}
		if (this.tarjeta == null) {
			throw new Exception("Hubo un error al recuperar la información sobre la tarjeta del cliente.");
		}

		/**
		 * TODO Deberá extraer de la cuenta del cliente el monto especificado (ya validado) y 
		 * 		obtener el saldo de la cuenta como resultado.
		 * 		Debe capturar la excepción SQLException y propagar una Exception más amigable. 		 * 		
		 */		

		try {
			String extraccion ="CALL extraccion("+codigoATM+", "+tarjeta+", "+monto+", @resultado);";
			logger.info("Se intenta realizar la extraccion en la caja {}, desde la cuenta asociada a la tarjeta nro {} por un monto de {}",codigoATM, tarjeta, monto);
			actualizacion(extraccion);
			
			rs=consulta("SELECT @resultado");
			
			if(rs == null) 
				throw new Exception("Error al transferir dinero.");
			
			String resultado = "";
			if(rs.next()) {
				resultado = rs.getString("@resultado");
				logger.info("Resultado de la extraccion: {}", resultado);
				
				if(!ModeloATM.EXTRACCION_EXITOSA.equals(resultado)) {
					throw new Exception("Error en la transaccion: " + resultado);
				}
			}
		} catch (java.sql.SQLException ex) {
			// Manejo de errores SQL no controlados (como fallas en la conexión o errores de sintaxis)
	        logger.error("Error SQL inesperado: {}", ex.getMessage());
	        throw new Exception("Error al realizar la extracción en la base de datos.", ex);
	    } catch (Exception ex) {
	        // Manejo de otros errores generales
	        logger.error("Error general: {}", ex.getMessage());
	        throw ex;  // Propagar cualquier otro error capturado
		}
		
		return this.obtenerSaldo();

	}

	@Override
	public int parseCuenta(String p_cuenta) throws Exception {
		/**
		 * TODO Verifica que el codigo de la cuenta sea valido. 
		 * 		Debe capturar la excepción SQLException y propagar una Exception más amigable. 
		 * 		Debe generar excepción si la cuenta es vacia (ya realizado en el if anterior), negativo 
		 * 		o no se puede generar el parsing.
		 *	
		 * 		Debe retornar el código de la cuenta en formato int.
		 */
		logger.info("Intenta realizar el parsing de un codigo de cuenta {}", p_cuenta);

		if (p_cuenta == null) {
			throw new Exception("El código de la cuenta no puede ser vacío");
		}
		
		int cod_cuenta = -1;
		
		try {
			cod_cuenta = Integer.parseInt(p_cuenta.trim());
			
			if(cod_cuenta<0) {
				throw new Exception("El código de la cuenta no puede ser negativo");
			}

			try {
				String consulta_nro_ca_sql = "SELECT * FROM trans_cajas_ahorro WHERE nro_ca="+cod_cuenta;
				rs = consulta(consulta_nro_ca_sql);
				if(rs==null)
					throw new Exception("El numero de cuenta no es válido.");
			} catch (SQLException ex) {
				logger.error("SQLException: {}", ex.getMessage());
				logger.error("SQLState: {}", ex.getSQLState());
				logger.error("VendorError: {}", ex.getErrorCode());
			}
			
			logger.info("Encontró la cuenta en la BD.");
	        return cod_cuenta;
	        
		} catch (NumberFormatException e) {
			throw new Exception("Error en el parsing del número de cuenta.");
		}

	}	
	
	@Override
	public Double transferir(Double monto, int cajaDestino) throws Exception {
		logger.info("Realiza la transferencia de ${} sobre a la cuenta {}", monto, cajaDestino);
		
		if (this.codigoATM == null) {
			throw new Exception("Hubo un error al recuperar la información sobre el ATM.");
		}
		if (this.tarjeta == null) {
			throw new Exception("Hubo un error al recuperar la información sobre la tarjeta del cliente.");
		}

		/**
		 * TODO Deberá extraer de la cuenta del cliente el monto especificado (ya fue validado) y
		 * 		de obtener el saldo de la cuenta como resultado.
		 * 		Debe capturar la excepción SQLException y propagar una Exception más amigable. 
		 */
		
		try {
			String transferencia= " CALL transferencia("+codigoATM+", "+tarjeta+", "+cajaDestino+", "+monto+", @resultado)";
			logger.info("Se intenta realizar la transferencia en la caja {}, desde la cuenta asociada a la tarjeta nro {} a {} por un monto de {}",codigoATM, tarjeta, cajaDestino, monto);
			actualizacion(transferencia);
			
			rs=consulta("SELECT @resultado");
			
			if(rs == null) 
				throw new Exception("Error al transferir dinero.");
			String resultado = "";
			if(rs.next()) {
				resultado = rs.getString("@resultado");
				logger.info("Resultado de la extraccion: {}", resultado);
				
				if(!ModeloATM.TRANSFERENCIA_EXITOSA.equals(resultado)) {
					throw new Exception("Error en la transaccion: " + resultado);
				}
			}
		} catch (java.sql.SQLException ex) {
			// Manejo de errores SQL no controlados (como fallas en la conexión o errores de sintaxis)
	        logger.error("Error SQL inesperado: {}", ex.getMessage());
	        throw new Exception("Error al realizar la extracción en la base de datos.", ex);
	    } catch (Exception ex) {
	        // Manejo de otros errores generales
	        logger.error("Error general: {}", ex.getMessage());
	        throw ex;  // Propagar cualquier otro error capturado
		}
		return this.obtenerSaldo();
	}

	@Override
	public Double parseMonto(String p_monto) throws Exception {
		
		logger.info("Intenta realizar el parsing del monto {}", p_monto);
		
		if (p_monto == null) {
			throw new Exception("El monto no puede estar vacío");
		}

		try 
		{
			double monto = Double.parseDouble(p_monto);
			DecimalFormat df = new DecimalFormat("#.00");

			monto = Double.parseDouble(corregirComa(df.format(monto)));
			
			if(monto < 0)
			{
				throw new Exception("El monto no debe ser negativo.");
			}
			
			return monto;
		}		
		catch (NumberFormatException e)
		{
			throw new Exception("El monto no tiene un formato válido.");
		}	
	}

	private String corregirComa(String n)
	{
		String toReturn = "";
		
		for(int i = 0;i<n.length();i++)
		{
			if(n.charAt(i)==',')
			{
				toReturn = toReturn + ".";
			}
			else
			{
				toReturn = toReturn+n.charAt(i);
			}
		}
		
		return toReturn;
	}	
	
	

	
}
