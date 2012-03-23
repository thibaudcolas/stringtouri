package deprecated;

import org.openrdf.query.BindingSet;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

/**
 * Classe principale du prototype.
 * 
 * @author Thibaud Colas
 * @version 16032012
 * @see Jeu, JeuEphemere, JeuSesame, JeuSPARQL
 */
public class PassimInsee {

	//public static Jeu insee = new JeuEphemere("Geo INSEE", "./rdf/insee/","regions", "http://rdf.insee.fr/geo/");
	//public static Jeu passim = new JeuEphemere("PASSIM", "./rdf/passim-propre.rdf","", "T");
	public static Jeu insee = new JeuSesame("http://localhost:8080/openrdf-sesame","geo-insee");
	public static Jeu passim = new JeuSesame("http://localhost:8080/openrdf-sesame","passim-propre");
	
	public static LinkedList<String> convert(TupleQueryResult tupqres, String val) throws QueryEvaluationException {
		BindingSet bs;
		LinkedList<String> res = new LinkedList<String>();
		while (tupqres.hasNext()) {
			bs = tupqres.next();
			res.add(bs.getValue(val).stringValue());
		}
		return res;
	}
	
	public static HashMap<String, String> convert(TupleQueryResult tupqres, String key, String val) throws QueryEvaluationException {
		BindingSet bs;
		HashMap<String, String> conv = new HashMap<String, String>();
		while (tupqres.hasNext()) {
			bs = tupqres.next();
			conv.put(bs.getValue(key).stringValue(), bs.getValue(val).stringValue());
		}
		return conv;
	}
	
	public static String format(String nompas, String uriGEO) {
		String res = "\t\t<passim:cityThrough rdf:resource=\"" + uriGEO + "\"/> <!-- " + nompas + " -->\n";
		return res;
	}
	
	public static void main(String[] args) {
		try {
			HashMap<String, String> geo = convert(insee.SPARQLQuery("SELECT ?nomcom ?com WHERE { ?com a geo:Commune . ?com geo:nom ?nomcom }"), "nomcom", "com");
			LinkedList<String> pas = convert(passim.SPARQLQuery("SELECT ?ville WHERE {?s passim:cityThrough ?ville}"),"ville");
			
			// Nombre de communes en France selon Wikipedia : 36682
			System.out.println(insee.getNom() + " : " + geo.size() + " | " + passim.getNom() + " : " + pas.size());
			String sameas = "";
			LinkedList<String> dechets = new LinkedList<String>();
			int cpt = 0;
			int cptbis = 0;
			for(String uri : pas) {
				if (geo.containsKey(uri)) {
					sameas += format(uri, geo.get(uri));
					cpt++;
				}
				else {
					cptbis++;
					dechets.add(uri);
				}
			}
			System.out.println(cpt + " interconnexions créés.");
			System.out.println(cptbis + " communes délaissées.");
			System.out.println(dechets);
			
			BufferedWriter res = new BufferedWriter(new FileWriter("./rdf/resultat.rdf"));
			res.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rdf:RDF \n\txmlns:passim=\"http://data.lirmm.fr/ontologies/passim#\" \n\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n\n\t<rdf:Description rdf:about=\"http://SMTH\">\n" + sameas + "\t</rdf:Description>\n</rdf:RDF>\n");
			res.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			insee.shutdown();
			passim.shutdown();
		}
		

	}

}
