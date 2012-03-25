package prototype;

/**
 * Classe principale du prototype.
 * 
 * @author Thibaud Colas
 * @version 18032012
 * @see Liaison, Jeu
 */
public class Prototype {
	
	public static Jeu geoinsee = new JeuEphemere("./rdf/insee/","regions","TE");
	public static Jeu passimpropre = new JeuEphemere("./rdf/Brute3.rdf","","ET");
	public static Liaison test = new LiaisonTypee(geoinsee, passimpropre, "geo:nom", "tt:Région", "geo:Region", "");
	
	public static void main(String[] args) {
		try {

			System.out.println("Res : \n" + test.createNewStatements());
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
