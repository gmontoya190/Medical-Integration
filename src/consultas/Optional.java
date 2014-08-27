package consultas;

/**
 * Clase que representa las tripla optional para la consulta de
 * operador extract. 
 * @version 1.0, Junio 2013
 * @author Guillermo Montoya
 */
import java.util.ArrayList;
import java.util.List;

public class Optional {

	private List<TriplaModeloLocal> triplasOptional;

	public Optional() {
		triplasOptional = new ArrayList<TriplaModeloLocal>();
	}

	public void addTripla(TriplaModeloLocal t) {
		triplasOptional.add(t);
	}

	public List<TriplaModeloLocal> getTriplasOptional() {
		return triplasOptional;
	}

	public void setTriplasOptional(List<TriplaModeloLocal> triplasOptional) {
		this.triplasOptional = triplasOptional;
	}

}