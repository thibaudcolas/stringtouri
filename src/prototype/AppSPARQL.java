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
	 * @param urlint : Target data set endpoints.
	 * @throws RepositoryException Error inside one or both the data sets.
	 */
	public AppSPARQL(String urlref, String urlint) throws RepositoryException {
		super(new JeuSPARQL(urlref), new JeuSPARQL(urlint));
	}
}
