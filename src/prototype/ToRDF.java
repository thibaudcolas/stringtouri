package prototype;

import java.util.HashMap;
import java.util.LinkedList;

import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

/**
 * Classe qui exporte l'interconnexion sous forme de RDFXML
 * 
 * @author Thibaud Colas
 * @version 26032012
 * @see To
 */
public class ToRDF extends To {

	private static final String baliseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	private HashMap<String, String> namespaces;
	
	public ToRDF(Jeu j) {
		super(j);
		namespaces = new HashMap<String, String>();
	}

	public ToRDF(Jeu j, LinkedList<Statement> m) {
		super(j, m);
		namespaces = new HashMap<String, String>();
	}
	
	public ToRDF(Jeu j, LinkedList<Statement> m, boolean a) {
		super(j, m, a);
		namespaces = new HashMap<String, String>();
	}
	
	@Override
	public String getOutput() {
		if(output.equals("")) output = writeRDF();
		return output;
	}
	
	private String writeRDF() {
		return baliseXML + "<rdf:RDF \n"+writeNamespaces()+">\n" + writeStatements() + "</rdf:RDF>\n";
	}
	
	private String writePredicate(URI p) {
		return (p.getNamespace().startsWith("http://") ? namespaces.get(p.getNamespace())+":" : p.getNamespace()) + p.getLocalName();
	}
	
	private String writeNamespaces() {
		String rdf = "";
		try {
			for(Namespace n : amodif.getNamespaceList()) {
				namespaces.put(n.getName(), n.getPrefix());
				rdf += writeNamespace(n);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return rdf;
	}
	
	private String writeNamespace(Namespace n) {
		return "xmlns:" + n.getPrefix() + "=\"" + n.getName() + "\"\n";
	}
	
	private String writeStatements() {
		String rdf = "";
		LinkedList<Statement> tmp = all ? getGoodStatements() : maj;
		for (Statement m : tmp) {
			rdf += writeDesc(m.getSubject().stringValue(), writeProp(writePredicate(m.getPredicate()), m.getObject().stringValue()));
			rdf += "\n";
		}
		
		return rdf;
	}
	
	private String writeDesc(String suj, String props) {
		String rdf = "<rdf:Description rdf:about=\""+suj+"\">\n"
				+ props + "</rdf:Description>\n";
		return rdf;
	}
	
	private String writeProp(String prop, String obj) {
		return "\t" + (obj.startsWith("http://") ? writeResourceProp(prop, obj) : writeTextProp(prop, obj)) + "\n";
	}
	
	private String writeResourceProp(String prop, String obj) {
		return "<"+prop+" rdf:resource=\""+obj+"\"/>";
	}
	
	private String writeTextProp(String prop, String obj) {
		return "<"+prop+">"+obj+"</"+prop+">";
	}

}
