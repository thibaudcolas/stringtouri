package prototype;

import org.openrdf.repository.RepositoryException;

public class AppSPARQL extends App {

	public AppSPARQL(String urlref, String urlint) throws RepositoryException {
		super(new JeuSPARQL(urlref), new JeuSPARQL(urlint));
	}
}
