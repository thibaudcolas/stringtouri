package prototype;

import java.util.LinkedList;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * A data set stored inside a local or remote Sesame server.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see HTTPRepository
 */
public class SesameDataSet extends DataSet {
	
	/**
	 * The Sesame server URL.
	 */
	private String serverurl;
	/**
	 * The identifier of the repository.
	 */
	private String repositoryid;
	
	/**
	 * Lazy constructor.
	 * @param repositoryurl : Direct URL to the Sesame repository.
	 * @throws RepositoryException The initialization has failed and no recovery is possible.
	 */
	public SesameDataSet(String repositoryurl) throws RepositoryException {
		super(repositoryurl);
		try {
			serverurl = repositoryurl;
			repositoryid = repositoryurl;
			repository = new HTTPRepository(repositoryurl);
			repository.initialize();
			
			queries = new LinkedList<String>();
			
			connection = repository.getConnection();
		} 
		catch (RepositoryException e) {
			throw new RepositoryException("While creating new JeuSesame - " + repositoryurl, e);
		}
	}
	
	/**
	 * Default constructor.
	 * @param url : Sesame server's URL.
	 * @param identifier : Repository id.
	 * @throws RepositoryException The initialization has failed and no recovery is possible.
	 */
	public SesameDataSet(String url, String identifier) throws RepositoryException {
		super(url + " - " + identifier);
		try {	
			serverurl = url;
			repositoryid = identifier;
			repository = new HTTPRepository(serverurl, repositoryid);
			repository.initialize();
			
			queries = new LinkedList<String>();
			
			connection = repository.getConnection();
		} 
		catch (RepositoryException e) {
			throw new RepositoryException("While creating new JeuSesame - " + url + " " + identifier, e);
		}
	}
	
	public final String getServerURL() {
		return serverurl;
	}
	
	public final String getRepositoryID() {
		return repositoryid;
	}
}
