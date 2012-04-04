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
			test = new AppSesame("http://localhost:8080/openrdf-sesame/","geo-insee-all","passim-test");
			test.setLiaisonTypee("geo:nom", "passim:centerTown", "geo:Commune", "");
			test.setRDFOutput();
			test.initiateInterconnexion(true);
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
