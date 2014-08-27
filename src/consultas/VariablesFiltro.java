package consultas;

/**
 * Clase que representa el filtro de la consulta del operador filter.
 * 
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */

public class VariablesFiltro {

	private String idEtiqueta = "";
	private String valorEtiqueta = "";
	private String valor = "";
	private String simboloComparacion = "";
	private String variableLogica = "";

	public VariablesFiltro(String idEtiqueta, String valor,
			String simboloComparacion) {
		this.valor = valor;
		this.idEtiqueta = idEtiqueta;
		this.simboloComparacion = simboloComparacion;
	}

	public String getValorEtiqueta() {
		return valorEtiqueta;
	}

	public void setValorEtiqueta(String valorEtiqueta) {
		this.valorEtiqueta = valorEtiqueta;
	}

	public String getEtiqueta() {
		return this.idEtiqueta;
	}

	public String getValor() {
		return this.valor;
	}

	public String getSimbolo() {
		return this.simboloComparacion;
	}

	public void setVarLogica(String var) {
		this.variableLogica = var;
	}

	public String getVarLogica() {
		return this.variableLogica;
	}

}
