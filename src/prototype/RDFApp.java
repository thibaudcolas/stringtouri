package prototype;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

/**
 * Helps handling the interlinking with RDF data.
 * 
 * @author Thibaud Colas
 * @version 05042012
 * @see App
 */
public class RDFApp extends App {

	/**
	 * Default constructor.
	 * @param rpath : Filepath to source data.
	 * @param gpath : Filepath to target data.
	 * @param rfilter : Name filter.
	 * @param gfilter : Name filter.
	 */
	public RDFApp(String rpath, String gpath, String rfilter, String gfilter) {
		super();
		
		try {
			reference = new RDFDataSet(rpath, rfilter, "");
			goal = new RDFDataSet(gpath, gfilter, "");
			
			name = reference.getName() + " - " + goal.getName();
	
			if (LOG.isInfoEnabled()) {
				LOG.info("Creation AppRDF " + name);
			}
		}
		catch (RepositoryException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODERE);
		} catch (RDFParseException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODEIO);
		} catch (IOException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODEIO);
		}
	}
}
