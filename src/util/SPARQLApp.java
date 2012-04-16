package util;

import org.apache.log4j.Level;
import org.openrdf.repository.RepositoryException;

/**
 * Helps handling the interlinking using SPARQL.
 * 
 * @author Thibaud Colas
 * @version 05042012
 * @see App
 */
public class SPARQLApp extends App {

	/**
	 * Constructor for two SPARQL endpoints.
	 * @param rurl : Source data set endpoint.
	 * @param gurl : Target data set endpoints.
	 */
	public SPARQLApp(String rurl, String gurl) {
		super();
		
		try {
			reference = new SPARQLDataSet(rurl);
			goal = new SPARQLDataSet(gurl);
			
			name = reference.getName() + " - " + goal.getName();
	
			if (LOG.isDebugEnabled()) {
				LOG.debug("Created SPARQLApp " + name);
			}
		}
		catch (RepositoryException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODERE);
		}
	}
	
	/**
	 * Constructor for two SPARQL endpoints with logging selection.
	 * @param rurl : Source data set endpoint.
	 * @param gurl : Target data set endpoints.
	 * @param logging : Log level to use.
	 */
	public SPARQLApp(String rurl, String gurl, Level logging) {
		super(logging);
		
		try {
			reference = new SPARQLDataSet(rurl, logging);
			goal = new SPARQLDataSet(gurl);
			
			name = reference.getName() + " - " + goal.getName();
	
			if (LOG.isDebugEnabled()) {
				LOG.debug("Created SPARQLApp " + name);
			}
		}
		catch (RepositoryException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODERE);
		}
	}
}
