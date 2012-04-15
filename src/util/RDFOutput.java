package util;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;

/**
 * Exports the interlinking as RDFXML.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see Output
 */
public class RDFOutput extends Output {

	private static final String XMLTAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	/**
	 * Lazy constructor.
	 * @param ds : A data set.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public RDFOutput(DataSet ds, String p) throws RepositoryException {
		super(ds, p);
	}

	/**
	 * Default constructor.
	 * @param ds : A data set.
	 * @param ns : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public RDFOutput(DataSet ds, HashMap<String, LinkedList<Statement>> ns, String p) throws RepositoryException {
		super(ds, ns, p);
	}
	
	/**
	 * Alternative constructor.
	 * @param ds : A data set.
	 * @param ns : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @param a : Tells wether to process all of the statements within the data set or just the new ones.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public RDFOutput(DataSet ds, HashMap<String, LinkedList<Statement>> ns, String p, boolean a) throws RepositoryException {
		super(ds, ns, p, a);
	}
	
	
	/**
	 * Retrieves the output of the process as RDFXML.
	 * @return The RDFXML output.
	 */
	@Override
	public String getOutput() {
		return writeRDF();
	}
	
	/**
	 * Never supposed to be called.
	 * @throws RepositoryException Fatal error while updating the data set.
	 */
	public void updateDataSet() throws RepositoryException {
		throw new RepositoryException("Invalid attempt to update using RDF writer - " + olddataset.getName());
	}
	
	/**
	 * Main method to write RDFXML.
	 * @return RDFXML code.
	 */
	private String writeRDF() {
		return XMLTAG + "<rdf:RDF \n" + writeNamespaces() 
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
	 * @param prefix : The namespace's prefix.
	 * @param namespaceurl : The namespace's URL.
	 * @return A namespace ready to be written into a file.
	 */
	private String writeNamespace(String prefix, String namespaceurl) {
		return "\txmlns:" + prefix + "=\"" + namespaceurl + "\"\n";
	}
	
	/**
	 * Writes every given statement in RDFXML.
	 * @return RDFXML using <rdf:Description> tags.
	 */
	private String writeStatements() {
		String rdf = "";
		String predicates = "";
		LinkedList<Statement> tmp;
		// Classement par sujet.
		for (String sujet : newtuples.keySet()) {
			tmp = newtuples.get(sujet);
			for (Statement m : tmp) {
				predicates += writePredicate(filterPredicate(m.getPredicate()), m.getObject().stringValue());
			}
			rdf += writeDescription(sujet, predicates);
			rdf += "\n";
			predicates = "";
		}
		
		return rdf;
	}
	
	/**
	 * Writes a rdf:Description tag.
	 * @param subject : The subject to be used.
	 * @param predicates : Its associated predicates.
	 * @return A rdf:Description tag as a string.
	 */
	private String writeDescription(String subject, String predicates) {
		return "<rdf:Description rdf:about=\"" + subject + "\">\n"
				+ predicates + "</rdf:Description>\n";
	}
	
	/**
	 * Chooses whether or not to use rdf:resource.
	 * @param predicate : A predicate.
	 * @param object : An object.
	 * @return A predicate - object pair.
	 */
	private String writePredicate(String predicate, String object) {
		return "\t" + (object.startsWith("http://") ? writeResourcePredicate(predicate, object) : writeTextPredicate(predicate, object)) + "\n";
	}
	
	/**
	 * Writes a predicate - object pair using rdf:resource.
	 * @param predicate : A predicate.
	 * @param object : An object.
	 * @return A predicate - object pair.
	 */
	private String writeResourcePredicate(String predicate, String object) {
		return "<" + predicate + " rdf:resource=\"" + object + "\"/>";
	}
	
	/**
	 * Writes a predicate - object pair without using rdf:resource.
	 * @param predicate : A predicate.
	 * @param object : An object.
	 * @return A predicate - object pair.
	 */
	private String writeTextPredicate(String predicate, String object) {
		return "<" + predicate + ">" + object + "</" + predicate + ">";
	}

}
