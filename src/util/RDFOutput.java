package util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

	private String charset;
	
	/**
	 * Lazy constructor.
	 * @param ds : A data set.
	 * @param p : The predicate for which we want to update values.
	 * @param c : Charset to use when writing RDF.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public RDFOutput(DataSet ds, String p, String c) throws RepositoryException {
		super(ds, p);
		charset = c;
	}

	/**
	 * Default constructor.
	 * @param ds : A data set.
	 * @param ns : The new statements to use.
	 * @param p : The predicate for which we want to update values.
	 * @param c : Charset to use when writing RDF.
	 * @throws RepositoryException Error while fetching namespaces.
	 */
	public RDFOutput(DataSet ds, HashMap<String, LinkedList<Statement>> ns, String p, String c) throws RepositoryException {
		super(ds, ns, p);
		charset = c;
	}
	
	/**
	 * Retrieves the output of the process as RDFXML.
	 * @return The RDFXML output.
	 */
	@Override
	public String getOutput() {
		String content = "";
		try {
		// Open the file that is the first 
		  // command line parameter
		  FileInputStream fstream = new FileInputStream("./rdf/t2.rdf");
		  // Get the object of DataInputStream
		  DataInputStream in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String strLine;
		  //Read File Line By Line
		  String newline = "";
		  String tmpobj = "";
		  String tmpsubj = "";
		  LinkedList<Statement> tmpupdates = null;
		  int cptupdates = 0;
		  int cpt = 0;
		  while ((strLine = br.readLine()) != null)   {
			  if (strLine.contains("rdf:about")) {
				  tmpsubj = strLine.split("\"")[1];
				  tmpupdates = newtuples.get(tmpsubj);
				  //System.out.println(tmpupdates);
				  cptupdates = 0;
				  newline = strLine;
				  cpt++;
				  System.out.println(cpt);
			  }
			  else if (strLine.contains(linkingpredicate)) {
				  //System.out.println(strLine);
				  tmpobj = tmpupdates != null ? tmpupdates.get(cptupdates).getObject().stringValue() : "";
				  newline = (!"".equals(tmpobj)) ? "<" + linkingpredicate + " rdf:resource=\"" + tmpobj  + "\"/>" : strLine;
				  cptupdates++;
			  }
			  else {
				  newline = strLine;
			  }
			  content += newline + "\n";
		  }
		  //Close the input stream
		  in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return content;
		//return writeRDF();
	}
	
	/**
	 * Never supposed to be called.
	 * @throws RepositoryException Fatal error while updating the data set.
	 * @deprecated
	 */
	public void updateDataSet() throws RepositoryException {
		throw new RepositoryException("Invalid attempt to update using RDF writer - " + olddataset.getName());
	}
	
	/**
	 * Main method to write RDFXML.
	 * @return RDFXML code.
	 * @deprecated
	 */
	@SuppressWarnings("unused")
	private String writeRDF() {
		return "<?xml version=\"1.0\" encoding=\"" + charset + "\"?>\n" 
				+ "<rdf:RDF \n" + writeNamespaces() + ">\n" 
				+ writeStatements() + "</rdf:RDF>\n";
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
				predicates += writePredicate(m);
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
	
	/** Checks wether a link points to a rdf:resource or just to a standard web page.
	 * 
	 * @param subjectstring : The subject of the statement.
	 * @param predicatestring : The predicate of the statement.
	 * @param objectstring : The object of the statement.
	 * @return True if the link is a RDF URI.
	 */
	//FIXME Isn't able to recognize existing interlinkings.
	private boolean isRDFResource(String subjectstring, String predicatestring, String objectstring) {
		return predicatestring.equals(linkingpredicate)
				|| predicatestring.equals("owl:sameAs")
				|| namespaces.containsKey(objectstring.split("#")[0] + "#")
				|| objectstring.substring(0, objectstring.lastIndexOf("/")).equals(subjectstring.substring(0, Math.max(subjectstring.lastIndexOf("/"), 0)));
	}
	
	/**
	 * Writes the predicate line as needed, handling URI, boolean, string and integer values.
	 * @param statement : The statement to be written.
	 * @return A predicate - object pair.
	 */
	private String writePredicate(Statement s) {
		String predicatestring = filterPredicate(s.getPredicate());
		String objectstring = s.getObject().stringValue();
		
		String rdf;
		if (objectstring.startsWith("http://") && isRDFResource(s.getSubject().stringValue(), predicatestring, objectstring)) {
			rdf = "<" + predicatestring + " rdf:resource=\"" + objectstring + "\"/>";
		}
		else if (objectstring.equals("true") || objectstring.equals("false")){
			rdf = "<" + predicatestring + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">" + objectstring + "</" + predicatestring + ">";
		}
		else {
			try {
				rdf = "<" + predicatestring + " rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">" + Integer.parseInt(objectstring) + "</" + predicatestring + ">";
			}
			catch (NumberFormatException e) {
				rdf = "<" + predicatestring + ">" + objectstring + "</" + predicatestring + ">";
				// Old filter : objectstring.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;")
			}
		}
		return "\t" + rdf + "\n";
	}
}
