package prototype;

import org.openrdf.repository.RepositoryException;

public class AppSPARQL extends App {

	public AppSPARQL(String urlref, String urlint) throws RepositoryException {
		
		reference = new JeuSPARQL(urlref);
		objectif = new JeuSPARQL(urlint);
	}
}
