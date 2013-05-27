package me.assembla.stringtouri.dataset;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * A data set stored inside a local or remote Sesame server.
 * 
 * @author Thibaud Colas
 * @version 2012-09-30
 * @see HTTPRepository
 */
public class SesameDataset extends Dataset {
	
	/**
	 * The URL to the repository.
	 */
	private String repositoryurl;
	
	/**
	 * Default constructor.
	 * @param repurl : Direct URL to the Sesame repository.
	 * @param cont : Context associeted with the dataset.
	 * @throws RepositoryException The initialization has failed and no recovery is possible.
	 */
	public SesameDataset(String repurl, String cont) throws RepositoryException {
		super(repurl, cont);
		try {
			repositoryurl = repurl;
			repository = new HTTPRepository(repurl);
			repository.initialize();
			
			connection = repository.getConnection();
		} 
		catch (RepositoryException e) {
			throw new RepositoryException("While creating new SesameDataSet - " + repositoryurl, e);
		}
	}
	
	public final String getRepositoryURL() {
		return repositoryurl;
	}
}
