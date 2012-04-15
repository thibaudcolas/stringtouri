package prototype;

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
	
			if (LOG.isInfoEnabled()) {
				LOG.info("Created SPARQLApp " + name);
			}
		}
		catch (RepositoryException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODERE);
		}
	}
}
