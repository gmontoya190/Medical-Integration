package consultas;

/**
 * Clase que representa las triplas del modelo global.
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */

import java.util.ArrayList;
import java.util.List;

public class TriplaModeloGlobal {

	private String idEntidad;
	private String idValor;
	private String relacion;
	private String idRelacion;
	private String tipoTripla;
	private String caminoConsulta;
	private List<TriplaModeloLocal> triplasdeMapeo;
	private List<Fuente> fuentes;
	private boolean filtro;

	public TriplaModeloGlobal(String idNodoE, String idNodoV, String relacion,
			String idRelacion, String tipo)

	{
		this.idEntidad = idNodoE;
		this.idValor = idNodoV;
		this.relacion = relacion;
		this.idRelacion = idRelacion;
		this.tipoTripla = tipo;
		fuentes = new ArrayList<Fuente>();
		triplasdeMapeo = new ArrayList<TriplaModeloLocal>();
		this.filtro = false;
	}

	public List<TriplaModeloLocal> getTriplasdeMapeo() {
		return triplasdeMapeo;
	}

	public void setTriplasdeMapeo(List<TriplaModeloLocal> triplasdeMapeo) {

		this.triplasdeMapeo = triplasdeMapeo;
		// dividirTripla();

	}

	public String getE()// id de la entidad
	{
		return this.idEntidad;
	}

	public String getR() {
		return this.relacion;
	}

	public String getV() {
		return this.idValor;
	}

	public String getCamino() {
		return this.caminoConsulta;
	}

	public String getIdTripla() {
		return this.idRelacion;
	}

	public boolean getFiltro() {
		return this.filtro;
	}

	public void setCamino(String p) {
		this.caminoConsulta = p;
	}

	public void setTriplasDeMapeo(List<TriplaModeloLocal> t) {
		this.triplasdeMapeo = t;
	}

	public void setFiltro() {
		this.filtro = true;

	}

	public void addFuente(Fuente f) {
		fuentes.add(f);
	}

	public boolean integradoraOTipo3()// Integradora o tipo 3 sin mapeo
	{
		boolean triplaId = false;

		if (triplasdeMapeo.isEmpty()) {
			triplaId = true;
		}

		return triplaId;

	}

	public void dividirTripla() {
		for (int i = 0; i < triplasdeMapeo.size(); i++) {
			TriplaModeloLocal t = triplasdeMapeo.get(i);
			t.asignarValoresTripla(t);
		}
	}

	public String getTipoTripla() {
		return tipoTripla;
	}

	public void setTipoTripla(String tipoTripla) {
		this.tipoTripla = tipoTripla;
	}

}
