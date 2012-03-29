package prototype;

/**
 * Classe principale du prototype.
 * 
 * @author Thibaud Colas
 * @version 26032012
 * @see Liaison, Jeu, To
 */
public class Prototype {
	
//	public static Jeu geoinsee = new JeuEphemere("./rdf/insee/","regions","TE");
//	public static Jeu passimpropre = new JeuEphemere("./rdf/Brute3.rdf","","ET");
//	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "tt:Région", "geo:Region", "");
	public static Jeu geoinsee = new JeuSesame("http://localhost:8080/openrdf-sesame","geo-insee-all");
	public static Jeu passimpropre = new JeuSesame("http://localhost:8080/openrdf-sesame","passim-test");
	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "passim:centerTown", "geo:Commune", "");
//	public static Jeu geoinsee = new JeuEphemere("./rdf/insee/","departements","TE");
//	public static Jeu passimpropre = new JeuEphemere("./rdf/passim-propre.rdf","","ET");
//	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "passim:department", "geo:Departement", "");
	
	public static void main(String[] args) {
		try {
			//To tmp = new ToRDF(passimpropre, test.getInterconnexion(), "passim:cityThrough", true);
			//System.out.println(tmp.getOutput());
			To tmp = new ToSPARQL(passimpropre, test.getInterconnexion(), "passim:centerTown");
			System.out.println(tmp.getOutput(false));
			//System.out.println(test.getInterconnexion());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			test.shutdown();
		}
	}
}
