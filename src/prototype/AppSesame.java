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
	 */
	public AppSesame(String urlref, String urlobj) {
		super();
		
		try {
			reference = new JeuSPARQL(urlref);
			objectif = new JeuSPARQL(urlobj);
			
			nom = reference.getNom() + " - " + objectif.getNom();
	
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
	
	/**
	 * Default constructor.
	 * @param urlsesame : URL to SESAME server.
	 * @param depotref : Source repository identifier.
	 * @param depotobj : Target repository identifier.
	 */
	public AppSesame(String urlsesame, String depotref, String depotobj) {
		super();
		
		try {
			reference = new JeuSesame(urlsesame, depotref);
			objectif = new JeuSesame(urlsesame, depotobj);
			
			nom = reference.getNom() + " - " + objectif.getNom();
	
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
