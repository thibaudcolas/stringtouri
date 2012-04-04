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
	 * @throws RepositoryException Bad things happened to one or both the data sets.
	 * @throws RDFParseException RDF inside one of the files is corrupted.
	 * @throws IOException Error while reading one of the files.
	 */
	public AppRDF(String refpath, String objpath, String reffilter, String objfilter) throws RepositoryException, RDFParseException, RuntimeException, IOException {
		super(new JeuRDF(refpath, reffilter, ""), new JeuRDF(objpath, objfilter, ""));
	}
}
