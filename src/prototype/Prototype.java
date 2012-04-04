package prototype;


/**
 * Temporary main test class.
 * 
 * @author Thibaud Colas
 * @version 04042012
 */
public class Prototype {
	
	public static void main(String[] args) {
		App test = null;
		try {
			//test = new AppSesame("http://localhost:8080/openrdf-sesame/","geo-insee-all","passim-test");
			test = new AppRDF("./rdf/insee/","./rdf/Brute3.rdf", "regions", "");
			test.setLiaisonTypee("geo:nom", "tt:Région", "geo:Region", "");
			test.setRDFOutput();
			test.initiateInterconnexion(false);
			System.out.println(test.getOutput());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			test.shutdown();
		}
	}
}
