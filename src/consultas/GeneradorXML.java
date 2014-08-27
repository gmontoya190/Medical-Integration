package consultas;

/**
 * Clase que genera las consultas finales en el formato de salida.
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.util.List;

public class GeneradorXML {

	public void generarSubconsultasFuente(List<Subconsulta> listaSub,
			int idFuente) {
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation implementation = builder.getDOMImplementation();

			Document document = implementation.createDocument(null,
					"consultas", null);
			document.setXmlVersion("1.0");

			Element raiz = document.getDocumentElement();		

			for (int i = 0; i < listaSub.size(); i++) {
				Subconsulta sub = listaSub.get(i);
				Element consulta = document.createElement("consulta");
				raiz.appendChild(consulta);
				Element select = document.createElement("select");
				consulta.appendChild(select);				
				for (int j = 0; j < sub.getDatosRetorno().size(); j++) // DATOS
					            										// DE
																		// RETORNO
				{
					
					Element nodoRetorno = document.createElement("nodoRetorno");
					String valor = sub.getDatosRetorno().get(j);
					Text etiquetaNodo = document.createTextNode(valor); // Ingresamos
																		// la
																		// info
					nodoRetorno.appendChild(etiquetaNodo);
					select.appendChild(nodoRetorno);
				}

				Element where = document.createElement("where");
				Element patron = document.createElement("patron");
				consulta.appendChild(where);
				where.appendChild(patron);

				for (int k = 0; k < sub.getPatronConsulta().size(); k++)// PATRON
																		// DE
																		// CONSULTAS
				{
					Element arco = document.createElement("arco");
					Element clase = document.createElement("clase");
					Element relacion = document.createElement("relacion");
					Element objeto = document.createElement("objeto");

					TriplaModeloLocal t = sub.getPatronConsulta().get(k);
					String valorClase = t.getEntidad();
					String valorRelacion = t.getRelacion();
					String valorObjeto = t.getObjeto();

					Text textClase = document.createTextNode(valorClase);
					Text textRelacion = document.createTextNode(valorRelacion);
					Text textObjeto = document.createTextNode(valorObjeto);

					clase.appendChild(textClase);
					relacion.appendChild(textRelacion);
					objeto.appendChild(textObjeto);

					arco.appendChild(clase);
					arco.appendChild(relacion);
					arco.appendChild(objeto);

					patron.appendChild(arco);

				}
				if (sub.getListaOptional().isEmpty()) {

					if (sub.isFiltro()) {

						Element filtro = document.createElement("filtro");
						Element expresion = document.createElement("expresion");
						patron.appendChild(filtro);
						filtro.appendChild(expresion);
						Element dato = document.createElement("dato");
						String valor = sub.getListaFiltro().get(0)
								.getValorEtiqueta();
						Text valorDato = document.createTextNode(valor);
						dato.appendChild(valorDato);

						Element operadorComparacion = document
								.createElement("operadorComparacion");
						String valorOpComp = sub.getListaFiltro().get(0)
								.getSimbolo();
						Text valorDatoComp = document
								.createTextNode(valorOpComp);
						operadorComparacion.appendChild(valorDatoComp);

						Element valorNodo = document.createElement("valor");
						String nodoValor = sub.getListaFiltro().get(0)
								.getValor();
						Text nodoCampoValor = document
								.createTextNode(nodoValor);
						valorNodo.appendChild(nodoCampoValor);

						expresion.appendChild(dato);
						expresion.appendChild(operadorComparacion);
						expresion.appendChild(valorNodo);
						where.appendChild(filtro);
						filtro.appendChild(expresion);

					}

				}

				else {
					for (int k = 0; k < sub.getListaOptional().size(); k++) {

						Optional op = sub.getListaOptional().get(k);

						Element opcional = document.createElement("optional");

						for (int m = 0; m < op.getTriplasOptional().size(); m++) {
							TriplaModeloLocal t = op.getTriplasOptional()
									.get(m);

							Element arco = document.createElement("arco");
							Element clase = document.createElement("clase");
							Element relacion = document
									.createElement("relacion");
							Element objeto = document.createElement("objeto");

							String valorClase = t.getEntidad();
							String valorRelacion = t.getRelacion();
							String valorObjeto = t.getObjeto();

							Text textClase = document
									.createTextNode(valorClase);
							Text textRelacion = document
									.createTextNode(valorRelacion);
							Text textObjeto = document
									.createTextNode(valorObjeto);

							clase.appendChild(textClase);
							relacion.appendChild(textRelacion);
							objeto.appendChild(textObjeto);

							arco.appendChild(clase);
							arco.appendChild(relacion);
							arco.appendChild(objeto);

							opcional.appendChild(arco);

						}

						patron.appendChild(opcional);

					}
				}

			}

			Source source = new DOMSource(document);
			Result result = new StreamResult(new java.io.File("resultados consultas/consultasFuente"
					+ idFuente + ".xml")); // nombre del archivo
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(source, result);

		} catch (Exception e) {
			System.out.println("Error al crear el archivo : " + e.getMessage());
		}

	}	
}
