package prototype;

/**
 * Main temporary test class.
 * 
 * @author Thibaud Colas
 * @version 01042012
 */
public class Prototype {
	
//	public static Jeu geoinsee = new JeuEphemere("./rdf/insee/", "regions", "TE");
//	public static Jeu passimpropre = new JeuEphemere("./rdf/Brute3.rdf", "", "ET");
//	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "tt:Région", "geo:Region", "");
//	public static Jeu geoinsee = new JeuSesame("http://localhost:8080/openrdf-sesame", "geo-insee-all");
//	public static Jeu passimpropre = new JeuSesame("http://localhost:8080/openrdf-sesame", "passim-propre");
//	public static Jeu passimtest = new JeuSesame("http://localhost:8080/openrdf-sesame", "passim-test");
//	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "passim:cityThrough", "geo:Commune", "");
	public static Jeu geoinsee;
	public static Jeu passimpropre;
	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "passim:department", "geo:Departement", "");
	
	public static void main(String[] args) {
		try {
			geoinsee = new JeuEphemere("./rdf/insee/", "departements", "TE");
			passimpropre  = new JeuEphemere("./rdf/passim-propre.rdf", "", "ET");
			To tmp = new ToSPARQL(passimpropre, test.getInterconnexion(), "passim:department");
			System.out.println(tmp.getOutput(true));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			test.shutdown();
		}
	}
}
