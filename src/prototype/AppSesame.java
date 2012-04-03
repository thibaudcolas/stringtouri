package prototype;

import org.openrdf.repository.RepositoryException;

public class AppSesame extends App {

	public AppSesame(String urlref, String urlobj) throws RepositoryException {
		reference = new JeuSesame(urlref);
		objectif = new JeuSesame(urlobj);
	}
	
	public AppSesame(String urlsesame, String depotref, String depotobj) throws RepositoryException {
		
		reference = new JeuSesame(urlsesame, depotref);
		objectif = new JeuSesame(urlsesame, depotobj);
	}
}
