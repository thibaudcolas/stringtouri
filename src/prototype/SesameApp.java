package prototype;

import org.openrdf.repository.RepositoryException;

/**
 * Helps handling the interlinking with Sesame data sets.
 * 
 * @author Thibaud Colas
 * @version 04042012
 * @see App
 */
public class SesameApp extends App {

	/**
	 * Simple constructor.
	 * @param rurl : URL to reference SESAME repository.
	 * @param gurl : URL to goal SESAME repository.
	 */
	public SesameApp(String rurl, String gurl) {
		super();
		
		try {
			reference = new SPARQLDataSet(rurl);
			goal = new SPARQLDataSet(gurl);
			
			name = reference.getName() + " - " + goal.getName();
	
			if (LOG.isInfoEnabled()) {
				LOG.info("Created SesameApp " + name);
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
	 * @param sid : Source repository identifier.
	 * @param gid : Goal repository identifier.
	 */
	public SesameApp(String urlsesame, String sid, String gid) {
		super();
		
		try {
			reference = new SesameDataSet(urlsesame, sid);
			goal = new SesameDataSet(urlsesame, gid);
			
			name = reference.getName() + " - " + goal.getName();
	
			if (LOG.isInfoEnabled()) {
				LOG.info("Creation AppRDF " + name);
			}
		}
		catch (RepositoryException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODERE);
		}
	}
}
