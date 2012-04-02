package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;

/**
 * Exports the interlinking as RDFXML.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see To
 */
public class ToRDF extends To {

	private static final String BALISEXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	/**
	 * Lazy constructor.
	 * @param j : A data set.
	 * @param p : The predicate for which we want to update values.
	 */
	public ToRDF(Jeu j, String p) {
		super(j, p);
	}

	/**
	 * Default constructor.
	 * @param j : A data set.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 */
	public ToRDF(Jeu j, HashMap<String, LinkedList<Statement>> m, String p) {
		super(j, m, p);
	}
	
	/**
	 * Alternative constructor.
	 * @param j : A data set.
	 * @param m : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @param a : Tells wether to process all of the statements within the data set or just the new ones.
	 */
	public ToRDF(Jeu j, HashMap<String, LinkedList<Statement>> m, String p, boolean a) {
		super(j, m, p, a);
	}
	
	
	/**
	 * Retrieves the output of the process as RDFXML.
	 * @param executer : Tells whether or not to execute the output on the data set.
	 * @return The RDFXML output.
	 */
	@Override
	public String getOutput(boolean executer) {
		if (output.equals("")) {
			output = writeRDF();
		}
		return output;
	}
	
	/**
	 * Main method to write RDFXML.
	 * @return RDFXML code.
	 */
	private String writeRDF() {
		return BALISEXML + "<rdf:RDF \n" + writeNamespaces() 
				+ ">\n" + writeStatements() + "</rdf:RDF>\n";
	}
	
	/**
	 * Writes the namespaces on the top of a RDFXML file.
	 * @return Namespaces as string.
	 */
	private String writeNamespaces() {
		String rdf = "";
		for (String ns : namespaces.keySet()) {
			rdf += writeNamespace(namespaces.get(ns), ns);
		}
		return rdf;
	}
	
	/**
	 * Writes a namespace on top of a RDFXML file.
	 * @param pre : The namespace's prefix.
	 * @param name : The namespace's URL.
	 * @return A namespace ready to be written into a file.
	 */
	private String writeNamespace(String pre, String name) {
		return "\txmlns:" + pre + "=\"" + name + "\"\n";
	}
	
	/**
	 * Writes every given statement in RDFXML.
	 * @return RDFXML using <rdf:Description> tags.
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
	
	/**
	 * Writes a rdf:Description tag.
	 * @param suj : The subject to be used.
	 * @param props : Its associated predicates.
	 * @return A rdf:Description tag as a string.
	 */
	private String writeDesc(String suj, String props) {
		return "<rdf:Description rdf:about=\"" + suj + "\">\n"
				+ props + "</rdf:Description>\n";
	}
	
	/**
	 * Chooses whether or not to use rdf:resource.
	 * @param prop : A predicate.
	 * @param obj : An object.
	 * @return A predicate - object pair.
	 */
	private String writeProp(String prop, String obj) {
		return "\t" + (obj.startsWith("http://") ? writeResourceProp(prop, obj) : writeTextProp(prop, obj)) + "\n";
	}
	
	/**
	 * Writes a predicate - object pair using rdf:resource.
	 * @param prop : A predicate.
	 * @param obj : An object.
	 * @return A predicate - object pair.
	 */
	private String writeResourceProp(String prop, String obj) {
		return "<" + prop + " rdf:resource=\"" + obj + "\"/>";
	}
	
	/**
	 * Writes a predicate - object pair without using rdf:resource.
	 * @param prop : A predicate.
	 * @param obj : An object.
	 * @return A predicate - object pair.
	 */
	private String writeTextProp(String prop, String obj) {
		return "<" + prop + ">" + obj + "</" + prop + ">";
	}

}
