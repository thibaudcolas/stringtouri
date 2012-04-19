package app;

import util.App;
import util.RDFApp;

/**
 * Temporary main test class.
 * 
 * @author Thibaud Colas
 * @version 16042012
 */
public class Prototype {
	
	public static void main(String[] args) {
		//App test = new AppSesame("http://localhost:8080/openrdf-sesame/", "geo-insee-all", "passim-propre");
		App test = new RDFApp("./rdf/insee/", "./rdf/Brute3.rdf", "regions", "");
		test.useTypedLinkage("geo:nom", "tt:Région", "geo:Region", "");
		test.useSPARQLOutput(true);
		System.out.println(test.getOutput());
		test.shutdown();
	}
}
