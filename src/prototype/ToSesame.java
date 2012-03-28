package prototype;

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

	private JeuSesame destination;
	
	public ToSesame(Jeu j) {
		super(j);
		destination = (JeuSesame)j;
	}

	public ToSesame(Jeu j, LinkedList<Statement> m) {
		super(j, m);
		destination = (JeuSesame)j;
	}
	
	public ToSesame(Jeu j, JeuSesame js, LinkedList<Statement> m, boolean a) {
		super(j, m, a);
		destination = js;
	}

	@Override
	public String getOutput() {
		if (output.equals("")) majStatements();
		return output;
	}
	
	public void majStatements() {
		LinkedList<Statement> tmp = all ? getGoodStatements() : maj;
		LinkedList<Statement> aenlever = new LinkedList<Statement>();
		try {
			for (Statement m : maj) {
				aenlever.addAll(destination.getAllStatements(m.getSubject(), m.getPredicate()).asList());
			}
			destination.removeAllStatements(aenlever);
			output += "- " + aenlever.size() + " - " + aenlever + "\n";
			output += "+ " + tmp.size() + " - " + tmp + "\n";
			//destination.addAllStatements(tmp);
			//TODO utiliser un Set, attention aux doublons, mieux gérer les propriétés
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isNewRepository() {
		String depot = ((JeuSesame)amodif).getIdDepot() != null ? ((JeuSesame)amodif).getIdDepot() : "";
		String sesame = ((JeuSesame)amodif).getURLSesame() != null ? ((JeuSesame)amodif).getURLSesame() : "";
		return destination.getIdDepot().equals(depot) && destination.getURLSesame().equals(sesame);
	}

}
