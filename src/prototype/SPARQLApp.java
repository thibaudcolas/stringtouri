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
	 * @param urlref : Source data set endpoint.
	 * @param urlobj : Target data set endpoints.
	 */
	public SPARQLApp(String urlref, String urlobj) {
		super();
		
		try {
			reference = new SPARQLDataSet(urlref);
			objectif = new SPARQLDataSet(urlobj);
			
			nom = reference.getName() + " - " + objectif.getName();
	
			if (LOG.isInfoEnabled()) {
				LOG.info("Creation AppRDF " + nom);
			}
		}
		catch (RepositoryException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODERE);
		}
	}
}
