package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 * Classe qui exporte l'interconnexion sous forme de RDFXML.
 * 
 * @author Thibaud Colas
 * @version 29032012
 * @see To
 */
public class ToRDF extends To {

	private static final String BALISEXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	public ToRDF(Jeu j, String p) {
		super(j, p);
	}

	public ToRDF(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) {
		super(j, m, p);
	}
	
	public ToRDF(Jeu j, HashMap<String, LinkedList<Statement>> m, String p, boolean a) {
		super(j, m, p, a);
	}
	
	@Override
	public String getOutput(boolean executer) {
		if (output.equals("")) {
			output = writeRDF();
		}
		return output;
	}
	
	private String writeRDF() {
		return BALISEXML + "<rdf:RDF \n" + writeNamespaces() 
				+ ">\n" + writeStatements() + "</rdf:RDF>\n";
	}
	
	private String writeNamespaces() {
		String rdf = "";
		for (String ns : namespaces.keySet()) {
			rdf += writeNamespace(namespaces.get(ns), ns);
		}
		return rdf;
	}
	
	private String writeNamespace(String pre, String name) {
		return "\txmlns:" + pre + "=\"" + name + "\"\n";
	}
	
	/**
	 * Écrit tous les triplets de maj en les groupant par sujet.
	 * @return Du RDFXML structuré en balises rdf:Description.
	 */
	private String writeStatements() {
		String rdf = "";
		String props = "";
		LinkedList<Statement> tmp;
		// Classement par sujet.
		for (String sujet : maj.keySet()) {
			tmp = maj.get(sujet);
			for (Statement m : tmp) {
				props += writeProp(filterPredicate(m.getPredicate()), m.getObject().stringValue());
			}
			rdf += writeDesc(sujet, props);
			rdf += "\n";
			props = "";
		}
		
		return rdf;
	}
	
	private String writeDesc(String suj, String props) {
		return "<rdf:Description rdf:about=\"" + suj + "\">\n"
				+ props + "</rdf:Description>\n";
	}
	
	/**
	 * Utilisé pour convertir une propriété sous forme d'URI en sa version courte utilisant un préfixe.
	 * @param p : L'URI propriété à convertir.
	 * @return Une propriété sous la forme préfixe:propriété.
	 */
	private String filterPredicate(URI p) {
		String ns = p.getNamespace();
		return (ns.startsWith("http://") ? namespaces.get(ns) + ":" : ns) + p.getLocalName();
	}
	
	private String writeProp(String prop, String obj) {
		return "\t" + (obj.startsWith("http://") ? writeResourceProp(prop, obj) : writeTextProp(prop, obj)) + "\n";
	}
	
	private String writeResourceProp(String prop, String obj) {
		return "<" + prop + " rdf:resource=\"" + obj + "\"/>";
	}
	
	private String writeTextProp(String prop, String obj) {
		return "<" + prop + ">" + obj + "</" + prop + ">";
	}

}
