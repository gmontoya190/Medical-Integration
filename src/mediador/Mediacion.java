package mediador;
/**
 * Clase que permite el acceso al modelo global de GDM de Neo4j. 
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */


import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.Traversal;

import consultas.*;


public class Mediacion {
   
   /** 
   * Variable que contiene la ruta donde est&aacute; almacenado el modelo.  
   */
	private static final String DB_PATH = "modelo global";
	/** 
	* Variables que contiene las clases de nodos.  
	*/
	private static final String NODO_COM_E = "compuesto etiquetado";
	private static final String NODO_OBJETO = "objeto";
	/** 
	* Variable que permite el acceso al modelo GDM.  
	*/
	private static GraphDatabaseService graphDb;
	/** 
	* Variable que contiene los ind&iacute;ces de los nodos para su b&uacute;squeda en el modelo.  
	*/
	private static Index<Node> nodeIndex;
	/** 
	* Variable que contiene los ind&iacute;ces de las relaciones para su b&uacute;squeda en el modelo.  
	*/
	private static Index<Relationship> relaIndex;
	/** 
	* Fuentes que tiene el modelo.  
	*/
	private int numeroFuentes = 3;
	/** 
	* Conjunto de fuentes a las que pertenece un nodo.  
	*/
	private int fuentesNodo[];
	
	/** 
	* Constructor sin par&aacute;metros que inicializa los ind&iacute;ces y la conexi&oacute;n al modelo.  
	*/
	public Mediacion() {

		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		nodeIndex = graphDb.index().forNodes("nodes");
		relaIndex = graphDb.index().forRelationships("relationships");
		registerShutdownHook();

	}	
	/** 
	* Resgistra el hilo de conexi&oacute;n al modelo GDM 
	*/
	private static void shutdown() {
		graphDb.shutdown();
	}

	/** 
	* Permite  mantener la conexi&oacute;n al modelo de GDM mientras haya una
	* instancia de la m&aacute;quina virtual de java.
	*/
	private static void registerShutdownHook() {		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdown();
			}
		});
	}
	/** 
	* Permite  mantener la conexi&oacute;n al modelo de GDM mientras haya una
	* instancia de la m&aacute;quina virtual de java.
	*/
	public void crearNodo(Node nodo) {
		Transaction tx = graphDb.beginTx();
		try {
			nodo = graphDb.createNode();

			tx.success();
		} finally {
			tx.finish();
		}
	}
	/** 
	* M&eacute:todos para pijar propiedades en los nodos. Estos aplican
	* Cuando se crea el modelo por primera vez.
	*/
	
	/** 
	* Fija el tipo la etiqueta de un nodo.
	* @param pNodo nodo que se le setea la propiedad.
	* @param pNombre etiqueta del nodo. 
	*/
	public void setNombre(Node pNodo, String pNombre)

	{
		pNodo.setProperty("nombre", pNombre);
	}
	/** 
	* Fija el tipo de clase de un nodo.
	* @param pNodo nodo que se le setea la propiedad.
	* @param pTipoNodo tipo de clase del nodo. 
	*/
	public void setTipoNodo(Node pNodo, String pTipoNodo)

	{
		pNodo.setProperty("tipoNodo", pTipoNodo);
	}
	/** 
	* Fija el identificador del nodo y agrega este al index para su posterior b&uacute;squeda.
	* @param pNodo nodo que se le setea la propiedad.
	* @param pId identificador del nodo. 
	*/
	public void setIdNodo(Node pNodo, String pId)

	{
		pNodo.setProperty("idNodo", pId);
		nodeIndex.add(pNodo, "idNodo", pId);
	}
	/** 
	* Fija las fuentes a las que pertenece un nodo.Cuando se llama este m&eacute;todo
	* el array delas fuentes contiene las fuentes del nodo. Cuando se le asignan se limpia este array
	* con el fin de manejar un solo array para todos los nodos.
	* @param pNodo nodo que se le setea la propiedad.	* 
	*/
	public void setFuenteNodo(Node pNodo)

	{
		pNodo.setProperty("fuente", fuentesNodo);
		limpiarArrayFuentes();
	}
	/** 
	* Limpia el arrary de fuentes poniendo en cada posici&oacute;n un cero.	
	*/
	public void limpiarArrayFuentes() {
		for (int i = 0; i < fuentesNodo.length; i++) {
			fuentesNodo[i] = 0;

		}
	}
	/** 
	* Inicializa el arrary de fuentes poniendo en cada posici&oacute;n un cero.	
	* @param pFuentesNodo array que contiene las fuentes actuales del nodo.
	*/
	public void inicializarArray(int pFuentesNodo[]) {
		for (int i = 0; i < pFuentesNodo.length; i++) {
			pFuentesNodo[i] = 0;

		}
	}
	/** 
	* Inicializa el arrary de fuentes.	
	*/
	public void inicializarVector() {
		fuentesNodo = new int[3];
	}
	/** 
	* Setea las propiedades de una relaci&oacute;n.	
	* @param pNodoInicial Nodo de donde parte la relaci&oacute;n.
	* @param pNodoFinal Nodo donde termina la relaci&oacute;n.
	* @param pRel tipo de relaci&oacute;n entre los nodos.
	* @param pIdRela identificador de la relaci&oacute;n.
	* @param pTipoArco tipo de arco de la relaci&oacute;n.
	* @param pEtiquetaRel etiqueta de la relaci&oacute;n.
	*/
	public void setRelacion(Node pNodoInicial, Node pNodoFinal, RelTypes pRel,
			String pIdRela, String pTipoArco, String pEtiquetaRel) {

		Relationship relacion = pNodoInicial
				.createRelationshipTo(pNodoFinal, pRel);

		relacion.setProperty("idRela",  pIdRela);
		relacion.setProperty("tipoArco", pTipoArco);
		relacion.setProperty("relacion", pEtiquetaRel);

		relaIndex.add(relacion, "idRela", pIdRela);		
	}
	/** 
	* Busca la etiqueta de un nodo.
	* @param pId identificador del nodo. 
	* @return String la etiqueta del nodo.	
	*/
	
	public String buscarNodoEtiqueta(String pId) {

		String etiqueta = "";

		Transaction tx = graphDb.beginTx();
		try {

			Node foundUser = nodeIndex.get("idNodo", pId).getSingle();
			etiqueta = (String) foundUser.getProperty("nombre");

			tx.success();
		} catch (NullPointerException e) {

			javax.swing.JOptionPane.showMessageDialog(null,
					"no se encuentra el nodo con id: " + pId, "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("error " + e.getMessage()
					+ "no se encuentra el nodo con id: " + pId);
			System.exit(0);
		}

		finally {
			tx.finish();
		}

		return etiqueta;
	}
	/** 
	* Obtiene una tripla del modelo.
	* @param pId identificador de la tripla. 
	* @return tripla objeto de este tipo.	
	*/
	public TriplaModeloGlobal getArcoPorID(String pId) {
		String idEntidad = "";
		String relacion = "";
		String idValor = "";
		String tipoArco = "";
		Transaction tx = graphDb.beginTx();
		try {

			Relationship found = relaIndex.get("idRela", pId).getSingle();
			Node nodoEntidad = found.getStartNode();
			Node nodoValor = found.getEndNode();
			idEntidad = (String) nodoEntidad.getProperty("idNodo");
			relacion = (String) found.getProperty("relacion");
			idValor = (String) nodoValor.getProperty("idNodo");
			tipoArco = (String) found.getProperty("tipoArco");

			tx.success();
		}

		catch (NullPointerException e) {
			javax.swing.JOptionPane.showMessageDialog(null,
					"no se encuentra el arco con id: " + pId, "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("error " + e.getMessage()
					+ "no se encuentra este arco " + pId);
			System.exit(0);
		}

		finally {
			tx.finish();
		}

		TriplaModeloGlobal t = new TriplaModeloGlobal(idEntidad, idValor, relacion, pId, tipoArco);

		return t;

	}
	/** 
	* Verifica si un nodo es de clase valor b&aacute;sico.
	* @param pId identificador del nodo. 
	* @return true si el nodo es de clase valor b&aacute;sico.	
	*/
	public boolean esBasico(String id) {
		String tipo = "";
		boolean basico = false;

		Transaction tx = graphDb.beginTx();
		try {

			Node foundUser = nodeIndex.get("idNodo", id).getSingle();
			tipo = (String) foundUser.getProperty("tipoNodo");

			tx.success();
		}

		finally {
			tx.finish();
		}

		if (tipo.equals("basico")) {
			basico = true;
		}

		return basico;
	}
	/** 
	* Verifica si un nodo es de clase objeto.
	* @param pId identificador del nodo. 
	* @return true si el nodo es de clase objeto.	
	*/
	public boolean esObjeto(String pId) {
		String tipo = "";
		boolean objeto = false;

		Transaction tx = graphDb.beginTx();
		try {

			Node foundUser = nodeIndex.get("idNodo", pId).getSingle();
			tipo = (String) foundUser.getProperty("tipoNodo");

			tx.success();
		}

		finally {
			tx.finish();
		}

		if (tipo.equals("basico")) {
			objeto = true;
		}

		return objeto;
	}
	/** 
	* Halla las fuentes donde hay informaci&oacute;n para un arco.
	* @param pIdArco identificador del arco. 
	* @return int[] array que contiene las fuentes del arco, representadas con enteros.	
	*/
	public int[] fuentesArco(String pIdArco) {
		int fuentesNodoIni[] = new int[3];
		inicializarArray(fuentesNodoIni);
		int fuentesNodoFin[] = new int[3];
		inicializarArray(fuentesNodoFin);
		int fuenteArco[] = new int[3];
		inicializarArray(fuenteArco);
		Transaction tx = graphDb.beginTx();
		try {

			Relationship found = relaIndex.get("idRela", pIdArco).getSingle();
			Node nodoEntidad = found.getStartNode();
			Node nodoValor = found.getEndNode();
			fuentesNodoIni = (int[]) nodoEntidad.getProperty("fuente");
			fuentesNodoFin = (int[]) nodoValor.getProperty("fuente");
			int contadorFuentes = 0;
			for (int i = 0; i < numeroFuentes; i++) {
				int fuenteIni = fuentesNodoIni[i];

				for (int j = 0; j < numeroFuentes; j++) {
					int fuenteFinal = fuentesNodoFin[j];
					if (fuenteFinal == fuenteIni
							&& (fuenteFinal != 0 && fuenteIni != 0)) {
						fuenteArco[contadorFuentes] = fuenteIni;
						contadorFuentes++;
					}
				}
			}			

			tx.success();
		}

		catch (NullPointerException e) {
			System.out.println("error " + e.getMessage()
					+ "no se encuentra este arco " + pIdArco);
		} catch (org.neo4j.graphdb.NotFoundException e) {
			System.out.println("error " + e.getMessage()
					+ "no se encuentra el atributo fuente para " + pIdArco);
		}

		finally {
			tx.finish();
		}

		return fuenteArco;
	}
	/** 
	* Encuentra la etiqueta que agrega a el nodo de clase
	* objeto de un arco de une un nodo de clase compuesto no etiquetado 
	* con un nodo de clase valor b&aacute;co.
	* @param pIdTripla identificador del arco. 
	* @return  Etiqueta del nodo que agrega.
	*/
	public String getIdNodoAgreg(String pIdTripla) {

		Transaction tx = graphDb.beginTx();
		String idNodoAgre = "";
		String tipoNodo = "";
		try {
			Relationship rel = relaIndex.get("idRela", pIdTripla).getSingle();
			Node inicial = rel.getStartNode();
			Iterable<Relationship> relaciones = inicial
					.getRelationships(Direction.INCOMING);

			for (Relationship relacion : relaciones) {
				Node nAgregado = relacion.getStartNode();
				tipoNodo = (String) nAgregado.getProperty("tipoNodo");
				if (tipoNodo.equals(NODO_COM_E) || tipoNodo.equals(NODO_OBJETO)) {
					idNodoAgre = (String) nAgregado.getProperty("idNodo");
				}
			}

			tx.success();
		} catch (NullPointerException e) {
			System.out.println("error " + e.getMessage()
					+ "no se encuentra este arco " + pIdTripla);
		} catch (org.neo4j.graphdb.NotFoundException e) {
			System.out.println("error " + e.getMessage()
					+ "no se encuentra el atributo fuente para " + pIdTripla);
		} finally {
			tx.finish();
		}

		return idNodoAgre;
	}
	/** 
	* Obtiene los caminos posibles entre dos nodos.
	* Llamaa a otro procedemiento para construir las triplas 
	* que pertenecen a estos caminos.
	* @param  pIdNodo1 identificador del primer nodo. 
	* @param  pIdNodo2 identificador del segundo nodo.
	* @return  List<Camino> contiene la lista de caminos.
	*/
	public List<Camino> obtenerCaminos(String pIdNodo1, String pIdNodo2)
	{
		PathFinder<Path> finder = GraphAlgoFactory.allSimplePaths(
				Traversal.expanderForAllTypes(), 15);

		List<Camino> caminos = new ArrayList<Camino>();
		List<Node> nodos = new ArrayList<Node>();
		List<TriplaModeloGlobal> triplasCamino = new ArrayList<TriplaModeloGlobal>();

		Node nodoStart = nodeIndex.get("idNodo", pIdNodo1).getSingle();
		Node nodoFinal = nodeIndex.get("idNodo", pIdNodo2).getSingle();
		Node nodoActual = nodeIndex.get("idNodo", pIdNodo1).getSingle();

		Iterable<Path> paths = finder.findAllPaths(nodoStart, nodoFinal);
		int camino = 1;

		for (Path path : paths) {

			Camino cam = new Camino();
			cam.setNombre("P" + camino);
			camino++;

			for (Node nodo : path.nodes()) {

				nodos.add(nodo);

			}
			triplasCamino = construirTriplas(nodos);
			cam.setTriplasCamino(triplasCamino);
			caminos.add(cam);
			nodos.clear();

		}

		return caminos;
	}
	/** 
	* Construye los arcos para un camino.	
	* @param  pNodos Lista de nodos de un camino.
	* @return  List<tripla> contiene la lista de arcos del camino .
	*/
	public List<TriplaModeloGlobal> construirTriplas(List<Node> pNodos)// ARCOS PARA UN
															// CAMINO
	{

		List<TriplaModeloGlobal> triplasCamino = new ArrayList<TriplaModeloGlobal>();
		for (int i = 0; i < pNodos.size(); i++) {
			if (i != pNodos.size() - 1) {
				Node nodo1 = pNodos.get(i);
				Node nodo2 = pNodos.get(i + 1);
				Iterable<Relationship> relacionesLLegada = nodo2
						.getRelationships(Direction.INCOMING);
				Iterable<Relationship> relacionesSalida = nodo2
						.getRelationships(Direction.OUTGOING);				

				for (Relationship relacion : relacionesLLegada) {
					Node nodoStart = relacion.getStartNode();
					Node nodoFinal = relacion.getEndNode();

					if (nodoStart.getProperty("idNodo").equals(
							nodo1.getProperty("idNodo"))) {
						String idRelacion = (String) relacion
								.getProperty("idRela");
						String idNodoE = (String) nodoStart
								.getProperty("idNodo");
						String idNodoValor = (String) nodoFinal
								.getProperty("idNodo");
						String etiquetaRelacion = (String) relacion
								.getProperty("relacion");
						String tipo = (String) relacion.getProperty("tipoArco");

						TriplaModeloGlobal t = new TriplaModeloGlobal(idNodoE, idNodoValor,
								etiquetaRelacion, idRelacion, tipo);
						triplasCamino.add(t);

					}

				}

				for (Relationship relacion : relacionesSalida) {
					Node nodoStart = relacion.getStartNode();
					Node nodoFinal = relacion.getEndNode();

					try {

						if (nodoFinal.getProperty("idNodo").equals(
								nodo1.getProperty("idNodo"))) {
							String idRelacion = (String) relacion
									.getProperty("idRela");
							String idNodoE = (String) nodoStart
									.getProperty("idNodo");
							String idNodoValor = (String) nodoFinal
									.getProperty("idNodo");
							String etiquetaRelacion = (String) relacion
									.getProperty("relacion");
							String tipo = (String) relacion
									.getProperty("tipoArco");

							TriplaModeloGlobal t = new TriplaModeloGlobal(idNodoE, idNodoValor,
									etiquetaRelacion, idRelacion, tipo);
							triplasCamino.add(t);

						}

					} catch (Exception e) {
						System.out.println("El nodo "
								+ nodoFinal.getProperty("nombre")
								+ "  No tiene id ");
					}
				}

			}

		}

		return triplasCamino;
	}
	/** 
	* Fija el tipo de relaciones entre dos nodos.
	* @return  enum contiene el valor de la relaci&0acute;n .
	*/
	private static enum RelTypes implements RelationshipType {
		OF, ATTENDEDBY, PRESENTLINES, REVIEWOFSYSTEMS, PASTMEDICALHISTORY, FAMILYHISTORY, MEDICINEORDER, TESTRESULTS, DEMOGRAPHICS, GENDER, DATEOFBIRTH, ADDRESS, TELEPHONE, ETHNICGROUP, IDNUMBER, LASTNAME, FIRSTNAME, SPECIALTY, SYMPTOM, OBSERVATION, PROCEDURE,
		// PMDISEASE,
		RELATIONSHIP, DISEASE, MEDICINE, DOSALE, TEST, RESULT, PDESCRIPTION,
		// PCODE,
		SPDESCRIPTION, DDESCRIPTION, DCODE, MDESCRIPTION, MCODE, ENCOUNTERTYPE, ENCOUNTERDATE, DIAGNOSIS, SYSTEM, HIJO, PADRE

	}

	public static void main(String[] args) {
		Mediacion m = new Mediacion();
		// m.obetenerCaminos("pLN","1c");
		// m.obtenerCaminos("1p","1dC");
		// m.fuentesArco("didco");
		// System.out.println(m.getIdNodoAgreg("dmge"));
		// m.crearModelo();
		// m.getPropiedadesNodo();
		// new mediacion().getPropiedadesNodo();
		m.buscarNodoEtiqueta("py23");

	}

}
