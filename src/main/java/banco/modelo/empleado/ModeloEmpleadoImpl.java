package banco.modelo.empleado;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import banco.modelo.ModeloImpl;
import banco.modelo.empleado.beans.ClienteBean;
import banco.modelo.empleado.beans.ClienteMorosoBean;
import banco.modelo.empleado.beans.DAOCliente;
import banco.modelo.empleado.beans.DAOClienteImpl;
import banco.modelo.empleado.beans.DAOClienteMoroso;
import banco.modelo.empleado.beans.DAOClienteMorosoImpl;
import banco.modelo.empleado.beans.DAOEmpleado;
import banco.modelo.empleado.beans.DAOEmpleadoImpl;
import banco.modelo.empleado.beans.DAOPago;
import banco.modelo.empleado.beans.DAOPagoImpl;
import banco.modelo.empleado.beans.DAOPrestamo;
import banco.modelo.empleado.beans.DAOPrestamoImpl;
import banco.modelo.empleado.beans.EmpleadoBean;
import banco.modelo.empleado.beans.PagoBean;
import banco.modelo.empleado.beans.PrestamoBean;

public class ModeloEmpleadoImpl extends ModeloImpl implements ModeloEmpleado {

	private static Logger logger = LoggerFactory.getLogger(ModeloEmpleadoImpl.class);	

	// Indica el usuario actualmente logueado. Null corresponde que todavia no se ha autenticado
	private Integer legajo = null;
	private ResultSet rs = null;
	
	public ModeloEmpleadoImpl() {
		logger.debug("Se crea el modelo Empleado.");
	}
	
	@Override
	public boolean autenticarUsuarioAplicacion(String legajo, String password) throws Exception {
		logger.info("Se intenta autenticar el legajo {} con password {}", legajo, password);
		/** 
		 * TODO Código que autentica que exista un legajo de empleado y que el password corresponda a ese legajo
		 *      (el password guardado en la BD está en MD5) 
		 *      En caso exitoso deberá registrar el legajo en la propiedad legajo y retornar true.
		 *      Si la autenticación no es exitosa porque el legajo no es válido o el password es incorrecto
		 *      deberá retornar falso y si hubo algún otro error deberá producir una excepción.
		 */
		Integer legajoInt = null;		
		try {
			legajoInt = Integer.valueOf(legajo.trim());
        }
        catch (Exception ex) {
        	throw new Exception("Se esperaba que el legajo sea un valor entero.");
        }

		try {
			String consulta_contrasenia_valida_sql = "SELECT * FROM Empleado E WHERE E.legajo = '"+legajo+"' AND E.password= MD5('"+password+"')";
			rs = consulta(consulta_contrasenia_valida_sql);
				  
				if (!rs.next()) {
					logger.info("La contraseña ingresada es incorrecta");
		            return false;
		        } else {
		        	logger.info("La contraseña ingresada es correcta");
		    		this.legajo=legajoInt;
		    		conectar(legajo,password);
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
	public EmpleadoBean obtenerEmpleadoLogueado() throws Exception {
		logger.info("Solicita al DAO un empleado con legajo {}", this.legajo);
		if (this.legajo == null) {
			logger.info("No hay un empleado logueado.");
			throw new Exception("No hay un empleado logueado. La sesión terminó.");
		}
		
		DAOEmpleado dao = new DAOEmpleadoImpl(this.conexion);
		return dao.recuperarEmpleado(this.legajo);
	}	
	
	@Override
	public ArrayList<String> obtenerTiposDocumento() throws Exception {
		logger.info("Recupera los tipos de documentos.");
		/** 
		 * TODO Debe retornar una lista de strings con los tipos de documentos. 
		 *      Deberia propagar una excepción si hay algún error en la consulta.
		 */
		
		ArrayList<String> tipos = new ArrayList<String>();
		try {
			String consulta_tipos_doc = "SELECT DISTINCT tipo_doc FROM Empleado;";
			rs = consulta(consulta_tipos_doc);
				  
		    logger.info("Se registran los distintos tipos de documento");
		    while(rs.next()) {
		    	String tipo_doc = rs.getString("tipo_doc");
		        tipos.add(tipo_doc);
		    }
			}
		catch (java.sql.SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new Exception("Error en la consulta SQL al intentar obtener los tipos de documento.");
			}
		return tipos;
	}	

	@Override
	public double obtenerTasa(double monto, int cantidadMeses) throws Exception {

		logger.info("Busca la tasa correspondiente a el monto {} con una cantidad de meses {}", monto, cantidadMeses);

		/** 
		 * TODO Debe buscar la tasa correspondiente según el monto y la cantidadMeses. 
		 *      Deberia propagar una excepción si hay algún error de conexión o 
		 *      no encuentra el monto dentro del [monto_inf,monto_sup] y la cantidadMeses.
		 */
		
		double tasa = 0.00;
		try {
			String consulta_tasa = "SELECT tasa FROM Tasa_prestamo where periodo=("+cantidadMeses+") "
					+ "AND monto_inf<="+monto+" AND monto_sup>="+monto+";";
			rs = consulta(consulta_tasa);
			
		     if (!rs.next()) {
					logger.info("No hay tasas para ese monto y esa cantidad de meses");
					throw new Exception("Error, la tasa no existe para la cantidad de meses y el monto indicados.");
		        } else {
		        	tasa = rs.getDouble("tasa");
		        	logger.info("La tasa es {}",tasa);
		        }
		        
			}
		catch (java.sql.SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			}
   		return tasa;
	}

	@Override
	public double obtenerInteres(double monto, double tasa, int cantidadMeses) {
		return (monto * tasa * cantidadMeses) / 1200;
	}

	@Override
	public double obtenerValorCuota(double monto, double interes, int cantidadMeses) {
		return (monto + interes) / cantidadMeses;
	}
		
	@Override
	public ClienteBean recuperarCliente(String tipoDoc, int nroDoc) throws Exception {
		DAOCliente dao = new DAOClienteImpl(this.conexion);
		return dao.recuperarCliente(tipoDoc, nroDoc);
	}

	@Override
	public ArrayList<Integer> obtenerCantidadMeses(double monto) throws Exception {
		logger.info("recupera los períodos (cantidad de meses) según el monto {} para el prestamo.", monto);

		/** 
		 * TODO Debe buscar los períodos disponibles según el monto. 
		 *      Deberia propagar una excepción si hay algún error de conexión o 
		 *      no encuentra el monto dentro del [monto_inf,monto_sup].
		 */
			
		ArrayList<Integer> cantMeses = new ArrayList<Integer>();
		try {
			String consulta_cant_meses = "SELECT periodo FROM Tasa_prestamo where monto_inf<="+monto+" AND monto_sup>="+monto+";";
			rs = consulta(consulta_cant_meses);
			
			if (rs ==null) 
				throw new Exception("Error, no hay un período válido para el monto "+monto);
			while(rs.next()) {
	        	cantMeses.add(rs.getInt("periodo"));
	        	logger.info("La cantidad de meses puede ser {}",rs.getInt("periodo"));
			}
			}
		catch (java.sql.SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new Exception("Error en la consulta SQL al obtener el período disponible para un préstamo de cierto monto");
		}
		
		return cantMeses;
	}

	@Override	
	public Integer prestamoVigente(int nroCliente) throws Exception 
	{
		logger.info("Verifica si el cliente {} tiene algun prestamo que tienen cuotas por pagar.", nroCliente);

		/** 
		 * TODO Busca algún prestamo del cliente que tenga cuotas sin pagar (vigente) retornando el nro_prestamo
		 *      si no existe prestamo del cliente o todos están pagos retorna null.
		 *      Si hay una excepción la propaga con un mensaje apropiado.
		 */
		
		try {
			int nro_prestamo = 0;
			String consulta_prestamos_clientes = "SELECT * FROM Prestamo P WHERE P.nro_cliente="+nroCliente+" AND EXISTS "
					+ "(SELECT * FROM Pago PA WHERE PA.nro_prestamo=P.nro_prestamo AND PA.fecha_pago IS NULL);";
			rs = consulta(consulta_prestamos_clientes);

			if (!rs.next()) {
				logger.info("No hay prestamos para el cliente nro {}",nroCliente);
				return null;
			} else {
			    nro_prestamo = rs.getInt("nro_prestamo");
				return nro_prestamo;
			}

			}
		catch (java.sql.SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new Exception("Error en la consulta SQL al obtener los préstamos vigentes del cliente");
		}
		

	}

	@Override
	public void crearPrestamo(PrestamoBean prestamo) throws Exception {
		logger.info("Crea un nuevo prestamo.");
		
		if (this.legajo == null) {
			throw new Exception("No hay un empleado registrado en el sistema que se haga responsable por este prestamo.");
		}
		else 
		{
			logger.info("Actualiza el prestamo con el legajo {}",this.legajo);
			prestamo.setLegajo(this.legajo);
			
			DAOPrestamo dao = new DAOPrestamoImpl(this.conexion);		
			dao.crearPrestamo(prestamo);
		}
	}
	
	@Override
	public PrestamoBean recuperarPrestamo(int nroPrestamo) throws Exception {
		logger.info("Busca el prestamo número {}", nroPrestamo);
		
		DAOPrestamo dao = new DAOPrestamoImpl(this.conexion);		
		return dao.recuperarPrestamo(nroPrestamo);
	}
	
	@Override
	public ArrayList<PagoBean> recuperarPagos(Integer prestamo) throws Exception {
		logger.info("Solicita la busqueda de pagos al modelo sobre el prestamo {}.", prestamo);
		
		DAOPago dao = new DAOPagoImpl(this.conexion);		
		return dao.recuperarPagos(prestamo);
	}
	
	@Override
	public void pagarCuotas(String p_tipo, int p_dni, int nroPrestamo, List<Integer> cuotasAPagar) throws Exception {
		ClienteBean c = this.recuperarCliente(p_tipo.trim(), p_dni);
		
		if (nroPrestamo != this.prestamoVigente(c.getNroCliente())) {
			throw new Exception ("El nro del prestamo no coincide con un prestamo vigente del cliente");
		}

		if (cuotasAPagar.size() == 0) {
			throw new Exception ("Debe seleccionar al menos una cuota a pagar.");
		}
		
		DAOPago dao = new DAOPagoImpl(this.conexion);
		dao.registrarPagos(nroPrestamo, cuotasAPagar);		
	}

	@Override
	public ArrayList<ClienteMorosoBean> recuperarClientesMorosos() throws Exception {
		logger.info("Modelo solicita al DAO que busque los clientes morosos");
		DAOClienteMoroso dao = new DAOClienteMorosoImpl(this.conexion);
		return dao.recuperarClientesMorosos();	
	}
	

	
}
