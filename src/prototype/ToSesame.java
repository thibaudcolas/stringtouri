package prototype;

import java.util.LinkedList;

import org.openrdf.model.Statement;

/**
 * Classe qui met à jour un serveur Sesame avec de nouveaux triplets
 * 
 * @author Thibaud Colas
 * @version 26032012
 * @see To
 */
public class ToSesame extends To {

	public ToSesame(Jeu j) {
		super(j);
	}

	public ToSesame(Jeu j, LinkedList<Statement> m) {
		super(j, m);
	}

	@Override
	public String getOutput() {
		return null;
	}

}
