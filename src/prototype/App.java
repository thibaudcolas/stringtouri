package prototype;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

/**
 * Handles the interlinking process from A to Z.
 * 
 * @author Thibaud Colas
 * @version 04042012
 * @see Jeu
 */
public class App {
	
	/**
	 * Data set to which we'll link data.
	 */
	protected Jeu reference;
	/**
	 * Data set where the links will be made.
	 */
	protected Jeu objectif;
	/**
	 * Predicate where the values are going to be replaced with external links.
	 */
	protected String datatomaj;
	/**
	 * Linking component managing linking rules to apply.
	 */
	protected Liaison linkage;
	/**
	 * Output handler, telling how the update will be processed.
	 */
	protected To sortie;
	
	/**
	 * Main Logger to record actions on pretty much everything.
	 */
	protected static final Logger log = Logger.getLogger(App.class.getName());
	
	/**
	 * Shortened constructor to use with set**** methods.
	 * @param ref : Source data set.
	 * @param obj : Target data set.
	 */
	public App(Jeu ref, Jeu obj) {
		reference = ref;
		objectif = obj;
	}
	
	/**
	 * Full constructor.
	 * @param ref : Source data set.
	 * @param obj : Target data set.
	 * @param d : Linking predicate.
	 * @param l : Linking handler.
	 * @param t : Output handler.
	 * @param a : Tells whether to output all the data or just the new statements.
	 * @throws RepositoryException XXX logging
	 * @throws QueryEvaluationException XXX logging
	 * @throws MalformedQueryException XXX logging
	 */
	public App(Jeu ref, Jeu obj, String d, Liaison l, To t, boolean a) throws RepositoryException, QueryEvaluationException, MalformedQueryException {
		reference = ref;
		objectif = obj;
		datatomaj = d;
		linkage = l;
		sortie = t;
		sortie.setMaj(l.getInterconnexion(), a);
	}
	
	/**
	 * Sets the linking handler to a simple one.
	 * @param referenceprop : Type of predicate to look for in the source data set.
	 * @param objectifprop : Type of predicate to look for in the target data set.
	 */
	public void setLiaisonSimple(String referenceprop, String objectifprop) {
		linkage = new LiaisonSimple(reference, objectif, referenceprop, objectifprop);
		datatomaj = objectifprop;
	}
	
	/**
	 * Sets the linking handler to be much more complex.
	 * @param referenceprop : Type of predicate to look for in the source data set.
	 * @param objectifprop : Type of predicate to look for in the target data set.
	 * @param referencetype : Type of the values on source side.
	 * @param objectiftype : Type of the values on target side.
	 */
	public void setLiaisonTypee(String referenceprop, String objectifprop, String referencetype, String objectiftype) {
		linkage = new LiaisonTypee(reference, objectif, referenceprop, objectifprop, referencetype, objectiftype);
		datatomaj = objectifprop;
	}
	
	/**
	 * Customizable criteria linking handler setter.
	 * @param referenceprop : Type of predicate to look for in the source data set.
	 * @param objectifprop : Type of predicate to look for in the target data set.
	 * @param referencequery : SPARQL query to be made.
	 * @param objectifquery : SPARQL query to be made.
	 */
	public void setLiaisonLibre(String referenceprop, String objectifprop, String referencequery, String objectifquery) {
		linkage = new LiaisonLibre(reference, objectif, referenceprop, objectifprop, referencequery, objectifquery);
		datatomaj = objectifprop;
	}
	
	/**
	 * Sets output to be RDFXML.
	 * @throws RepositoryException XXX logging
	 */
	public void setRDFOutput() throws RepositoryException {
		sortie = new ToRDF(objectif, datatomaj);
	}
	
	/**
	 * Sets output to be new Sesame statements.
	 * @throws RepositoryException XXX logging
	 */
	public void setSesameOutput() throws RepositoryException {
		sortie = new ToSesame(objectif, datatomaj);
	}
	
	/**
	 * Sets output to be SPARQL Update queries.
	 * @throws RepositoryException XXX logging
	 */
	public void setSPARQLOutput() throws RepositoryException {
		sortie = new ToSPARQL(objectif, datatomaj);
	}
	
	/**
	 * Starts the interlinking process.
	 * @param a : Tells wheter to process all the statements or just the updated ones.
	 * @throws QueryEvaluationException
	 * @throws MalformedQueryException XXX logging
	 * @throws RuntimeException XXX logging
	 * @throws RepositoryException XXX logging
	 */
	public void initiateInterconnexion(boolean a) throws QueryEvaluationException, MalformedQueryException, RuntimeException, RepositoryException {
		sortie.setMaj(linkage.getInterconnexion(), a);
	}
	
	/**
	 * Gets a String output of the interlinking.
	 * @return Output as a string containing statements / RDFXML / queries.
	 */
	public String getOutput() {
		return sortie.getOutput();
	}
	
	/**
	 * Updates statements inside the repository.
	 * @throws RepositoryException XXX logging
	 * @throws MalformedQueryException XXX logging
	 * @throws UpdateExecutionException XXX logging
	 */
	public void doUpdate() throws RepositoryException, MalformedQueryException, UpdateExecutionException {
		sortie.majStatements();
	}

	/**
	 * Shuts down both data sets.
	 */
	public void shutdown() {
		reference.shutdown();
		objectif.shutdown();
	}
	
	/** 
	 * Writes the output to a file.
	 * @param chemin : The path to the file where to write the output.
	 * @throws IOException Error while writing to the filepath.
	 */
	public final void storeOutput(final String chemin) throws IOException {
		File f = new File(chemin);
		if (f.isFile() && f.canWrite()) {
			BufferedWriter res = new BufferedWriter(new FileWriter(chemin));
			res.write(sortie.getOutput());
			res.close();
		}
		else {
			throw new IOException("Fichier inutilisable / introuvable - " + chemin);
		}
	}
}
