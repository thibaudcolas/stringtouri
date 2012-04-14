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
	 * @param refpath : Filepath to source data.
	 * @param objpath : Filepath to target data.
	 * @param reffilter : Name filter.
	 * @param objfilter : Name filter.
	 */
	public RDFApp(String refpath, String objpath, String reffilter, String objfilter) {
		super();
		
		try {
			reference = new RDFDataSet(refpath, reffilter, "");
			objectif = new RDFDataSet(objpath, objfilter, "");
			
			nom = reference.getNom() + " - " + objectif.getNom();
	
			if (LOG.isInfoEnabled()) {
				LOG.info("Creation AppRDF " + nom);
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
