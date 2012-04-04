package prototype;


/**
 * Main temporary test class.
 * 
 * @author Thibaud Colas
 * @version 04042012
 */
public class Prototype {
	
	public static void main(String[] args) {
		App test = null;
		try {
			test = new AppSesame("http://localhost:8080/openrdf-sesame/","geo-insee","passim-test");
			test.setLiaisonTypee("geo:nom", "passim:centerTown", "geo:Commune", "");
			test.setSPARQLOutput();
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
