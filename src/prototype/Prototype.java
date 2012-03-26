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
	public static Jeu geoinsee = new JeuSesame("http://localhost:8080/openrdf-sesame","geo-insee");
	public static Jeu passimpropre = new JeuSesame("http://localhost:8080/openrdf-sesame","passim-propre");
	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "passim:cityThrough", "geo:Commune", "");
//	public static Jeu geoinsee = new JeuEphemere("./rdf/insee/","departements","TE");
//	public static Jeu passimpropre = new JeuEphemere("./rdf/passim-propre.rdf","","ET");
//	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "passim:department", "geo:Departement", "");
	
	public static void main(String[] args) {
		try {
			//To tmp = new ToRDF(passimpropre, test.getNewStatements(), true);
			To tmp = new ToSesame(passimpropre, test.getNewStatements());
			System.out.println(tmp.getOutput());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			test.shutdown();
		}
		

	}
	
	public String writeRDF(String sub, String prop, String obj) {
		String ret = "<rdf:Description rdf:about=\""+sub+"\">\n" 
			+ "\t<"+prop+" rdf:resource=\""+obj+"\"/>\n"
			+ "</rdf:Description>\n";
		
		return ret;
	}
	
	public String writeRDF(String sub, String prop, String obj, String comment) {
		String ret = "<rdf:Description rdf:about=\""+sub+"\">\n" 
			+ "\t<"+prop+" rdf:resource=\""+obj+"\"/>"
			+ " <!-- "+comment+" -->\n"
			+ "</rdf:Description>\n";
		
		return ret;
	}

}
