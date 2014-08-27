package consultas;

/**
 * Clase que representa las triplas de los modelos locales.
 * 
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */

public class TriplaModeloLocal {

	private String valor;
	private int idFuente;
	private String entidad;
	private String relacion;
	private String objeto;

	public TriplaModeloLocal(String v, int id)

	{
		this.valor = v;
		this.idFuente = id;
		this.entidad = "";
		this.relacion = "";
		this.objeto = "";
	}

	public int getIdfuente() {
		return this.idFuente;
	}

	public String getValor() {
		return this.valor;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getRelacion() {
		return relacion;
	}

	public void setRelacion(String relacion) {
		this.relacion = relacion;
	}

	public String getObjeto() {
		return objeto;
	}

	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}

	public void asignarValoresTripla(TriplaModeloLocal t) {
		int i = 0;

		int cont = 0;
		String valor = t.getValor();
		int tamano = valor.length();
		String entidad = "";
		String relacion = "";
		String objeto = "";
		while (i != tamano) {
			// System.out.println("valor en i "+valor.charAt(i));
			if (valor.charAt(i) != ' ' && cont == 0)

			{

				entidad += valor.charAt(i);
				// System.out.println("valor entidad "+entidad);

			}

			if (cont == 1 && valor.charAt(i) != ' ') {
				relacion += valor.charAt(i);

			}
			if (cont == 2 && valor.charAt(i) != ' ') {
				objeto += valor.charAt(i);
			}

			if (i != tamano - 1) {

				if (valor.charAt(i + 1) == ' ' && valor.charAt(i) != ' ') {
					cont++;
				}

			}
			i++;
		}

		/*
		 * System.out.println("entidad "+entidad);
		 * System.out.println("relacion "+relacion);
		 * System.out.println("objeto "+objeto);
		 */

		t.setEntidad(entidad);
		t.setObjeto(objeto);
		t.setRelacion(relacion);

	}

	public static void main(String[] args) {

		TriplaModeloLocal t = new TriplaModeloLocal(
				"?Person  peLastName  ?paLastName", 1);
		t.asignarValoresTripla(t);
	}

}
