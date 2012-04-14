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
public class JeuRDF extends DataSet {
	
	/**
	 * The base data set URI.
	 */
	private String baseuri;
	
	/**
	 * Simple constructor.
	 * @param source : Source file / folder.
	 * @param start : Filter on the filenames to import.
	 * @param uri : Base URI for the data.
	 * @throws RepositoryException The initialization has failed and no recovery is possible.
	 * @throws IOException File/folder error.
	 * @throws RDFParseException File(s) content isn't correct RDFXML.
	 */
	public JeuRDF(String source, String start, String uri) throws RepositoryException, IOException, RDFParseException {
		super(source);
		try {
			baseuri = uri;
			queries = new LinkedList<String>();
			
			rep = new SailRepository(new MemoryStore());
			rep.initialize();
			
			con = rep.getConnection();
			
			addSource(source, start, baseuri);
		} 
		catch (RepositoryException e) {
			throw new RepositoryException("While creating new JeuRDF - " + source, e);
		}
	}
	
	public final String getBaseURI() {
		return baseuri;
	}
}
