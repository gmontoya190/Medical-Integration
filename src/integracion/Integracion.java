package integracion;

/**
 * Clase que obtiene los mapeos integradores.
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */

import java.util.ArrayList;
import java.util.List;

import conexionBD.ConexionBD;
import consultas.TriplaModeloLocal;

public class Integracion {

	private ConexionBD bd = ConexionBD.obtenerInstancia();

	public List<TriplaModeloLocal> obtenerTriplasInt(String idNodo, int idFuente) {
		List<TriplaModeloLocal> triplas = new ArrayList<TriplaModeloLocal>();
		triplas = bd.obtenerMapeoIn(idNodo, idFuente);

		for (int i = 0; i < triplas.size(); i++) {
			TriplaModeloLocal t = triplas.get(i);
			t.asignarValoresTripla(t);
		}

		return triplas;
	}

}
