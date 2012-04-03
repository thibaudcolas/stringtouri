package prototype;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

public class AppRDF extends App {

	public AppRDF(String refpath, String objpath, String reffilter, String objfilter) throws RepositoryException, RDFParseException, RuntimeException, IOException {
		super(new JeuRDF(refpath, reffilter, ""), new JeuRDF(objpath, objfilter, ""));
	}
}
