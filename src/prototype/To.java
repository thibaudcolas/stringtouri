package prototype;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

/**
 * Classe abstraite pour le traitement du résultat de l'interconnexion.
 * 
 * @author Thibaud Colas
 * @version 26032012
 * @see Jeu, ToRDF, ToSesame, ToSPARQL
 */
public abstract class To {
	
	protected Jeu amodif;
	protected LinkedList<Statement> maj;
	protected String output;
	protected boolean all;
	
	protected To(Jeu j) {
		amodif = j;
		maj = new LinkedList<Statement>();
		output = "";
		all = false;
	}
	
	protected To(Jeu j, LinkedList<Statement> m) {
		amodif = j;
		maj = m;
		output = "";
		all = false;
	}
	
	protected To(Jeu j, LinkedList<Statement> m, boolean a) {
		amodif = j;
		maj = m;
		output = "";
		all = a;
	}
	
	public void addStatement(String s, String p, String o) {
		maj.add(new StatementImpl(new URIImpl(s), new URIImpl(p), new URIImpl(o)));
	}
	
	protected LinkedList<Statement> getGoodStatements() {
		LinkedList<Statement> tmp = null;
		try {
			LinkedList<Statement> tous = amodif.getAllStatements();
			LinkedList<Statement> modifs = maj;
			boolean comparaison;
			int i;
			Statement si = null;
			int cpt = 0;
			for (Statement s : tous) {
				comparaison = true;
				i = 0;
				while(comparaison && i < modifs.size()) {
					si = modifs.get(i);
					comparaison = !(si.getSubject().stringValue().equals(s.getSubject().stringValue()) 
							&& si.getPredicate().getLocalName().equals(s.getPredicate().getLocalName()));
					i++;
				}
				if (!comparaison) {
					tous.set(cpt, si);
					modifs.remove(si);
				}
				cpt++;
			}
			tmp = tous;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return tmp;
	}

	public LinkedList<Statement> getMaj() {
		return maj;
	}

	public void setMaj(LinkedList<Statement> maj) {
		this.maj = maj;
	}

	public abstract String getOutput();
	
	public void writeToFile(String chemin) {
		File f = new File(chemin);
		if (f.isFile() && f.canWrite()) {
			try {
				BufferedWriter res = new BufferedWriter(new FileWriter(chemin));
				res.write(getOutput());
				res.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			System.err.println("Fichier inutilisable");
		}
	}
}
