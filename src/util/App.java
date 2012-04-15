package util;

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
 * @see DataSet
 */
public class App {
	
	/**
	 * Name for display purposes.
	 */
	protected String name;
	/**
	 * Data set to which we'll link data.
	 */
	protected DataSet reference;
	/**
	 * Data set where the links will be made.
	 */
	protected DataSet goal;
	/**
	 * Predicate where the values are going to be replaced with external links.
	 */
	protected String updatedpredicate;
	/**
	 * Linking component managing linking rules to apply.
	 */
	protected Linkage linkage;
	/**
	 * Output handler, telling how the update will be processed.
	 */
	protected Output output;
	
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
	 * @param r : Source data set.
	 * @param g : Goal data set.
	 */
	public App(DataSet r, DataSet g) {
		name = r.getName() + " - " + g.getName();
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Created app " + name);
		}
		
		reference = r;
		goal = g;
	}
	
	/**
	 * Full constructor.
	 * @param r : Source data set.
	 * @param g : Target data set.
	 * @param p : Linking predicate.
	 * @param l : Linking handler.
	 * @param o : Output handler.
	 * @param a : Tells whether to output all the data or just the new statements.
	 */
	public App(DataSet r, DataSet g, String p, Linkage l, Output o, boolean a) {
		name = r.getName() + " - " + g.getName();
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Created app " + name);
		}
		
		reference = r;
		goal = g;
		updatedpredicate = p;
		linkage = l;
		output = o;
		generateNewLinks(a);	
	}
	
	/**
	 * Sets the linking handler to a simple one.
	 * @param referencepredicate : Type of predicate to look for in the source data set.
	 * @param goalpredicate : Type of predicate to look for in the target data set.
	 */
	public void useSimpleLinkage(String referencepredicate, String goalpredicate) {
		linkage = new StandardLinkage(reference, goal, referencepredicate, goalpredicate);
		updatedpredicate = goalpredicate;
	}
	
	/**
	 * Sets the linking handler to be much more complex.
	 * @param referencepredicate : Type of predicate to look for in the source data set.
	 * @param goalpredicate : Type of predicate to look for in the target data set.
	 * @param referencetype : Type of the values on source side.
	 * @param goaltype : Type of the values on target side.
	 */
	public void useTypedLinkage(String referencepredicate, String goalpredicate, String referencetype, String goaltype) {
		linkage = new TypedLinkage(reference, goal, referencepredicate, goalpredicate, referencetype, goaltype);
		updatedpredicate = goalpredicate;
	}
	
	/**
	 * Customizable criteria linking handler setter.
	 * @param referencepredicate : Type of predicate to look for in the source data set.
	 * @param goalpredicate : Type of predicate to look for in the target data set.
	 * @param referencequery : SPARQL query to be made.
	 * @param goalquery : SPARQL query to be made.
	 */
	public void useFreeLinkage(String referencepredicate, String goalpredicate, String referencequery, String goalquery) {
		linkage = new TailorLinkage(reference, goal, referencepredicate, goalpredicate, referencequery, goalquery);
		updatedpredicate = goalpredicate;
	}
	
	/**
	 * Sets output to be RDFXML.
	 */
	public void useRDFOutput() {
		try {
			output = new RDFOutput(goal, updatedpredicate);
		} catch (RepositoryException e) {
			LOG.fatal("Export " + name + " RDF - " + e);
			shutdown();
			System.exit(CODERE);
		}
	}
	
	/**
	 * Sets output to be new Sesame statements.
	 */
	public void useSesameOutput() {
		try {
			output = new SesameOutput(goal, updatedpredicate);
		} catch (RepositoryException e) {
			LOG.fatal("Export " + name + " Sesame - " + e);
			shutdown();
			System.exit(CODERE);
		}
	}
	
	/**
	 * Sets output to be SPARQL Update queries.
	 */
	public void useSPARQLOutput() {
		try {
			output = new SPARQLOutput(goal, updatedpredicate);
		} catch (RepositoryException e) {
			LOG.fatal("Export " + name + " SPARQL - " + e);
			shutdown();
			System.exit(CODERE);
		}
	}
	
	/**
	 * Starts the interlinking process.
	 * @param a : Tells wheter to process all the statements or just the updated ones.
	 */
	public void generateNewLinks(boolean a) {
		try {
			output.setNewTuples(linkage.generateLinks(), a);
		} catch (RepositoryException e) {
			LOG.fatal("Interlink " + name + " - " + e);
			shutdown();
			System.exit(CODERE);
		} catch (QueryEvaluationException e) {
			LOG.fatal("Interlink " + name + " - " + e);
			shutdown();
			System.exit(CODEQY);
		} catch (MalformedQueryException e) {
			LOG.fatal("Interlink " + name + " - " + e);
			shutdown();
			System.exit(CODEQY);
		}
	}
	
	/**
	 * Gets a String output of the interlinking.
	 * @return Output as a string containing statements / RDFXML / queries.
	 */
	public String getOutput() {
		return output.getOutput();
	}
	
	/**
	 * Updates statements inside the repository.
	 */
	public final void updateData() {
		try {
			output.updateDataSet();
		} catch (RepositoryException e) {
			LOG.fatal("Interlink " + name + " update - " + e);
			shutdown();
			System.exit(1);
		} catch (UpdateExecutionException e) {
			LOG.fatal("Interlink " + name + " update - " + e);
			shutdown();
			System.exit(CODEQY);
		} catch (MalformedQueryException e) {
			LOG.fatal("Interlink " + name + " update - " + e);
			shutdown();
			System.exit(CODEQY);
		}
	}

	/**
	 * Shuts down both data sets.
	 */
	public void shutdown() {
		reference.shutdown();
		goal.shutdown();
	}
	
	/** 
	 * Writes the output to a file.
	 * @param path : The path to the file where to write the output.
	 */
	public final void storeOutput(final String path) {
		try { 
			File f = new File(path);
			if (!f.exists()) {
				f.createNewFile();
			}
			if (f.isFile() && f.canWrite()) {
				BufferedWriter res = new BufferedWriter(new FileWriter(path));
				res.write(output.getOutput());
				res.close();
				
				if (LOG.isInfoEnabled()) {
					LOG.info("Export " + name + " output - " + path);
				}
			}
			else {
				throw new IOException("File not writable/corrupted - " + path);
			}
		}
		catch (IOException e) {
			LOG.fatal("Export " + name + " - " + e);
			shutdown();
			System.exit(CODEIO);
		}
	}
}
