package prototype;

/**
 * Classe principale du prototype.
 * 
 * @author Thibaud Colas
 * @version 18032012
 * @see Liaison, Jeu
 */
public class PassimInsee2 {
	
	public static Jeu geoinsee = new JeuEphemere("./rdf/insee/","regions","TE");
	public static Jeu passimpropre = new JeuEphemere("./rdf/Brute3.rdf","","ET");
	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "tt:Région", "geo:Region", "");
	public static ToRDF tmp;
	
	public static void main(String[] args) {
		try {
			tmp = (ToRDF)test.interconnexion();
			tmp.writeFile("./rdf/resultat.rdf");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			test.shutdown();
		}
		

	}

}
