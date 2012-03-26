package prototype;

import java.util.LinkedList;

import org.openrdf.model.Statement;

/**
 * Classe qui met à jour un SPARQL endpoint avec des requêtes SPARQL.
 * 
 * @author Thibaud Colas
 * @version 26032012
 * @see To
 */
public class ToSPARQL extends To {

	public ToSPARQL(Jeu j) {
		super(j);
		// TODO Auto-generated constructor stub
	}

	public ToSPARQL(Jeu j, LinkedList<Statement> m) {
		super(j, m);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getOutput() {
		// TODO Auto-generated method stub
		return null;
	}

}
