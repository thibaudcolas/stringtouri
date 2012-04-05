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
 * @version 05042012
 * @see Jeu
 */
public class App {
	
	/**
	 * Name for display purposes.
	 */
	protected String nom;
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
	protected static final Logger LOG = Logger.getLogger(App.class.getName());
	
	protected static final int CODERE = 1;
	protected static final int CODEIO = 2;
	protected static final int CODEQY = 3;
	
	/**
	 * Shortest constructor to use only with parent classes.
	 */
	protected App() {
		
	}
	
	/**
	 * Shortened constructor to use with setLiaisonXXX methods.
	 * @param ref : Source data set.
	 * @param obj : Target data set.
	 */
	public App(Jeu ref, Jeu obj) {
		nom = ref.getNom() + " - " + obj.getNom();
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Creation App " + nom);
		}
		
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
	 */
	public App(Jeu ref, Jeu obj, String d, Liaison l, To t, boolean a) {
		nom = ref.getNom() + " - " + obj.getNom();
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Creation App " + nom);
		}
		
		reference = ref;
		objectif = obj;
		datatomaj = d;
		linkage = l;
		sortie = t;
		initiateInterconnexion(a);	
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
	 */
	public void setRDFOutput() {
		try {
			sortie = new ToRDF(objectif, datatomaj);
		} catch (RepositoryException e) {
			LOG.fatal("Export " + nom + " RDF - " + e);
			shutdown();
			System.exit(CODERE);
		}
	}
	
	/**
	 * Sets output to be new Sesame statements.
	 */
	public void setSesameOutput() {
		try {
			sortie = new ToSesame(objectif, datatomaj);
		} catch (RepositoryException e) {
			LOG.fatal("Export " + nom + " Sesame - " + e);
			shutdown();
			System.exit(CODERE);
		}
	}
	
	/**
	 * Sets output to be SPARQL Update queries.
	 */
	public void setSPARQLOutput() {
		try {
			sortie = new ToSPARQL(objectif, datatomaj);
		} catch (RepositoryException e) {
			LOG.fatal("Export " + nom + " SPARQL - " + e);
			shutdown();
			System.exit(CODERE);
		}
	}
	
	/**
	 * Starts the interlinking process.
	 * @param a : Tells wheter to process all the statements or just the updated ones.
	 */
	public void initiateInterconnexion(boolean a) {
		try {
			sortie.setMaj(linkage.getInterconnexion(), a);
		} catch (RepositoryException e) {
			LOG.fatal("Interlink " + nom + " - " + e);
			shutdown();
			System.exit(CODERE);
		} catch (QueryEvaluationException e) {
			LOG.fatal("Interlink " + nom + " - " + e);
			shutdown();
			System.exit(CODEQY);
		} catch (MalformedQueryException e) {
			LOG.fatal("Interlink " + nom + " - " + e);
			shutdown();
			System.exit(CODEQY);
		}
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
	 */
	public final void doUpdate() {
		try {
			sortie.majStatements();
		} catch (RepositoryException e) {
			LOG.fatal("Interlink " + nom + " update - " + e);
			shutdown();
			System.exit(1);
		} catch (UpdateExecutionException e) {
			LOG.fatal("Interlink " + nom + " update - " + e);
			shutdown();
			System.exit(CODEQY);
		} catch (MalformedQueryException e) {
			LOG.fatal("Interlink " + nom + " update - " + e);
			shutdown();
			System.exit(CODEQY);
		}
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
	 */
	public final void storeOutput(final String chemin) {
		try { 
			File f = new File(chemin);
			if (!f.exists()) {
				f.createNewFile();
			}
			if (f.isFile() && f.canWrite()) {
				BufferedWriter res = new BufferedWriter(new FileWriter(chemin));
				res.write(sortie.getOutput());
				res.close();
				
				if (LOG.isInfoEnabled()) {
					LOG.info("Export " + nom + " output - " + chemin);
				}
			}
			else {
				throw new IOException("File not writable/corrupted - " + chemin);
			}
		}
		catch (IOException e) {
			LOG.fatal("Export " + nom + " - " + e);
			shutdown();
			System.exit(CODEIO);
		}
	}
}
