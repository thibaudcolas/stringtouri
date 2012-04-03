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
			test = new AppRDF("./rdf/insee/","./rdf/passim-propre.rdf","departements","");
			test.setLiaisonTypee("geo:nom", "passim:department", "geo:Departement", "");
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
