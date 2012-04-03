package prototype;

import org.openrdf.repository.RepositoryException;

public class AppSesame extends App {

	public AppSesame(String urlref, String urlobj) throws RepositoryException {
		super(new JeuSesame(urlref), new JeuSesame(urlobj));
	}
	
	public AppSesame(String urlsesame, String depotref, String depotobj) throws RepositoryException {
		super(new JeuSesame(urlsesame, depotref), new JeuSesame(urlsesame, depotobj));
	}
}
