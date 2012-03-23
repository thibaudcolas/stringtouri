package prototype;

/**
 * Classe principale du prototype.
 * 
 * @author Thibaud Colas
 * @version 18032012
 * @see Liaison, Jeu
 */
public class Prototype {
	
	public static Jeu passim = new JeuEphemere("./rdf/Brute3.rdf","", "T");
	public static Jeu geoinsee = new JeuEphemere("./rdf/insee/","regions", "T");
	public static Liaison test = new LiaisonTypee(geoinsee, passim, "geo:nom", "tt:Région", "geo:Region", "");
	
	public static void main(String[] args) {
		try {

			System.out.println("Res : \n" + test.interconnexion());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			test.shutdown();
		}
		

	}

}
