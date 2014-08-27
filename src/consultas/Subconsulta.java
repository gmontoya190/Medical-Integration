package consultas;

/**
 * Clase que representa las subconsultas hacia la fuente.
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */
import java.util.List;
import java.util.ArrayList;

public class Subconsulta {

	private String select = "";
	private String where = "";
	private String filter = "";
	private int fuente = 0;
	private List<String> datosRetorno;
	private List<TriplaModeloLocal> patronConsulta;
	private List<VariablesFiltro> listaFiltro;
	private List<Optional> listaOptional;
	private boolean filtro;

	public List<TriplaModeloLocal> getPatronConsulta() {
		return patronConsulta;
	}

	public void setPatronConsulta(List<TriplaModeloLocal> patronConsulta) {
		this.patronConsulta = patronConsulta;
	}

	public Subconsulta() {
		datosRetorno = new ArrayList<String>();
		patronConsulta = new ArrayList<TriplaModeloLocal>();
		listaFiltro = new ArrayList<VariablesFiltro>();
		listaOptional = new ArrayList<Optional>();
		filtro = false;
	}

	public List<Optional> getListaOptional() {
		return listaOptional;
	}

	public void setListaOptional(List<Optional> listaOptional) {
		this.listaOptional = listaOptional;
	}

	public List<VariablesFiltro> getListaFiltro() {
		return listaFiltro;
	}

	public void setListaFiltro(List<VariablesFiltro> listaFiltro) {
		this.listaFiltro = listaFiltro;
	}

	public void addDatoRetorno(String dato) {
		datosRetorno.add(dato);
	}

	public void addPatronConsulta(TriplaModeloLocal t) {
		patronConsulta.add(t);
	}

	public void addVariableFiltro(VariablesFiltro var) {
		listaFiltro.add(var);
	}

	public void addOptional(Optional op) {
		listaOptional.add(op);
	}

	public List<String> getDatosRetorno() {
		return datosRetorno;
	}

	public void setDatosRetorno(List<String> datosRetorno) {
		this.datosRetorno = datosRetorno;
	}

	public int getFuente() {
		return fuente;
	}

	public void setFuente(int fuente) {
		this.fuente = fuente;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public boolean isFiltro() {
		return filtro;
	}

	public void setFiltro(boolean filtro) {
		this.filtro = filtro;
	}

}
