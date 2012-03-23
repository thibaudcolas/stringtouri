package prototype;

/**
 * Classe principale du prototype.
 * 
 * @author Thibaud Colas
 * @version 18032012
 * @see Liaison, Jeu
 */
public class DemoPassimInsee {
	
	public static JeuSesame geoinsee = new JeuSesame("http://localhost:8080/openrdf-sesame","geo-insee");
	public static JeuSesame passimpropre = new JeuSesame("http://localhost:8080/openrdf-sesame","passim-propre");
	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "passim:cityThrough", "geo:Commune", "");
	
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
