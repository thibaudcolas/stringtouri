package app;

import util.App;
import util.RDFApp;

/**
 * Sample to test with Datalift.
 * @author Thibaud Colas.
 * @version 24042012
 *
 */
public class StringToURI {

	public static void main(String[] args) {
		App test = new RDFApp("/Users/Will/Desktop/continents.rdf", "/Users/Will/Desktop/countries-tolink.rdf", "", "");
		test.useTypedLinkage("gn:name", "geographis:onContinent", "geographis:Continent", "gn:Country");
		test.useSPARQLOutput(false);
		test.storeOutput("/Users/Will/Desktop/datalift-resultat.txt");
		test.shutdown();
	}

}
