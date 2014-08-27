package consultas;

/**
 * Clase que representa las fuentes del modelo.  
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */

import java.util.ArrayList;
import java.util.List;

public class Fuente {

	private String etiqueta;
	private String url;
	private String descripcion;
	private int id;
	private List<TriplaModeloGlobal> listaTriplasConsulta;

	public Fuente(String e, int id, String url, String des) {
		this.etiqueta = e;
		this.url = url;
		this.descripcion = des;
		this.id = id;
		listaTriplasConsulta = new ArrayList<TriplaModeloGlobal>();
	}

	public String getE() {
		return this.etiqueta;
	}

	public int getId() {
		return this.id;
	}

	public void setTriplasConsulta(TriplaModeloGlobal t) {

		listaTriplasConsulta.add(t);
	}

	public void imprimirTriplasxConsulta() {
		for (int i = 0; i < listaTriplasConsulta.size(); i++) {
			TriplaModeloGlobal t = (TriplaModeloGlobal) listaTriplasConsulta
					.get(i);
			System.out.println("etiqueta tripla  " + t.getR());
			System.out.println("id nodo valor  " + t.getV());
			System.out.println("filtro  " + t.getFiltro());

		}
	}

	public List<TriplaModeloGlobal> getListaTriplasConsulta() {
		return listaTriplasConsulta;
	}

	public void setListaTriplasConsulta(
			List<TriplaModeloGlobal> listaTriplasConsulta) {
		this.listaTriplasConsulta = listaTriplasConsulta;
	}

}