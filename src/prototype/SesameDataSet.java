package prototype;

import java.util.LinkedList;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * A data set stored inside a local or remote SESAME server.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see HTTPRepository
 */
public class SesameDataSet extends DataSet {
	
	/**
	 * The SESAME server URL.
	 */
	private String sesame;
	/**
	 * The identifier of the repository.
	 */
	private String depot;
	
	/**
	 * Lazy constructor.
	 * @param sd : Direct URL to the SESAME repository.
	 * @throws RepositoryException The initialization has failed and no recovery is possible.
	 */
	public SesameDataSet(String sd) throws RepositoryException {
		super(sd);
		try {
			sesame = sd;
			depot = sd;
			rep = new HTTPRepository(sd);
			rep.initialize();
			
			queries = new LinkedList<String>();
			
			con = rep.getConnection();
		} 
		catch (RepositoryException e) {
			throw new RepositoryException("While creating new JeuSesame - " + sd, e);
		}
	}
	
	/**
	 * Default constructor.
	 * @param s : SESAME server's URL.
	 * @param d : Repository id.
	 * @throws RepositoryException The initialization has failed and no recovery is possible.
	 */
	public SesameDataSet(String s, String d) throws RepositoryException {
		super(s + " - " + d);
		try {	
			sesame = s;
			depot = d;
			rep = new HTTPRepository(sesame, depot);
			rep.initialize();
			
			queries = new LinkedList<String>();
			
			con = rep.getConnection();
		} 
		catch (RepositoryException e) {
			throw new RepositoryException("While creating new JeuSesame - " + s + " " + d, e);
		}
	}
	
	public final String getURLSesame() {
		return sesame;
	}
	
	public final String getIdDepot() {
		return depot;
	}
}
