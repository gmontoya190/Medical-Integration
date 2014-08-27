package conexionBD;

/**
 * Clase que permite el acceso a la base de datos de mapeos. 
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */

import java.sql.Connection;
import conexionBD.*;
import consultas.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConexionBD {
	/**
	 * Variables que establece la conexi&oacute; con la base de datos.
	 */
	Connection conexion;
	Statement stmt;
	ResultSet rs;
	private static ConexionBD INSTANCIA = null;
	/**
	 * Variables que guarda la ruta de la base de datos.
	 */
	private final String nombreBD = "BD mapeos/mapeos";

	/**
	 * Constructor por defecto que establece la conexi&oacute;n.
	 */
	private ConexionBD() {
		establecerConexion();
	}

	/**
	 * Retorna una instancia de esta clase si antes no estaba creada.
	 */
	public static ConexionBD obtenerInstancia() {
		if (INSTANCIA == null)
			INSTANCIA = new ConexionBD();

		return INSTANCIA;
	}

	/**
	 * Establece la conexi&oacute:n con al base de datos.
	 */
	public void establecerConexion() {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.out.println("conexion de driver no establecida");
		}

		try {

			conexion = DriverManager.getConnection("jdbc:sqlite:" + nombreBD);
			stmt = conexion.createStatement();
		} catch (SQLException e) {
			System.err.println("problema con conexion");
			e.printStackTrace();
		}
	}

	/**
	 * Obtiene la lista de triplas a las que se mapea un arco del modelo global.
	 * 
	 * @param pIdTriplaModeloGlobal
	 * @return Lista de triplas.
	 */
	public List<TriplaModeloLocal> obtenerMapeosTripla(
			String pIdTriplaModeloGlobal) {
		List<TriplaModeloLocal> listaTriplas = new ArrayList<TriplaModeloLocal>();
		try {

			rs = stmt
					.executeQuery("SELECT TRIPLAMODELOLOCAL,IDFUENTE FROM MAPEOS WHERE IDTRIPLA='"
							+ pIdTriplaModeloGlobal + "'");
			while (rs.next()) {

				String tripla = rs.getString("TRIPLAMODELOLOCAL");
				int idFuente = rs.getInt("IDFUENTE");

				TriplaModeloLocal t = new TriplaModeloLocal(tripla, idFuente);

				t.asignarValoresTripla(t);
				listaTriplas.add(t);

			}

		} catch (SQLException e) {
			System.err.println("No es posible recuperar las triplas de :"
					+ pIdTriplaModeloGlobal);
			System.err.println("error: " + e.getMessage());

		}
		return listaTriplas;

	}

	/**
	 * Obtiene la lista de fuentes del modelo *
	 * 
	 * @return Lista de fuentes.
	 */
	public List<Fuente> obtenerListaFuentes() {
		String etiqueta = "";
		int id = 0;
		List<Fuente> listaFuentes = new ArrayList<Fuente>();

		try {

			rs = stmt.executeQuery("SELECT IDFUENTE, NOMBRE FROM FUENTE");
			while (rs.next()) {

				id = rs.getInt("IDFUENTE");
				etiqueta = rs.getString("NOMBRE");
				Fuente f = new Fuente(etiqueta, id, "", "");
				listaFuentes.add(f);

			}

		} catch (SQLException e) {
			System.err.println("No es posible recuperar la lista de fuentes");

		}

		return listaFuentes;
	}

	/**
	 * Obtiene el numero de fuentes del modelo.
	 * 
	 * @return Numero de fuentes.
	 */
	public int obtenerNumeroFuentes() {
		int n = 0;

		try {

			rs = stmt
					.executeQuery("SELECT COUNT(IDFUENTE) AS FUENTES FROM FUENTE");
			rs.next();

			n = rs.getInt("FUENTES");

		} catch (SQLException e) {
			System.err.println("No es posible recuperar el numero de fuentes");

		}

		return n;
	}

	/**
	 * Obtiene los mapeos de integraci&oacute;n para un nodo en una fuente.
	 * 
	 * @param pIdNodo
	 *            identificador del nodo.
	 * @param pIdFuente
	 *            indentificador de la fuente.
	 * @return Lista de triplas
	 * */
	public List<TriplaModeloLocal> obtenerMapeoIn(String pIdNodo, int pIdFuente)

	{
		List<TriplaModeloLocal> triplas = new ArrayList<TriplaModeloLocal>();
		String valorTripla = "";
		try {

			rs = stmt
					.executeQuery("SELECT TRIPLAINTEGRADORA FROM INTEGRACION WHERE IDNODO='"
							+ pIdNodo
							+ "'"
							+ "AND IDFUENTE='"
							+ pIdFuente
							+ "'");
			while (rs.next()) {

				valorTripla = rs.getString("TRIPLAINTEGRADORA");
				TriplaModeloLocal t = new TriplaModeloLocal(valorTripla,
						pIdFuente);
				triplas.add(t);

			}

		} catch (SQLException e) {
			System.err
					.println("No es posible recuperar las triplas de integracion de :"
							+ pIdNodo);
			System.err.println("error: " + e.getMessage());

		}

		return triplas;
	}

}
