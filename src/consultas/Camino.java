package consultas;

/**
 * Clase que representa los caminos en una consulta
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */

import java.util.ArrayList;
import java.util.List;

public class Camino {
	/**
	 * Variable que almacena el nombre del camino.
	 */
	private String nombre;
	/**
	 * Variable que almacena las triplas que pertenecen al camino.
	 */
	private List<TriplaModeloGlobal> triplasCamino;

	/**
	 * Obtiene el nombre del camino.
	 * 
	 * @return Nombre del camino
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del camino.
	 * 
	 * @param pNombre
	 *            el Nombre a establecer
	 */
	public void setNombre(String pNombre) {
		this.nombre = pNombre;
	}

	/**
	 * Obtiene las triplas que pertenecen al camino.
	 * 
	 * @return Lista de triplas del camino.
	 */
	public List<TriplaModeloGlobal> getTriplasCamino() {
		return triplasCamino;
	}

	/**
	 * Establece la lista de triplas del camino.
	 * 
	 * @param pTriplasCamino
	 *            Las lista de triplas a establecer.
	 */
	public void setTriplasCamino(List<TriplaModeloGlobal> pTriplasCamino) {
		this.triplasCamino = pTriplasCamino;
	}

	/**
	 * Agrega una tripla a la lista de triplas del camino.
	 * 
	 * @param pTripla
	 *            Tripla a agregar.
	 */
	public void addTripla(TriplaModeloGlobal pTripla) {
		triplasCamino.add(pTripla);
	}

	/**
	 * Constructor por defecto que inicializa la lista de triplas.
	 */
	public Camino() {
		triplasCamino = new ArrayList<TriplaModeloGlobal>();
	}

}
