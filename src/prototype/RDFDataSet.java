package prototype;

import java.io.IOException;
import java.util.LinkedList;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

/**
 * A data set stored locally which lasts only for runtime.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see SailRepository
 */
public class RDFDataSet extends DataSet {
	
	/**
	 * The base data set URI.
	 */
	private String baseuri;
	
	/**
	 * Simple constructor.
	 * @param path : Source file / folder.
	 * @param prefix : Prefix of the filenames to import.
	 * @param uri : Base URI for the data.
	 * @throws RepositoryException The initialization has failed and no recovery is possible.
	 * @throws IOException File/folder error.
	 * @throws RDFParseException File(s) content isn't correct RDFXML.
	 */
	public RDFDataSet(String path, String prefix, String uri) throws RepositoryException, IOException, RDFParseException {
		super(path);
		try {
			baseuri = uri;
			queries = new LinkedList<String>();
			
			repository = new SailRepository(new MemoryStore());
			repository.initialize();
			
			connection = repository.getConnection();
			
			addTuples(path, prefix, baseuri);
		} 
		catch (RepositoryException e) {
			throw new RepositoryException("While creating new JeuRDF - " + path, e);
		}
	}
	
	public final String getBaseURI() {
		return baseuri;
	}
}
