package util;

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
public class SPARQLDataSet extends DataSet {
	
	/**
	 * The SPARQL endpoint's URL.
	 */
	private String endpointurl;
	
	/**
	 * Default constructor.
	 * @param url : SPARQL endpoint URL.
	 * @throws RepositoryException The initialization has failed and no recovery is possible.
	 */
	public SPARQLDataSet(String url) throws RepositoryException {
		super(url);
		try {	
			endpointurl = url;
			repository = new SPARQLRepository(url);
			repository.initialize();
			
			queries = new LinkedList<String>();
			
			connection = repository.getConnection();
		} 
		catch (RepositoryException e) {
			throw new RepositoryException("While creating new JeuSPARQL - " + url, e);
		}
	}
	
	public final String getEndpointURL() {
		return endpointurl;
	}
}
