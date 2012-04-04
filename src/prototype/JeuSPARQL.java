package prototype;

import java.util.LinkedList;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;

/**
 * A data set accessed using a SPARQL endpoint.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see SPARQLRepository
 */
public class JeuSPARQL extends Jeu {
	
	/**
	 * The SPARQL endpoint's URL.
	 */
	private String endpoint;
	
	/**
	 * Default constructor.
	 * @param ep : SPARQL endpoint URL.
	 * @throws RuntimeException The initialization has failed and no recovery is possible.
	 */
	JeuSPARQL(String ep) throws RepositoryException {
		super(ep);
		try {	
			endpoint = ep;
			rep = new SPARQLRepository(ep);
			rep.initialize();
			
			queries = new LinkedList<String>();
			
			con = rep.getConnection();
		} 
		catch (RepositoryException e) {
			throw new RepositoryException("While creating new JeuSPARQL - " + ep, e);
		}
	}
	
	public final String getEndPoint() {
		return endpoint;
	}
}
