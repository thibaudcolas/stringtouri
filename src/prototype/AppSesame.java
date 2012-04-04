package prototype;

import org.openrdf.repository.RepositoryException;

/**
 * Helps handling the interlinking with Sesame data sets.
 * 
 * @author Thibaud Colas
 * @version 04042012
 * @see App
 */
public class AppSesame extends App {

	/**
	 * Simple constructor.
	 * @param urlref : URL to source SESAME repository.
	 * @param urlobj : URL to target SESAME repository.
	 * @throws RepositoryException Error inside one or both the data sets.
	 */
	public AppSesame(String urlref, String urlobj) throws RepositoryException {
		super(new JeuSesame(urlref), new JeuSesame(urlobj));
	}
	
	/**
	 * Default constructor.
	 * @param urlsesame : URL to SESAME server.
	 * @param depotref : Source repository identifier.
	 * @param depotobj : Target repository identifier.
	 * @throws RepositoryException Error inside one or both the data sets.
	 */
	public AppSesame(String urlsesame, String depotref, String depotobj) throws RepositoryException {
		super(new JeuSesame(urlsesame, depotref), new JeuSesame(urlsesame, depotobj));
	}
}
