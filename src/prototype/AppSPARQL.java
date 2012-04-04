package prototype;

import org.openrdf.repository.RepositoryException;

/**
 * Helps handling the interlinking using SPARQL.
 * 
 * @author Thibaud Colas
 * @version 04042012
 * @see App
 */
public class AppSPARQL extends App {

	/**
	 * Constructor for two SPARQL endpoints.
	 * @param urlref : Source data set endpoint.
	 * @param urlobj : Target data set endpoints.
	 */
	public AppSPARQL(String urlref, String urlobj) {
		super();
		
		try {
			reference = new JeuSPARQL(urlref);
			objectif = new JeuSPARQL(urlobj);
			
			nom = reference.getNom() + " - " + objectif.getNom();
	
			if (log.isInfoEnabled()) {
				log.info("Creation AppRDF " + nom);
			}
		}
		catch (RepositoryException e) {
			log.fatal(e);
			shutdown();
			System.exit(1);
		}
	}
}
