package consultas;

/**
 * Clase que realiza el procesamiento de consulta para
 * los dos operadores implementados.
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */
import integracion.Integracion;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mediador.Mediacion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import conexionBD.ConexionBD;

public class Consulta {
	/**
	 * Variables para generar las consultas .
	 */
	private final String OPERADOR_FILTER = "VALUEFILTER";
	private final String OPERADOR_EXTRACT = "EXTRACT";
	private final String RESERVADA_OPTIONAL = "OPTIONAL";
	private final String CAMINO_IESIMO = "P";
	/**
	 * Variable que almacena el numero de fuentes.
	 */
	private int Numero_Fuentes = 0;
	/**
	 * Variable que almacena el numero de caminos en una consulta.
	 */
	private int caminosConsulta = 0;
	/**
	 * Variable que almacena el operador de la consulta.
	 */
	private static String operadorConsulta = "";

	/**
	 * Variable que almacena la lista de datos de retorno en una consulta.
	 */
	private static List<String> datosRetorno;
	/**
	 * Variable que almacena la lista de filtros de la consulta.
	 */
	private static List<VariablesFiltro> variablesFiltroLista;
	/**
	 * Variable que almacena la lista de triplas de un camino espec&iacute;fico.
	 */
	private static List<TriplaModeloGlobal> triplasxCamino;
	/**
	 * Variable que almacena la lista de triplas que pertenecen al patron de una
	 * consulta .
	 */
	private static List<TriplaModeloGlobal> patronTriplas;
	/**
	 * Variable que almacena la lista de caminos para la consulta del operador
	 * filter.
	 */
	private static List<Camino> caminosxConsultaFilter;
	/**
	 * Variable que almacena la lista de caminos para la consulta del operador
	 * extract.
	 */
	private static List<Camino> caminosxConsultaExtract;
	/**
	 * Variable que almacena la lista de subconsultas por camino.
	 * 
	 */
	private static List<Subconsulta> subconsultasXCamino;
	/**
	 * Variable que almacena la lista de subconsultas finales.
	 * 
	 */
	private static List<Subconsulta> subconsultasFinales;
	/**
	 * Variable que almacena la lista de fuentes del modelo.
	 * 
	 */
	private static List<Fuente> fuentes;
	/**
	 * Variable que almacena la lista de datos de retorno para una consulta cuando 
	 * involucra arcos de tipo1.
	 */
	private static Hashtable<String, String> valoresRetornoSubTipo1 = new Hashtable<String, String>();
	/**
	 * Variable que almacena la lista de datos de retorno para una consulta cuando 
	 * involucra arcos de tipo2.
	 */
	private static Hashtable<String, String> valoresRetornoSubTipo2 = new Hashtable<String, String>();
	/**
	 * Variable que almacena el subgrafo esquema para una consulta del operador de extract.
	 */
	private static Hashtable<String, TriplaModeloGlobal> caminoFinalOpExtract = new Hashtable<String, TriplaModeloGlobal>();
	/**
	 * Variable que permite el acceso al modelo global de Neo4j.
	 */
	private Mediacion m;
	/**
	 * Variable que permite la lectura del la consulta en xml.
	 */
	private Document docConsulta;
	/**
	 * Variable que permite el acceso a la base de datos mapeos.
	 */
	private ConexionBD bd = ConexionBD.obtenerInstancia();
	/**
	 * Variable que permite el acceso a los mapeos de integraci&oacute;n.
	 */
	private Integracion integrar;
	/**
	 * Variable que permite la generaci&oacute;n de la consulta en el formato de salida.
	 */
	private GeneradorXML generador;
	/**
	 * Constructor por defecto que inicializa todas la variables ultilizadas.
	 */
	public Consulta() {
		triplasxCamino = new ArrayList<TriplaModeloGlobal>();
		subconsultasXCamino = new ArrayList<Subconsulta>();
		subconsultasFinales = new ArrayList<Subconsulta>();
		integrar = new Integracion();
		generador = new GeneradorXML();
		m = new Mediacion();
	}
	/**
	 * Obtiene las fuentes del modelo.
	 */
	public void obtenerFuentesModelo() {
		fuentes = bd.obtenerListaFuentes();
		Numero_Fuentes = bd.obtenerNumeroFuentes();
		}
	/**
	 * Asigna las tripla mapeadas a las fuentes donde hay informacio&acute;n.
	 * @param Lista de triplas.
	 */
	public void asignarTriplasFuentes(List<TriplaModeloLocal> triplasMapeadas) {
		for (int i = 0; i < triplasMapeadas.size(); i++) {
			TriplaModeloLocal tripla = (TriplaModeloLocal) triplasMapeadas
					.get(i);
			for (int j = 0; j < Numero_Fuentes; j++) {

				Fuente f = (Fuente) fuentes.get(j);
				if (f.getId() == tripla.getIdfuente()) { 
				}
			}
		}

	}
	/**
	 * Lee la consulta en el archivo xml.
	 * 
	 */
	public void leerConsulta() {
		String ruta = "";

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"XML (.xml)", "xml");
		JFileChooser jf = new JFileChooser();
		jf.setFileFilter(filter);

		int seleccion = jf.showOpenDialog(new JTextArea());
		if (seleccion == JFileChooser.APPROVE_OPTION) {
			try {
				ruta = jf.getSelectedFile().getAbsolutePath();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				docConsulta = dBuilder.parse(new File(ruta));
				docConsulta.getDocumentElement().normalize();

				System.out.println("El elemento raiz es: "
						+ docConsulta.getDocumentElement().getNodeName());

				NodeList consulta = docConsulta
						.getElementsByTagName("consulta");

				Node tipoConsulta = consulta.item(0);

				Element elemento = (Element) tipoConsulta;

				operadorConsulta = getTagValue("operador", elemento);

			} catch (Exception e) {
				javax.swing.JOptionPane.showMessageDialog(null,
						"Seleccione un archivo de extension XML ", "Error",
						JOptionPane.ERROR_MESSAGE);
				System.out.println("Seleccione un archivo de extension XML "
						+ e.getMessage());

			}
		}
		System.out.println("operador " + operadorConsulta);
		if (operadorConsulta.isEmpty()) {
			javax.swing.JOptionPane.showMessageDialog(null,
					"Falta valor del elemento operador", "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("Falta valor de un elemento");
			System.exit(0);

		}

		else {
			if (operadorConsulta.equals(OPERADOR_EXTRACT)
					|| operadorConsulta.equals(OPERADOR_FILTER)) {
				leerNodosRetorno();
			}		

			if (!operadorConsulta.equals(OPERADOR_FILTER)
					&& !operadorConsulta.contentEquals(OPERADOR_EXTRACT)) {
				javax.swing.JOptionPane
						.showMessageDialog(
								null,
								"El valor del elemento operador no es el esperado, EXTRACT O VALUEFILTER",
								"Error", JOptionPane.ERROR_MESSAGE);
				System.out
						.println("El valor del elemento operador no es el esperado, EXTRACT O VALUEFILTER");
			}

		}

	}
	/**
	 * Obtiene el valor de un nodo del xml a traves de su etiqueta.
	 * @param pStag etiqueta del nodo xml.
	 * @param pElement nodo que se busca obtener su valor.
	 * 	 
	 */
	private static String getTagValue(String pSTag, Element pElement) {
		NodeList nlList =pElement.getElementsByTagName(pSTag).item(0)
				.getChildNodes();
		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();

	}
	/**
	 * Obtiene los nodos retorno del arhivo xml.   	 
	 */
	public void leerNodosRetorno() {
		try {
			datosRetorno = new ArrayList<String>();

			NodeList nodosRespuesta = docConsulta
					.getElementsByTagName("nodosRespuesta");

			Node nodoRespuesta = nodosRespuesta.item(0);

			Element elemento = (Element) nodoRespuesta;

			NodeList nlList = elemento.getElementsByTagName("nodo");

			for (int i = 0; i < nlList.getLength(); i++) {

				Node nodoRetorno = nlList.item(i);

				if (nodoRetorno.getNodeType() == Node.ELEMENT_NODE) {

					Element elementoNodo = (Element) nodoRetorno;

					String id = getTagValue("id", elementoNodo);
					if (id.isEmpty()) {
						javax.swing.JOptionPane
								.showMessageDialog(
										null,
										"Falta valor de un elemento en la estrutura de nodos de retorno",
										"Error", JOptionPane.ERROR_MESSAGE);
						System.out
								.println("Falta valor de un elemento en la estrutura de nodos de retorno");

						System.exit(0);
						System.out.println("paso");

					}					
					datosRetorno.add(id);

				}
			}
			System.out.println("datos retorno  ");
			System.out.println(" ");
			for (int i = 0; i < datosRetorno.size(); i++) {

				System.out.println(datosRetorno.get(i));
				m.buscarNodoEtiqueta(datosRetorno.get(i));

			}

		} catch (NullPointerException e) {

			javax.swing.JOptionPane
					.showMessageDialog(
							null,
							"El archivo de entrada no contiene algun elemento , revisar la estructura de los datos de retorno",
							"Error", JOptionPane.ERROR_MESSAGE);
			System.out
					.println("El archivo de entrada no contiene algun elemento , revisar la estructura de los datos de retorno"
							+ e.getMessage());
		}

		if (operadorConsulta.equals(OPERADOR_FILTER)) {

			try {
				leerPatronTriplas();
			} catch (NullPointerException e) {
				javax.swing.JOptionPane.showMessageDialog(null,
						"El archivo de entrada no contiene algun elemento, revisar la estrut"
								+ "ura del patron de triplas ", "Error",
						JOptionPane.ERROR_MESSAGE);
				System.out
						.println("El archivo de entrada no contiene algun elemento, revisar la estrutura del patron de triplas"
								+ e.getMessage());
			}
		}

		if (operadorConsulta.equals(OPERADOR_EXTRACT)) {

			hallarCaminos();
		}
	}
	/**
	 * Obtiene los arcos para la consulta del operador filter del archivo
	 * xml.   	 
	 */
	public void leerPatronTriplas() {

		patronTriplas = new ArrayList<TriplaModeloGlobal>();
		caminosxConsultaFilter = new ArrayList<Camino>();

		NodeList nodosRespuesta = docConsulta
				.getElementsByTagName("nodosRespuesta");

		Node nodoRespuesta = nodosRespuesta.item(0);

		Element elemento = (Element) nodoRespuesta;
		NodeList caminos = elemento.getElementsByTagName("pk");
		System.out.println("numero de caminos " + caminos.getLength());
		caminosConsulta = caminos.getLength();

		for (int i = 0; i < caminos.getLength(); i++) {

			Node camino = caminos.item(i);
			Element elementoCamino = (Element) camino;
			NodeList triplasCamino = elementoCamino
					.getElementsByTagName("arco");
			Camino cam = new Camino();
			cam.setNombre(CAMINO_IESIMO + (i + 1));
			for (int j = 0; j < triplasCamino.getLength(); j++) {
				Node tripla = triplasCamino.item(j);

				if (tripla.getNodeType() == Node.ELEMENT_NODE) {
					Element elementoTripla = (Element) tripla;
					String idTripla = getTagValue("id", elementoTripla);
					if (idTripla.isEmpty()) {
						javax.swing.JOptionPane
								.showMessageDialog(
										null,
										"Falta valor de un elemento en la estrutura de las triplas",
										"Error", JOptionPane.ERROR_MESSAGE);
						System.out
								.println("Falta valor de un elemento en la estrutura de las triplas");
						System.exit(0);
					}

					TriplaModeloGlobal t = (TriplaModeloGlobal) m
							.getArcoPorID(idTripla);
					t.setCamino(CAMINO_IESIMO + (i + 1));
					cam.addTripla(t);
					patronTriplas.add(t);

				}

			}
			
			caminosxConsultaFilter.add(cam);

		}		
		try {
			leerFiltro();
		} catch (NullPointerException e) {
			javax.swing.JOptionPane
					.showMessageDialog(
							null,
							"El archivo de entrada no contiene algun elemento, revisar la estrutura del filtro ",
							"Error", JOptionPane.ERROR_MESSAGE);
			System.out
					.println("El archivo de entrada no contiene algun elemento, revisar la estrutura del filtro"
							+ e.getMessage());
		}

	}
	/**
	 * Obtiene el filtro de la consulta del operador filter del archivo xml.
	 *   	 
	 */
	public void leerFiltro() {

		variablesFiltroLista = new ArrayList<VariablesFiltro>();

		NodeList filtros = docConsulta.getElementsByTagName("filtro");

		Node filtro = filtros.item(0);

		Element elemento = (Element) filtro;

		NodeList condiciones = elemento.getElementsByTagName("condicion");		

		for (int i = 0; i < condiciones.getLength(); i++) {

			Node nodoCondicion = condiciones.item(i);

			if (nodoCondicion.getNodeType() == Node.ELEMENT_NODE) {

				Element elementoNodo = (Element) nodoCondicion;
				
				String e = getTagValue("nodo", elementoNodo);

				String v = getTagValue("valor", elementoNodo);

				String comparacion = getTagValue("operador", elementoNodo);
				if (v.isEmpty() || e.isEmpty() || comparacion.isEmpty()) {
					javax.swing.JOptionPane
							.showMessageDialog(
									null,
									"Falta valor de un elemento en la estrutura del filtro",
									"Error", JOptionPane.ERROR_MESSAGE);
					System.out
							.println("Falta valor de un elemento en la estrutura del filtro");
					System.exit(0);
				}
				VariablesFiltro f = new VariablesFiltro(e, v, comparacion);
				if (condiciones.getLength() > 1) {
					if (i != condiciones.getLength() - 1) {
						String variableLogica = getTagValue("operadorUnion",
								elemento);	

					}
				}

				variablesFiltroLista.add(f);

			}

		}//Termina la lectura del archivo para la consulta del operador filter y se llaman
		//los metodos para generar las consultas para este operador.
		asignarMapeosAtripla();
		obtenerFuentesModelo();
		asignarTriplasFiltro();
		obtenerTriplasCamino();
		imprimirSubconsultasCamino();
		identificarSubcosultasXFuenteOPFilter();

	}
	/**
	 * Asigna los mapeos a cada tripla de la consulta.
	 *   	 
	 */
	public void asignarMapeosAtripla() {
		List<TriplaModeloLocal> triplasMapeadas = new ArrayList<TriplaModeloLocal>();

		for (int i = 0; i < patronTriplas.size(); i++) {
			TriplaModeloGlobal t = (TriplaModeloGlobal) patronTriplas.get(i);
			triplasMapeadas = bd.obtenerMapeosTripla(t.getIdTripla());

			t.setTriplasDeMapeo(triplasMapeadas);
		}

	}
	/**
	 * Obtiene las triplas por cada camino y llama a crear las consultas.
	 *   	 
	 */
	public void obtenerTriplasCamino() {		
		for (int i = 0; i < caminosConsulta; i++) {
			Camino c = (Camino) caminosxConsultaFilter.get(i);
			triplasxCamino = c.getTriplasCamino();			
			System.out.println("CAMINO " + c.getNombre());
			setTriplasFuentesxCam();// DEL CAMINO ACTUAL CUALES TRIPLAS SON DE
									// LA FUENTE
			
			crearConsultasxCamino(); // CREA LAS CONSULTAS PARA EL CAMINO
			imprimirSubconsultasCamino();			
			subconsultasXCamino.clear();			
		}

	}
	/**
	 * Asigna las triplas a las fuente o fuentes que pertenece.
	 *   	 
	 */
	public void setTriplasFuentesxCam() {
		for (int i = 0; i < triplasxCamino.size(); i++) {
			TriplaModeloGlobal t = (TriplaModeloGlobal) triplasxCamino.get(i);
			int fuentesTripla[] = new int[3];
			fuentesTripla = m.fuentesArco(t.getIdTripla());
			for (int j = 0; j < Numero_Fuentes; j++) {

				Fuente f = (Fuente) fuentes.get(j);

				for (int k = 0; k < Numero_Fuentes; k++) {

					if (f.getId() == fuentesTripla[k]) { 
						f.setTriplasConsulta(t);
						t.addFuente(f);
					}
				}
			}
		}

	}	
	/**
	 * Crea las consultas por cada camino en el operador filter.
	 *   	 
	 */
	public void crearConsultasxCamino() {
		List<TriplaModeloGlobal> triplasFuentes = new ArrayList<TriplaModeloGlobal>();
		List<TriplaModeloGlobal> grupoTriplas = new ArrayList<TriplaModeloGlobal>();

		for (int i = 0; i < Numero_Fuentes; i++) {
			Fuente f = (Fuente) fuentes.get(i);

			triplasFuentes = f.getListaTriplasConsulta();

			
			for (int j = 0; j < triplasFuentes.size(); j++) {
				TriplaModeloGlobal t = (TriplaModeloGlobal) triplasFuentes.get(j);		

				if (t.getTipoTripla().equals("1")
						&& t.integradoraOTipo3() == false) {
					grupoTriplas = buscarTipo1YTipo3(t.getE(), f, j);

					if (grupoTriplas.size() == 0) {
						construirConsultaIndividualTipo1(t, f);
					}

					else {
						construirConsultaGruplaTipo1Y3(t, f, grupoTriplas);
					}
				}
				if (t.getTipoTripla().equals("3")
						&& t.integradoraOTipo3() == false) {
					grupoTriplas = buscarTipo1YTipo3(t.getE(), f, j);

					if (grupoTriplas.size() == 0) {
						construirConsultaIndividualTipo1(t, f);
					}

					else {
						construirConsultaGruplaTipo1Y3(t, f, grupoTriplas);
					}
				}

				if (t.getTipoTripla().equals("2")) {
					construirConsultaIndividualTipo2(t, f);
				}
			}

			f.getListaTriplasConsulta().clear();

		}
	}
	/**
	 * Crea la consulta para una arco de tipo 2 en el operador de filter.
	 * @param pTripla para la que se va a generar la consulta.
	 * @param pFuente a la que pertenece la tripla y para la cual se va a generar la consulta.
	 *   	 
	 */
	public void construirConsultaIndividualTipo2(TriplaModeloGlobal pTripla, Fuente pFuente) {
		String select = "";
		String where = "{";		
		List<TriplaModeloLocal> mapeosIntEntidad1 = new ArrayList<TriplaModeloLocal>();
		List<TriplaModeloLocal> mapeosIntEntidad2 = new ArrayList<TriplaModeloLocal>();
		List<TriplaModeloLocal> mapeosArco = new ArrayList<TriplaModeloLocal>();
		mapeosIntEntidad1 = integrar.obtenerTriplasInt(pTripla.getE(), pFuente.getId());
		mapeosIntEntidad2 = integrar.obtenerTriplasInt( pTripla.getV(), pFuente.getId());
		mapeosArco = obtenerMapeosFuenteActual(pFuente,  pTripla.getTriplasdeMapeo());
		Subconsulta s = new Subconsulta();

		where += " ";
		for (int i = 0; i < mapeosIntEntidad1.size(); i++) // Primera Entidad
		{
			String mapeo = mapeosIntEntidad1.get(i).getValor();			
			String valor = obtenerValorBasicoTripla(mapeo);
			valoresRetornoSubTipo1.put(valor, valor);
			s.addPatronConsulta(mapeosIntEntidad1.get(i));
			if (i == 0) {
				where += mapeo;
				where += " ";
			} else {
				where += ".";
				where += " ";
				where += mapeo;
				where += " ";
			}

		}

		where += ".";
		where += " ";

		for (int i = 0; i < mapeosIntEntidad2.size(); i++) // Segunda Entidad
		{
			String mapeo = mapeosIntEntidad2.get(i).getValor();			
			String valor = obtenerValorBasicoTripla(mapeo);
			valoresRetornoSubTipo1.put(valor, valor);
			s.addPatronConsulta(mapeosIntEntidad2.get(i));
			if (i == 0) {
				where += mapeo;
				where += " ";
			} else {
				where += ".";
				where += " ";
				where += mapeo;
				where += " ";
			}

		}

		where += ".";
		where += " ";
		obtenerValoresRTriplaTipo3(mapeosArco);
		for (int i = 0; i < mapeosArco.size(); i++)// Mapeos incluyendo el arco
													// completo
		{
			TriplaModeloLocal tripla = (TriplaModeloLocal) mapeosArco.get(i);
			s.addPatronConsulta(tripla);

			if (mapeosArco.size() == 1) {

				where += tripla.getValor();
				where += " ";

			}

			else {
				if (i != mapeosArco.size() - 1) {

					where += tripla.getValor();
					where += " ";
					where += ".";
					where += " ";

				} else {

					where += tripla.getValor();
					where += " ";

				}

			}

		}

		if (pTripla.getFiltro())

		{
			where += " ";
			where += ".";
			where += " ";
			s.setFiltro(true);
			for (int i = 0; i < variablesFiltroLista.size(); i++) {
				VariablesFiltro var = (VariablesFiltro) variablesFiltroLista
						.get(i);
				if (pTripla.getV().equals(var.getEtiqueta())) {
					where += "FILTER(";
					String valorEtiqueta = "?"
							+ m.buscarNodoEtiqueta(var.getEtiqueta());
					where += valorEtiqueta;
					where += var.getSimbolo();
					where += "\"" + var.getValor() + "\"";
					where += ")";
					var.setValorEtiqueta(valorEtiqueta);

					s.addVariableFiltro(var);

				}

			}
		}
		where += "}";

		for (Enumeration<String> e = valoresRetornoSubTipo1.keys(); e
				.hasMoreElements();) {
			String dato = valoresRetornoSubTipo1.get(e.nextElement());
			select += dato;
			s.addDatoRetorno(dato);
			select += " ";
		}

		valoresRetornoSubTipo1.clear();

		s.setSelect(select);
		s.setWhere(where);
		s.setFuente(pFuente.getId());
		subconsultasXCamino.add(s);
		subconsultasFinales.add(s);
	}
	/**
	 * Crea la consulta para una arco de tipo 1 en el operador de filter.
	 * @param pTripla para la que se va a generar la consulta.
	 * @param pFuente a la que pertenece la tripla y para la cual se va a generar la consulta.
	 *   	 
	 */
	public void construirConsultaIndividualTipo1(TriplaModeloGlobal pTripla, Fuente pFuente)
	// Cuando  la tripla es de tipo 1 y no hay  mas  entidades  para  unirse									
	
	{
		String select = " ";
		String where = "{";	

		List<TriplaModeloLocal> mapeosInt = new ArrayList<TriplaModeloLocal>();
		List<TriplaModeloLocal> mapeosArco = new ArrayList<TriplaModeloLocal>();
		Subconsulta s = new Subconsulta();
		
		if (pTripla.getTipoTripla().equals("1")) {
			mapeosInt = integrar.obtenerTriplasInt(pTripla.getE(), pFuente.getId());
		} else {
			mapeosInt = integrar.obtenerTriplasInt(
					m.getIdNodoAgreg(pTripla.getIdTripla()), pFuente.getId());
		}
		
		mapeosArco = obtenerMapeosFuenteActual(pFuente, pTripla.getTriplasdeMapeo());		
		where += " ";
		for (int i = 0; i < mapeosInt.size(); i++) {
			TriplaModeloLocal triplaInt = mapeosInt.get(i);
			String mapeo = triplaInt.getValor();			
			String valor = obtenerValorBasicoTripla(mapeo);
			valoresRetornoSubTipo1.put(valor, valor);
			s.addPatronConsulta(triplaInt);

			if (i == 0) {
				where += mapeo;
				where += " ";
			} else {
				where += ".";
				where += " ";
				where += mapeo;
				where += " ";
			}

		}

		where += ".";
		where += " ";
		obtenerValoresRTriplaTipo3(mapeosArco);
		for (int i = 0; i < mapeosArco.size(); i++) {
			TriplaModeloLocal tripla = (TriplaModeloLocal) mapeosArco.get(i);
			s.addPatronConsulta(tripla);

			if (mapeosArco.size() == 1) {

				where += tripla.getValor();
				where += " ";

			}

			else {
				if (i != mapeosArco.size() - 1) {

					where += tripla.getValor();
					where += " ";
					where += ".";
					where += " ";

				} else {

					where += tripla.getValor();
					where += " ";

				}

			}

		}

		if (pTripla.getFiltro())

		{
			where += " ";
			where += ".";
			where += " ";
			s.setFiltro(true);
			for (int i = 0; i < variablesFiltroLista.size(); i++) {
				VariablesFiltro var = (VariablesFiltro) variablesFiltroLista
						.get(i);
				if (pTripla.getV().equals(var.getEtiqueta())) {
					String valorEtiqueta = "?"
							+ m.buscarNodoEtiqueta(var.getEtiqueta());
					where += "FILTER(";
					where += valorEtiqueta;
					where += var.getSimbolo();
					where += "\"" + var.getValor() + "\"";
					where += ")";
					var.setValorEtiqueta(valorEtiqueta);
					s.addVariableFiltro(var);

				}

			}
		}
		where += "}";

		for (Enumeration<String> e = valoresRetornoSubTipo1.keys(); e
				.hasMoreElements();) {
			String dato = valoresRetornoSubTipo1.get(e.nextElement());
			select += dato;
			s.addDatoRetorno(dato);
			select += " ";
		}
		valoresRetornoSubTipo1.clear();

		s.setSelect(select);
		s.setWhere(where);
		s.setFuente(pFuente.getId());
		subconsultasXCamino.add(s);
		subconsultasFinales.add(s);
	}
	/**
	 * Obtiene los mapeos de una tripla para una fuente espec&iacute;ca. 
	 * @param pFuente fuente a la que se quieren obtener los mapeos.
	 * @param  mapeosArco los mapeos de la tripla incluyendo todas las fuentes.
	 *   	 
	 */
	public List<TriplaModeloLocal> obtenerMapeosFuenteActual(Fuente pFuente,
			List<TriplaModeloLocal> mapeosArco) {
		List<TriplaModeloLocal> mapeosFinal = new ArrayList<TriplaModeloLocal>();
		for (int i = 0; i < mapeosArco.size(); i++) {
			TriplaModeloLocal tripla = mapeosArco.get(i);

			if (tripla.getIdfuente() == pFuente.getId()) {
				mapeosFinal.add(tripla);
			}

		}

		return mapeosFinal;
	}
	/**
	 * Crea la consulta para triplas que se refieren a un mismo nodo clase objeto. 
	 * @param pTripla tripla que se va a construir la consulta.
	 * @param pFuente Fuente para la cual se va a construir la consulta.
	 *   	 
	 */
	public void construirConsultaGruplaTipo1Y3(TriplaModeloGlobal pTripla, Fuente pFuente,
			List<TriplaModeloGlobal> grupoTriplas) {
		String select = "";
		String where = "{";
		
		List<TriplaModeloLocal> mapeosInt = new ArrayList<TriplaModeloLocal>();
		List<TriplaModeloLocal> mapeosArco = new ArrayList<TriplaModeloLocal>();

		if (pTripla.getTipoTripla().equals("1")) {
			mapeosInt = integrar.obtenerTriplasInt(pTripla.getE(),  pFuente.getId());
		} else {
			mapeosInt = integrar.obtenerTriplasInt(
					m.getIdNodoAgreg(pTripla.getIdTripla()), pFuente.getId());
		}
		mapeosArco = obtenerMapeosFuenteActual(pFuente, pTripla.getTriplasdeMapeo());
		Subconsulta s = new Subconsulta();

		for (int i = 0; i < mapeosInt.size(); i++)// Mapeos de Integracion de la
													// Entidad que agrupa
		{
			String mapeo = mapeosInt.get(i).getValor();
			// select+=obtenerValorBasicoTripla(mapeo);
			String valor = obtenerValorBasicoTripla(mapeo);
			valoresRetornoSubTipo1.put(valor, valor);
			s.addPatronConsulta(mapeosInt.get(i));
			if (i == 0) {
				where += mapeo;
				where += " ";
			} else {
				where += ".";
				where += " ";
				where += mapeo;
				where += " ";
			}

			select += " ";
		}

		where += ".";
		where += " ";

		obtenerValoresRTriplaTipo3(mapeosArco);
		for (int j = 0; j < mapeosArco.size(); j++) // Mapeos de la Tripla Que
													// tiene un grupo de triplas
		{
			TriplaModeloLocal tripla = (TriplaModeloLocal) mapeosArco.get(j);
			s.addPatronConsulta(tripla);

			if (mapeosArco.size() == 1) {

				where += tripla.getValor();
				where += " ";

			}

			else {
				if (j != mapeosArco.size() - 1) {

					where += tripla.getValor();
					where += " ";
					where += ".";
					where += " ";

				} else {

					where += tripla.getValor();
					where += " ";

				}

			}

		}

		where += ".";
		where += " ";

		for (int i = 0; i < grupoTriplas.size(); i++) // Los mapeos de cada
														// tripla que se
														// agruparon
		{
			mapeosArco = obtenerMapeosFuenteActual(pFuente, grupoTriplas.get(i)
					.getTriplasdeMapeo());
			obtenerValoresRTriplaTipo3(mapeosArco);		

			for (int j = 0; j < mapeosArco.size(); j++) {
				TriplaModeloLocal tripla = (TriplaModeloLocal) mapeosArco
						.get(j);
				s.addPatronConsulta(tripla);

				if (mapeosArco.size() == 1) {

					where += tripla.getValor();
					where += " ";
				}

				else {
					if (j != mapeosArco.size() - 1) {

						where += tripla.getValor();
						where += " ";
						where += ".";
						where += " ";

					} else {

						where += tripla.getValor();
						where += " ";

					}

				}

			}

		}
		for (int j = 0; j < grupoTriplas.size(); j++)

		{
			TriplaModeloGlobal triplaAgrupada = grupoTriplas.get(j);

			if (triplaAgrupada.getFiltro())

			{
				where += " ";
				where += ".";
				where += " ";
				System.out.println("entro a filtro " + "tripla :"
						+ triplaAgrupada.getIdTripla());

				for (int i = 0; i < variablesFiltroLista.size(); i++) {
					VariablesFiltro var = (VariablesFiltro) variablesFiltroLista
							.get(i);
					if (triplaAgrupada.getV().equals(var.getEtiqueta())) {
						String valorEtiqueta = "?"
								+ m.buscarNodoEtiqueta(var.getEtiqueta());
						where += "FILTER(";
						where += valorEtiqueta;
						where += var.getSimbolo();
						where += "\"" + var.getValor() + "\"";
						where += ")";
						var.setValorEtiqueta(valorEtiqueta);
						s.addVariableFiltro(var);

					}

				}
				where += "}";
			}

		}

		if (pTripla.getFiltro())

		{
			where += " ";
			where += ".";
			where += " ";
			System.out
					.println("entro a filtro " + "tripla :" + pTripla.getIdTripla());

			for (int i = 0; i < variablesFiltroLista.size(); i++) {
				VariablesFiltro var = (VariablesFiltro) variablesFiltroLista
						.get(i);
				if (pTripla.getV().equals(var.getEtiqueta())) {
					String valorEtiqueta = "?"
							+ m.buscarNodoEtiqueta(var.getEtiqueta());
					where += "FILTER(";
					where += valorEtiqueta;
					where += var.getSimbolo();
					where += "\"" + var.getValor() + "\"";
					where += ")";
					var.setValorEtiqueta(valorEtiqueta);
					s.addVariableFiltro(var);

				}

			}
			where += "}";
		}

		for (Enumeration<String> e = valoresRetornoSubTipo1.keys(); e
				.hasMoreElements();)// LEE LOS VALORES RETORNO Y LOS MUESTRA EN
									// EL SELECT
		{
			String dato = valoresRetornoSubTipo1.get(e.nextElement());
			select += dato;

			s.addDatoRetorno(dato);
			select += " ";
		}
		valoresRetornoSubTipo1.clear();

		s.setSelect(select);
		s.setWhere(where);
		s.setFuente(pFuente.getId());
		subconsultasXCamino.add(s);
		subconsultasFinales.add(s);
	}
	
	public void obtenerValoresRTriplaTipo3(List<TriplaModeloLocal> mapeosArco) {

		for (int i = 0; i < mapeosArco.size(); i++) {
			TriplaModeloLocal triplaMapeo = mapeosArco.get(i);
			String valor = obtenerValorBasicoTripla(triplaMapeo.getValor());			
			valoresRetornoSubTipo1.put(valor, valor);

		}

	}

	public void obtenerValoresRTriplaTipo2(List<String> mapeosInt1,
			List<String> mapeosInt2, List<TriplaModeloLocal> mapeosArco) {
		for (int i = 0; i < mapeosInt1.size(); i++) {
			String triplaInt = mapeosInt1.get(i);
			String valor = obtenerValorBasicoTripla(triplaInt);			
			valoresRetornoSubTipo2.put(valor, valor);

		}
		for (int i = 0; i < mapeosInt2.size(); i++) {
			String triplaInt = mapeosInt2.get(i);
			String valor = obtenerValorBasicoTripla(triplaInt);			
			valoresRetornoSubTipo2.put(valor, valor);

		}

		for (int i = 0; i < mapeosArco.size(); i++) {
			TriplaModeloLocal triplaMapeo = mapeosArco.get(i);
			String valor = obtenerValorBasicoTripla(triplaMapeo.getValor());			
			valoresRetornoSubTipo2.put(valor, valor);

		}

	}

	public String obtenerValorBasicoTripla(String tripla) {

		int i = 0;
		int cont = 0;
		char posicionActual = ' ';
		String valorFinal = "";

		while (i != tripla.length()) {
			posicionActual = tripla.charAt(i);

			if (posicionActual == '?') {
				cont++;
			}

			if (cont == 2) {
				valorFinal += tripla.charAt(i);
			}

			i++;
		}
		
		return valorFinal;
	}

	public void imprimirSubconsultasCamino() {
		for (int i = 0; i < subconsultasXCamino.size(); i++) {
			Subconsulta s = (Subconsulta) subconsultasXCamino.get(i);
			System.out.println("La fuente " + s.getFuente());
			System.out.println("Select  " + s.getSelect());
			System.out.println("Where " + s.getWhere());

		}
	}

	public void identificarSubcosultasXFuenteOPFilter() {
		List<Subconsulta> consultasFuente = new ArrayList<Subconsulta>();
		for (int i = 0; i < Numero_Fuentes; i++) {
			Fuente f = (Fuente) fuentes.get(i);

			for (int j = 0; j < subconsultasFinales.size(); j++) {
				Subconsulta sub = subconsultasFinales.get(j);
				if (f.getId() == sub.getFuente()) {
					consultasFuente.add(sub);
				}
			}

			generador.generarSubconsultasFuente(consultasFuente, f.getId());
			consultasFuente.clear();

		}
	}

	public void identificarSubcosultasXFuenteOPExtract() {
		List<Subconsulta> consultasFuente = new ArrayList<Subconsulta>();
		for (int i = 0; i < Numero_Fuentes; i++) {
			Fuente f = (Fuente) fuentes.get(i);

			for (int j = 0; j < subconsultasXCamino.size(); j++) {
				Subconsulta sub = subconsultasXCamino.get(j);
				if (f.getId() == sub.getFuente()) {
					consultasFuente.add(sub);
				}
			}

			generador.generarSubconsultasFuente(consultasFuente, f.getId());
			consultasFuente.clear();

		}
	}

	public List<TriplaModeloGlobal> buscarTipo1YTipo3(String idEntidad,
			Fuente f, int posicion) {
		List<TriplaModeloGlobal> grupoTriplas = new ArrayList<TriplaModeloGlobal>();

		for (int i = 0; i < f.getListaTriplasConsulta().size(); i++) {
			TriplaModeloGlobal t = (TriplaModeloGlobal) f
					.getListaTriplasConsulta().get(i);			
			if (i != posicion) {
				if (t.getTipoTripla().equals("1") && t.getE().equals(idEntidad)
						&& t.integradoraOTipo3() == false) {

					grupoTriplas.add(t);
					f.getListaTriplasConsulta().remove(i);

				}

				if (t.getTipoTripla().equals("3")
						&& t.integradoraOTipo3() == false) {

					String IdNodoAgre = m.getIdNodoAgreg(t.getIdTripla());					
					if (IdNodoAgre.equals(idEntidad)
							&& t.integradoraOTipo3() == false)

					{

						grupoTriplas.add(t);
						f.getListaTriplasConsulta().remove(i);
					}
				}
			}
		}

		return grupoTriplas;
	}

	public void asignarTriplasFiltro() {
		String idNodoFiltro = "";
		for (int i = 0; i < variablesFiltroLista.size(); i++) {
			VariablesFiltro var = (VariablesFiltro) variablesFiltroLista.get(i);
			idNodoFiltro = var.getEtiqueta();

			for (int j = 0; j < patronTriplas.size(); j++) {
				TriplaModeloGlobal t = (TriplaModeloGlobal) patronTriplas
						.get(j);			

				if (t.integradoraOTipo3() == false
						&& idNodoFiltro.equals(t.getV())) {					
					t.setFiltro();

				}
			}

		}
	}

	public void hallarCaminos() {

		caminosxConsultaExtract = new ArrayList<Camino>();		
		String idNodo1 = "";
		String idNodo2 = " ";
		for (int i = 0; i < datosRetorno.size(); i++) {
			idNodo1 = datosRetorno.get(i);
			System.out.println("id nodo 1 " + idNodo1);

			for (int j = (i + 1); j < datosRetorno.size(); j++) {
				idNodo2 = datosRetorno.get(j);
				System.out.println("id nodo 2 " + idNodo2);
				caminosxConsultaExtract = m.obtenerCaminos(idNodo1, idNodo2);
				extraerTriplasCamino(caminosxConsultaExtract);

			}

		}

		
		obtenerFuentesModelo();
		setTriplasFuentesCaminoExtract();
		asignarMapeosAtriplaExtract();
		crearConsultasExtract();
		imprimirSubconsultasCamino();
		identificarSubcosultasXFuenteOPExtract();

	}

	public void extraerTriplasCamino(List<Camino> caminos) {
		List<TriplaModeloGlobal> triplasCamino = new ArrayList<TriplaModeloGlobal>();

		for (int i = 0; i < caminos.size(); i++) {
			Camino cam = caminos.get(i);
			triplasCamino = cam.getTriplasCamino();

			for (int j = 0; j < triplasCamino.size(); j++) {
				TriplaModeloGlobal tri = triplasCamino.get(j);
				String idTripla = tri.getIdTripla();
				caminoFinalOpExtract.put(idTripla, tri);
			}

		}
	}

	

	public void setTriplasFuentesCaminoExtract() {
		for (Enumeration<String> e = caminoFinalOpExtract.keys(); e
				.hasMoreElements();) {
			TriplaModeloGlobal t = caminoFinalOpExtract.get(e.nextElement());
			int fuentesTripla[] = new int[3];
			fuentesTripla = m.fuentesArco(t.getIdTripla());
			for (int j = 0; j < Numero_Fuentes; j++) {

				Fuente f = (Fuente) fuentes.get(j);

				for (int k = 0; k < Numero_Fuentes; k++) {

					if (f.getId() == fuentesTripla[k]) { 
						f.setTriplasConsulta(t);
						t.addFuente(f);
					}
				}
			}
		}

	}

	public void asignarMapeosAtriplaExtract() {
		List<TriplaModeloLocal> triplasMapeadas = new ArrayList<TriplaModeloLocal>();

		for (Enumeration<String> e = caminoFinalOpExtract.keys(); e
				.hasMoreElements();) {
			TriplaModeloGlobal t = caminoFinalOpExtract.get(e.nextElement());
			triplasMapeadas = bd.obtenerMapeosTripla(t.getIdTripla());
			t.setTriplasDeMapeo(triplasMapeadas);
		}
	}

	public void crearConsultasExtract() {
		List<TriplaModeloGlobal> triplasFuentes = new ArrayList<TriplaModeloGlobal>();
		List<TriplaModeloGlobal> grupoTriplas = new ArrayList<TriplaModeloGlobal>();

		for (int i = 0; i < Numero_Fuentes; i++) {
			Fuente f = (Fuente) fuentes.get(i);

			triplasFuentes = f.getListaTriplasConsulta();			
			for (int j = 0; j < triplasFuentes.size(); j++) {
				TriplaModeloGlobal t = (TriplaModeloGlobal) triplasFuentes
						.get(j);				
				if (t.getTipoTripla().equals("1")
						&& t.integradoraOTipo3() == false) {
					grupoTriplas = buscarTipo1YTipo3(t.getE(), f, j);

					if (grupoTriplas.size() == 0) {
						construirConsultaIndividualTipo1Extract(t, f);
					}

					else {

						construirConsultaGruplaTipo1Y3Extract(t, f,
								grupoTriplas);
					}
				}
				if (t.getTipoTripla().equals("3")
						&& t.integradoraOTipo3() == false) {

					if (grupoTriplas.size() == 0) {
						construirConsultaIndividualTipo1Extract(t, f);
					}

					else {

						construirConsultaGruplaTipo1Y3Extract(t, f,
								grupoTriplas);
					}

				}

				if (t.getTipoTripla().equals("2")) {
					construirConsultaIndividualTipo2Extract(t, f);
				}
			}

			f.getListaTriplasConsulta().clear();

		}
	}

	public void construirConsultaIndividualTipo1Extract(TriplaModeloGlobal t,
			Fuente f)// Cuando 	 la  tripla es  de tipo  1  y no hay  mas entidades  para  unirse
	{
		String select = " ";
		String where = "{";

		List<TriplaModeloLocal> mapeosInt = new ArrayList<TriplaModeloLocal>();
		List<TriplaModeloLocal> mapeosArco = new ArrayList<TriplaModeloLocal>();
		Subconsulta s = new Subconsulta();
		
		if (t.getTipoTripla().equals("1")) {
			mapeosInt = integrar.obtenerTriplasInt(t.getE(), f.getId());
		} else {
			mapeosInt = integrar.obtenerTriplasInt(
					m.getIdNodoAgreg(t.getIdTripla()), f.getId());
		}
		
		mapeosArco = obtenerMapeosFuenteActual(f, t.getTriplasdeMapeo());
		
		where += " ";
		for (int i = 0; i < mapeosInt.size(); i++) {
			String mapeo = mapeosInt.get(i).getValor();			
			String valor = obtenerValorBasicoTripla(mapeo);
			valoresRetornoSubTipo1.put(valor, valor);
			s.addPatronConsulta(mapeosInt.get(i));

			if (i == 0) {
				where += mapeo;
				where += " ";
			} else {
				where += ".";
				where += " ";
				where += mapeo;
				where += " ";
			}

		}

		where += ".";
		where += " ";
		where += RESERVADA_OPTIONAL;
		where += "{";
		obtenerValoresRTriplaTipo3(mapeosArco);
		Optional op = new Optional();
		for (int i = 0; i < mapeosArco.size(); i++) {
			TriplaModeloLocal tripla = (TriplaModeloLocal) mapeosArco.get(i);

			op.addTripla(tripla);

			if (mapeosArco.size() == 1) {

				where += tripla.getValor();
				where += " ";

			}

			else {
				if (i != mapeosArco.size() - 1) {

					where += tripla.getValor();
					where += " ";
					where += ".";
					where += " ";

				} else {

					where += tripla.getValor();
					where += " ";

				}

			}

		}
		s.addOptional(op);
		where += "}}";

		for (Enumeration<String> e = valoresRetornoSubTipo1.keys(); e
				.hasMoreElements();) {
			String valor = valoresRetornoSubTipo1.get(e.nextElement());
			select += valor;
			s.addDatoRetorno(valor);
			select += " ";
		}

		valoresRetornoSubTipo1.clear();

		s.setSelect(select);
		s.setWhere(where);
		s.setFuente(f.getId());
		subconsultasXCamino.add(s);
	}

	public void construirConsultaGruplaTipo1Y3Extract(TriplaModeloGlobal t,
			Fuente f, List<TriplaModeloGlobal> grupoTriplas) {
		String select = "";
		String where = "{";

		List<TriplaModeloLocal> mapeosInt = new ArrayList<TriplaModeloLocal>();
		List<TriplaModeloLocal> mapeosArco = new ArrayList<TriplaModeloLocal>();

		if (t.getTipoTripla().equals("1")) {
			mapeosInt = integrar.obtenerTriplasInt(t.getE(), f.getId());
		} else {
			mapeosInt = integrar.obtenerTriplasInt(
					m.getIdNodoAgreg(t.getIdTripla()), f.getId());
		}
		Subconsulta s = new Subconsulta();

		mapeosArco = obtenerMapeosFuenteActual(f, t.getTriplasdeMapeo());
		
		for (int i = 0; i < mapeosInt.size(); i++)// Mapeos de Integracion de la
													// Entidad que agrupa
		{
			String mapeo = mapeosInt.get(i).getValor();			
			String valor = obtenerValorBasicoTripla(mapeo);
			valoresRetornoSubTipo1.put(valor, valor);

			s.addPatronConsulta(mapeosInt.get(i));

			if (i == 0) {
				where += mapeo;
				where += " ";
			} else {
				where += ".";
				where += " ";
				where += mapeo;
				where += " ";
			}

			select += " ";
		}

		where += ".";
		where += " ";
		where += RESERVADA_OPTIONAL;
		where += "{";

		Optional op = new Optional();
		obtenerValoresRTriplaTipo3(mapeosArco);// VALORES RETORNO DE LOS MAPEOS
		for (int j = 0; j < mapeosArco.size(); j++) // Mapeos de la Tripla Que
													// tiene un grupo de triplas
		{
			TriplaModeloLocal tripla = (TriplaModeloLocal) mapeosArco.get(j);
			op.addTripla(tripla);

			if (mapeosArco.size() == 1) {

				where += tripla.getValor();
				where += " ";

			}

			else {
				if (j != mapeosArco.size() - 1) {

					where += tripla.getValor();
					where += " ";
					where += ".";
					where += " ";

				} else {

					where += tripla.getValor();
					where += " ";

				}

			}

		}
		where += "}";
		where += ".";
		where += " ";
		s.addOptional(op);

		for (int i = 0; i < grupoTriplas.size(); i++) // Los mapeos de cada
														// tripla que se
														// agruparon
		{
			mapeosArco = obtenerMapeosFuenteActual(f, grupoTriplas.get(i)
					.getTriplasdeMapeo());
			obtenerValoresRTriplaTipo3(mapeosArco);

			
			where += RESERVADA_OPTIONAL;
			where += "{";
			Optional opcional = new Optional();
			for (int j = 0; j < mapeosArco.size(); j++) {
				TriplaModeloLocal tripla = (TriplaModeloLocal) mapeosArco
						.get(j);
				opcional.addTripla(tripla);

				if (mapeosArco.size() == 1) {

					if (grupoTriplas.size() == 1) {
						where += tripla.getValor();
						where += " ";
						where += "}";
						where += " ";
					}

					else {
						where += tripla.getValor();
						where += " ";
						where += "}";
						where += " ";
						where += ".";
						where += " ";
					}

				}

				else {
					if (j != mapeosArco.size() - 1) {

						where += tripla.getValor();
						where += " ";
						where += ".";
						where += " ";

					} else {

						where += tripla.getValor();
						where += " ";
						where += "}";
						if (i != grupoTriplas.size() - 1) {
							where += ".";
							where += " ";
						}
					}

				}

			}

			s.addOptional(opcional);

		}

		for (Enumeration<String> e = valoresRetornoSubTipo1.keys(); e
				.hasMoreElements();) {
			String valor = valoresRetornoSubTipo1.get(e.nextElement());
			select += valor;
			select += " ";
			s.addDatoRetorno(valor);
		}

		where += "}";
		valoresRetornoSubTipo1.clear();

		s.setSelect(select);
		s.setWhere(where);
		s.setFuente(f.getId());
		subconsultasXCamino.add(s);
	}

	public void construirConsultaIndividualTipo2Extract(TriplaModeloGlobal t,
			Fuente f) {
		String select = "";
		String where = "{";

		List<TriplaModeloLocal> mapeosIntEntidad1 = new ArrayList<TriplaModeloLocal>();
		List<TriplaModeloLocal> mapeosIntEntidad2 = new ArrayList<TriplaModeloLocal>();
		List<TriplaModeloLocal> mapeosArco = new ArrayList<TriplaModeloLocal>();
		mapeosIntEntidad1 = integrar.obtenerTriplasInt(t.getE(), f.getId());
		mapeosIntEntidad2 = integrar.obtenerTriplasInt(t.getV(), f.getId());
		mapeosArco = obtenerMapeosFuenteActual(f, t.getTriplasdeMapeo());
		Subconsulta s = new Subconsulta();

		where += " ";
		for (int i = 0; i < mapeosIntEntidad1.size(); i++) // Primera Entidad
		{
			String mapeo = mapeosIntEntidad1.get(i).getValor();			
			String valor = obtenerValorBasicoTripla(mapeo);
			valoresRetornoSubTipo1.put(valor, valor);
			s.addPatronConsulta(mapeosIntEntidad1.get(i));
			if (i == 0) {
				where += mapeo;
				where += " ";
			} else {
				where += ".";
				where += " ";
				where += mapeo;
				where += " ";
			}

		}

		where += ".";
		where += " ";

		for (int i = 0; i < mapeosIntEntidad2.size(); i++) // Segunda Entidad
		{
			String mapeo = mapeosIntEntidad2.get(i).getValor();			
			String valor = obtenerValorBasicoTripla(mapeo);
			valoresRetornoSubTipo1.put(valor, valor);
			s.addPatronConsulta(mapeosIntEntidad2.get(i));
			if (i == 0) {
				where += mapeo;
				where += " ";
			} else {
				where += ".";
				where += " ";
				where += mapeo;
				where += " ";
			}

		}

		where += ".";
		where += " ";

		obtenerValoresRTriplaTipo3(mapeosArco);
		for (int i = 0; i < mapeosArco.size(); i++)// Mapeos incluyendo el arco
													// completo
		{
			TriplaModeloLocal tripla = (TriplaModeloLocal) mapeosArco.get(i);
			s.addPatronConsulta(tripla);

			if (mapeosArco.size() == 1) {

				where += tripla.getValor();
				where += " ";

			}

			else {
				if (i != mapeosArco.size() - 1) {

					where += tripla.getValor();
					where += " ";
					where += ".";
					where += " ";

				} else {

					where += tripla.getValor();
					where += " ";

				}

			}

		}
		where += "}";

		for (Enumeration<String> e = valoresRetornoSubTipo1.keys(); e
				.hasMoreElements();) {
			String valor = valoresRetornoSubTipo1.get(e.nextElement());
			select += valor;
			select += " ";
			s.addDatoRetorno(valor);
		}

		valoresRetornoSubTipo1.clear();

		s.setSelect(select);
		s.setWhere(where);
		s.setFuente(f.getId());
		subconsultasXCamino.add(s);
	}

	public static void main(String[] args) {
		Consulta c = new Consulta();
		try {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			c.leerConsulta();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
