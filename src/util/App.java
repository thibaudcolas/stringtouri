package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Level;
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
	 * Logging level to use at runtime.
	 */
	protected Level logginglevel;
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
	 * Shortest constructor with logging definition.
	 * @param logging : Logging level to use.
	 */
	protected App(Level logging) {
		logginglevel = logging;
		LOG.setLevel(logging);
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
	 * Full constructor with logging selection.
	 * @param r : Source data set.
	 * @param g : Target data set.
	 * @param p : Linking predicate.
	 * @param l : Linking handler.
	 * @param o : Output handler.
	 * @param a : Tells whether to output all the data or just the new statements.
	 * @param logging : Logging level to use.
	 */
	public App(DataSet r, DataSet g, String p, Linkage l, Output o, boolean a, Level logging) {
		logginglevel = logging;
		LOG.setLevel(logging);
		name = r.getName() + " - " + g.getName();
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Created app " + name);
		}
		
		reference = r;
		reference.setLoggingLevel(logging);
		goal = g;
		goal.setLoggingLevel(logging);
		updatedpredicate = p;
		linkage = l;
		linkage.setLoggingLevel(logging);
		output = o;
		output.setLoggingLevel(logging);
		generateNewLinks(a);	
	}
	
	/**
	 * Sets the linking handler to a simple one.
	 * @param referencepredicate : Type of predicate to look for in the source data set.
	 * @param goalpredicate : Type of predicate to look for in the target data set.
	 */
	public void useSimpleLinkage(String referencepredicate, String goalpredicate) {
		linkage = new StandardLinkage(reference, goal, referencepredicate, goalpredicate);
		linkage.setLoggingLevel(logginglevel);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Created simple linkage " + name + ".");
		}
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
		linkage.setLoggingLevel(logginglevel);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Created typed linkage " + name + ".");
		}
		updatedpredicate = goalpredicate;
	}
	
	/**
	 * Customizable criteria linking handler setter.
	 * @param referencepredicate : Type of predicate to look for in the source data set.
	 * @param goalpredicate : Type of predicate to look for in the target data set.
	 * @param referencequery : SPARQL query to be made.
	 * @param goalquery : SPARQL query to be made.
	 */
	public void useTailorLinkage(String referencepredicate, String goalpredicate, String referencequery, String goalquery) {
		linkage = new TailorLinkage(reference, goal, referencepredicate, goalpredicate, referencequery, goalquery);
		linkage.setLoggingLevel(logginglevel);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Created tailor linkage " + name + ".");
		}
		updatedpredicate = goalpredicate;
	}
	
	/**
	 * Sets output to be RDFXML.
	 */
	public void useRDFOutput() {
		try {
			output = new RDFOutput(goal, updatedpredicate);
			output.setLoggingLevel(logginglevel);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Created RDF output " + goal.getName() + ".");
				LOG.debug("Export " + goal.getName() + " namespace retrieval.");
			}
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
			output.setLoggingLevel(logginglevel);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Created Sesame output " + goal.getName() + ".");
				LOG.debug("Export " + goal.getName() + " namespace retrieval.");
			}
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
			output.setLoggingLevel(logginglevel);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Created SPARQL output " + goal.getName() + ".");
				LOG.debug("Export " + goal.getName() + " namespace retrieval.");
			}
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
			if (LOG.isInfoEnabled()) {
				LOG.info("New links from " + reference.getName() + " to " + goal.getName() + " retrieved.");
			}
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
			if (LOG.isInfoEnabled()) {
				LOG.info("Data set " + goal.getName() + " updated.");
			}
			
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
		if (reference != null) {
			reference.shutdown();
		}
		if (goal != null) {
			goal.shutdown();
		}
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Shutdown app " + name);
		}
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
	
	/**
	 * Allows to set the logging level.
	 * @param level : The logging level.
	 */
	public void setLoggingLevel(Level level) {
		LOG.setLevel(level);
	}
}
