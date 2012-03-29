package prototype;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;

/**
 * Classe abstraite pour le traitement du résultat de l'interconnexion.
 * 
 * @author Thibaud Colas
 * @version 29032012
 * @see Jeu, ToRDF, ToSesame, ToSPARQL
 */
public abstract class To {
	protected Jeu jeumaj;
	protected HashMap<String, LinkedList<Statement>> maj;
	protected String prop;
	
	protected String output;
	
	protected HashMap<String, String> namespaces;
	
	protected To(Jeu j, String p) {
		jeumaj = j;
		prop = p;
		handleNamespaces();
		maj = new HashMap<String, LinkedList<Statement>>();
		output = "";
	}
	
	protected To(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) {
		jeumaj = j;
		prop = p;
		handleNamespaces();
		maj = m;
		output = "";
	}
	
	protected To(Jeu j, HashMap<String, LinkedList<Statement>> m, String p, boolean a) {
		jeumaj = j;
		handleNamespaces();
		prop = p;
		maj = a ? getFilteredStatements(m) : m;
		output = "";
	}
	
	/**
	 * Importe l'ensemble des namespaces pour ajouter les préfixes aux propriétés.
	 */
	private void handleNamespaces() {
		try {
			namespaces = new HashMap<String, String>();
			List<Namespace> nstmp = jeumaj.getNamespaceList();
			for (Namespace n : nstmp) {
				namespaces.put(n.getName(), n.getPrefix());
			}
		} catch (RepositoryException e) {
			System.err.println("Erreur récupération namespaces - " + e);
		}
	}
	
	/**
	 * Filtre tous les triplets du jeu de données pour ajouter les nouveaux / retirer les anciens.
	 * @param nouv : Les nouveaux triplets.
	 * @return Un ensemble de triplets mis à jour.
	 */
	protected final HashMap<String, LinkedList<Statement>> getFilteredStatements(HashMap<String, LinkedList<Statement>> nouv) {
		HashMap<String, LinkedList<Statement>> resultat = getAllStatements();
		LinkedList<Statement> tmpold;
		LinkedList<Statement> tmpnew;
		String tmpprop;
		
		for (String suj : nouv.keySet()) {
			tmpold = resultat.get(suj);
			if (tmpold != null) {
				tmpnew = new LinkedList<Statement>();
				for (Statement s : tmpold) {
					tmpprop = namespaces.get(s.getPredicate().getNamespace()) + ":" + s.getPredicate().getLocalName(); 
					if (!tmpprop.equals(prop)) {
						System.out.println(tmpprop + "=" + prop);
						tmpnew.add(s);
					}
				}
				tmpnew.addAll(nouv.get(suj));
				resultat.put(suj, tmpnew);
			}
		}
		return resultat;
	}
	
	/**
	 * Récupère tous les triplets du jeu et les classe par sujet.
	 * @return Les triplets classés par sujet.
	 */
	private HashMap<String, LinkedList<Statement>> getAllStatements() {
		HashMap<String, LinkedList<Statement>> resultat = new HashMap<String, LinkedList<Statement>>();
		try {
			LinkedList<Statement> tous = jeumaj.getAllStatements();
			LinkedList<Statement> tmpmaj;
			String tmpsuj;
			for (Statement s : tous) {
				tmpsuj = s.getSubject().stringValue();
				tmpmaj = resultat.get(tmpsuj);
				if (tmpmaj == null) {
					tmpmaj = new LinkedList<Statement>();
					resultat.put(tmpsuj, tmpmaj);
				}
				tmpmaj.add(s);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return resultat;
	}

	public final HashMap<String, LinkedList<Statement>> getMaj() {
		return maj;
	}

	public final void setMaj(HashMap<String, LinkedList<Statement>> m) {
		this.maj = m;
	}

	/**
	 * Cette méthode sera redéfinie dans chaque classe fille et permet d'unifier les sorties possibles.
	 * @return La sortie de l'application, RDFXML, requêtes ou autre.
	 */
	public abstract String getOutput();
	
	/**
	 * Permet d'écrire le résultat dans un fichier.
	 * @param chemin : Le chemin du fichier en question.
	 */
	public final void writeToFile(final String chemin) {
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
