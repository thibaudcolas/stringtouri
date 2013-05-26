package me.assembla.stringtouri;

import me.assembla.stringtouri.dataset.SesameDataset;

import org.openrdf.repository.RepositoryException;

/**
 * Helps handling the interlinking with Sesame data sets.
 * 
 * @author Thibaud Colas
 * @version 13072012
 * @see App
 */
public class SesameApp extends App {

	/**
	 * Simple constructor.
	 * @param rurl : URL to reference SESAME repository.
	 * @param gurl : URL to goal SESAME repository.
	 * @param rcont : Context in reference SESAME repository.
	 * @param gcont : Context in goal SESAME repository.
	 * @throws RepositoryException 
	 */
	public SesameApp(String rurl, String gurl, String rcont, String gcont) throws RepositoryException {
		super();
		
		try {
			reference = new SesameDataset(rurl, rcont);
			goal = new SesameDataset(gurl, gcont);
			goalContext = gcont;
			
			name = reference.getName() + " - " + goal.getName();
	
			if (LOG.isDebugEnabled()) {
				LOG.debug("Created SesameApp " + name);
			}
		}
		catch (RepositoryException e) {
			LOG.fatal(e);
			shutdown();
			throw e;
		}
	}
}
