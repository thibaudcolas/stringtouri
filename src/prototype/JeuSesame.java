package prototype;

import java.util.LinkedList;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * A data set stored inside a local or remote SESAME server.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see HTTPRepository
 */
public class JeuSesame extends Jeu {
	
	/**
	 * The SESAME server URL.
	 */
	private String sesame;
	/**
	 * The identifier of the repository.
	 */
	private String depot;
	
	/**
	 * Lazy constructor.
	 * @param sd : Direct URL to the SESAME repository.
	 */
	JeuSesame(String sd) {
		try {	
			nom = sd;
			sesame = sd;
			depot = sd;
			rep = new HTTPRepository(sd);
			rep.initialize();
			
			queries = new LinkedList<String>();
			
			con = rep.getConnection();
		} 
		catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Default constructor.
	 * @param s : SESAME server's URL.
	 * @param d : Repository id.
	 */
	JeuSesame(String s, String d) {
		try {	
			nom = s + " - " + d;
			sesame = s;
			depot = d;
			rep = new HTTPRepository(sesame, depot);
			rep.initialize();
			
			queries = new LinkedList<String>();
			
			con = rep.getConnection();
		} 
		catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	public final String getURLSesame() {
		return sesame;
	}
	
	public final String getIdDepot() {
		return depot;
	}
}
