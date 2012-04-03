package prototype;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

public class AppRDF extends App {

	public AppRDF(String refpath, String objpath, String reffilter, String objfilter) throws RepositoryException, RDFParseException, RuntimeException, IOException {
	
		reference = new JeuRDF(refpath, reffilter, "");
		objectif = new JeuRDF(objpath, objfilter, "");
	}
	
	public AppRDF(String refurl, String objpath, String objfilter) throws RDFParseException, RuntimeException, IOException, RepositoryException {
		reference = new JeuSPARQL(refurl);
		objectif = new JeuRDF(objpath, objfilter, "");
	}
}
