package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;

//TODO make it work

/**
 * Classe qui met à jour un serveur Sesame avec de nouveaux triplets
 * 
 * @author Thibaud Colas
 * @version 26032012
 * @see To
 */
public class ToSesame extends To {

	private Jeu destination;
	
	public ToSesame(Jeu j, String p) {
		super(j, p);
		destination = j;
	}

	public ToSesame(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) {
		super(j, m, p);
		destination = j;
	}
	
	public ToSesame(Jeu j, Jeu js, HashMap<String, LinkedList<Statement>> m, String p, boolean a) {
		super(j, m, p, a);
		destination = js;
	}

	@Override
	public String getOutput(boolean executer) {
		if (output.equals("")) {
			majStatements();
		}
		return output;
	}
	
	//FIXME
	//TODO gérer les cas export intégral
	public void majStatements() {
	}
}
