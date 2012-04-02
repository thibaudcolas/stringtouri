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
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

/**
 * Abstract class to process the result of the interlinking.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Liaison
 */
public abstract class To {
	
	/**
	 * The data set to use.
	 */
	protected Jeu jeumaj;
	/**
	 * The new statements produced by the interlinking process.
	 */
	protected HashMap<String, LinkedList<Statement>> maj;
	/**
	 * The predicate where linking has been made.
	 */
	protected String prop;
	/**
	 * A generic output string.
	 */
	protected String output;
	/**
	 * Namespaces to be used during the process.
	 */
	protected HashMap<String, String> namespaces;
	
	/**
	 * Super-class lazy constructor.
	 * @param j : A data set.
	 * @param p : The predicate for which we want to update values.
	 */
	protected To(Jeu j, String p) {
		jeumaj = j;
		prop = p;
		handleNamespaces();
		maj = new HashMap<String, LinkedList<Statement>>();
		output = "";
	}
	
	/**
	 * Super-class default constructor.
	 * @param j : A data set.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 */
	protected To(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) {
		jeumaj = j;
		prop = p;
		handleNamespaces();
		maj = m;
		output = "";
	}
	
	/**
	 * Super-class alternative constructor.
	 * @param j : A data set.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @param a : Tells wether to process all of the statements within the data set or just the new ones.
	 */
	protected To(Jeu j, HashMap<String, LinkedList<Statement>> m, String p, boolean a) {
		jeumaj = j;
		handleNamespaces();
		prop = p;
		maj = a ? getFilteredStatements(m) : m;
		output = "";
	}
	
	/**
	 * Imports the namespaces to be used later.
	 */
	private void handleNamespaces() {
		try {
			namespaces = new HashMap<String, String>();
			List<Namespace> nstmp = jeumaj.getNamespaceList();
			for (Namespace n : nstmp) {
				namespaces.put(n.getName(), n.getPrefix());
			}
		} catch (RepositoryException e) {
			//XXX logging && remontée
			System.err.println("Erreur récupération namespaces - " + e);
		}
	}
	
	/**
	 * Used to shorten a predicate into its local version.
	 * @param p : The predicate to convert.
	 * @return The predicate using a local name.
	 */
	protected String filterPredicate(URI p) {
		String ns = p.getNamespace();
		return (ns.startsWith("http://") ? namespaces.get(ns) + ":" : ns) + p.getLocalName();
	}
	
	/**
	 * Filters statements inside the data set to update the new ones.
	 * @param nouv : The new statements.
	 * @return A set of up to date statements.
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
				// Pour tous les anciens triplets.
				for (Statement s : tmpold) {
					tmpprop = namespaces.get(s.getPredicate().getNamespace()) + ":" + s.getPredicate().getLocalName(); 
					if (!tmpprop.equals(prop)) {
						// On ne prend que ceux qui n'ont pas la propriété qui nous intéresse.
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
	 * Retrieves every statement inside the data set.
	 * @return Hashmap of all the statements grouped by subject.
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
	 * Abstract generic method to retrieve the output of the process.
	 * @param executer : Tells whether or not to execute the output on the data set.
	 * @return The output of the interlinking.
	 */
	public abstract String getOutput(boolean executer);
	
	/**
	 * Writes the output to a file.
	 * @param chemin : The path to the file where to write the output.
	 */
	public final void writeToFile(final String chemin) {
		File f = new File(chemin);
		if (f.isFile() && f.canWrite()) {
			try {
				BufferedWriter res = new BufferedWriter(new FileWriter(chemin));
				res.write(getOutput(false));
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
