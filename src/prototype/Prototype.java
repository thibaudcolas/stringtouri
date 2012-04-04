package prototype;


/**
 * Temporary main test class.
 * 
 * @author Thibaud Colas
 * @version 05042012
 */
public class Prototype {
	
	public static void main(String[] args) {
		App test = null;
		try {
			test = new AppSesame("http://localhost:8080/openrdf-sesame/","geo-insee-all","passim-propre");
			//test = new AppRDF("./rdf/insee/","./rdf/Brute3.rdf", "regions", "");
			test.setLiaisonTypee("geo:nom", "passim:department", "geo:Departement", "");
			test.setSPARQLOutput();
			test.initiateInterconnexion(false);
			//System.out.println(test.getOutput());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (test != null) {
				test.shutdown();
			}
		}
	}
}
