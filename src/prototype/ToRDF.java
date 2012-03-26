package prototype;

import java.util.LinkedList;

import org.openrdf.model.Namespace;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

public class ToRDF extends To {

	private static final String baliseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	private boolean all;
	
	public ToRDF(Jeu j) {
		super(j);
		all = false;
	}

	public ToRDF(Jeu j, LinkedList<Statement> m) {
		super(j, m);
		all = false;
	}
	
	public ToRDF(Jeu j, LinkedList<Statement> m, boolean a) {
		super(j, m);
		all = a;
	}
	
	public String getOutput() {
		if(output.equals("")) output = writeRDF();
		return output;
	}
	
	private String writeRDF() {
		return baliseXML + "<rdf:RDF \n"+writeNamespaces()+">\n" + writeStatements() + "</rdf:RDF>\n";
	}
	
	private String writeNamespaces() {
		String rdf = "";
		try {
			for(Namespace n : amodif.getNamespaceList())
				rdf += writeNamespace(n);
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
		LinkedList<Statement> tmp = null;
		if(all) { 
			try {
				LinkedList<Statement> tous = amodif.getAllStatements();
				LinkedList<Statement> modifs = maj;
				boolean comparaison;
				int i;
				Statement si = null;
				int cpt = 0;
				for (Statement s : tous) {
					comparaison = true;
					i = 0;
					while(comparaison && i < modifs.size()) {
						si = modifs.get(i);
						comparaison = !(si.getSubject().stringValue().equals(s.getSubject().stringValue()) 
								&& si.getPredicate().getLocalName().equals(s.getPredicate().getLocalName()));
						i++;
					}
					if (!comparaison) {
						tous.set(cpt, si);
					}
					cpt++;
					tmp = tous;
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		else { 
			tmp = maj;
		}
		for (Statement m : tmp) {
			rdf += writeDesc(m.getSubject().stringValue(), writeProp(m.getPredicate().stringValue(), m.getObject().stringValue(), true));
			rdf += "\n";
		}
		
		return rdf;
	}
	
	private String writeDesc(String suj, String props) {
		String rdf = "<rdf:Description rdf:about=\""+suj+"\">\n"
				+ props + "</rdf:Description>\n";
		return rdf;
	}
	
	private String writeProp(String prop, String obj, boolean isresource) {
		return "\t" + (isresource ? writeResourceProp(prop, obj) : writeTextProp(prop, obj)) + "\n";
	}
	
	private String writeResourceProp(String prop, String obj) {
		return "<"+prop+" rdf:resource=\""+obj+"\"/>";
	}
	
	private String writeTextProp(String prop, String obj) {
		return "<"+prop+">"+obj+"</"+prop+">";
	}

}
