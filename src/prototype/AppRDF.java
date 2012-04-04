package prototype;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

/**
 * Helps handling the interlinking with RDF data.
 * 
 * @author Thibaud Colas
 * @version 04042012
 * @see App
 */
public class AppRDF extends App {

	/**
	 * Default constructor.
	 * @param refpath : Filepath to source data.
	 * @param objpath : Filepath to target data.
	 * @param reffilter : Name filter.
	 * @param objfilter : Name filter.
	 */
	public AppRDF(String refpath, String objpath, String reffilter, String objfilter) {
		super();
		
		try {
			reference = new JeuRDF(refpath, reffilter, "");
			objectif = new JeuRDF(objpath, objfilter, "");
			
			nom = reference.getNom() + " - " + objectif.getNom();
	
			if (log.isInfoEnabled()) {
				log.info("Creation AppRDF " + nom);
			}
		}
		catch (RepositoryException e) {
			log.fatal(e);
			shutdown();
			System.exit(1);
		} catch (RDFParseException e) {
			log.fatal(e);
			shutdown();
			System.exit(4);
		} catch (IOException e) {
			log.fatal(e);
			shutdown();
			System.exit(2);
		}
	}
}
